package com.example.mobileapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerPetrolType;
    EditText etPrice, etUsage;
    RadioGroup rgBudi;
    Button btnCalculate;
    BottomNavigationView bottomNavigation;

    TextView tvStatusTitle, tvStatusDescription, tvResultNote;
    TextView tvPetrolTypeValue, tvPriceValue, tvUsageValue;
    TextView tvTotalCostValue, tvBudiRebateValue, tvFinalPayableValue;
    TableLayout resultTable;

    final double BUDI_SUBSIDY_RATE = 1.99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_main);

        fixSystemBarColor();

        View statusBarBackground = findViewById(R.id.statusBarBackground);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Petrol Cost Calculator");
        }

        spinnerPetrolType = findViewById(R.id.spinnerPetrolType);
        etPrice = findViewById(R.id.etPrice);
        etUsage = findViewById(R.id.etUsage);
        rgBudi = findViewById(R.id.rgBudi);
        btnCalculate = findViewById(R.id.btnCalculate);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        tvStatusTitle = findViewById(R.id.tvStatusTitle);
        tvStatusDescription = findViewById(R.id.tvStatusDescription);
        tvResultNote = findViewById(R.id.tvResultNote);

        resultTable = findViewById(R.id.resultTable);

        tvPetrolTypeValue = findViewById(R.id.tvPetrolTypeValue);
        tvPriceValue = findViewById(R.id.tvPriceValue);
        tvUsageValue = findViewById(R.id.tvUsageValue);
        tvTotalCostValue = findViewById(R.id.tvTotalCostValue);
        tvBudiRebateValue = findViewById(R.id.tvBudiRebateValue);
        tvFinalPayableValue = findViewById(R.id.tvFinalPayableValue);

        String[] petrolTypes = {
                "-- Choose Petrol Type --",
                "RON95",
                "RON97",
                "Diesel"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                petrolTypes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPetrolType.setAdapter(adapter);

        btnCalculate.setOnClickListener(v -> calculate());

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.bottom_home);

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                return true;
            }

            if (item.getItemId() == R.id.bottom_about) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });
    }

    private void fixSystemBarColor() {
        Window window = getWindow();

        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.parseColor("#0F3D3E"));

        WindowInsetsControllerCompat controller =
                new WindowInsetsControllerCompat(window, window.getDecorView());

        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);
    }

    void calculate() {
        String petrolType = spinnerPetrolType.getSelectedItem().toString();
        String priceStr = etPrice.getText().toString().trim();
        String usageStr = etUsage.getText().toString().trim();
        int selectedId = rgBudi.getCheckedRadioButtonId();

        if (petrolType.equals("-- Choose Petrol Type --")) {
            Toast.makeText(this, "Please choose a petrol type!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (priceStr.isEmpty() || usageStr.isEmpty() || selectedId == -1) {
            Toast.makeText(this, "Please complete all required information!", Toast.LENGTH_SHORT).show();
            return;
        }

        double pricePerLiter;
        double fuelUsage;

        try {
            pricePerLiter = Double.parseDouble(priceStr);
            fuelUsage = Double.parseDouble(usageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pricePerLiter <= 0 || fuelUsage <= 0) {
            Toast.makeText(this, "Values must be greater than 0!", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isBudiEligible = (selectedId == R.id.rbYes);

        double totalPetrolCost = fuelUsage * pricePerLiter;
        double budiRebate = 0;
        double finalPayableAmount = totalPetrolCost;

        String statusTitle;
        String statusDescription;
        String note = "";

        if (isBudiEligible && petrolType.equals("RON95")) {
            statusTitle = "✅ ELIGIBLE";
            statusDescription = "BUDI MADANI rebate has been applied for RON95.";

            budiRebate = fuelUsage * BUDI_SUBSIDY_RATE;
            finalPayableAmount = totalPetrolCost - budiRebate;

            if (finalPayableAmount < 0) {
                finalPayableAmount = 0;
                note = "⚠️ Note: Rebate cannot exceed the total petrol cost.";
            }

        } else if (isBudiEligible && !petrolType.equals("RON95")) {
            statusTitle = "❌ NOT ELIGIBLE";
            statusDescription = "BUDI MADANI rebate is only available for RON95.";

            budiRebate = 0;
            finalPayableAmount = totalPetrolCost;
            note = "Reason: BUDI MADANI is only applicable for RON95.";

        } else {
            statusTitle = "❌ NOT APPLIED";
            statusDescription = "Normal petrol cost is calculated without BUDI MADANI rebate.";

            budiRebate = 0;
            finalPayableAmount = totalPetrolCost;
        }

        tvStatusTitle.setText(statusTitle);
        tvStatusDescription.setText(statusDescription);

        resultTable.setVisibility(View.VISIBLE);

        tvPetrolTypeValue.setText(petrolType);
        tvPriceValue.setText("RM " + String.format(Locale.US, "%.2f", pricePerLiter));
        tvUsageValue.setText(String.format(Locale.US, "%.2f", fuelUsage) + " liter");
        tvTotalCostValue.setText("RM " + String.format(Locale.US, "%.2f", totalPetrolCost));
        tvBudiRebateValue.setText("RM " + String.format(Locale.US, "%.2f", budiRebate));
        tvFinalPayableValue.setText("RM " + String.format(Locale.US, "%.2f", finalPayableAmount));

        tvResultNote.setText(note);
    }
}