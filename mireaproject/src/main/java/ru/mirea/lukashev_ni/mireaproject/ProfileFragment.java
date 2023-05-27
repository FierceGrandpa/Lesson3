package ru.mirea.lukashev_ni.mireaproject;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final int CAMERA_CAPTURE_REQUEST_CODE = 102;
    private static final int PICK_IMAGE_REQUEST_CODE = 103;

    private EditText usernameEditText;
    private EditText emailEditText;
    private Spinner genderSpinner;
    private Button saveButton;
    private Button chooseImageButton;
    private ImageView avatarImageView;

    private SharedPreferences sharedPreferences;
    private Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        saveButton = view.findViewById(R.id.saveButton);
        chooseImageButton = view.findViewById(R.id.chooseImageButton);
        avatarImageView = view.findViewById(R.id.avatarImageView);

        sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        saveButton.setOnClickListener(v -> saveProfile());
        chooseImageButton.setOnClickListener(v -> showImageSourceDialog());

        loadProfile();

        return view;
    }

    private void showImageSourceDialog() {
        String[] options = {"Камера", "Галерея"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Выбрать источник изображения");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkCameraPermission();
            } else if (which == 1) {
                openImagePicker();
            }
        });
        builder.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CAPTURE_REQUEST_CODE);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            avatarImageView.setImageBitmap(bitmap);
        } else if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                avatarImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveProfile() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("gender", gender);
        if (selectedImageUri != null) {
            editor.putString("avatarUri", selectedImageUri.toString());
        } else {
            editor.remove("avatarUri");
        }
        editor.apply();
    }

    private void loadProfile() {
        String username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        String gender = sharedPreferences.getString("gender", "");
        String avatarUriString = sharedPreferences.getString("avatarUri", null);

        usernameEditText.setText(username);
        emailEditText.setText(email);

        if (gender != null) {
            int genderIndex = getGenderIndex(gender);
            genderSpinner.setSelection(genderIndex);
        }

        if (avatarUriString != null) {
            selectedImageUri = Uri.parse(avatarUriString);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                avatarImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getGenderIndex(String gender) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.gender_options,
                android.R.layout.simple_spinner_item
        );

        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(gender)) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }
}


