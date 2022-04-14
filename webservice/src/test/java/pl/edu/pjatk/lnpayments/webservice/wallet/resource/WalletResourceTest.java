package pl.edu.pjatk.lnpayments.webservice.wallet.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.CreateWalletRequest;
import pl.edu.pjatk.lnpayments.webservice.wallet.service.WalletService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WalletResourceTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletResource walletResource;

    @Test
    void shouldReturnCreatedForNewWalletRequest() {
        CreateWalletRequest request = new CreateWalletRequest(2, Collections.emptyList());

        ResponseEntity<?> response = walletResource.createWallet(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(walletService).createWallet(any(), eq(2));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnOkForChannelCloseRequest(boolean force) {
        ResponseEntity<?> response = walletResource.closeAllChannels(force);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(walletService).closeAllChannels(force);
    }

    @Test
    void shouldReturnOkForTransactionRequest() {
        ResponseEntity<?> response = walletResource.transferFunds();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(walletService).transferToWallet();
    }

    @Test
    void shouldReturnOkForBalanceRequest() {
        ResponseEntity<?> response = walletResource.getWalletDetails();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(walletService).getDetails();
    }
}
