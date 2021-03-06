package com.badgr.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.badgr.R;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.sql.sqlRunner;
import com.badgr.ui.login.LoginActivity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class RegisterActivity extends Activity {

    //---------------------------------------------Class Fields-------------------------------------//

    private EditText fNameEdit;
    private EditText lNameEdit;
    private EditText emailEdit;
    private EditText passEdit;
    private EditText ageEdit;
    private EditText troopEdit;
    private Button regButton;

    private TextView passLength;
    private TextView passCapital;
    private TextView passNumber;
    private FrameLayout loading;

    private boolean usernameCheckSuccess = false;
    private boolean userInDB = false;


    @Override
    public void onCreate(Bundle SIS) {
        super.onCreate(SIS);
        setContentView(R.layout.register_page);

        //sets texts and buttons from layout
        {
            fNameEdit = findViewById(R.id.registerFName);
            lNameEdit = findViewById(R.id.registerLName);
            emailEdit = findViewById(R.id.registerEmail);
            passEdit = findViewById(R.id.registerPass);
            ageEdit = findViewById(R.id.registerAge);
            troopEdit = findViewById(R.id.registerTroop);
            regButton = findViewById(R.id.registerButton);

            passLength = findViewById(R.id.lengthCheck);
            passCapital = findViewById(R.id.capitalCheck);
            passNumber = findViewById(R.id.numberCheck);
            loading = findViewById(R.id.loadingScreen);
        }

        //adds textChangeListeners to items, tries to update button whenever text is changed
        {
            //first name update
            fNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isFNameValid(fNameEdit.getText().toString()))
                        fNameEdit.setError("Invalid First Name");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isFNameValid(fNameEdit.getText().toString()))
                        fNameEdit.setError("Invalid First Name");
                    regButton.setEnabled(update());
                }
            });
            //last name update
            lNameEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isFNameValid(lNameEdit.getText().toString()))
                        lNameEdit.setError("Invalid Last Name");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isFNameValid(lNameEdit.getText().toString()))
                        lNameEdit.setError("Invalid Last Name");
                    regButton.setEnabled(update());
                }
            });
            //email update
            emailEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isUserNameValid(emailEdit.getText().toString()))
                        emailEdit.setError("Invalid email");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isUserNameValid(emailEdit.getText().toString()))
                        emailEdit.setError("Invalid email");
                    regButton.setEnabled(update());
                }
            });
            //password update
            passEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //if password characteristics are valid, change text colors
                    if (RegisterViewModel.passUpperValid(passEdit.getText().toString())) {
                        passCapital.setTextColor(Color.rgb(106, 196, 79));
                    } else {
                        passCapital.setTextColor(Color.rgb(205, 63, 62));
                    }
                    if (RegisterViewModel.passNumberValid(passEdit.getText().toString())) {
                        passNumber.setTextColor(Color.rgb(106, 196, 79));
                    } else {
                        passNumber.setTextColor(Color.rgb(205, 63, 62));
                    }
                    if (RegisterViewModel.passLengthValid(passEdit.getText().toString())) {
                        passLength.setTextColor(Color.rgb(106, 196, 79));
                    } else {
                        passLength.setTextColor(Color.rgb(205, 63, 62));
                    }

                }
            });
            //age update
            ageEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isAgeValid(ageEdit.getText().toString()))
                        ageEdit.setError("Age must be <= 120 and > 0");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isAgeValid(ageEdit.getText().toString()))
                        ageEdit.setError("Age must be <= 120 and > 0");
                    regButton.setEnabled(update());
                }
            });
            //troop update
            troopEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!RegisterViewModel.isTroopValid(troopEdit.getText().toString()))
                        troopEdit.setError("Troop must be <= 9999 and > 0");
                    regButton.setEnabled(update());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegisterViewModel.isTroopValid(troopEdit.getText().toString()))
                        troopEdit.setError("Troop must be <= 9999 and > 0");
                    regButton.setEnabled(update());
                }
            });
        }

        //register button on click
        regButton.setOnClickListener(this::attemptRegister);
    }


    /**
     * Creates a new scoutPerson p and uses it to add user to database
     *
     * @param v needed for android:onClick
     */
    private void attemptRegister(View v) {

        //-------------------------------------------------Toggles Loading Screen Layout Over Register Page-----------------------------//

        toggleVis();

        //creates a new scoutPerson with the given info
        scoutPerson p = new scoutPerson();
        p.setFName(fNameEdit.getText().toString().trim());
        p.setLName(lNameEdit.getText().toString().trim());
        p.setUser(emailEdit.getText().toString());
        p.setPass(passEdit.getText().toString().trim());
        p.setAge(ageEdit.getText().toString());
        p.setSM(p.getAge() >= 18);
        p.setTroop(troopEdit.getText().toString());


        //-------------------------------------------Username check----------------------------------------//

        //waits for loading screen to initialize
        new Handler().postDelayed(() ->
        {
            //if username exists
            if (checkUsernameExists()) {
                if (usernameCheckSuccess) {
                    //display toast that username exists
                    Toast.makeText(this, "Email already exists. Please try a different email.", Toast.LENGTH_LONG).show();
                } else {
                    //hopefully should never run, but error message in case of username check failure
                    Toast.makeText(this, "There was an error checking email address. Please try again.", Toast.LENGTH_LONG).show();
                }
                return;
            }


            //---------------------------------------------Add User Attempt------------------------------------//

            //Sets a countDownLatch, which ensures this thread is run before anything else happens
            CountDownLatch cDL = new CountDownLatch(1);

            //creates add user to database thread
            ExecutorService STE = Executors.newSingleThreadExecutor();
            STE.execute(() -> {
                //if error with adding user
                if (!sqlRunner.addUser(p)) {
                    //toast error message
                    runOnUiThread(() -> {
                        final Toast toast = Toast.makeText(getApplicationContext(), "Error occurred with adding user. Please try again", Toast.LENGTH_SHORT);
                        toast.show();
                    });
                } else
                    cDL.countDown();
            });


            //waits until previous thread has completed to move on
            try {
                cDL.await();
            } catch (InterruptedException e) {
                Toast.makeText(this, "Error occurred with adding user. Please try again", Toast.LENGTH_SHORT).show();
                return;
            }


            //---------------------------------------------Open Login page-------------------------------------//

            Toast.makeText(this, "Register Successful. Please Log In.", Toast.LENGTH_LONG).show();
            Intent oLogin = new Intent(this, LoginActivity.class);
            startActivity(oLogin);
        }, 200);

    }

    //checks to see if email is already in database using the sqlRunner class method userInDatabase
    private boolean checkUsernameExists() {
        emailEdit = findViewById(R.id.registerEmail);
        String email = emailEdit.getText().toString();

        //creates check thread
        CountDownLatch cdl = new CountDownLatch(1);
        ExecutorService STE = Executors.newSingleThreadExecutor();
        STE.execute(() -> {
            //is user in the database already
            userInDB = sqlRunner.isEmailInDatabase(email);
            cdl.countDown();
        });


        //waits until previous thread has completed to move on
        try {
            cdl.await();
        } catch (InterruptedException e) {
            usernameCheckSuccess = false;
            return false;
        }

        usernameCheckSuccess = true;
        return userInDB;
    }


    //orientation change handler
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //opens the login screen
    public void openLoginApp(View view) {
        Intent oLogin = new Intent(this, LoginActivity.class);
        startActivity(oLogin);
    }

    private void toggleVis() {
        loading = findViewById(R.id.loadingScreen);
        ProgressBar spinner = findViewById(R.id.progress_loader);
        TextView loadingText = findViewById(R.id.loading);

        //set loading screen to true
        loading.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        regButton.setVisibility(View.INVISIBLE);

    }

    /**
     * Tries to run the text checks located in RegisterViewModel to update the submit button status
     *
     * @return if text checks are true, so the regButton.setEnabled(update()) returns true
     */
    private boolean update() {
        return RegisterViewModel.registerDataChanged(
                fNameEdit.getText().toString(),
                lNameEdit.getText().toString(),
                emailEdit.getText().toString(),
                passEdit.getText().toString(),
                ageEdit.getText().toString(),
                troopEdit.getText().toString());

    }
}
