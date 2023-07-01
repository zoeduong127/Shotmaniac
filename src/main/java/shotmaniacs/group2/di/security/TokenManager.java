package shotmaniacs.group2.di.security;

import io.jsonwebtoken.*;
import shotmaniacs.group2.di.model.AccountType;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import static java.lang.String.valueOf;

public class TokenManager {
    private static SecretKey tokenKey;

    static {
        try {
            tokenKey = loadKeyFromFile(new File(System.getProperty("user.dir")).getParent() + "\\webapps\\shotmaniacs2\\security\\tokenKey.key");
            System.out.println(new File(System.getProperty("user.dir")).getParent() + "\\webapps\\shotmaniacs2\\security\\tokenKey.key");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            tokenKey = loadKeyFromFile(new File(System.getProperty("user.dir")) + "\\src\\main\\webapp\\security\\tokenKey.key");
        } catch (IOException ignored) {
        }
    }

    // Private constructor to prevent instantiation
    private TokenManager() {
    }

    private static SecretKey loadKeyFromFile(String filePath) throws IOException {
        String realPath =  filePath;
        byte[] keyBytes = Files.readAllBytes(Paths.get(realPath));
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public static Claims decodeTokens(String token) {
        try {
            // Create a JwtParser instance with the desired settings
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(tokenKey).build();

            return jwtParser.parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            System.out.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    public static String generateToken(String accountEmail, int accountID, AccountType accountType, Timestamp expiration) {
        // Set the token expiration time
        Date expirationDate = new Date(expiration.getTime());
        String token = Jwts.builder()
                .setSubject(accountEmail)
                .claim("role", valueOf(accountType))
                .claim("account_id", accountID)
                .setExpiration(expirationDate)
                .signWith(tokenKey)
                .compact();

        return token;
    }
}

