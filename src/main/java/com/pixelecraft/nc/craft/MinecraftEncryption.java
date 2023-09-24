package com.pixelecraft.nc.craft;

import io.github.csl.logging.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;

public class MinecraftEncryption {
    private static final Logger log = Logger.getLogger(MinecraftEncryption.class);

    public static KeyPair getKeyPair() {
        try {
            KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
            var0.initialize(1024);
            return var0.generateKeyPair();
        } catch (NoSuchAlgorithmException var1) {
            var1.printStackTrace();
            log.log(Level.SEVERE,"Key pair generation failed!");
            return null;
        }
    }

    public static byte[] encrypt(String var0, PublicKey var1, SecretKey var2) {
        try {
            return subEncrypt("SHA-1", var0.getBytes("ISO_8859_1"), var2.getEncoded(), var1.getEncoded());
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    private static byte[] subEncrypt(String var0, byte[]... var1) {
        try {
            MessageDigest var2 = MessageDigest.getInstance(var0);
            byte[][] var3 = var1;
            int var4 = var1.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                byte[] var6 = var3[var5];
                var2.update(var6);
            }

            return var2.digest();
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static PublicKey getPublicKey(byte[] var0) {
        try {
            X509EncodedKeySpec var1 = new X509EncodedKeySpec(var0);
            KeyFactory var2 = KeyFactory.getInstance("RSA");
            return var2.generatePublic(var1);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ignored) {
        }
        log.log(Level.SEVERE,"Public key reconstitute failed!");
        return null;
    }

    public static SecretKey getSecretKey(PrivateKey var0, byte[] var1) {
        return new SecretKeySpec(encrypt(var0, var1), "AES");
    }

    public static byte[] encrypt(Key var0, byte[] var1) {
        return subEncrypt(2, (Key)var0, (byte[])var1);
    }

    private static byte[] subEncrypt(int var0, Key var1, byte[] var2) {
        try {
            return subEncrypt(var0, var1.getAlgorithm(), var1).doFinal(var2);
        } catch (IllegalBlockSizeException var4) {
            var4.printStackTrace();
        } catch (BadPaddingException var5) {
            var5.printStackTrace();
        }

        log.log(Level.SEVERE,"Cipher data failed!");
        return null;
    }

    private static Cipher subEncrypt(int var0, String var1, Key var2) {
        try {
            Cipher var3 = Cipher.getInstance(var1);
            var3.init(var0, var2);
            return var3;
        } catch (InvalidKeyException var4) {
            var4.printStackTrace();
        } catch (NoSuchAlgorithmException var5) {
            var5.printStackTrace();
        } catch (NoSuchPaddingException var6) {
            var6.printStackTrace();
        }

        log.log(Level.SEVERE,"Cipher creation failed!");
        return null;
    }

    public static Cipher getEncoded(int var0, Key var1) {
        try {
            Cipher var2 = Cipher.getInstance("AES/CFB8/NoPadding");
            var2.init(var0, var1, new IvParameterSpec(var1.getEncoded()));
            return var2;
        } catch (GeneralSecurityException var3) {
            throw new RuntimeException(var3);
        }
    }
}
