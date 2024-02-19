package com.example.cardiovasculardisease;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText age, gender, height, weight, ap_hi, ap_lo, cholesterol, gluc, smoking, alcohol, activity;
    Button predict;
    TextView result;
    String url = "https://cvd-o2ov.onrender.com/predict";

    @SuppressLint({"WrongViewCast", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner genderSpinner = findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        int defaultGenderSelection  = adapter.getPosition("Male");
        genderSpinner.setSelection(defaultGenderSelection);

        // Populate cholesterol Spinner
        Spinner cholesterolSpinner = findViewById(R.id.cholesterol_spinner);
        ArrayAdapter<CharSequence> cholesterolAdapter = ArrayAdapter.createFromResource(this,
                R.array.cholesterol_levels, android.R.layout.simple_spinner_item);
        cholesterolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cholesterolSpinner.setAdapter(cholesterolAdapter);

        // Set default selection for cholesterol Spinner
        int defaultCholesterolSelection = cholesterolAdapter.getPosition("Normal");
        cholesterolSpinner.setSelection(defaultCholesterolSelection);

        // Populate glucose Spinner
        Spinner glucoseSpinner = findViewById(R.id.glucose_spinner);
        ArrayAdapter<CharSequence> glucoseAdapter = ArrayAdapter.createFromResource(this,
                R.array.glucose_levels, android.R.layout.simple_spinner_item);
        glucoseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        glucoseSpinner.setAdapter(glucoseAdapter);

        // Set default selection for glucose Spinner
        int defaultGlucoseSelection = glucoseAdapter.getPosition("Normal");
        glucoseSpinner.setSelection(defaultGlucoseSelection);

        // Populate smoking Spinner
        Spinner smokingSpinner = findViewById(R.id.smoking_spinner);
        ArrayAdapter<CharSequence> smokingAdapter = ArrayAdapter.createFromResource(this,
                R.array.smoking_options, android.R.layout.simple_spinner_item);
        smokingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        smokingSpinner.setAdapter(smokingAdapter);

        // Set default selection for smoking Spinner
        int defaultSmokingSelection = smokingAdapter.getPosition("No");
        smokingSpinner.setSelection(defaultSmokingSelection);

        // Populate alcohol intake Spinner
        Spinner alcoholSpinner = findViewById(R.id.alcohol_spinner);
        ArrayAdapter<CharSequence> alcoholAdapter = ArrayAdapter.createFromResource(this,
                R.array.alcohol_options, android.R.layout.simple_spinner_item);
        alcoholAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        alcoholSpinner.setAdapter(alcoholAdapter);

        // Set default selection for alcohol intake Spinner
        int defaultAlcoholSelection = alcoholAdapter.getPosition("No");
        alcoholSpinner.setSelection(defaultAlcoholSelection);

        // Populate physical activity Spinner
        Spinner activitySpinner = findViewById(R.id.activity_spinner);
        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this,
                R.array.activity_options, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(activityAdapter);

        // Set default selection for physical activity Spinner
        int defaultActivitySelection = activityAdapter.getPosition("No");
        activitySpinner.setSelection(defaultActivitySelection);


        EditText age = findViewById(R.id.age);
        EditText height = findViewById(R.id.height);
        EditText weight = findViewById(R.id.weight);
        EditText ap_hi = findViewById(R.id.sbp);
        EditText ap_lo = findViewById(R.id.dbp);

        result = findViewById(R.id.result);

        predict = findViewById(R.id.predict); // Fixed: Use 'predict' instead of 'myButton'
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hit the api
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray predictionArray = jsonObject.getJSONArray("prediction");

                                    Log.d("ResponseData", "Response received: " + response);

                                    String data = jsonObject.getString("prediction");

                                    if(predictionArray.length() > 0) {
                                        int predictionValue = predictionArray.getInt(0); // Get the first value of the prediction array
                                        if(predictionValue == 1) {
                                            result.setText("You are likely to have Cardiovascular Disease.");
                                        } else {
                                            result.setText("You are not likely to have Cardiovascular Disease.");
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){

                    @Override
                    protected Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<>();
                        params.put("age", age.getText().toString());
                        params.put("height", height.getText().toString());
                        params.put("weight", weight.getText().toString());
                        params.put("gender", genderSpinner.getSelectedItem().toString().equals("Male") ? "0" : "1");
                        // Use correct parameter names based on the working Python script:
                        params.put("sbp", ap_hi.getText().toString()); // instead of "sbp"
                        params.put("dbp", ap_lo.getText().toString()); // instead of "dbp"
                        // Adjust other parameters as necessary
                        params.put("cholesterol", String.valueOf(cholesterolSpinner.getSelectedItemPosition() + 1)); // Assuming spinner positions align with values
                        params.put("glucose", String.valueOf(glucoseSpinner.getSelectedItemPosition() + 1)); // "gluc" instead of "glucose"
                        params.put("smoking", smokingSpinner.getSelectedItem().toString().equals("Yes") ? "1" : "0");
                        params.put("alcohol", alcoholSpinner.getSelectedItem().toString().equals("Yes") ? "1" : "0");
                        params.put("activity", activitySpinner.getSelectedItem().toString().equals("Yes") ? "1" : "0");

                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            Log.d("FormData", entry.getKey() + ": " + entry.getValue());
                        }

                        return params;
                    }

                };
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(stringRequest);
            }
        });
    }
}