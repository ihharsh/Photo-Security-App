package com.example.filesafetyapp;

import static com.example.filesafetyapp.MainActivity.decryptCode;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ModifyData {

    public static void changeFileData(Intent data, Context context) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Get the selected files URI
        Uri fileUri = data.getData();

        // Grant temporary access to the file // This is only applicable to system apps
        // getContentResolver().grantUriPermission(getPackageName(), fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // convert file into byte array
        byte[] fileData = readFile(fileUri,context);




        // Modify the file data
        //byte[] modifiedData = modifyData(fileData);

        byte[] modifiedData;

        if(decryptCode== 1){
            modifiedData = ImageModifier.modifyDataForDecryption(fileData); // encrypted file's modified data is decrypted data
        } else{
            modifiedData = ImageModifier.modifyDataForEncryption(fileData);
        }



        // Write the modified data to the file
        // writeFile(fileUri, modifiedData); // This is only applicable to system apps
        if (decryptCode == 1) {
            saveFiletoGallery(modifiedData,context);
        } else {
            saveFileEncrypted(modifiedData,context);
        }


        //getContentResolver().revokeUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    private static void saveFiletoGallery(byte[] data, Context context) throws IOException {
        File appGallery = new File(Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_PICTURES),"Photo Crypto");

            if(!appGallery.exists()){
                appGallery.mkdirs();
            }

            File file = new File(appGallery,"decrypted_image.jpeg");
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();

            MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),"mediastore",null);

            Toast.makeText(context, "Image saved to PhotoCrypto Folder", Toast.LENGTH_SHORT).show();




    }

    // saves the file to the external file directory
    private static void saveFileEncrypted(byte[] data,Context context) throws IOException {

        File externalFilesDir = context.getExternalFilesDir(null);
        File file = new File(externalFilesDir, "encrypted.txt");



        // Save the encrypted data to a new file
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(data);

        outputStream.close();
        Toast.makeText(context, "Encrypted file created", Toast.LENGTH_SHORT).show();

    }

    private static byte[] readFile(Uri fileUri, Context context) throws IOException {

        InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
