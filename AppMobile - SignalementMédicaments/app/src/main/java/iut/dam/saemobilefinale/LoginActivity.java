package iut.dam.saemobilefinale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

public class LoginActivity extends AppCompatActivity {

    private Context context = this;
    Admin admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        Button buttonLogin = findViewById(R.id.buttonLogin);
        EditText emailET = findViewById(R.id.TextEmail);
        EditText passwordET = findViewById(R.id.TextPassword);
        int buttonColor = Color.parseColor("#FFD700");
        buttonLogin.setBackgroundColor(buttonColor);
        buttonLogin.setBackgroundColor(Color.parseColor("#FFFFFF"));
        ImageView img_Back = findViewById(R.id.fleche);
        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(emailET.getText().toString(), passwordET.getText().toString());
                buttonLogin.setBackgroundColor(Color.parseColor("#003d66"));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonLogin.setBackgroundColor(Color.parseColor("#FFFFFF")); // Couleur d'origine
                    }
                }, 150); // Délai en millisecondes
            }
        });
    }

    private void login(String email, String password) {
        if (!(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            Toast.makeText(getApplicationContext(), R.string.email_not_valid, Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si le mot de passe est vide
        if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.empty_field, Toast.LENGTH_SHORT).show();
            return;
        }

        String urlString = "http://192.168.1.13/Pharmacie/login.php?email=" + email + "&password=" + password;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    admin = Admin.getFromJson(responseBody);
                    runOnUiThread(() -> {
                        // Utiliser les données extraites comme nécessaire
                        Toast.makeText(getApplicationContext(), R.string.successful_login, Toast.LENGTH_SHORT).show();
                            Intent newIntent = new Intent(LoginActivity.this, AdminActivity.class);
                            startActivity(newIntent);
                             finish();
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Utilisateur inconnu, veuillez réessayer", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), R.string.login_failure_message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
