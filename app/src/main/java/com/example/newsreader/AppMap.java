package com.example.newsreader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AppMap extends AppCompatActivity {

    private static final String ACTIVITY_NAME = "AppMap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_map);

        // Set the theme to the NewsReader theme for this activity
        setTheme(R.style.Theme_NewsReader);

        Button newsButton = findViewById(R.id.newsButton);
        Button favButton = findViewById(R.id.favButton);

        // Intent to open the NewsRoom activity
        Intent newsPageIntent = new Intent(this, NewsRoom.class);
        newsButton.setOnClickListener(v -> startActivity(newsPageIntent));

        // Intent to open the Favourites activity
        Intent favPageIntent = new Intent(this, Favourites.class);
        favButton.setOnClickListener(v -> startActivity(favPageIntent));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(ACTIVITY_NAME, "In onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(ACTIVITY_NAME, "In onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(ACTIVITY_NAME, "In onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(ACTIVITY_NAME, "In onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(ACTIVITY_NAME, "In onDestroy()");
    }
}
