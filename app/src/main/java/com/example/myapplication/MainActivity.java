package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });

        // Check the subscription status on app launch
        checkSubscriptionStatus(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Method to initiate the subscription status check
    public void checkSubscriptionStatus(Context context) {
        new GetAdvertisingIdTask(context).execute();
    }

    private class GetAdvertisingIdTask extends AsyncTask<Void, Void, String> {
        private Context context;

        public GetAdvertisingIdTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Fetch the GAID (Google Advertising ID)
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                String advertisingId = adInfo.getId();
                return advertisingId;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SubChecker", "Failed to retrieve Advertising ID: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String advertisingId) {
            if (advertisingId != null) {
                // Proceed to check the subscription status using the fetched GAID
                new CheckSubscriptionTask().execute(advertisingId);
            } else {
                Log.e("SubChecker", "Failed to retrieve Advertising ID");
            }
        }
    }

    private class CheckSubscriptionTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String advertisingId = params[0];
            //String urlString = "https://play-unite.com/checkSubscription?deviceIdentifier=" + advertisingId;
            String urlString = "https://play-unite.com/checkSubscription?deviceIdentifier=0387519f-2242-4477-bea0-296327074f6";
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Log the entire response for debugging
                    String responseString = response.toString().trim();
                    Log.i("SubChecker", "HTTP Response: " + responseString);

                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(responseString);
                    boolean isSubscribed = jsonResponse.getBoolean("isSubscribed");

                    return isSubscribed;
                } else {
                    Log.e("SubChecker", "HTTP Error Response Code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SubChecker", "Exception during HTTP request: " + e.getMessage());
            }
            return false; // Return false if there was an error or the response was not true
        }

        @Override
        protected void onPostExecute(Boolean isSubscribed) {
            // Handle the result here
            if (isSubscribed) {
                Log.i("SubChecker", "Subscription is active");
            } else {
                Log.i("SubChecker", "No active subscription");
            }
        }
    }
}
