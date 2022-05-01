package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.admin.converter.AdminConverter;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.common.exception.NotFoundException;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.WalletDetails;

import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@Service
public class WalletService {

    private final BitcoinService bitcoinService;
    private final AdminService adminService;
    private final WalletRepository walletRepository;
    private final LightningWalletService lightningWalletService;
    private final ChannelService channelService;
    private final AdminConverter adminConverter;

    @Autowired
    public WalletService(BitcoinService bitcoinService,
                         AdminService adminService,
                         WalletRepository walletRepository,
                         LightningWalletService lightningWalletService,
                         ChannelService channelService,
                         AdminConverter adminConverter) {
        this.bitcoinService = bitcoinService;
        this.adminService = adminService;
        this.walletRepository = walletRepository;
        this.lightningWalletService = lightningWalletService;
        this.channelService = channelService;
        this.adminConverter = adminConverter;
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
        adminUsers.forEach(user -> user.setWallet(wallet));
        walletRepository.save(wallet);
    }

    public void transferToWallet() {
        Wallet activeWallet = getActiveWallet();
        String txId = lightningWalletService.transfer(activeWallet.getAddress());
        log.info("Funds were transferred in tx: {}", txId);
    }

    public void closeAllChannels(boolean withForce) {
        channelService.closeAllChannels(withForce);
    }

    public WalletDetails getDetails() {
        Wallet activeWallet = getActiveWallet();
        return WalletDetails.builder()
                .address(activeWallet.getAddress())
                .admins(adminConverter.convertAllToDto(activeWallet.getUsers()))
                .bitcoinWalletBalance(bitcoinService.getBalance())
                .channelsBalance(channelService.getChannelsBalance())
                .lightningWalletBalance(lightningWalletService.getBalance())
                .build();
    }

    public Wallet getActiveWallet() {
        return walletRepository.findFirstByStatus(WalletStatus.ON_DUTY)
                .orElseThrow(() -> new NotFoundException("Active wallet not found"));
    }
}
