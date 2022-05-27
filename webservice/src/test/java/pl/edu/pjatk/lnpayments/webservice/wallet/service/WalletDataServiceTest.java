package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.exception.NotFoundException;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.WalletStatus;
import pl.edu.pjatk.lnpayments.webservice.wallet.repository.WalletRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletDataServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletDataService walletDataService;

    @Test
    void shouldThrowNotFoundExceptionWhenWalletDoesNotExist() {
        when(walletRepository.findFirstByStatus(WalletStatus.ON_DUTY)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> walletDataService.getActiveWallet());
    }

    @Test
    void shouldReturnActiveWallet() {
        Wallet wallet = Wallet.builder().address("2137").build();
        when(walletRepository.findFirstByStatus(WalletStatus.ON_DUTY)).thenReturn(Optional.of(wallet));

        Wallet actual = walletDataService.getActiveWallet();

        assertThat(actual).isEqualTo(wallet);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenWalletInCreationDoesNotExist() {
        when(walletRepository.findFirstByStatus(WalletStatus.IN_CREATION)).thenReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> walletDataService.getWalletInCreation());
    }

    @Test
    void shouldReturnWalletInCreation() {
        Wallet wallet = Wallet.builder().address("2137").build();
        when(walletRepository.findFirstByStatus(WalletStatus.IN_CREATION)).thenReturn(Optional.of(wallet));

        Wallet actual = walletDataService.getWalletInCreation();

        assertThat(actual).isEqualTo(wallet);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnProperValueAboutActiveWalletExistence(boolean exists) {
        when(walletRepository.existsByStatus(WalletStatus.ON_DUTY)).thenReturn(exists);

        boolean actual = walletDataService.existsActive();

        assertThat(actual).isEqualTo(exists);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldReturnProperValueAboutWalletInCreationExistence(boolean exists) {
        when(walletRepository.existsByStatus(WalletStatus.IN_CREATION)).thenReturn(exists);

        boolean actual = walletDataService.existInCreation();

        assertThat(actual).isEqualTo(exists);
    }

    @Test
    void shouldCallDeleteInRepositoryWhenDeletingWalletInCreation() {
        walletDataService.deleteCreatingWallet();

        verify(walletRepository).deleteByStatus(WalletStatus.IN_CREATION);
    }

    @Test
    void shouldCallRepositoryWhileSavingWallet() {
        Wallet wallet = Wallet.builder().address("2137").build();
        walletDataService.save(wallet);

        verify(walletRepository).save(wallet);
    }

}
