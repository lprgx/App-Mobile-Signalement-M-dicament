package iut.dam.saemobilefinale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button btn_Signaler = findViewById(R.id.btn_Signaler);
        Button btn_Connexion = findViewById(R.id.btn_Connecter);


        btn_Signaler.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btn_Connexion.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btn_Signaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                btn_Signaler.setBackgroundColor(Color.parseColor("#003d66"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_Signaler.setBackgroundColor(Color.parseColor("#FFFFFF")); // Couleur d'origine
                    }
                }, 150);
                finish();
            }
        });
        btn_Connexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, LoginActivity.class));
                btn_Connexion.setBackgroundColor(Color.parseColor("#003d66"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_Connexion.setBackgroundColor(Color.parseColor("#FFFFFF")); // Couleur d'origine
                    }
                }, 150);
                finish();
            }
        });
    }
}