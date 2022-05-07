package pl.edu.pjatk.lnpayments.webservice.common.validation;

import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BitcoinAddressValidatorTest {

    @Mock
    private WalletAppKit walletAppKit;

    @InjectMocks
    private BitcoinAddressValidator bitcoinAddressValidator;

    @ParameterizedTest
    @MethodSource("testData")
    void shouldProperlyValidateKeys(String key, boolean isValid) {
        when(walletAppKit.params()).thenReturn(RegTestParams.get());
        assertThat(bitcoinAddressValidator.isValid(key, null)).isEqualTo(isValid);
    }

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of("", false),
                Arguments.of("2137", false),
                Arguments.of("2N61hyQz11Y8kJ3tjh42w1QAAgmJFdanYEv", true)
        );
    }
}
