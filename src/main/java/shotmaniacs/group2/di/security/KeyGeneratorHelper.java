package shotmaniacs.group2.di.security;

import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.IOException;

public class KeyGeneratorHelper {

    public static void main(String[] args) {
        try {
            // Generate a symmetric key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();

            // Store the symmetric key to a file
            storeKeyToFile(secretKey, "src/main/java/shotmaniacs/group2/di/security/tokenKey.key");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void storeKeyToFile(SecretKey secretKey, String filePath) throws IOException {
        byte[] encodedKey = secretKey.getEncoded();
        FileOutputStream outputStream = new FileOutputStream(filePath);
        outputStream.write(encodedKey);
        outputStream.close();
    }
}

