package com.example.mysosapplication;
import java.security.MessageDigest;
public class Sha256 {
    public static byte[] encryptSHA(byte[] data, String shaN) throws Exception {

        MessageDigest sha = MessageDigest.getInstance(shaN);
        sha.update(data);
        return sha.digest();

    }
}

