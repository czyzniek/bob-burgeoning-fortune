package pl.czyzniek.bbf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BobBurgeoningFortuneAppIntegrationTest {

    private final BobBurgeoningFortuneApp sut = new BobBurgeoningFortuneApp();

    @Test
    void shouldCompareBobsCrypto() {
        //given
        final String cryptoFileName = "classpath://test_bobs_crypto.txt";
        final String compareCurrency = "USD";

        //when+then
        Assertions.assertDoesNotThrow(() -> sut.main(new String[] {cryptoFileName, compareCurrency}));
    }

    @Test
    void shouldCompareBobsCryptoWithDefaultCurrency() {
        //given
        final String cryptoFileName = "classpath://test_bobs_crypto.txt";

        //when+then
        Assertions.assertDoesNotThrow(() -> sut.main(new String[] {cryptoFileName}));
    }
}