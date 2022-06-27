package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.admin.converter.AdminConverter;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;
import pl.edu.pjatk.lnpayments.webservice.transaction.service.TransactionService;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.WalletDetails;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@Service
public class WalletService {

    private final BitcoinService bitcoinService;
    private final AdminService adminService;
    private final LightningWalletService lightningWalletService;
    private final ChannelService channelService;
    private final AdminConverter adminConverter;
    private final TransactionService transactionService;
    private final WalletDataService walletDataService;
    private final PaymentFacade paymentFacade;

    @Autowired
    public WalletService(BitcoinService bitcoinService,
                         AdminService adminService,
                         LightningWalletService lightningWalletService,
                         ChannelService channelService,
                         AdminConverter adminConverter,
                         TransactionService transactionService,
                         WalletDataService walletDataService, PaymentFacade paymentFacade) {
        this.bitcoinService = bitcoinService;
        this.adminService = adminService;
        this.lightningWalletService = lightningWalletService;
        this.channelService = channelService;
        this.adminConverter = adminConverter;
        this.transactionService = transactionService;
        this.walletDataService = walletDataService;
        this.paymentFacade = paymentFacade;
    }

    @Transactional
    public void createWallet(List<String> adminEmails, int minSignatures) {
        if (walletDataService.existsActive()) {
            recreateWallet(adminEmails, minSignatures);
        } else {
            buildNewWallet(adminEmails, minSignatures, WalletStatus.ON_DUTY);
        }
    }

    public void transferToWallet() {
        if (walletDataService.existInCreation()) {
            throw new ValidationException("When wallet is in creation, transfer is unavailable");
        }
        Wallet activeWallet = walletDataService.getActiveWallet();
        String txId = lightningWalletService.transfer(activeWallet.getAddress());
        log.info("Funds were transferred in tx: {}", txId);
    }

    public void closeAllChannels(boolean withForce) {
        channelService.closeAllChannels(withForce);
    }

    public WalletDetails getDetails() {
        Wallet activeWallet = walletDataService.getActiveWallet();
        return WalletDetails.builder()
                .address(activeWallet.getAddress())
                .admins(adminConverter.convertAllToDto(activeWallet.getUsers()))
                .bitcoinWalletBalance(bitcoinService.getBalance())
                .channelsBalance(channelService.getChannelsBalance())
                .lightningWalletBalance(lightningWalletService.getBalance())
                .totalIncomeData(paymentFacade.aggregateTotalIncomeData())
                .build();
    }


    @Transactional
    public void swapWallets() {
        Wallet oldWallet = walletDataService.getActiveWallet();
        Wallet newWallet = walletDataService.getWalletInCreation();
        oldWallet.setStatus(WalletStatus.REMOVED);
        newWallet.setStatus(WalletStatus.ON_DUTY);
    }

    public void deleteWalletInCreation() {
        walletDataService.deleteCreatingWallet();
    }

    private void recreateWallet(List<String> adminEmails, int minSignatures) {
        if (walletDataService.existInCreation()) {
            throw new ValidationException("There is already a wallet in creation process");
        }
        if(transactionService.isTransactionInProgress()) {
            throw new ValidationException("There is already a transaction in progress");
        }
        List<AdminUser> adminUsers = adminService.findAllWithKeys(adminEmails);
        Wallet activeWallet = walletDataService.getActiveWallet();
        if (minSignatures == activeWallet.getMinSignatures() && adminUsers.containsAll(activeWallet.getUsers())) {
            throw new InconsistentDataException("Wallet cannot be recreated with the same data");
        }
        Wallet newWallet = buildNewWallet(adminEmails, minSignatures, WalletStatus.IN_CREATION);
        transactionService.createTransaction(newWallet.getAddress());
    }

    private Wallet buildNewWallet(List<String> adminEmails, int minSignatures, WalletStatus status) {
        if (adminEmails.size() < minSignatures) {
            throw new InconsistentDataException("You can't require more confirmations than you have users");
        }
        List<AdminUser> adminUsers = adminService.findAllWithKeys(adminEmails);
        Wallet wallet = bitcoinService.createWallet(adminUsers, minSignatures);
        wallet.setStatus(status);
        adminUsers.forEach(user -> user.setWallet(wallet));
        return walletDataService.save(wallet);
    }
}
