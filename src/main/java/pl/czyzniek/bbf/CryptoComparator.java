package pl.czyzniek.bbf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CryptoComparator {
    private final CryptoCompareValueProvider cryptoCompareValueProvider;

    CryptoComparator(CryptoCompareValueProvider cryptoCompareValueProvider) {
        this.cryptoCompareValueProvider = cryptoCompareValueProvider;
    }

    static CryptoComparator defaultInstance() {
        return new CryptoComparator(new CryptoCompareApiProvider());
    }

    BigDecimal compareCrypto(String cryptoCode, String currencyToCompare) {
        return Optional.ofNullable(cryptoCode)
            .filter(value -> value.length() != 0)
            .flatMap(value -> cryptoCompareValueProvider.getCompareValue(value, currencyToCompare))
            .orElse(BigDecimal.ZERO);
    }

    interface CryptoCompareValueProvider {
        Optional<BigDecimal> getCompareValue(String cryptoCode, String currency);
    }

    private static class CryptoCompareApiProvider implements CryptoCompareValueProvider {
        private static final String CRYPTO_COMPARE_URL = "https://min-api.cryptocompare.com/data/price?fsym=%s&tsyms=%s";
        private static final Pattern RESPONSE_PATTERN = Pattern.compile("\\{?((\\\"[a-zA-Z]+\\\")\\s*\\:\\s*([\\d\\.]+))\\}?");

        @Override
        public Optional<BigDecimal> getCompareValue(String cryptoCode, String currency) {
            try {
                final URL cryptoCompareUrl = new URL(String.format(CRYPTO_COMPARE_URL, cryptoCode, currency));
                try(final InputStream response = cryptoCompareUrl.openStream();
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8))) {
                    return reader.lines()
                        .flatMap(line -> Arrays.stream(line.split(",")))
                        .map(RESPONSE_PATTERN::matcher)
                        .filter(Matcher::matches)
                        .map(this::mapResponseLine)
                        .filter(crypto -> crypto.currencyCode.equals(currency))
                        .findFirst()
                        .map(CryptoResponse::getValue);
                }
            } catch (IOException e) {
                System.err.println("Could not get compare value from external provider!");
                e.printStackTrace();
            }
            return Optional.of(BigDecimal.ZERO);
        }

        private CryptoResponse mapResponseLine(Matcher matchedLine) {
            final String foundCurrency = matchedLine.group(2).replace("\"", "");
            final String foundCurrencyValue= matchedLine.group(3);
            try {
                return new CryptoResponse(foundCurrency, new BigDecimal(foundCurrencyValue));
            } catch (NumberFormatException e) {
                System.err.println("Could not format compare value from external provider! Returning to default - 0.");
                e.printStackTrace();
                return new CryptoResponse(foundCurrency, BigDecimal.ZERO);
            }
        }
    }

    private static class CryptoResponse {
        private final String currencyCode;
        private final BigDecimal value;

        CryptoResponse(String currencyCode, BigDecimal value) {
            this.currencyCode = currencyCode;
            this.value = value;
        }

        BigDecimal getValue() {
            return value;
        }
    }
}
