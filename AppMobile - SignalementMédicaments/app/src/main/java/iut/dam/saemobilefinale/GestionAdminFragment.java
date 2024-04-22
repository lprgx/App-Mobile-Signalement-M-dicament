package iut.dam.saemobilefinale;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GestionAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GestionAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GestionAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GestionAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GestionAdminFragment newInstance(String param1, String param2) {
        GestionAdminFragment fragment = new GestionAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gestion_admin, container, false);

        EditText editTextFirstName = rootView.findViewById(R.id.editTextFirstName);
        EditText editTextLastName = rootView.findViewById(R.id.editTextLastName);
        EditText editTextEmail = rootView.findViewById(R.id.editTextEmail);
        EditText editTextPassword = rootView.findViewById(R.id.editTextPassword);
        EditText editTextPoste = rootView.findViewById(R.id.editTextPoste);
        Button buttonRegister = rootView.findViewById(R.id.buttonRegister);

        buttonRegister.setBackgroundColor(Color.parseColor("#FFFFFF"));
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRegister.setBackgroundColor(Color.parseColor("#003d66"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonRegister.setBackgroundColor(Color.parseColor("#FFFFFF")); // Couleur d'origine
                    }
                }, 150); // Délai en millisecondes
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String poste = editTextPoste.getText().toString();

                if (!(validateFields(firstName, lastName, email, password, poste)))
                    return;

                String urlString = "http://192.168.1.13/Pharmacie/register.php?email=" + email + "&password=" + password + "&firstname=" + firstName + "&lastname=" + lastName + "&designation=" + poste ;
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(urlString)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String responseBody = response.body().string();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (responseBody.equals("\"Admin registered successfully!\"")) {
                                    Toast.makeText(getContext(), responseBody, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getContext(), LoginActivity.class));
                                    getActivity().finish();
                                } else {
                                    Toast.makeText(getContext(), R.string.email_already_exits, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), R.string.registration_failure_message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        return rootView;
    }

    private boolean validateFields(String firstName, String lastName, String email, String password, String poste) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || poste.isEmpty()) {
            Toast.makeText(getContext(), R.string.empty_field, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), R.string.email_not_valid, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*\\d.*")) {
            Toast.makeText(getContext(), R.string.invalid_password_format, Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }
}