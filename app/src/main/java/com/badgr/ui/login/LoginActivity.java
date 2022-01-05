package com.badgr.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;

import androidx.lifecycle.ViewModelProvider;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.badgr.R;
import com.badgr.data.Result;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.databinding.ActivityLoginBinding;
import com.badgr.scoutPagesAndClasses.scoutPage;
import com.badgr.sql.sqlRunner;
import com.badgr.ui.register.RegisterActivity;
import com.badgr.ui.register.RegisterViewModel;

import java.util.concurrent.CountDownLatch;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        com.badgr.databinding.ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar spinner = findViewById(R.id.progress_loader_login);
        final TextView loadingText = findViewById(R.id.loading_login);
        final FrameLayout loadingFrame = findViewById(R.id.loadingScreenLogin);


        loginViewModel.getLoginResult().observe(this, loginResult -> {
            toggleLoading(loadingFrame, spinner, loadingText);
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);
            toggleLoading(loadingFrame, spinner, loadingText);
        });


        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!RegisterViewModel.isUserNameValid(usernameEditText.getText().toString()))
                    usernameEditText.setError("Invalid email");
                loginButton.setEnabled(updateBut());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!RegisterViewModel.isUserNameValid(usernameEditText.getText().toString()))
                    usernameEditText.setError("Invalid email");
                loginButton.setEnabled(updateBut());
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                loginButton.setEnabled(updateBut());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginButton.setEnabled(updateBut());
            }
        });



        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                toggleLoading(loadingFrame, spinner, loadingText);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v ->
                {
                    toggleLoading(loadingFrame, spinner, loadingText);

                    CountDownLatch loginCDL = new CountDownLatch(1);
                    loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                    loginCDL.countDown();

                    //waits until action has completed to move on
                    try {
                        loginCDL.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    toggleLoading(loadingFrame, spinner, loadingText);
                }
        );
    }

    //Changes orientation successfully
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void updateUiWithUser(@NonNull scoutPerson p) {
        String welcome = (getString(R.string.welcome) + " " + p.getFName() + " "+ p.getLName() + "!");
        // TODO : initiate successful logged in experience

        if (p.isSM()) openSMApp();
        else openApp();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    public void openApp() {
        Intent open = new Intent(this, scoutPage.class);
        startActivity(open);
    }


    //TODO fix
    public void openSMApp() {
        Intent open = new Intent(this, scoutPage.class);
        startActivity(open);
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
        EditText pass = findViewById(R.id.password);
        pass.setText("");

    }

    public void viewRegisterClicked(View view) {
        Intent openRegister = new Intent(this, RegisterActivity.class);
        startActivity(openRegister);
    }

    public boolean updateBut() {
        EditText user = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);
        return !(user.getText().toString().equals("") || pass.getText().toString().equals(""));
    }

    public static void toggleLoading(FrameLayout f, ProgressBar p, TextView t)
    {
        if (f.getVisibility() == View.INVISIBLE)
        {
            f.setVisibility(View.VISIBLE);
            p.setVisibility(View.VISIBLE);
            t.setVisibility(View.VISIBLE);
        }
        else
        {
            f.setVisibility(View.INVISIBLE);
            p.setVisibility(View.INVISIBLE);
            t.setVisibility(View.INVISIBLE);
        }
    }
}