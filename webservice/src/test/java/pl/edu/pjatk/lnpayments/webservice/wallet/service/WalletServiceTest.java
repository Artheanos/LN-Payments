package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private BitcoinService bitcoinService;

    @Mock
    private AdminService adminService;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldCreateWalletForValidData() {
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        List<String> adminEmails = List.of(admin1.getEmail(), admin2.getEmail());
        List<AdminUser> adminUsers = List.of(admin1, admin2);
        Wallet wallet = new Wallet("123", "456", "789", adminUsers);
        when(walletRepository.existsByStatus(any())).thenReturn(false);
        when(adminService.findAllWithKeys(adminEmails)).thenReturn(adminUsers);
        when(bitcoinService.createWallet(adminUsers, 2)).thenReturn(wallet);

        walletService.createWallet(adminEmails, 2);

        verify(walletRepository).existsByStatus(any());
        verify(bitcoinService).createWallet(adminUsers, 2);
        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(captor.capture());
        Wallet savedWallet = captor.getValue();
        assertThat(savedWallet.getAddress()).isEqualTo(wallet.getAddress());
        assertThat(savedWallet.getRedeemScript()).isEqualTo(wallet.getRedeemScript());
        assertThat(savedWallet.getScriptPubKey()).isEqualTo(wallet.getScriptPubKey());
        assertThat(savedWallet.getUsers()).isEqualTo(wallet.getUsers());
    }

    @Test
    void shouldThrowExceptionWhenWalletAlreadyExists() {
        when(walletRepository.existsByStatus(any())).thenReturn(true);

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> walletService.createWallet(Collections.emptyList(), 2))
                .withMessage("Wallet already exists!");
    }

    @Test
    void shouldThrowExceptionWhenMoreRequiredSignaturesThanEmails() {
        List<String> adminEmails = List.of("asd@asd.pl", "dsa@dsa.lp");
        when(walletRepository.existsByStatus(any())).thenReturn(false);

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> walletService.createWallet(adminEmails, 3))
                .withMessage("You can't require more confirmations than you have users");
    }

}
