package ru.mirea.lukashev_ni.mireaproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyWorkerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyWorkerFragment extends Fragment {
    EditText numberInput;
    Button calculateButton;

    public MyWorkerFragment() {}

    public static MyWorkerFragment newInstance() {
        return new MyWorkerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_worker, container, false);

        numberInput = view.findViewById(R.id.numberInput);
        calculateButton = view.findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(v -> calculateFactorial());

        return view;
    }

    private void calculateFactorial() {
        int number = Integer.parseInt(numberInput.getText().toString());

        Data inputData = new Data.Builder()
                .putInt("number", number)
                .build();

        WorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(workRequest.getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        String result = workInfo.getOutputData().getString("result");
                        Toast.makeText(requireContext(), "Результат: " + result, Toast.LENGTH_LONG).show();
                    }
                });

        WorkManager.getInstance(requireContext()).enqueue(workRequest);
    }
}