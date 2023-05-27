package ru.mirea.lukashev_ni.mireaproject;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class FileHandlingFragment extends Fragment {

    private static final int PICK_FILE_REQUEST_CODE = 1;

    private static final String TAG = "FileHandlingFragment";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String AES_KEY = "secretkey";
    private static final String AES_IV = "vector";

    private TextView selectedFileTextView;
    private Button selectFileButton;

    public FileHandlingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_handling, container, false);

        selectedFileTextView = rootView.findViewById(R.id.selectedFileTextView);
        selectFileButton = rootView.findViewById(R.id.selectFileButton);
        selectFileButton.setOnClickListener(v -> openFilePicker());

        return rootView;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                handleSelectedFile(selectedFileUri);
            }
        }
    }

    private void handleSelectedFile(Uri fileUri) {
        String fileName = getFileName(fileUri);
        if (fileName != null) {
            File selectedFile = new File(fileUri.getPath());
            File encryptedFile = encryptFile(selectedFile, fileName);
            if (encryptedFile != null) {
                Toast.makeText(requireContext(), "Файл успешно зашифрован и сохранен", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Ошибка при шифровании и сохранении файла", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Не удалось получить имя файла", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri fileUri) {
        String fileName = null;
        try (Cursor cursor = requireContext().getContentResolver().query(fileUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting file name from Uri", e);
        }
        return fileName;
    }

    private File encryptFile(File inputFile, String fileName) {
        try {
            FileInputStream fis = new FileInputStream(inputFile);

            byte[] salt = AES_IV.getBytes();
            int iterationCount = 65536;
            int keyLength = 256;

            KeySpec keySpec = new PBEKeySpec(AES_KEY.toCharArray(), salt, iterationCount, keyLength);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey secretKey = new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), AES_ALGORITHM);

            byte[] ivBytes = AES_IV.getBytes();
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            File encryptedFile = createEncryptedFile(fileName);
            FileOutputStream fos = new FileOutputStream(encryptedFile);
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                cos.write(buffer, 0, bytesRead);
            }

            cos.close();
            fis.close();

            return encryptedFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private File createEncryptedFile(String originalFileName) {
        String dateTimeString = getDateTimeString();
        String[] fileNameParts = originalFileName.split("\\.");
        String fileExtension = fileNameParts[fileNameParts.length - 1];
        String encryptedFileName = fileNameParts[0] + "_" + dateTimeString + "." + fileExtension;

        File encryptedFile = new File(requireContext().getFilesDir(), encryptedFileName);
        if (!encryptedFile.exists()) {
            try {
                encryptedFile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Error creating encrypted file", e);
            }
        }
        return encryptedFile;
    }

    private String getDateTimeString() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return String.format("%04d%02d%02d_%02d%02d%02d", year, month, day, hour, minute, second);
    }
}
