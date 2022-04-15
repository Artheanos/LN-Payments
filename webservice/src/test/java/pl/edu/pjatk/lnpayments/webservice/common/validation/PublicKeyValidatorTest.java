package pl.edu.pjatk.lnpayments.webservice.common.validation;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PublicKeyValidatorTest {

    private final PublicKeyValidator publicKeyValidator = new PublicKeyValidator();

    @ParameterizedTest
    @MethodSource("testKeys")
    void shouldProperlyValidateKeys(String key, boolean isValid) {
        assertThat(publicKeyValidator.isValid(key, null)).isEqualTo(isValid);
    }

    private static Stream<Arguments> testKeys() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of("", false),
                Arguments.of("2137", false),
                Arguments.of("0346b221a71369a6f70be9660ae560096396cf6813a051fcaf50a418d517007fcb", true)
        );
    }

}
