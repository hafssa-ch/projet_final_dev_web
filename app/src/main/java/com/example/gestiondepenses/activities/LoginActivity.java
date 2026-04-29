package com.example.gestiondepenses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gestiondepenses.R;
import com.example.gestiondepenses.network.ApiClient;
import com.example.gestiondepenses.network.ApiService;
import com.example.gestiondepenses.network.LoginRequest;
import com.example.gestiondepenses.network.LoginResponse;
import com.example.gestiondepenses.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login); // <-- correction : utiliser super

        ApiClient.init(this);
        apiService = ApiClient.getApiService();
        tokenManager = ApiClient.getTokenManager();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
            String password = etPassword.getText() == null ? "" : etPassword.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }
            performLogin(email, password);
        });
    }

    private void performLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String token = loginResponse.getToken();
                    if (token == null || token.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Token invalide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tokenManager.saveToken(token,
                            loginResponse.getUser().getId(),
                            loginResponse.getUser().getName(),
                            loginResponse.getUser().getEmail());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Identifiants invalides", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Erreur réseau", t);
                Toast.makeText(LoginActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}