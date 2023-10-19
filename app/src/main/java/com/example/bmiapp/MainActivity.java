package com.example.bmiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    EditText weight_input, height_input, age_input;

    RadioGroup sex_input;
    String sex;
    TextView bmi_txt;
    Button calculate_btn;

    ArrayList<JSONObject> jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weight_input = findViewById(R.id.weight_input);
        height_input = findViewById(R.id.height_input);
        bmi_txt = findViewById(R.id.bmi_txt);
        calculate_btn = findViewById(R.id.calculate_btn);
        age_input = findViewById(R.id.age_input);
        sex_input = findViewById(R.id.sex_input);

        sex_input.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton radioButton = findViewById(i);
            sex = radioButton.getText().toString().trim();
        });

        calculate_btn.setOnClickListener(v -> {
            Thread thread = new Thread(() -> {
                Double mass = Double.valueOf(weight_input.getText().toString().trim());
                Double height = Double.valueOf(height_input.getText().toString().trim());
                int age = Integer.valueOf(age_input.getText().toString().trim());

                jsonObject = getBmi(mass, height, age, sex);
            });
            thread.start();
            try {
                thread.join();
                if (jsonObject == null) {
                    Toast.makeText(MainActivity.this, "xpp", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                    String bmi = jsonObject.get(0).getJSONObject("data").getString("bmi");
                    bmi_txt.setText(bmi);
                }
            } catch (InterruptedException | JSONException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public static double round(double value) {
        int precision = 2;
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(precision, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public ArrayList<JSONObject> getBmi(double mass, double height, int age, String sex) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://fitness-calculator.p.rapidapi.com/bmi?age="+age+"&weight="+mass+"&height="+height)
                    .get()
                    .addHeader("X-RapidAPI-Key", "")
                    .addHeader("X-RapidAPI-Host", "fitness-calculator.p.rapidapi.com")
                    .build();

            Response response = client.newCall(request).execute();
            JSONObject jsonObject1 = new JSONObject(response.body().string());
            ArrayList<JSONObject> ret = new ArrayList<>();
            ret.add(jsonObject1);



            return ret;
        } catch (Exception e) {
            Log.d("CHUJ", e.toString());
        }
        return null;
    }
}