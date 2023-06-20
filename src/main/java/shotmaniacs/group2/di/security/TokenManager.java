package shotmaniacs.group2.di.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import shotmaniacs.group2.di.model.AccountType;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;

import static java.lang.String.valueOf;

public class TokenManager {
    private static SecretKey tokenKey;

    static {
        try {
            tokenKey = loadKeyFromFile("\\security\\tokenKey.key");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Private constructor to prevent instantiation
    private TokenManager() {
    }

    private static SecretKey loadKeyFromFile(String filePath) throws IOException {
        String realPath = new File(System.getProperty("user.dir")).getParent() + "\\webapps\\shotmaniacs2" + filePath;
        byte[] keyBytes = Files.readAllBytes(Paths.get(realPath));
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public static Claims decodeTokens(String token) {
        try {
            // Create a JwtParser instance with the desired settings
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(tokenKey).build();

            // Parse and decode the token
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateToken(String accountEmail, int accountID, AccountType accountType, Timestamp expiration) {
        // Set the token expiration time
        Date expirationDate = new Date(expiration.getTime());
        String token = Jwts.builder()
                .setSubject(accountEmail)
                .claim("account_type", valueOf(accountType))
                .claim("account_id", accountID)
                .setExpiration(expirationDate)
                .signWith(tokenKey)
                .compact();

        return token;
    }
}

