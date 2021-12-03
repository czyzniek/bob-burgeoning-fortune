package pl.czyzniek.bbf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class CryptoFileReader {
    private static final Pattern CRYPTO_PATTERN = Pattern.compile("^\\.*(\\w+)\\s*=\\s*(\\d+)\\.*$");
    private static final FileProvider DEFAULT_FILE_PROVIDER = new DefaultFileProvider();
    private static final List<FileProvider> SPECIFIC_FILE_PROVIDERS = Collections.singletonList(
        new ClasspathFileProvider()
    );

    List<Crypto> readCryptoFile(String cryptoFilePath) {
        try {
            final FileProvider foundFileProvider = SPECIFIC_FILE_PROVIDERS.stream()
                .filter(fileProvider -> fileProvider.canBeApplied(cryptoFilePath))
                .findFirst()
                .orElse(DEFAULT_FILE_PROVIDER);
            try (final InputStream is = foundFileProvider.provideFileStream(cryptoFilePath);
                 final BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines()
                    .map(CRYPTO_PATTERN::matcher)
                    .filter(Matcher::matches)
                    .map(this::parseCrypto)
                    .collect(Collectors.toList());
            }
        } catch (IOException e) {
            System.err.printf("Could not read crypto file from path: %s%n", cryptoFilePath);
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private Crypto parseCrypto(Matcher line) {
        try {
            return new Crypto(
                line.group(1),
                Integer.parseInt(line.group(2)));
        } catch (NumberFormatException ex) {
            System.err.printf("Cannot parse crypto amount: %s. Returning default 0%n", line.group(2));
            ex.printStackTrace();
            return new Crypto(line.group(1), 0);
        }
    }

    private interface FileProvider {
        boolean canBeApplied(String filePath);

        InputStream provideFileStream(String filePath);
    }

    private static class ClasspathFileProvider implements FileProvider {
        private static final String CLASSPATH_PREFIX = "classpath://";

        @Override
        public boolean canBeApplied(String filePath) {
            return filePath.startsWith(CLASSPATH_PREFIX);
        }

        @Override
        public InputStream provideFileStream(String filePath) {
            final String formattedFilePath = filePath.replace(CLASSPATH_PREFIX, "");
            final InputStream fileResource = ClassLoader.getSystemResourceAsStream(formattedFilePath);
            if (fileResource == null) {
                System.err.printf("Could not find file under: %s", filePath);
                throw new IllegalArgumentException(String.format("Could not find file under: %s", filePath));
            }
            return fileResource;
        }
    }

    private static class DefaultFileProvider implements FileProvider {

        @Override
        public boolean canBeApplied(String filePath) {
            return true;
        }

        @Override
        public InputStream provideFileStream(String filePath) {
            try {
                return new FileInputStream(Paths.get(filePath).toFile());
            } catch (FileNotFoundException e) {
                System.err.printf("Could not find file under: %s%n", filePath);
                e.printStackTrace();
                throw new IllegalArgumentException(String.format("Could not find file under: %s", filePath));
            }
        }
    }

    static class Crypto {
        private final String code;
        private final int amount;

        Crypto(String code, int amount) {
            this.code = code;
            this.amount = amount;
        }

        String getCode() {
            return code;
        }

        int getAmount() {
            return amount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Crypto crypto = (Crypto) o;
            return amount == crypto.amount && Objects.equals(code, crypto.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, amount);
        }
    }
}
