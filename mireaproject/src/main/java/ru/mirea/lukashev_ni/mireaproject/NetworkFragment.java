package ru.mirea.lukashev_ni.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkFragment extends Fragment {
    private RecyclerView recyclerView;
    private CurrencyAdapter currencyAdapter;
    private List<Currency> currencyList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_network, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        currencyList = new ArrayList<>();
        currencyAdapter = new CurrencyAdapter(currencyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(currencyAdapter);
        fetchDataFromNetwork();
        return rootView;
    }

    private void fetchDataFromNetwork() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.apilayer.com/exchangerates_data/latest?base=RUB")
                .addHeader("apikey", "Br1wjvovX34LbgnqxktOUfyWTJMabU5a")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();

                getActivity().runOnUiThread(() -> parseResponseData(responseData));
            }
        });
    }

    private void parseResponseData(String responseData) {
        try {
            JSONObject json = new JSONObject(responseData);
            String baseCurrency = json.getString("base");
            String date = json.getString("date");
            JSONObject rates = json.getJSONObject("rates");

            Iterator<String> keys = rates.keys();
            while (keys.hasNext()) {
                String currencyCode = keys.next();
                double exchangeRate = rates.getDouble(currencyCode);

                Currency currency = new Currency(currencyCode, exchangeRate);
                currencyList.add(currency);
            }

            currencyAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

