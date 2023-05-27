package ru.mirea.lukashev_ni.mireaproject;

import android.os.AsyncTask;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudSpeechRecognitionService {
    private static final MediaType AUDIO_MEDIA_TYPE = MediaType.parse("audio/3gpp");
    private static final String API_URL = "https://api.example.com/speech-to-text";

    public static void recognizeSpeech(String filePath, CloudSpeechRecognitionListener listener) {
        new SpeechRecognitionTask(filePath, listener).execute();
    }

    private static class SpeechRecognitionTask extends AsyncTask<Void, Void, String> {
        private final String filePath;
        private final CloudSpeechRecognitionListener listener;

        public SpeechRecognitionTask(String filePath, CloudSpeechRecognitionListener listener) {
            this.filePath = filePath;
            this.listener = listener;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(AUDIO_MEDIA_TYPE, new java.io.File(filePath));
            Request request = new Request.Builder().url(API_URL).post(body).build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && listener != null) {
                listener.onSpeechRecognized(result);
            }
        }
    }
}
