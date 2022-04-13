package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.common.exception.NotFoundException;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;

import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@Service
public class WalletService {

    private final BitcoinService bitcoinService;
    private final AdminService adminService;
    private final WalletRepository walletRepository;
    private final TransferService transferService;
    private final ChannelService channelService;

    @Autowired
    public WalletService(BitcoinService bitcoinService,
                         AdminService adminService,
                         WalletRepository walletRepository,
                         TransferService transferService,
                         ChannelService channelService) {
        this.bitcoinService = bitcoinService;
        this.adminService = adminService;
        this.walletRepository = walletRepository;
        this.transferService = transferService;
        this.channelService = channelService;
    }

    public void createWallet(List<String> adminEmails, int minSignatures) {
        if (walletRepository.existsByStatus(WalletStatus.ON_DUTY)) {
            throw new ValidationException("Wallet already exists!");
        }
        if (adminEmails.size() < minSignatures) {
            throw new InconsistentDataException("You can't require more confirmations than you have users");
        }
        List<AdminUser> adminUsers = adminService.findAllWithKeys(adminEmails);
        Wallet wallet = bitcoinService.createWallet(adminUsers, minSignatures);
        walletRepository.save(wallet);
    }

    public void transferToWallet() {
        Wallet activeWallet = walletRepository.findFirstByStatus(WalletStatus.ON_DUTY).orElseThrow(NotFoundException::new);
        String txId = transferService.transfer(activeWallet.getAddress());
        log.info("Funds were transferred in tx: {}", txId);
    }

    public void closeAllChannels(boolean withForce) {
        channelService.closeAllChannels(withForce);
    }
}
