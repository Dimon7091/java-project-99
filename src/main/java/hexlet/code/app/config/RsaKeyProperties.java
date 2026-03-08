package hexlet.code.app.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@ConfigurationProperties(prefix = "rsa")

@Getter
@Setter
public class RsaKeyProperties {
    private String privateKey;  // может быть PEM строкой или путем к файлу
    private String publicKey;   // может быть PEM строкой или путем к файлу

    private RSAPrivateKey rsaPrivateKey;
    private RSAPublicKey rsaPublicKey;

    @PostConstruct
    public void init() throws Exception {
        // Загружаем ключи, поддерживая разные источники
        String privateKeyContent = loadKeyContent(privateKey);
        String publicKeyContent = loadKeyContent(publicKey);

        this.rsaPrivateKey = loadPrivateKey(privateKeyContent);
        this.rsaPublicKey = loadPublicKey(publicKeyContent);
    }

    /**
     * Загружает содержимое ключа из разных источников:
     * - Если строка начинается с "file:" - читает из файла
     * - Если строка начинается с "classpath:" - читает из ресурсов
     * - Иначе считает, что это сам ключ в PEM формате
     */
    private String loadKeyContent(String source) throws IOException {
        if (source == null) {
            throw new IllegalArgumentException("Key source cannot be null");
        }

        // Из файла
        if (source.startsWith("file:")) {
            String path = source.substring(5);
            return Files.readString(Path.of(path));
        }

        // Из classpath (ресурсов)
        if (source.startsWith("classpath:")) {
            String resourcePath = source.substring(10);
            try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    throw new IOException("Resource not found: " + resourcePath);
                }
                return new String(is.readAllBytes());
            }
        }

        // Пробуем как прямой путь к файлу (для обратной совместимости)
        try {
            Path path = Path.of(source);
            if (Files.exists(path)) {
                return Files.readString(path);
            }
        } catch (Exception ignored) {
            // Игнорируем, пробуем как PEM
        }

        // Считаем, что это сам PEM ключ
        return source;
    }

    private RSAPrivateKey loadPrivateKey(String key) throws Exception {
        // Очищаем от PEM заголовков и пробелов
        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Пробуем PKCS8 формат
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            // Пробуем PKCS1 формат (для старых ключей)
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        }
    }

    private RSAPublicKey loadPublicKey(String key) throws Exception {
        String publicKeyPEM = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replace("-----END RSA PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public RSAPrivateKey getPrivateKey() {
        return rsaPrivateKey;
    }

    public RSAPublicKey getPublicKey() {
        return rsaPublicKey;
    }
}
