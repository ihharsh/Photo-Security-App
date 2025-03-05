package com.example.filesafetyapp;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.filesafetyapp.databinding.ActivityEncryptionTypeBinding;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Inflater;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class encryptionType extends AppCompatActivity {

    private ActivityEncryptionTypeBinding binding;

    private int SELECT_IMAGE_CODE = 1;

    public static boolean isEncryptClicked = true;

    public static boolean aes = false;
    public static boolean blowfish = false;
    public static boolean both = false;


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
        binding = ActivityEncryptionTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.encryptBtnAes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aes = true;
                blowfish = false;
                both = false;
                isEncryptClicked = true;
                requestStoragePermission();


            }
        });

        binding.encryptBtnBlowfish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aes = false;
                blowfish = true;
                both = false;
                isEncryptClicked = true;
                requestStoragePermission();
            }
        });

        binding.encryptBtnAesBlowfish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aes = false;
                blowfish = false;
                both = true;
                isEncryptClicked = true;
                requestStoragePermission();
            }
        });

    }

    private void selectFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,SELECT_IMAGE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            ModifyData.changeFileData(data,this);
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }


    }
}