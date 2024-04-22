package iut.dam.saemobilefinale;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
public class MainActivity extends AppCompatActivity {

    Button btn_scan;
    Button btn_signaler;
    Map<Signalement,Medicament> MesSignalements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_scan = findViewById(R.id.btn_Scan);
        btn_signaler = findViewById(R.id.btn_Signaler);
        EditText editTextCIP = findViewById(R.id.editTextCIP);
        MesSignalements = new HashMap<>();
        readAndProcessSignalements();
        readAndProcessSignalementsHorsCo();
        btn_signaler.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btn_scan.setBackgroundColor(Color.parseColor("#FFFFFF"));
        ImageView img_Back = findViewById(R.id.fleche);
        img_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
                finish();
            }
        });

        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_scan.setBackgroundColor(Color.parseColor("#003d66"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_scan.setBackgroundColor(Color.parseColor("#FFFFFF")); // Couleur d'origine
                    }
                }, 150); // Délai en millisecondes

                scan_code();
            }
        });

        btn_signaler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_signaler.setBackgroundColor(Color.parseColor("#003d66"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btn_signaler.setBackgroundColor(Color.parseColor("#FFFFFF")); // Couleur d'origine
                    }
                }, 150); // Délai en millisecondes
                String CIP = editTextCIP.getText().toString();
                if(verifierCIP(CIP)){
                    if(!(estSignale(CIP))){
                        editTextCIP.setText("");
                        Calendar currentCalendar = Calendar.getInstance();
                        Date currentDate = currentCalendar.getTime();
                        currentCalendar.add(Calendar.HOUR_OF_DAY, 2);
                        Date newDate = currentCalendar.getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String formattedNewDate = dateFormat.format(newDate);
                        insererSignalement(CIP,formattedNewDate);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Vous venez de signaler ce medicament.", Toast.LENGTH_SHORT).show();
                    // Restaurer la couleur d'origine du bouton après un certain délai (par exemple, 1 seconde)

                }
                else
                    Toast.makeText(getApplicationContext(), "Le code doit avoir une longueur de 13 et ne contenir que des chiffres.", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void scan_code() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volumn up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            // Si le contenu du résultat n'est pas null, afficher le contenu dans une boîte de dialogue
            if(!(estSignale(result.getContents().substring(4, 17)))) {
                Calendar currentCalendar = Calendar.getInstance();
                Date currentDate = currentCalendar.getTime();
                currentCalendar.add(Calendar.HOUR_OF_DAY, 2);
                Date newDate = currentCalendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String formattedNewDate = dateFormat.format(newDate);
                insererSignalement(result.getContents().substring(4, 17),formattedNewDate);
            }
            else{
                Toast.makeText(getApplicationContext(), "Vous venez de signaler ce medicament.", Toast.LENGTH_SHORT).show();
            }

        } else {
            // Si le contenu du résultat est null, afficher un message d'erreur
            Toast.makeText(MainActivity.this, "Aucun code-barres scanné", Toast.LENGTH_SHORT).show();
        }
    });


    public boolean verifierCIP(String cip) {
        // Vérifier la longueur
        if (cip.length() != 13) {
            return false;
        }

        // Vérifier que tous les caractères sont des chiffres
        for (int i = 0; i < cip.length(); i++) {
            if (!Character.isDigit(cip.charAt(i))) {
                return false;
            }
        }

        return true;
    }
    public void insererSignalement(String CIP,String formattedNewDate){
        String urlString = "http://192.168.1.13/Pharmacie/insertSignalement.php?cip_13=" + CIP + "&current_date=" + formattedNewDate ;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    String message = jsonResponse.getString("message");

                    if (message.contains("Nouveau signalement insere avec succes")) {
                        String medicamentJson = jsonResponse.getString("medicament");
                        Medicament m = Medicament.getFromJson(medicamentJson);
                        Signalement s = new Signalement(CIP,formattedNewDate);
                        MesSignalements.put(s,m);
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Nouveau signalement inséré avec succès!", Toast.LENGTH_SHORT).show();
                            afficherSignalement(s,m);
                            writeSignalementToFile(s,m);
                        });
                    } else if (message.contains("Aucun medicament trouve avec ce CIP_13")) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Aucun médicament trouvé avec ce CIP_13", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Erreur lors de l'insertion du signalement: " + message, Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                searchCipAndGetName(CIP);
            }
        });
    }
    public boolean estSignale(String cip){
        for (Map.Entry<Signalement, Medicament> entry : MesSignalements.entrySet()) {
            if(entry.getKey().getCIP().equals(cip))
                return true;
        }
        return false;
    }
    private void afficherSignalement(Signalement signalement, Medicament medicament) {
        TableLayout tableLayout = findViewById(R.id.tableLayoutSignalements); // TableLayout dans le layout XML
        Typeface customTypeface = ResourcesCompat.getFont(this, R.font.police);

        // Créer une nouvelle rangée pour le signalement
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        // Créer une cellule pour le code CIP
        TextView textViewCIP = new TextView(this);
        textViewCIP.setText(signalement.getCIP());
        textViewCIP.setTypeface(customTypeface);

        textViewCIP.setTextColor(Color.WHITE); // Changer la couleur du texte en blanc
        textViewCIP.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewCIP.setPadding(0, 0, 0, 20);
        row.addView(textViewCIP); // Ajouter la cellule à la rangée

        // Créer une cellule pour le nom du médicament
        TextView textViewNom = new TextView(this);
        textViewNom.setText(medicament.getNom());
        textViewNom.setTypeface(customTypeface);

        textViewNom.setGravity(Gravity.CENTER_VERTICAL); // Aligner le texte verticalement au centre
        textViewNom.setTextColor(Color.WHITE); // Changer la couleur du texte en blanc
        textViewNom.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewNom.setPadding(0, 0, 0, 20);
        row.addView(textViewNom); // Ajouter la cellule à la rangée

        // Créer une cellule pour la date du signalement
        TextView textViewDate = new TextView(this);
        textViewDate.setText(signalement.getDate());
        textViewDate.setTypeface(customTypeface);

        textViewDate.setTextColor(Color.WHITE); // Changer la couleur du texte en blanc
        textViewDate.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewDate.setPadding(0, 0, 0, 20);
        row.addView(textViewDate); // Ajouter la cellule à la rangée

        // Ajouter la rangée au TableLayout
        tableLayout.addView(row);
    }

    private void searchCipAndGetName(String cipToFind) {

        String jsonString = readJsonFromFile(); // Lire le fichier JSON
        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString); // Parser le JSON en tableau

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i); // Obtenir l'objet JSON à chaque index

                    String cip = jsonObject.getString("CIP");
                    if (cip.substring(0,13).equals(cipToFind)) {// Vérifier si le code CIP correspond
                        String nom = jsonObject.getString("Nom");
                        Calendar currentCalendar = Calendar.getInstance();
                        Date currentDate = currentCalendar.getTime();
                        currentCalendar.add(Calendar.HOUR_OF_DAY, 1);
                        Date newDate = currentCalendar.getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String formattedNewDate = dateFormat.format(newDate);
                        Medicament m = new Medicament(cipToFind,nom);
                        Signalement s = new Signalement(cipToFind,formattedNewDate);
                        MesSignalements.put(s,m);
                        writeSignalementToFileHorsCo(s,m);
                        writeSignalementToFile(s,m);
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Nouveau signalement hors connexion inséré avec succès!", Toast.LENGTH_SHORT).show();
                            afficherSignalement(s,m);
                        });// Obtenir la valeur du champ "Nom"
                        Log.d(TAG, "CIP trouvé: " + cip + " - Nom: " + nom); // Afficher le résultat
                        break; // Arrêter la boucle si le code CIP est trouvé
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Erreur lors du parsing du JSON: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Impossible de lire le fichier JSON");
        }
    }

    private String readJsonFromFile() {
        try {
            InputStream is = getResources().openRawResource(R.raw.cip_nom);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            is.close();
            return sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la lecture du fichier: " + e.getMessage());
            return null;
        }
    }

    private void writeSignalementToFileHorsCo(Signalement signalement, Medicament medicament) {
        String fileName = "signalements_hors_connexion.json";
        JSONArray jsonArray = new JSONArray();

        // Lire le contenu existant du fichier
        String existingContent = readJsonFromFile(fileName);
        if (existingContent != null && !existingContent.isEmpty()) {
            try {
                jsonArray = new JSONArray(existingContent);
            } catch (JSONException e) {
                Log.e(TAG, "Erreur lors de la conversion du contenu existant en JSONArray: " + e.getMessage());
            }
        }

        // Créer le nouvel objet JSON à ajouter
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("CIP", signalement.getCIP());
            jsonObject.put("Date", signalement.getDate());
            jsonObject.put("Nom", medicament.getNom());
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la création du nouvel objet JSON: " + e.getMessage());
            return;
        }

        // Ajouter le nouvel objet au JSONArray existant
        jsonArray.put(jsonObject);

        // Réécrire le fichier avec le nouveau contenu complet
        try (FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
            writer.write(jsonArray.toString());
            Log.d(TAG, "Signalement ajouté avec succès dans le fichier: " + fileName);
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de l'écriture du fichier: " + e.getMessage());
        }
    }
    private void writeSignalementToFile(Signalement signalement, Medicament medicament) {
        String fileName = "signalements.json";
        JSONArray jsonArray = new JSONArray();

        // Lire le contenu existant du fichier
        String existingContent = readJsonFromFile(fileName);
        if (existingContent != null && !existingContent.isEmpty()) {
            try {
                jsonArray = new JSONArray(existingContent);
            } catch (JSONException e) {
                Log.e(TAG, "Erreur lors de la conversion du contenu existant en JSONArray: " + e.getMessage());
            }
        }

        // Créer le nouvel objet JSON à ajouter
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("CIP", signalement.getCIP());
            jsonObject.put("Date", signalement.getDate());
            jsonObject.put("Nom", medicament.getNom());
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors de la création du nouvel objet JSON: " + e.getMessage());
            return;
        }

        // Ajouter le nouvel objet au JSONArray existant
        jsonArray.put(jsonObject);

        // Réécrire le fichier avec le nouveau contenu complet
        try (FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
            writer.write(jsonArray.toString());
            Log.d(TAG, "Signalement ajouté avec succès dans le fichier: " + fileName);
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de l'écriture du fichier: " + e.getMessage());
        }
    }

    private String readJsonFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = openFileInput(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la lecture du fichier: " + e.getMessage());
            // Si le fichier n'existe pas, retourner null pour créer un nouveau JSONArray
            return null;
        }
        return content.toString();
    }
    private void readAndProcessSignalementsHorsCo() {
        String fileName = "signalements_hors_connexion.json";
        try {
            // Lire le contenu du fichier JSON
            String jsonString = readJsonFromFile(fileName);
            if (jsonString != null && !jsonString.isEmpty()) {
                JSONArray jsonArray = new JSONArray(jsonString);

                // Itérer à travers chaque objet du tableau JSON
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String codeCIP = jsonObject.getString("CIP");
                    String date = jsonObject.getString("Date");
                    insererSignalementHorsCo(codeCIP,date);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors du parsing du JSON: " + e.getMessage());
        }
    }
    private void readAndProcessSignalements() {
        String fileName = "signalements.json";
        try {
            // Lire le contenu du fichier JSON
            String jsonString = readJsonFromFile(fileName);
            if (jsonString != null && !jsonString.isEmpty()) {
                JSONArray jsonArray = new JSONArray(jsonString);

                // Itérer à travers chaque objet du tableau JSON
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String codeCIP = jsonObject.getString("CIP");
                    String date = jsonObject.getString("Date");
                    String nom = jsonObject.getString("Nom");
                    Medicament m = new Medicament(codeCIP,nom);
                    Signalement s = new Signalement(codeCIP,date);
                    MesSignalements.put(s,m);
                    afficherSignalement(s,m);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors du parsing du JSON: " + e.getMessage());
        }
    }
    public void insererSignalementHorsCo(String CIP,String formattedNewDate){
        String urlString = "http://192.168.1.13/Pharmacie/insertSignalement.php?cip_13=" + CIP + "&current_date=" + formattedNewDate ;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    String message = jsonResponse.getString("message");

                    if (message.contains("Nouveau signalement insere avec succes")) {
                        removeSignalementFromFile(CIP,formattedNewDate);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
            }
        });
    }
    private void removeSignalementFromFile(String cipToRemove, String dateToRemove) {
        String fileName = "signalements_hors_connexion.json";
        try {
            // Lire le contenu existant du fichier
            String jsonString = readJsonFromFile(fileName);
            if (jsonString != null && !jsonString.isEmpty()) {
                JSONArray jsonArray = new JSONArray(jsonString);
                JSONArray newArray = new JSONArray();

                // Itérer à travers le JSONArray pour trouver l'objet à supprimer
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String codeCIP = jsonObject.getString("CIP");
                    String date = jsonObject.getString("Date");

                    // Si l'objet courant ne correspond pas aux critères, l'ajouter au nouveau JSONArray
                    if (!codeCIP.equals(cipToRemove) || !date.equals(dateToRemove)) {
                        newArray.put(jsonObject);
                    }
                }

                // Réécrire le fichier avec le nouveau JSONArray
                writeJsonToFile(fileName, newArray);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Erreur lors du parsing du JSON: " + e.getMessage());
        }
    }

    private void writeJsonToFile(String fileName, JSONArray jsonArray) {
        try (FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de l'écriture dans le fichier: " + e.getMessage());
        }
    }
}