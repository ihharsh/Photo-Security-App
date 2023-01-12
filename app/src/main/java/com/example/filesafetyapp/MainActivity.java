package com.example.filesafetyapp;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST_CODE = 1;
    int SELECT_IMAGE_CODE =1;
    EditText editText;
    private static  int decryptCode = 0;
    private static final String KEY = "1234567812345678";



    ActivityResultLauncher<String> accessFileLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            selectFile();

                        } else {
                            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                        }
                    });

    private void requestStoragePermission() {
        accessFileLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        Button btn2 = findViewById(R.id.button2);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decryptCode = 0;
              requestStoragePermission();
               // userPrompt();


            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                decryptCode = 1;
                decrypt();

            }
        });




    }

    private void decrypt() {
        selectFile();

    }

    private void selectFile() {


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,PICK_FILE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_FILE_REQUEST_CODE) {
            try {
                changeFileData(data);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }




    }

    // get uri then read bytes from that uri, modifydata, save file
    private void changeFileData(Intent data) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Get the selected file's URI
        Uri fileUri = data.getData();

        // Grant temporary access to the file
       // getContentResolver().grantUriPermission(getPackageName(), fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Read the file into a byte array
        byte[] fileData = readFile(fileUri);




        // Modify the file data
        //byte[] modifiedData = modifyData(fileData);

        byte[] modifiedData;

        if(decryptCode== 1){
             modifiedData = ImageModifier.modifyDataForDecryption(fileData);
        } else{
             modifiedData = ImageModifier.modifyDataForEncryption(fileData);
        }



        // Write the modified data to the file
        //writeFile(fileUri, modifiedData);
        if (decryptCode == 1) {
            saveFiletoGallery(modifiedData);
        } else {
            saveFiletxt(modifiedData);
        }


        //getContentResolver().revokeUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    private void changeFileData_backup(Intent data) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Get the selected file's URI
        Uri fileUri = data.getData();

        // Read the file into a byte array
        byte[] fileData = readFile(fileUri);

        // Modify the file data
        byte[] modifiedData = modifyData(fileData);


        if (decryptCode == 1) {
            saveFiletoGallery(modifiedData);
        } else {
            saveFiletxt(modifiedData);
        }

    }

    private void saveFiletoGallery(byte[] data) throws IOException {
        if(decryptCode==1){
            File appGallery =
                    new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES),"MyAppGallery");

            if(!appGallery.exists()){
                appGallery.mkdirs();
            }

            File file = new File(appGallery,"decrypted_image.jpeg");
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();

            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),"decrypted_file",null);

            Toast.makeText(this, "Image saved to MyAppGallery", Toast.LENGTH_SHORT).show();

        }


    }

    // saves the file to the external file directory
    private void saveFiletxt(byte[] data) throws IOException {

        File externalFilesDir = getExternalFilesDir(null);
        File file = new File(externalFilesDir, "encrypted.pdf");



        // Save the encrypted data to a new file
        OutputStream outputStream = new FileOutputStream(file);
        outputStream.write(data);

        outputStream.close();
        Toast.makeText(this, "Encrypted file created", Toast.LENGTH_SHORT).show();

    }

    //return the encrypted bytes after the file is read into byte[]

    private byte[] modifyData(byte[] fileData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        // SecretKey secretKey = generateSecretKey();
        SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        if(decryptCode==1){
            cipher.init(Cipher.DECRYPT_MODE,secretKey );
        } else {
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        }


        byte[] encryptedData = cipher.doFinal(fileData);

        return encryptedData;
    }


    // returns the byte array of the file choosen
    private byte[] readFile(Uri fileUri) throws IOException {
        // Read the file into a byte array
        InputStream inputStream = getContentResolver().openInputStream(fileUri);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }

}


