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

    private static final int SELECT_IMAGE_CODE =1;
    public static  int decryptCode = 0;


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

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button encrypt_btn = findViewById(R.id.encrypt_btn);

        Button decrypt_button = findViewById(R.id.decrypt_button);

        encrypt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decryptCode = 0;

              requestStoragePermission();
               // userPrompt();


            }
        });

        decrypt_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                decryptCode = 1;
                requestStoragePermission();

            }
        });

    }

    private void selectFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,SELECT_IMAGE_CODE);

    }

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SELECT_IMAGE_CODE) {
            try
            {
                ModifyData.changeFileData(data,this);
            } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException |
                    NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e)
            {
                e.printStackTrace();
            }
        }




    }


}


