package com.example.digitalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.digitalapp.historyRecyclerView.HistoryAdapter;
import com.example.digitalapp.historyRecyclerView.HistoryObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(HistoryActivity.this);
        recyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new HistoryAdapter(getDataSetHistory(), HistoryActivity.this);
        recyclerView.setAdapter(mHistoryAdapter);

        for (int i = 0; i < 100; i++){
            HistoryObject obj = new HistoryObject(Integer.toString(i));
            resultsHistory.add(obj);
        }
        mHistoryAdapter.notifyDataSetChanged();
    }

    private ArrayList resultsHistory = new ArrayList<HistoryObject>();

    private List<HistoryObject> getDataSetHistory() {
        return resultsHistory;
    }
}
