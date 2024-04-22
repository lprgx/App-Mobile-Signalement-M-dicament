package iut.dam.saemobilefinale;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatistiqueFragment extends Fragment {
    private List<Signalement> mesSignalements;
    private List<Medicament> medicamentSignales;
    private List<Pathologie> pathologies;

    public StatistiqueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_statistique, container, false);
        mesSignalements = new ArrayList<>();
        medicamentSignales = new ArrayList<>();
        pathologies = new ArrayList<>();
        getStatistique(rootView);
        getSignalementByPathologie(rootView);
        Button btnRechercher = rootView.findViewById(R.id.btn_Rechercher);
        btnRechercher.setBackgroundColor(Color.parseColor("#FFFFFF"));
        EditText editTextRechercherMedicament = rootView.findViewById(R.id.editTextRechercherMedicament);
        btnRechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRechercher.setBackgroundColor(Color.parseColor("#003d66"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnRechercher.setBackgroundColor(Color.parseColor("#FFFFFF")); // Couleur d'origine
                    }
                }, 150); // Délai en millisecondes

            }
        });
        btnRechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nbSignalements = 0;
                String code = editTextRechercherMedicament.getText().toString();
                if (code.length() == 13) {
                    for (Medicament m : medicamentSignales) {
                        if (code.equals(m.getCIP_13())) {
                            for (Signalement s : mesSignalements) {
                                if (s.getCODE_CIS().equals(m.getCIS()))
                                    nbSignalements++;
                            }
                            break;
                        }
                    }
                } else if (code.length() == 8) {
                    for (Medicament m : medicamentSignales) {
                        if (code.equals(m.getCIS())) {
                            for (Signalement s : mesSignalements) {
                                if (s.getCODE_CIS().equals(code))
                                    nbSignalements++;
                            }
                            break;
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Vous devez entrer le code CIP_13 ou le code CIS", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "Il y a " + nbSignalements + " pour le médicament correspondant au code " + code, Toast.LENGTH_SHORT).show();

            }
        });

        return rootView;
    }

    private void getStatistique(View rootView) {
        String urlString = "http://192.168.1.13/Pharmacie/getSignalements.php";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String medicamentsJson = jsonObject.optString("medicaments");
                    String signalementsJson = jsonObject.optString("signalements");
                    medicamentSignales = Medicament.getListFromJson(medicamentsJson);
                    mesSignalements = Signalement.getListFromJson(signalementsJson);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            afficherSignalement(rootView);
                            afficherNbSignalement(rootView);
                        }
                    });

                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Utilisateur inconnu, veuillez réessayer", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), R.string.login_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getSignalementByPathologie(View rootView) {
        String urlString = "http://192.168.1.13/Pharmacie/getSignalementsByPath.php";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String pathologieJson = jsonObject.optString("Pathologie");
                    pathologies = Pathologie.getListFromJson(pathologieJson);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            afficherSignalementByPatologie(rootView);
                        }
                    });

                } catch (Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Utilisateur inconnu, veuillez réessayer", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), R.string.login_failure_message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void afficherSignalement(View rootView) {
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayoutSignalement2);

        Signalement.sortSignalementsByDateDescending(mesSignalements);
        for (Signalement s : mesSignalements) {
            for (Medicament m : medicamentSignales) {
                if (s.getCODE_CIS().equals(m.getCIS()))
                    afficherSignalement(rootView, s, m);

            }
        }
    }

    private void afficherSignalement(View rootView, Signalement signalement, Medicament medicament) {
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayoutSignalement2);
        Typeface customTypeface = ResourcesCompat.getFont(getContext(), R.font.police);

        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        TextView textViewCIP = new TextView(getContext());
        textViewCIP.setText(medicament.getCIP_13());
        textViewCIP.setTextColor(Color.WHITE);
        textViewCIP.setTypeface(customTypeface);

        textViewCIP.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewCIP.setPadding(0, 0, 0, 20);
        row.addView(textViewCIP);

        TextView textViewNom = new TextView(getContext());
        textViewNom.setText(medicament.getNom());
        textViewNom.setGravity(Gravity.CENTER_VERTICAL);
        textViewNom.setTextColor(Color.WHITE);
        textViewNom.setTypeface(customTypeface);

        textViewNom.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewNom.setPadding(0, 0, 0, 20);
        row.addView(textViewNom);

        TextView textViewDate = new TextView(getContext());
        textViewDate.setText(signalement.getDate());
        textViewDate.setTextColor(Color.WHITE);
        textViewDate.setTypeface(customTypeface);

        textViewDate.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewDate.setPadding(0, 0, 0, 20);
        row.addView(textViewDate);

        tableLayout.addView(row);
    }

    public void afficherNbSignalement(View rootView) {
        Map<String, Integer> nbSignalementsParMedicament = new HashMap<>();

        for (Signalement s : mesSignalements) {
            String codeCIS = s.getCODE_CIS();
            nbSignalementsParMedicament.put(codeCIS, nbSignalementsParMedicament.getOrDefault(codeCIS, 0) + 1);
        }

        afficherMedicamentsAvecNbSignalements(rootView, nbSignalementsParMedicament);
    }

    public void afficherMedicamentsAvecNbSignalements(View rootView, Map<String, Integer> nbSignalementsParMedicament) {
        medicamentSignales.sort((m1, m2) -> {
            Integer nbSignalements1 = nbSignalementsParMedicament.getOrDefault(m1.getCIS(), 0);
            Integer nbSignalements2 = nbSignalementsParMedicament.getOrDefault(m2.getCIS(), 0);
            return nbSignalements2.compareTo(nbSignalements1);
        });

        for (Medicament m : medicamentSignales) {
            Integer nbSignalements = nbSignalementsParMedicament.getOrDefault(m.getCIS(), 0);
            afficherSignalement(rootView, m, nbSignalements);
        }
    }

    public void afficherSignalement(View rootView, Medicament medicament, int nbSignalements) {
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayoutCompterSignalement);
        Typeface customTypeface = ResourcesCompat.getFont(getContext(), R.font.police);

        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        TextView textViewCIP = new TextView(getContext());
        textViewCIP.setText(medicament.getCIP_13());
        textViewCIP.setGravity(Gravity.CENTER);
        textViewCIP.setTextColor(Color.WHITE);
        textViewCIP.setTypeface(customTypeface);

        textViewCIP.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewCIP.setPadding(0, 0, 0, 20);
        row.addView(textViewCIP);

        TextView textViewNom = new TextView(getContext());
        textViewNom.setText(medicament.getNom());
        textViewNom.setGravity(Gravity.CENTER);
        textViewNom.setGravity(Gravity.CENTER);
        textViewNom.setTextColor(Color.WHITE);
        textViewNom.setTypeface(customTypeface);

        textViewNom.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewNom.setPadding(0, 0, 0, 20);
        row.addView(textViewNom);

        TextView textViewNb = new TextView(getContext());
        textViewNb.setText(String.valueOf(nbSignalements));
        textViewNb.setGravity(Gravity.CENTER);
        textViewNb.setTextColor(Color.WHITE);
        textViewNb.setTypeface(customTypeface);

        textViewNb.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewNb.setPadding(0, 0, 0, 20);
        row.addView(textViewNb);

        tableLayout.addView(row);
    }

    public void afficherSignalementByPatologie(View rootView) {
        Signalement.sortSignalementsByDateDescending(mesSignalements);
        for (Pathologie p : pathologies) {
            afficherSignalementByPatologie(rootView, p);
        }
    }

    private void afficherSignalementByPatologie(View rootView, Pathologie pathologie) {
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayoutSignalementPathologie);
        Typeface customTypeface = ResourcesCompat.getFont(getContext(), R.font.police);

        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        TextView textViewPathologie = new TextView(getContext());
        textViewPathologie.setText(pathologie.getPathologie());
        textViewPathologie.setTextColor(Color.WHITE);
        textViewPathologie.setTypeface(customTypeface);

        textViewPathologie.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewPathologie.setPadding(10, 10, 10, 10);
        textViewPathologie.setTextSize(8);
        textViewPathologie.setGravity(Gravity.CENTER);
        textViewPathologie.setBackgroundColor(Color.parseColor("#003d66"));
        row.addView(textViewPathologie);

        TextView textViewNbSignalements = new TextView(getContext());
        textViewNbSignalements.setText(String.valueOf(pathologie.getNb_signalements()));
        textViewNbSignalements.setGravity(Gravity.CENTER);
        textViewNbSignalements.setTextColor(Color.WHITE);
        textViewNbSignalements.setTypeface(customTypeface);

        textViewNbSignalements.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textViewNbSignalements.setPadding(10, 10, 10, 10);
        textViewNbSignalements.setTextSize(8);
        textViewNbSignalements.setBackgroundColor(Color.parseColor("#003d66"));
        row.addView(textViewNbSignalements);
        tableLayout.addView(row);
    }
}
