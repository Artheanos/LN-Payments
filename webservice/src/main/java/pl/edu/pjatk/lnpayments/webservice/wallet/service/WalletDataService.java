package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pjatk.lnpayments.webservice.common.exception.NotFoundException;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;

@Service
public class WalletDataService {

    private final WalletRepository walletRepository;

    @Autowired
    WalletDataService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet save(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public Wallet getActiveWallet() {
        return walletRepository.findFirstByStatus(WalletStatus.ON_DUTY)
                .orElseThrow(() -> new NotFoundException("Active wallet not found"));
    }

    public Wallet getWalletInCreation() {
        return walletRepository.findFirstByStatus(WalletStatus.IN_CREATION)
                .orElseThrow(() -> new NotFoundException("Wallet in creation not found"));
    }

    public void deleteCreatingWallet() {
        walletRepository.deleteByStatus(WalletStatus.IN_CREATION);
    }

    public boolean existsActive() {
        return walletRepository.existsByStatus(WalletStatus.ON_DUTY);
    }

    public boolean existInCreation() {
        return walletRepository.existsByStatus(WalletStatus.IN_CREATION);
    }
}
