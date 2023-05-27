package ru.mirea.lukashev_ni.mireaproject;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MicrophoneFragment extends Fragment {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 123;
    private MediaRecorder recorder;
    private Button recordButton;
    private Button stopButton;
    private String outputFile;

    public MicrophoneFragment() {
    }

    public static MicrophoneFragment newInstance() {
        return new MicrophoneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recordButton = view.findViewById(R.id.recordButton);
        stopButton = view.findViewById(R.id.stopButton);
        stopButton.setVisibility(View.GONE);

        recordButton.setOnClickListener(v -> {
            if (checkRecordAudioPermission()) {
                startRecording();
                recordButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
            } else {
                requestRecordAudioPermission();
            }
        });

        stopButton.setOnClickListener(v -> {
            stopRecording();
            processAudio(outputFile);
            stopButton.setVisibility(View.GONE);
            recordButton.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_microphone, container, false);
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        outputFile = requireContext().getExternalCacheDir().getAbsolutePath() + "/recording.3gp";
        recorder.setOutputFile(outputFile);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        }
    }

    private void processAudio(String filePath) {
        CloudSpeechRecognitionService.recognizeSpeech(filePath, result -> displaySpeechResult(result));
    }

    private void displaySpeechResult(String result) {
        TextView outputTextView = getView().findViewById(R.id.outputTextView);
        outputTextView.setText("Распознанный текст: " + result);
    }

    private boolean checkRecordAudioPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{ android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
                recordButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.VISIBLE);
            }
        }
    }

}