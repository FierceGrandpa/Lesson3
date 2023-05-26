package ru.mirea.lukashev_ni.mireaproject;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.math.BigInteger;

public class MyWorker extends Worker {
    static final String TAG = "MyWorker";
    public MyWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: start");

        int number = getInputData().getInt("number", 1);

        BigInteger factorial = calculateFactorial(number);
        Log.d(TAG, "doWork: factorial of " + number + " is " + factorial);

        Data outputData = new Data.Builder()
                .putString("result", factorial.toString())
                .build();

        Log.d(TAG, "doWork: end");
        return Result.success(outputData);
    }

    private BigInteger calculateFactorial(int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 1; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
