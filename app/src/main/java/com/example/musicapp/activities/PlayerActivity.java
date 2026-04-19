package com.example.musicapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicapp.R;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_player);
    }
}