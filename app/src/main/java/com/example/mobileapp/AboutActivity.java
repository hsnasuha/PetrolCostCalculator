package com.example.mobileapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {

    TextView tvGithub;
    BottomNavigationView bottomNavigationAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Allow app to draw behind system bar, then we add our own green background
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_about);

        fixSystemBarColor();

        View statusBarBackground = findViewById(R.id.statusBarBackgroundAbout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.aboutMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            statusBarBackground.getLayoutParams().height = systemBars.top;
            statusBarBackground.requestLayout();

            v.setPadding(
                    systemBars.left,
                    0,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbarAbout);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("About");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        tvGithub = findViewById(R.id.tvGithub);

        tvGithub.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/YOURUSERNAME/YOURREPO")
            );

            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Unable to open GitHub link", Toast.LENGTH_SHORT).show();
            }
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationAbout = findViewById(R.id.bottomNavigationAbout);
        bottomNavigationAbout.setSelectedItemId(R.id.bottom_about);

        bottomNavigationAbout.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                Intent intent = new Intent(AboutActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.bottom_about) {
                return true;
            }

            return false;
        });
    }

    private void fixSystemBarColor() {
        Window window = getWindow();

        // Status bar transparent because fake View gives the green background
        window.setStatusBarColor(Color.TRANSPARENT);

        // Bottom system navigation bar color
        window.setNavigationBarColor(Color.parseColor("#0F3D3E"));

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(window, window.getDecorView());

        // false = white icons, suitable for dark green background
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);
    }
}