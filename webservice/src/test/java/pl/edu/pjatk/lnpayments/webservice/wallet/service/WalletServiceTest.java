package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;

import javax.validation.ValidationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private TransferService transferService;

    @Mock
    private ChannelService channelService;

    @InjectMocks
    private WalletService walletService;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        logAppender = new ListAppender<>();
        logAppender.start();
        ((Logger) LoggerFactory.getLogger(WalletService.class)).addAppender(logAppender);
    }

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

    @Test
    void shouldTransferFundsToWalletAddressAndLogMessage() {
        Wallet wallet = Wallet.builder().address("2137").build();
        when(walletRepository.findFirstByStatus(WalletStatus.ON_DUTY)).thenReturn(Optional.of(wallet));

        walletService.transferToWallet();

        verify(transferService).transfer("2137");
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Funds were transferred in tx: {}", Level.INFO));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldCallChannelServiceWhenClosingAllWithProperFlag(boolean force) {
        walletService.closeAllChannels(force);

        verify(channelService).closeAllChannels(force);
    }

}
