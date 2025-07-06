package com.example.weatherly;

import androidx.appcompat.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Déclarations des éléments de l'interface utilisateur et des données
    private EditText cityEditText; // Champ pour entrer le nom de la ville
    private ListView weatherListView; // Liste pour afficher les prévisions météo
    private WeatherListModel model; // Adaptateur pour gérer l'affichage des données dans la ListView
    private ImageButton buttonOk; // Bouton de recherche
    List<WeatherItem> data = new ArrayList<>(); // Liste pour stocker les prévisions météo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues depuis le layout
        cityEditText = findViewById(R.id.cityEditText);
        weatherListView = findViewById(R.id.weatherListView);
        buttonOk = findViewById(R.id.ButtonOK);

        // Configuration de l'adaptateur pour la ListView
        model = new WeatherListModel(getApplicationContext(), R.layout.list_item, data);
        weatherListView.setAdapter(model);

        // Configuration du clic sur le bouton de recherche
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("myLog", "Fetching weather..."); // Log pour indiquer le début de la récupération météo
                data.clear(); // Nettoyage des données existantes
                model.notifyDataSetChanged(); // Mise à jour de l'affichage

                String ville = cityEditText.getText().toString(); // Récupération du texte entré par l'utilisateur
                cityEditText.clearFocus(); // Retirer le focus du champ après l'entrée

                hideKeyboard(); // Cacher le clavier pour une meilleure expérience utilisateur

                // Initialisation de la file d'attente des requêtes réseau
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                Log.i("myLog", "City: " + ville); // Log pour afficher la ville recherchée

                // URL de l'API pour récupérer les prévisions météo
                String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + ville + "&appid=5860614df99d5fefd81809664e5a5ba6&units=metric&lang=fr";

                // Création de la requête HTTP GET
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("myLog", "Response: " + response); // Log de la réponse de l'API

                            // Analyse du JSON reçu
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray list = jsonObject.getJSONArray("list");

                            // Parcours des prévisions météo dans le tableau JSON
                            for (int i = 0; i < list.length(); i++) {
                                JSONObject forecast = list.getJSONObject(i);

                                // Conversion de l'horodatage en date lisible
                                long dt = forecast.getLong("dt");
                                Date date = new Date(dt * 1000);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy 'T' HH:mm");
                                String dateString = sdf.format(date);

                                // Extraction des informations météo
                                JSONArray weatherArray = forecast.getJSONArray("weather");
                                String weatherCondition = weatherArray.getJSONObject(0).getString("main");

                                JSONObject main = forecast.getJSONObject("main");
                                int tempMin = (int) main.getDouble("temp_min");
                                int tempMax = (int) main.getDouble("temp_max");
                                int humidite = main.getInt("humidity");
                                int pression = main.getInt("pressure");

                                // Création d'un objet WeatherItem pour chaque prévision
                                WeatherItem meteoItem = new WeatherItem();
                                meteoItem.date = dateString;
                                meteoItem.tempMin = tempMin;
                                meteoItem.tempMax = tempMax;
                                meteoItem.humidite = humidite;
                                meteoItem.pression = pression;
                                meteoItem.image = weatherCondition;

                                data.add(meteoItem); // Ajout à la liste des données
                            }

                            model.notifyDataSetChanged(); // Mise à jour de l'affichage

                        } catch (Exception e) {
                            Log.e("myLog", "Parsing error", e); // Log pour signaler une erreur d'analyse
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("myLog", "Connection error", error); // Log pour signaler une erreur réseau

                        // Gestion des erreurs selon le code de statut HTTP
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            if (statusCode == 404) {
                                Toast.makeText(MainActivity.this, "Ville non trouvée. Veuillez vérifier le nom.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Erreur de connexion. Code: " + statusCode, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Erreur de connexion. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                queue.add(stringRequest); // Ajout de la requête à la file d'attente
            }
        });
    }

    // Méthode pour cacher le clavier
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
