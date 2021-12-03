package pl.czyzniek.bbf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CryptoComparatorTest {

    private final CryptoComparator.CryptoCompareValueProvider testCryptoCompareValueProvider = (cryptoCode, currency) -> {
        if ("USD".equals(currency)) {
            return Optional.of(BigDecimal.ZERO);
        }
        if ("EUR".equals(currency)) {
            return Optional.of(new BigDecimal("1000"));
        }
        return Optional.empty();
    };
    private final CryptoComparator sut = new CryptoComparator(testCryptoCompareValueProvider);

    @ParameterizedTest
    @MethodSource("compareCryptos")
    void shouldCompareCryptos(String cryptoCode, String compareCurrency, BigDecimal expectedValue) {
        //when
        final BigDecimal compareValue = sut.compareCrypto(cryptoCode, compareCurrency);

        //then
        assertEquals(expectedValue, compareValue);
    }

    static Stream<Arguments> compareCryptos() {
        return Stream.of(
            arguments("BTC", "EUR", new BigDecimal("1000")),
            arguments("BTC", "PLN", BigDecimal.ZERO),
            arguments("BTC", "USD", BigDecimal.ZERO)
        );
    }

}