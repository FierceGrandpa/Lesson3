package ru.mirea.lukashev_ni.mireaproject;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private List<Currency> currencyList;

    public CurrencyAdapter(List<Currency> currencyList) {
        this.currencyList = currencyList;
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false);
        return new CurrencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        Currency currency = currencyList.get(position);
        holder.currencyCodeTextView.setText(currency.getCode());
        holder.exchangeRateTextView.setText(String.valueOf(currency.getExchangeRate()));
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    static class CurrencyViewHolder extends RecyclerView.ViewHolder {

        TextView currencyCodeTextView;
        TextView exchangeRateTextView;

        CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);
            currencyCodeTextView = itemView.findViewById(R.id.currencyCodeTextView);
            exchangeRateTextView = itemView.findViewById(R.id.exchangeRateTextView);
        }
    }
}
