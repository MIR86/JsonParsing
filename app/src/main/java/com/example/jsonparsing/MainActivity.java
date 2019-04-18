package com.example.jsonparsing;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    private static String url = "https://api.myjson.com/bins/b6ym4";

    ArrayList<HashMap<String, String>> savoirsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        savoirsList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetSavoirs().execute();
    }

    private class GetSavoirs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Chargement..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Reponse serveur: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray Savoirs = jsonObj.getJSONArray("savoirs");

                    for (int i = 0; i < Savoirs.length(); i++) {
                        JSONObject c = Savoirs.getJSONObject(i);

                        String id = c.getString("id");
                        String titre = c.getString("titre");
                        String phrase = c.getString("phrase");



                        HashMap<String, String> inutile = new HashMap<>();

                        inutile.put("id", id);
                        inutile.put("titre", titre);
                        inutile.put("phrase", phrase);

                        savoirsList.add(inutile);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Erreure JSON: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Erreure JSON: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Le host destinaire n est pas joingnable");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "JSON inrecupérable.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, savoirsList,
                    R.layout.list_item, new String[]{"titre", "phrase",
                    }, new int[]{R.id.titre,
                    R.id.phrase});

            lv.setAdapter(adapter);
        }

    }
}
