package com.moore.burnedcaloriescalculator;

import android.content.SharedPreferences;
import android.drm.DrmStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.text.NumberFormat;

import static android.widget.AdapterView.*;

public class BurnedCaloriesCalculatorActivity extends AppCompatActivity
implements OnEditorActionListener{

    // define variables for the widgets
    private EditText weightET;
    private TextView milesRanTV;
    private SeekBar milesRanSeekBar;
    private TextView caloriesBurnedTV;
    private Spinner heightFeetSpinner;
    private Spinner heightInchesSpinner;
    private TextView bmiTV;
    private EditText nameET;
    private Button resetButton;

    // define the SharedPreferences
    private SharedPreferences savedValues;

    // define instance variables that should be saved
    private String weightString = "";
    private int milesRanProgress = 0;
    private int heightFeetInt = 0;
    private int heightInchesInt = 0;
    private String nameString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burned_calories_calculator);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.heartbeat);
        getSupportActionBar().setTitle("Burned Calories Calculator");

        // get references to widgets
        weightET = (EditText) findViewById(R.id.weightET);
        milesRanTV = (TextView) findViewById(R.id.milesRanTV);
        milesRanSeekBar = (SeekBar) findViewById(R.id.milesRanSeekBar);
        caloriesBurnedTV = (TextView) findViewById(R.id.caloriesBurnedTV);
        heightFeetSpinner = (Spinner) findViewById(R.id.heightFeetSpinner);
        heightInchesSpinner = (Spinner) findViewById(R.id.heightInchesSpinner);
        bmiTV = (TextView) findViewById(R.id.bmiTV);
        nameET = (EditText) findViewById(R.id.nameET);
        resetButton = (Button) findViewById(R.id.resetButton);

        //set array adapter for spinner
        ArrayAdapter<CharSequence> feetAdapter = ArrayAdapter.createFromResource(this,R.array.heightFeet_array,android.R.layout.simple_spinner_item);
        feetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightFeetSpinner.setAdapter(feetAdapter);

        ArrayAdapter<CharSequence> inchesAdapter = ArrayAdapter.createFromResource(this,R.array.heightInches_array,android.R.layout.simple_spinner_item);
        inchesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightInchesSpinner.setAdapter(inchesAdapter);

        // set the listeners

        weightET.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    calculateAndDisplay();
                }
                return false;
            }
        });
        nameET.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    calculateAndDisplay();
                }
                return false;
            }
        });

        milesRanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                milesRanProgress = progress;
                milesRanTV.setText(progress + "mi");
                calculateAndDisplay();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        heightFeetSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                heightFeetInt = position;
                calculateAndDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        heightInchesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                heightInchesInt = position;
                calculateAndDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        resetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                weightString = "";
                nameString = "";
                milesRanProgress = 0;
                heightInchesInt = 0;
                heightFeetInt = 0;
                weightET.setText("");
                heightFeetSpinner.setSelection(heightFeetInt);
                heightInchesSpinner.setSelection(heightInchesInt);
                milesRanSeekBar.setProgress(milesRanProgress);
                calculateAndDisplay();
            }
        });

        // get SharedPerferences Object
        savedValues = getSharedPreferences("SavedValues",MODE_PRIVATE);

        milesRanSeekBar.setProgress(milesRanProgress);
    }

    public void calculateAndDisplay(){
        // get weight

        weightString = weightET.getText().toString();

        int heightFeetCalc = heightFeetInt + 4;

        float weight;
        if (weightString.equals("")){
            weight = 0;
        } else{
            weight = Float.parseFloat(weightString);
        }

        // get miles progress
        int progress = milesRanSeekBar.getProgress();

        double caloriesBurned = 0;
        float bmi = 0;

        caloriesBurned = .75 * weight * progress;
        bmi = (weight * 703)/ ((12 * heightFeetCalc + heightInchesInt) * (12 * heightFeetCalc + heightInchesInt));

        NumberFormat decimal = NumberFormat.getNumberInstance();
        if (caloriesBurned <= 0){
            caloriesBurnedTV.setText(decimal.format(0.0));
        }else {
            caloriesBurnedTV.setText(decimal.format(caloriesBurned));
        }
        bmiTV.setText(decimal.format(bmi));


    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }

        return false;
    }

    @Override
    protected void onPause() {
        // save the instance variables

        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("weightString",weightString);
        editor.putInt("milesRanProgress",milesRanProgress);
        editor.putInt("heightFeetInt",heightFeetInt);
        editor.putInt("heightInchesInt",heightInchesInt);
        editor.putString("nameString",nameString);
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // get the instance variables

        weightString = savedValues.getString("weightString","");
        milesRanProgress = savedValues.getInt("milesRanProgress",0);
        heightFeetInt = savedValues.getInt("heightFeetInt", 0);
        heightInchesInt = savedValues.getInt("heightInchesInt", 0);
        nameString = savedValues.getString("nameString","");

        // set the weight and name if there is saved values
        if (nameString != "")
            nameET.setText(nameString);
        if (weightString != "")
            weightET.setText(weightString);

        milesRanSeekBar.setProgress(milesRanProgress);
        heightFeetSpinner.setSelection(heightFeetInt);
        heightInchesSpinner.setSelection(heightInchesInt);

        calculateAndDisplay();
    }

}
