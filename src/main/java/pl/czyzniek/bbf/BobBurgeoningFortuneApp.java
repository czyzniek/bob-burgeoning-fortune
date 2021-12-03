package pl.czyzniek.bbf;

import java.math.BigDecimal;

public class BobBurgeoningFortuneApp {
    private static final CryptoFileReader cryptoFileReader = new CryptoFileReader();
    private static final CryptoComparator cryptoComparator = CryptoComparator.defaultInstance();

    public static void main(String[] args) {
        final Arguments arguments = new Arguments(args);
        final BigDecimal totalBobsCryptoValue = cryptoFileReader.readCryptoFile(arguments.cryptoFileName).stream()
            .map(crypto -> {
                final BigDecimal compareValue = cryptoComparator.compareCrypto(crypto.getCode(), arguments.compareCurrency);
                return ComparedCrypto.fromCrypto(crypto, compareValue);
            })
            .peek(crypto -> System.out.printf("1 %s -> 1 %s = %f | Total amount: %f %s%n", crypto.getCode(), arguments.compareCurrency, crypto.getCompareValue(),
                crypto.calculateTotalValueInCompareCurrency(), arguments.compareCurrency))
            .map(ComparedCrypto::calculateTotalValueInCompareCurrency)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.printf("Total Bob's crypto value is: %f %s%n", totalBobsCryptoValue, arguments.compareCurrency);
    }

    private static class Arguments {
        private static final String DEFAULT_CRYPTO_FILE_NAME = "classpath://bobs_crypto.txt";
        private static final String DEFAULT_CURRENCY = "EUR";
        private final String cryptoFileName;
        private final String compareCurrency;

        Arguments(String[] args) {
            this.cryptoFileName = getIfExist(args, 0, DEFAULT_CRYPTO_FILE_NAME);
            this.compareCurrency = getIfExist(args, 1, DEFAULT_CURRENCY);
        }

        private static String getIfExist(String[] args, int index, String defaultValue) {
            try {
                return args[index];
            } catch (ArrayIndexOutOfBoundsException e) {
                return defaultValue;
            }
        }
    }

    private static class ComparedCrypto {
        private final String code;
        private final int amount;
        private final BigDecimal compareValue;

        ComparedCrypto(String code, int amount, BigDecimal compareValue) {
            this.code = code;
            this.amount = amount;
            this.compareValue = compareValue;
        }

        static ComparedCrypto fromCrypto(CryptoFileReader.Crypto crypto, BigDecimal compareValue) {
            return new ComparedCrypto(crypto.getCode(), crypto.getAmount(), compareValue);
        }

        BigDecimal getCompareValue() {
            return compareValue;
        }

        String getCode() {
            return code;
        }

        int getAmount() {
            return amount;
        }

        BigDecimal calculateTotalValueInCompareCurrency() {
            return compareValue.multiply(BigDecimal.valueOf(amount));
        }
    }
}
