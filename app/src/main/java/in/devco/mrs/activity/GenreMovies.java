package in.devco.mrs.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

import in.devco.mrs.R;
import in.devco.mrs.config.Config;
import in.devco.mrs.utils.ServerConnection;

public class GenreMovies extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_movies);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getIntent().getStringExtra("gen") + " Movies");

        LinearLayout linearLayout = findViewById(R.id.genre_movie_ll);

        new GenreMovieListTask(this, linearLayout, getIntent().getStringExtra("gen")).execute(Config.SERVER_NAME + "genre");
    }

    private static class GenreMovieListTask extends AsyncTask<String, String, String> {
        Context context;
        LinearLayout linearLayout;
        String gen;

        GenreMovieListTask(Context context, LinearLayout linearLayout, String gen) {
            this.context = context;
            this.linearLayout = linearLayout;
            this.gen = gen;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                HashMap<String, String> hm = new HashMap<>();
                hm.put("test", "ok");
                hm.put("gen", gen);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(ServerConnection.getPostDataString(hm));

                writer.flush();
                writer.close();
                os.close();
                urlConnection.connect();

                InputStream stream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return buffer.toString();
            } catch (Exception e) {
                Log.e("Url", "background" + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONArray result = new JSONArray(s);

                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int i=0; i<result.length(); i++) {
                    View v = layoutInflater.inflate(R.layout.movie_detail, linearLayout, false);

                    TextView title = v.findViewById(R.id.movie_name);
                    TextView genres = v.findViewById(R.id.movie_genres);
                    TextView rating = v.findViewById(R.id.movie_rating);
                    TextView year = v.findViewById(R.id.movie_year);

                    title.setText(result.getJSONObject(i).getString("title"));
                    rating.setText("Rating: " + result.getJSONObject(i).getInt("vote_average"));
                    year.setText("Year: " + result.getJSONObject(i).getInt("year"));

                    genres.setVisibility(View.GONE);

                    linearLayout.addView(v);
                }
            } catch (Exception e) {
                Log.e("Url", e.toString());
            }
        }
    }
}
