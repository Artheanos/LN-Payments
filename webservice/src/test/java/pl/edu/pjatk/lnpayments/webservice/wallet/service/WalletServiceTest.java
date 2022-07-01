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
import pl.edu.pjatk.lnpayments.webservice.admin.converter.AdminConverter;
import pl.edu.pjatk.lnpayments.webservice.admin.service.AdminService;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory;
import pl.edu.pjatk.lnpayments.webservice.payment.facade.PaymentFacade;
import pl.edu.pjatk.lnpayments.webservice.payment.model.AggregatedData;
import pl.edu.pjatk.lnpayments.webservice.transaction.service.TransactionService;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.BitcoinWalletBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.ChannelsBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.LightningWalletBalance;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.WalletDetails;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private BitcoinService bitcoinService;

    @Mock
    private AdminService adminService;

    @Mock
    private WalletDataService walletDataService;

    @Mock
    private LightningWalletService lightningWalletService;

    @Mock
    private ChannelService channelService;

    @Mock
    private AdminConverter adminConverter;

    @Mock
    private TransactionService transactionService;

    @Mock
    private PaymentFacade paymentFacade;

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
        Wallet wallet = new Wallet("123", "456", "789", adminUsers, 2);
        when(walletDataService.existsActive()).thenReturn(false);
        when(adminService.findAllWithKeys(adminEmails)).thenReturn(adminUsers);
        when(bitcoinService.createWallet(adminUsers, 2)).thenReturn(wallet);

        walletService.createWallet(adminEmails, 2);

        verify(bitcoinService).createWallet(adminUsers, 2);
        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletDataService).existsActive();
        verify(walletDataService).save(captor.capture());
        Wallet savedWallet = captor.getValue();
        assertThat(savedWallet.getAddress()).isEqualTo(wallet.getAddress());
        assertThat(savedWallet.getMinSignatures()).isEqualTo(wallet.getMinSignatures());
        assertThat(savedWallet.getRedeemScript()).isEqualTo(wallet.getRedeemScript());
        assertThat(savedWallet.getScriptPubKey()).isEqualTo(wallet.getScriptPubKey());
        assertThat(savedWallet.getUsers()).isEqualTo(wallet.getUsers());
    }

    @Test
    void shouldRecreateWalletWhenActiveAlreadyExists() {
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        List<String> adminEmails = List.of(admin1.getEmail(), admin2.getEmail());
        List<AdminUser> adminUsers = List.of(admin1, admin2);
        Wallet oldWallet = new Wallet("123", "456", "7189", adminUsers, 1);
        Wallet wallet = new Wallet("123", "456", "789", adminUsers, 2);
        when(walletDataService.existsActive()).thenReturn(true);
        when(walletDataService.existInCreation()).thenReturn(false);
        when(walletDataService.getActiveWallet()).thenReturn(oldWallet);
        when(walletDataService.save(wallet)).thenReturn(wallet);
        when(adminService.findAllWithKeys(adminEmails)).thenReturn(adminUsers);
        when(bitcoinService.createWallet(adminUsers, 2)).thenReturn(wallet);

        walletService.createWallet(adminEmails, 2);

        verify(transactionService).createTransaction(wallet.getAddress());
        verify(bitcoinService).createWallet(adminUsers, 2);
        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletDataService).existsActive();
        verify(walletDataService).save(captor.capture());
        Wallet savedWallet = captor.getValue();
        assertThat(savedWallet.getAddress()).isEqualTo(wallet.getAddress());
        assertThat(savedWallet.getMinSignatures()).isEqualTo(wallet.getMinSignatures());
        assertThat(savedWallet.getRedeemScript()).isEqualTo(wallet.getRedeemScript());
        assertThat(savedWallet.getScriptPubKey()).isEqualTo(wallet.getScriptPubKey());
        assertThat(savedWallet.getUsers()).isEqualTo(wallet.getUsers());
    }

    @Test
    void shouldThrowExceptionWhenMoreRequiredSignaturesThanEmails() {
        List<String> adminEmails = List.of("asd@asd.pl", "dsa@dsa.lp");
        when(walletDataService.existsActive()).thenReturn(false);

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> walletService.createWallet(adminEmails, 3))
                .withMessage("You can't require more confirmations than you have users");
    }

    @Test
    void shouldTransferFundsToWalletAddressAndLogMessage() {
        Wallet wallet = Wallet.builder().address("2137").build();
        when(walletDataService.getActiveWallet()).thenReturn(wallet);

        walletService.transferToWallet();

        verify(lightningWalletService).transfer("2137");
        assertThat(logAppender.list)
                .extracting(ILoggingEvent::getMessage, ILoggingEvent::getLevel)
                .contains(Tuple.tuple("Funds were transferred in tx: {}", Level.INFO));
    }

    @Test
    void shouldNotTransferWhenWalletIsInCreation() {
        when(walletDataService.existInCreation()).thenReturn(true);

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> walletService.transferToWallet())
                .withMessage("When wallet is in creation, transfer is unavailable");

        verify(lightningWalletService, never()).transfer(anyString());
        assertThat(logAppender.list).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldCallChannelServiceWhenClosingAllWithProperFlag(boolean force) {
        walletService.closeAllChannels(force);

        verify(channelService).closeAllChannels(force);
    }

    @Test
    void shouldReturnWalletBalance() {
        List<AggregatedData> balanceHistoricData = List.of(new AggregatedData(LocalDate.now().toString(), 12L));
        BitcoinWalletBalance bitcoinWalletBalance = BitcoinWalletBalance.builder().availableBalance(100L).unconfirmedBalance(100L).build();
        ChannelsBalance channelsBalance = ChannelsBalance.builder().openedChannels(1).totalBalance(100L).autoChannelCloseLimit(100L).build();
        LightningWalletBalance lightningWalletBalance = LightningWalletBalance.builder().availableBalance(100L).unconfirmedBalance(100L).autoTransferLimit(100L).build();
        Wallet wallet = Wallet.builder().address("2137").build();
        when(walletDataService.getActiveWallet()).thenReturn(wallet);
        when(lightningWalletService.getBalance()).thenReturn(lightningWalletBalance);
        when(bitcoinService.getBalance()).thenReturn(bitcoinWalletBalance);
        when(channelService.getChannelsBalance()).thenReturn(channelsBalance);
        when(paymentFacade.aggregateTotalIncomeData()).thenReturn(balanceHistoricData);

        WalletDetails details = walletService.getDetails();

        assertThat(details.getAddress()).isEqualTo("2137");
        assertThat(details.getChannelsBalance()).isEqualTo(channelsBalance);
        assertThat(details.getBitcoinWalletBalance()).isEqualTo(bitcoinWalletBalance);
        assertThat(details.getLightningWalletBalance()).isEqualTo(lightningWalletBalance);
        assertThat(details.getTotalIncomeData()).isEqualTo(balanceHistoricData);
    }


    @Test
    void shouldThrowExceptionWhenWalletInCreationAlreadyExists() {
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        List<String> adminEmails = List.of(admin1.getEmail(), admin2.getEmail());
        when(walletDataService.existsActive()).thenReturn(true);
        when(walletDataService.existInCreation()).thenReturn(true);

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> walletService.createWallet(adminEmails, 2))
                .withMessage("There is already a wallet in creation process");
        verify(walletDataService, never()).save(any());
        verify(transactionService, never()).createTransaction(anyString());
    }

    @Test
    void shouldThrowExceptionWhenTransactionIsPending() {
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        List<String> adminEmails = List.of(admin1.getEmail(), admin2.getEmail());
        when(walletDataService.existsActive()).thenReturn(true);
        when(walletDataService.existInCreation()).thenReturn(false);
        when(transactionService.isTransactionInProgress()).thenReturn(true);

        assertThatExceptionOfType(ValidationException.class)
                .isThrownBy(() -> walletService.createWallet(adminEmails, 1))
                .withMessage("There is already a transaction in progress");

        verify(walletDataService, never()).save(any());
        verify(transactionService, never()).createTransaction(anyString());
    }

    @Test
    void shouldThrowExceptionWhenTriedToRecreateWithSameData() {
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        List<String> adminEmails = List.of(admin1.getEmail(), admin2.getEmail());
        List<AdminUser> adminUsers = List.of(admin1, admin2);
        Wallet wallet = new Wallet("123", "456", "789", adminUsers, 2);
        when(walletDataService.existsActive()).thenReturn(true);
        when(walletDataService.existInCreation()).thenReturn(false);
        when(walletDataService.getActiveWallet()).thenReturn(wallet);
        when(adminService.findAllWithKeys(adminEmails)).thenReturn(adminUsers);

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> walletService.createWallet(adminEmails, 2));
        verify(walletDataService, never()).save(any());
        verify(transactionService, never()).createTransaction(anyString());
    }

    @Test
    void shouldSwapWallets() {
        Wallet oldWallet = new Wallet("123", "456", "7189", null, 1);
        oldWallet.setStatus(WalletStatus.ON_DUTY);
        Wallet wallet = new Wallet("123", "456", "789", null, 2);
        wallet.setStatus(WalletStatus.IN_CREATION);
        when(walletDataService.getActiveWallet()).thenReturn(oldWallet);
        when(walletDataService.getWalletInCreation()).thenReturn(wallet);

        walletService.swapWallets();

        assertThat(oldWallet.getStatus()).isEqualTo(WalletStatus.REMOVED);
        assertThat(wallet.getStatus()).isEqualTo(WalletStatus.ON_DUTY);
    }

    @Test
    void shouldInvokeServiceWhenDeletingWallet() {
        walletDataService.deleteCreatingWallet();

        verify(walletDataService).deleteCreatingWallet();
    }

}
