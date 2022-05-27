package pl.edu.pjatk.lnpayments.webservice.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    boolean existsByStatus(WalletStatus status);

    Optional<Wallet> findFirstByStatus(WalletStatus status);

    void deleteByStatus(WalletStatus status);
}
