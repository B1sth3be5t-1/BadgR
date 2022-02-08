package com.badgr.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.badgr.R;
import com.badgr.scoutClasses.scoutPerson;
import com.badgr.scoutPagesAndClasses.scoutMasterPage;
import com.badgr.scoutPagesAndClasses.scoutPage;
import com.badgr.ui.register.RegisterActivity;
import com.badgr.ui.register.RegisterViewModel;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar spinner = findViewById(R.id.progress_loader_login);
        final TextView loadingText = findViewById(R.id.loading_login);
        final FrameLayout loadingFrame = findViewById(R.id.loadingScreenLogin);
        final TextView noAccount = findViewById(R.id.noAccount);


        loginViewModel.getLoginResult().observe(this, loginResult -> {
            toggleLoading(loadingFrame, spinner, loadingText, true);
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
            toggleLoading(loadingFrame, spinner, loadingText, false);
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
                if (!RegisterViewModel.isUserNameValid(usernameEditText.getText().toString())) {
                    usernameEditText.setError("Invalid email");
                    loginButton.setEnabled(false);
                    return;
                }
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
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });
        loginButton.setOnClickListener(v -> {
            toggleLoading(loadingFrame, spinner, loadingText, true);

            loginViewModel.login(usernameEditText.getText().toString(), passwordEditText.getText().toString());

        });

        noAccount.setOnClickListener(v -> {
            Intent openRegister = new Intent(this, RegisterActivity.class);
            startActivity(openRegister);
        });
    }

    //Changes orientation
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void updateUiWithUser(@NonNull scoutPerson p) {
        String welcome = (getString(R.string.welcome) + " " + p.getFName() + " " + p.getLName() + "!");
        if (p.isSM()) openSMApp();
        else openApp();
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void openApp() {
        Intent open = new Intent(this, scoutPage.class);
        startActivity(open);
    }


    private void openSMApp() {
        Intent open = new Intent(this, scoutMasterPage.class);
        startActivity(open);
    }

    private void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
        EditText pass = findViewById(R.id.password);
        pass.setText("");

    }

    private boolean updateBut() {
        EditText user = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);
        return !(user.getText().toString().equals("") || pass.getText().toString().equals(""));
    }

    private static void toggleLoading(FrameLayout f, ProgressBar p, TextView t, boolean tog) {
        if (tog) {
            f.setVisibility(View.VISIBLE);
            p.setVisibility(View.VISIBLE);
            t.setVisibility(View.VISIBLE);
        } else {
            f.setVisibility(View.GONE);
            p.setVisibility(View.INVISIBLE);
            t.setVisibility(View.INVISIBLE);
        }
    }
}