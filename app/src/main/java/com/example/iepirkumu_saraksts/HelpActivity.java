package com.example.iepirkumu_saraksts;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Back button functionality
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        LinearLayout container = findViewById(R.id.helpItems);
        String[] icons = getResources().getStringArray(R.array.help_icons);
        String[] texts = getResources().getStringArray(R.array.help_texts);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < texts.length; i++) {
            View row = inflater.inflate(R.layout.item_help, container, false);
            ((TextView) row.findViewById(R.id.helpIcon)).setText(icons[i]);
            ((TextView) row.findViewById(R.id.helpText)).setText(texts[i]);
            container.addView(row);
        }

        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ((TextView) findViewById(R.id.helpVersion)).setText(getString(R.string.help_version, version));
        } catch (PackageManager.NameNotFoundException ignored) {
        }
    }
}
