package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "login_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember_me";

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private MaterialCheckBox rememberMeCheckBox;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);

        Button signInButton = findViewById(R.id.signInButton);
        TextView registerLink = findViewById(R.id.registerLink);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadCredentials(); // до установки слушателя флажка

        signInButton.setOnClickListener(v -> attemptLogin());

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        rememberMeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked && prefs.getBoolean(KEY_REMEMBER, false)) {
                showForgetSnackbar();
            }
        });
    }

    private void attemptLogin() {
        String email = String.valueOf(emailInput.getText()).trim();
        String password = String.valueOf(passwordInput.getText());

        emailLayout.setError(null);
        passwordLayout.setError(null);

        boolean valid = true;

        if (email.isEmpty()) {
            emailLayout.setError(getString(R.string.error_email_empty));
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError(getString(R.string.error_email_invalid));
            valid = false;
        }

        if (password.isEmpty()) {
            passwordLayout.setError(getString(R.string.error_password_empty));
            valid = false;
        }

        if (!valid) {
            return;
        }

        if (rememberMeCheckBox.isChecked()) {
            saveCredentials(email, password);
        }

        Toast.makeText(this, R.string.login_fields_ok, Toast.LENGTH_SHORT).show();
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putBoolean(KEY_REMEMBER, true);
        editor.apply();
    }

    private void loadCredentials() {
        boolean remember = prefs.getBoolean(KEY_REMEMBER, false);

        if (remember) {
            emailInput.setText(prefs.getString(KEY_EMAIL, ""));
            passwordInput.setText(prefs.getString(KEY_PASSWORD, ""));
            rememberMeCheckBox.setChecked(true);
        }
    }

    private void showForgetSnackbar() {
        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                R.string.snackbar_forget,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.snackbar_confirm, v -> clearCredentials());

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar sb, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    rememberMeCheckBox.setChecked(true);
                }
            }
        });

        snackbar.show();
    }

    private void clearCredentials() {
        prefs.edit().clear().apply();
    }
}