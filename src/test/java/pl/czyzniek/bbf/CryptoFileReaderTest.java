package pl.czyzniek.bbf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CryptoFileReaderTest {

    private final CryptoFileReader sut = new CryptoFileReader();

    @ParameterizedTest
    @MethodSource("cryptFileNames")
    void shouldReadCryptosFromFile(String cryptoFileName) {
        //when
        final List<CryptoFileReader.Crypto> cryptos = sut.readCryptoFile(cryptoFileName);

        //then
        assertIterableEquals(
            cryptos,
            Arrays.asList(new CryptoFileReader.Crypto("BTC", 10), new CryptoFileReader.Crypto("XRP", 2000))
        );
    }

    static Stream<Arguments> cryptFileNames() {
        return Stream.of(
            arguments("classpath://test_bobs_crypto.txt"),
            arguments(ClassLoader.getSystemResource("test_bobs_crypto.txt").getPath()));
    }

    @Test
    void shouldThrowExceptionWhenMalformedPathIsProvided() {
        //given
        final String path = "classpath://VERY_WRONG_PATH";

        //when
        final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> sut.readCryptoFile(path));

        //then
        assertEquals(ex.getMessage(), "Could not find file under: classpath://VERY_WRONG_PATH");
    }
}