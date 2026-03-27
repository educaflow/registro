package com.fpmislata.secretariavirtual.registro.config;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Codifica contraseñas con el formato de Shiro 2 + Argon2id, compatible con Axelor:
 * {@code $shiro2$argon2id$v=19$t=1,m=65536,p=4$<salt_base64>$<hash_base64>}
 */
public class ShiroArgon2PasswordEncoder implements PasswordEncoder {

    private static final int SALT_LENGTH  = 16;
    private static final int HASH_LENGTH  = 32;
    private static final int ITERATIONS   = 1;
    private static final int MEMORY_KB    = 65536;
    private static final int PARALLELISM  = 4;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        byte[] hash = argon2id(rawPassword.toString().getBytes(StandardCharsets.UTF_8), salt,
                ITERATIONS, MEMORY_KB, PARALLELISM);
        return buildEncoded(salt, hash, ITERATIONS, MEMORY_KB, PARALLELISM);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || !encodedPassword.startsWith("$shiro2$argon2id$")) {
            return false;
        }
        try {
            // $shiro2$argon2id$v=19$t=1,m=65536,p=4$<salt>$<hash>
            String[] parts = encodedPassword.split("\\$");
            // parts: ["", "shiro2", "argon2id", "v=19", "t=1,m=65536,p=4", "<salt>", "<hash>"]
            String[] costParts = parts[5].split(",");
            int t = Integer.parseInt(costParts[0].substring(2));
            int m = Integer.parseInt(costParts[1].substring(2));
            int p = Integer.parseInt(costParts[2].substring(2));
            byte[] salt     = decodeBase64(parts[6]);
            byte[] expected = decodeBase64(parts[7]);
            byte[] actual   = argon2id(rawPassword.toString().getBytes(StandardCharsets.UTF_8),
                    salt, t, m, p);
            return slowEquals(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] argon2id(byte[] password, byte[] salt, int t, int m, int p) {
        Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(19)
                .withIterations(t)
                .withMemoryAsKB(m)
                .withParallelism(p)
                .withSalt(salt)
                .build();
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);
        byte[] hash = new byte[HASH_LENGTH];
        generator.generateBytes(password, hash);
        return hash;
    }

    private String buildEncoded(byte[] salt, byte[] hash, int t, int m, int p) {
        return "$shiro2$argon2id$v=19$t=" + t + ",m=" + m + ",p=" + p
                + "$" + encodeBase64(salt)
                + "$" + encodeBase64(hash);
    }

    private String encodeBase64(byte[] bytes) {
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

    private byte[] decodeBase64(String s) {
        // añade padding si hace falta
        int mod = s.length() % 4;
        if (mod != 0) s = s + "=".repeat(4 - mod);
        return Base64.getDecoder().decode(s);
    }

    private boolean slowEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }
}
