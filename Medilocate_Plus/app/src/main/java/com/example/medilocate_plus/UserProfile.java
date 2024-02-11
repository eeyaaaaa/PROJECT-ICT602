package com.example.medilocate_plus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.BreakIterator;

public class UserProfile extends AppCompatActivity {

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // User is not authenticated, redirect to login
            Intent intent = new Intent(UserProfile.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            userID = user.getUid();

            final TextView nameTextView = findViewById(R.id.nameTV);
            final TextView emailTextView = findViewById(R.id.emailTV);
            final ImageView profileImageView = findViewById(R.id.profileIV);

            // Replace the following URL with the actual URL of your server-side API
            String serverUrl = "https://your-server-url.com/getUserProfile";

            // Execute the AsyncTask to get user profile details
            new GetUserProfileTask().execute(serverUrl, userID);

            Button signOut = findViewById(R.id.signout);
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Sign out from Firebase
                    FirebaseAuth.getInstance().signOut();

                    // Redirect to login
                    Intent intent = new Intent(UserProfile.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private class GetUserProfileTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String serverUrl = params[0];
            String userID = params[1];

            try {
                URL url = new URL(serverUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Pass the user ID to the server
                OutputStream os = connection.getOutputStream();
                os.write(("userID=" + userID).getBytes());
                os.flush();
                os.close();

                // Read the response from the server
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                return response.toString();

            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the server response and update the UI accordingly
            if (!"error".equals(result)) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    String name = jsonObject.getString("name");
                    String email = jsonObject.getString("email");
                    String profileUrl = jsonObject.getString("profileUrl");

                    // Access UI elements from the main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BreakIterator nameTextView;
                            nameTextView = null;
                            nameTextView.setText(name);
                            BreakIterator emailTextView;
                            emailTextView = null;
                            emailTextView.setText(email);

                            // Use an image loading library (e.g., Picasso) to load the profile picture
                            ImageView profileImageView = null;
                            Picasso.get().load(profileUrl).into(profileImageView);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle error
                Toast.makeText(UserProfile.this, "Error fetching user profile", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



