package edu.unibague.auth.security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Lee llaves RSA en formato PEM:
 *  - Privada: PKCS#8 ("-----BEGIN PRIVATE KEY-----")
 *  - Publica: X.509   ("-----BEGIN PUBLIC KEY-----")
 */
public final class PemKeyLoader {

    private PemKeyLoader() {
    }

    public static RSAPrivateKey loadPrivateKey(Path path) {
        String pem = read(path);
        if (pem.contains("BEGIN RSA PRIVATE KEY")) {
            throw new IllegalStateException("La llave privada esta en formato PKCS#1. "
                    + "Generela en PKCS#8 con: openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048");
        }
        try {
            byte[] der = decode(pem);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No fue posible leer la llave privada RSA: " + path, e);
        }
    }

    public static RSAPublicKey loadPublicKey(Path path) {
        String pem = read(path);
        try {
            byte[] der = decode(pem);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No fue posible leer la llave publica RSA: " + path, e);
        }
    }

    private static String read(Path path) {
        if (!Files.exists(path)) {
            throw new IllegalStateException("No existe la llave " + path.toAbsolutePath()
                    + ". Con Docker la crea el servicio key-generator; sin Docker ejecute scripts/generate-rsa-keys.sh");
        }
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new IllegalStateException("No fue posible leer " + path, e);
        }
    }

    private static byte[] decode(String pem) {
        String base64 = pem
                .replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(base64);
    }
}
