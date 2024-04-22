package iut.dam.saemobilefinale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1500; // Délai en millisecondes (ici, 2000 ms ou 2 secondes)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Trouvez l'image et le texte dans le layout
        ImageView imageViewLogo = findViewById(R.id.imageViewLogo);
        TextView textViewWelcome = findViewById(R.id.textViewWelcome);

        // Animation fade in pour l'image et le texte
        Animation rotateInAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        Animation textInAnimation = AnimationUtils.loadAnimation(this, R.anim.text_anim);
        imageViewLogo.startAnimation(rotateInAnimation);
        textViewWelcome.startAnimation(textInAnimation);

        // Utilisation d'un Handler pour passer à l'activité principale après un délai
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, MenuActivity.class);
                startActivity(mainIntent);
                finish(); // Ferme la Splash Activity après le passage à l'activité principale
            }
        }, SPLASH_TIME_OUT);
    }
}