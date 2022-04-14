package pl.edu.pjatk.lnpayments.webservice.wallet.service;

import org.bitcoinj.core.Coin;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.wallet.Wallet.BalanceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.edu.pjatk.lnpayments.webservice.common.entity.AdminUser;
import pl.edu.pjatk.lnpayments.webservice.helper.factory.UserFactory;
import pl.edu.pjatk.lnpayments.webservice.wallet.entity.Wallet;
import pl.edu.pjatk.lnpayments.webservice.wallet.resource.dto.BitcoinWalletBalance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BitcoinServiceTest {

    @Mock
    private WalletAppKit walletAppKit;

    @InjectMocks
    private BitcoinService bitcoinService;

    @Test
    void shouldCreateWallet() {
        AdminUser admin1 = UserFactory.createAdminUser("admin1@test.pl");
        AdminUser admin2 = UserFactory.createAdminUser("admin2@test.pl");
        List<AdminUser> adminUsers = List.of(admin1, admin2);
        when(walletAppKit.params()).thenReturn(RegTestParams.get());
        when(walletAppKit.wallet()).thenReturn(Mockito.mock(org.bitcoinj.wallet.Wallet.class));

        Wallet wallet = bitcoinService.createWallet(adminUsers, 2);

        assertThat(wallet.getAddress()).isEqualTo("2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv");
        assertThat(wallet.getRedeemScript()).isEqualTo("52210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb210346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb52ae");
        assertThat(wallet.getScriptPubKey()).isEqualTo("a9148c0b2c246ce6738f00f5dd47948966f791042ad887");
        assertThat(wallet.getUsers()).isEqualTo(adminUsers);
    }

    @Test
    void shouldObtainWalletBalance() {
        org.bitcoinj.wallet.Wallet walletMock = Mockito.mock(org.bitcoinj.wallet.Wallet.class);
        when(walletMock.getBalance(BalanceType.AVAILABLE)).thenReturn(Coin.valueOf(1000L));
        when(walletMock.getBalance(BalanceType.ESTIMATED)).thenReturn(Coin.valueOf(900L));
        when(walletAppKit.wallet()).thenReturn(walletMock);

        BitcoinWalletBalance balance = bitcoinService.getBalance();
        assertThat(balance.getAvailableBalance()).isEqualTo(1000L);
        assertThat(balance.getUnconfirmedBalance()).isEqualTo(100L);
    }

}
