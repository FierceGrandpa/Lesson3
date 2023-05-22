package ru.mirea.lukashev_ni.favoritebook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // Получение данных из MainActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String bookName = extras.getString(MainActivity.KEY);
            TextView textViewBook = findViewById(R.id.textViewBook);
            textViewBook.setText(String.format("Моя любимая книга: %s", bookName));
        }
    }

    // Отправка введенных пользователем данных по нажатию на кнопку
    public void sendBookName(View view) {
        EditText editTextBook = findViewById(R.id.editTextBook);
        String bookName = editTextBook.getText().toString();

        Intent data = new Intent();
        data.putExtra(MainActivity.USER_MESSAGE, bookName);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}