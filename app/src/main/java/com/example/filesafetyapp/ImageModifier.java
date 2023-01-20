package com.example.filesafetyapp;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ImageModifier {
    private static final String KEY_AES = "1234567812345678"; // aes key
    private static final String KEY_BLOWFISH = "8765432112345678"; // blowfish key

    public static byte[] modifyDataForEncryption(byte[] fileData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        // SecretKey secretKey = generateSecretKey();
        SecretKeySpec secretKey = new SecretKeySpec(KEY_AES.getBytes(), "AES");


        Cipher cipherAES = Cipher.getInstance("AES/ECB/PKCS5Padding"); // TRANSFORMATION = "algo/blocklevel/padding"
        cipherAES.init(Cipher.ENCRYPT_MODE,secretKey);


        byte[] encryptedDataAES = cipherAES.doFinal(fileData);





        SecretKeySpec blowfishKey = new SecretKeySpec(KEY_BLOWFISH.getBytes(), "Blowfish");
        // Initialize the Blowfish cipher
        Cipher blowfishCipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        blowfishCipher.init(Cipher.ENCRYPT_MODE, blowfishKey);

      // Encrypt the AES ciphertext with Blowfish
        byte[] blowfishCipherEncryptedData = blowfishCipher.doFinal(encryptedDataAES); // this is the encrypted data retrived from AES encryption



        return blowfishCipherEncryptedData;
    }

    public static byte[] modifyDataForDecryption(byte[] fileData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        SecretKeySpec blowfishKey = new SecretKeySpec(KEY_BLOWFISH.getBytes(), "Blowfish");
        Cipher blowfishCipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        blowfishCipher.init(Cipher.DECRYPT_MODE, blowfishKey);

        // Decrypt the Blowfish ciphertext
        byte[] blowfishCiphertext = blowfishCipher.doFinal(fileData);

        // Initialize the AES cipher in decrypt mode
        SecretKeySpec secretKey = new SecretKeySpec(KEY_AES.getBytes(), "AES");
        Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        aesCipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Decrypt the AES ciphertext
        byte[] decryptedData = aesCipher.doFinal(blowfishCiphertext);

        return decryptedData;

    }


}
