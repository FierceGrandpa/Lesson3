package ru.mirea.lukashev_ni.intentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        String time = intent.getStringExtra("time");

        String message = "Лукашев Никита Иванович, номер в списке группы: 15, квадрат: " + 15 * 15 + ", текущее время " + time;

        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
    }
}