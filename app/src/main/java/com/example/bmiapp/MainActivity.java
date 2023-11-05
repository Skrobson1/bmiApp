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
    TextView bmi_txt, bmi_category_txt, bfp_txt, calories_txt;
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
        bmi_category_txt = findViewById(R.id.bmi_category_txt);
//        bfp_txt = findViewById(R.id.bfp_txt);
//        calories_txt = findViewById(R.id.calories_txt);

        sex_input.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton radioButton = findViewById(i);
            sex = radioButton.getText().toString().trim();
        });

        calculate_btn.setOnClickListener(v -> {
            Thread thread = new Thread(() -> {
                Double mass = Double.valueOf(weight_input.getText().toString().trim());
                Double height = Double.valueOf(height_input.getText().toString().trim());
                int age = Integer.valueOf(age_input.getText().toString().trim());
                Log.e("Kutas", sex);
//                if (sex == "Mężczyzna") {
//                    sex = "male";
//                    Log.e("Kutas", sex);
//                } else if (sex == "Kobieta") {
//                    sex = "female";
//                    Log.e("Kutas", sex);
//                }

                jsonObject = getBmi(mass, height, age, sex.toLowerCase());
            });
            thread.start();
            try {
                thread.join();
                if (jsonObject == null) {
                    Toast.makeText(MainActivity.this, "xpp", Toast.LENGTH_LONG).show();
                } else {
//                    Toast.makeText(MainActivity.this, jsonObject.toString(), Toast.LENGTH_LONG).show();
                    Log.d("CHUJJ", jsonObject.toString());
                    String bmi = jsonObject.get(0).getJSONObject("info").getString("bmi");
                    String health = jsonObject.get(0).getJSONObject("info").getString("health");
                    bmi_category_txt.setText(health);
                    bmi_txt.setText("BMI: "+bmi);
//                    String bfp = String.valueOf(jsonObject.get(1).getJSONObject("info").getDouble("bfp"));
//                    bfp_txt.setText("Procent tkanki tłuszczowej: "+bfp);
//                    String tdee = String.valueOf(jsonObject.get(2).getJSONObject("info").getDouble("tdee"));
//                    calories_txt.setText("Dzienne zapotrzebowanie: "+tdee+" kalorii");

                }
            } catch (InterruptedException | JSONException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public ArrayList<JSONObject> getBmi(double mass, double height, int age, String sex) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://mega-fitness-calculator1.p.rapidapi.com/bmi?weight="+mass+"&height="+height)
                    .get()
                    .addHeader("X-RapidAPI-Key", "6f089759e1mshad9d0ee313c8173p110b9ejsna3f43baf66da")
                    .addHeader("X-RapidAPI-Host", "mega-fitness-calculator1.p.rapidapi.com")
                    .build();

            Response response = client.newCall(request).execute();
            JSONObject jsonObject1 = new JSONObject(response.body().string());
            ArrayList<JSONObject> ret = new ArrayList<>();
            ret.add(jsonObject1);

            request = new Request.Builder()
                    .url("https://mega-fitness-calculator1.p.rapidapi.com/bfp?weight="+mass+"&height="+height+"&age="+String.valueOf(age)+"&gender="+sex)
                    .get()
                    .addHeader("X-RapidAPI-Key", "6f089759e1mshad9d0ee313c8173p110b9ejsna3f43baf66da")
                    .addHeader("X-RapidAPI-Host", "mega-fitness-calculator1.p.rapidapi.com")
                    .build();

            response = client.newCall(request).execute();
            jsonObject1 = new JSONObject(response.body().string());
            ret.add(jsonObject1);

            request = new Request.Builder()
                    .url("https://mega-fitness-calculator1.p.rapidapi.com/tdee?weight=81&height=172&activitylevel=ma&age=26&gender=male")
                    .get()
                    .addHeader("X-RapidAPI-Key", "6f089759e1mshad9d0ee313c8173p110b9ejsna3f43baf66da")
                    .addHeader("X-RapidAPI-Host", "mega-fitness-calculator1.p.rapidapi.com")
                    .build();

            response = client.newCall(request).execute();
            jsonObject1 = new JSONObject(response.body().string());
            ret.add(jsonObject1);

            return ret;
        } catch (Exception e) {
            Log.d("CHUJ", e.toString());
        }
        return null;
    }
}