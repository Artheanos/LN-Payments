package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;
import org.lightningj.lnd.wrapper.ValidationException;
import org.lightningj.lnd.wrapper.message.SendCoinsRequest;
import org.lightningj.lnd.wrapper.message.SendCoinsResponse;
import org.lightningj.lnd.wrapper.message.WalletBalanceResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.exception.InconsistentDataException;
import pl.edu.pjatk.lnpayments.webservice.payment.exception.LightningException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private SynchronousLndAPI lndAPI;

    @InjectMocks
    private TransferService transferService;

    @Test
    void shouldTransferIfFundsAreSufficient() throws StatusException, ValidationException {
        WalletBalanceResponse balanceRequest = new WalletBalanceResponse();
        balanceRequest.setConfirmedBalance(2137L);
        SendCoinsResponse sendCoinsResponse = new SendCoinsResponse();
        sendCoinsResponse.setTxid("tx");
        when(lndAPI.walletBalance()).thenReturn(balanceRequest);
        when(lndAPI.sendCoins(any())).thenReturn(sendCoinsResponse);

        String txId = transferService.transfer("addr");

        assertThat(txId).isEqualTo("tx");
        ArgumentCaptor<SendCoinsRequest> sendCoinsRequestArgumentCaptor = ArgumentCaptor.forClass(SendCoinsRequest.class);
        verify(lndAPI).sendCoins(sendCoinsRequestArgumentCaptor.capture());
        SendCoinsRequest sendCoinsRequest = sendCoinsRequestArgumentCaptor.getValue();
        assertThat(sendCoinsRequest.getAddr()).isEqualTo("addr");
        assertThat(sendCoinsRequest.getSendAll()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenNoFundsAvailable() throws StatusException, ValidationException {
        WalletBalanceResponse balanceRequest = new WalletBalanceResponse();
        balanceRequest.setConfirmedBalance(0L);
        when(lndAPI.walletBalance()).thenReturn(balanceRequest);

        assertThatExceptionOfType(InconsistentDataException.class)
                .isThrownBy(() -> transferService.transfer(""))
                .withMessage("Not funds in the wallet");
    }

    @Test
    void shouldThrowExceptionWhenCoinSendFailed() throws StatusException, ValidationException {
        Class<ValidationException> exceptionClass = ValidationException.class;
        WalletBalanceResponse balanceRequest = new WalletBalanceResponse();
        balanceRequest.setConfirmedBalance(2137L);
        when(lndAPI.walletBalance()).thenReturn(balanceRequest);
        when(lndAPI.sendCoins(any())).thenThrow(exceptionClass);

        assertThatExceptionOfType(LightningException.class)
                .isThrownBy(() -> transferService.transfer(""))
                .withMessage("Unable to transfer funds")
                .withCauseExactlyInstanceOf(exceptionClass);
    }
}
