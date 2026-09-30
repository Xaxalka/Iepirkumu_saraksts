package com.example.iepirkumu_saraksts;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Back button functionality
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
