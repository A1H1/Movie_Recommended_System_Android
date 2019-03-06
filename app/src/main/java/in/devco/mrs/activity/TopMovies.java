package in.devco.mrs.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import in.devco.mrs.R;
import in.devco.mrs.config.Config;
import in.devco.mrs.utils.ServerConnection;

public class TopMovies extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_movies);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Top Movies");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout linearLayout = findViewById(R.id.top_movie_ll);
        final ListView listView = findViewById(R.id.search_list_view);
        listView.setVisibility(View.GONE);
        SearchView searchView = findViewById(R.id.search);

        Toast.makeText(this, Config.SERVER_NAME, Toast.LENGTH_LONG).show();

        new AllMoviesListTask(this, listView, searchView).execute(Config.SERVER_NAME + "all");
        new TopMovieListTask(this, linearLayout).execute(Config.SERVER_NAME + "top");

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listView.setVisibility(View.GONE);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            showRadioButtonDialog();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showRadioButtonDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(TopMovies.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radio_genre);
        List<String> stringList=new ArrayList<>();  // here is list

        stringList.add("Romance");
        stringList.add("Thriller");
        stringList.add("Science Fiction");
        stringList.add("Mystery");
        stringList.add("Adventure");
        stringList.add("Action");
        stringList.add("Crime");
        stringList.add("Drama");
        stringList.add("Comedy");
        stringList.add("Fantasy");

        RadioGroup rg = dialog.findViewById(R.id.top_movie_radio_group);

        for(int i=0;i<stringList.size();i++){
            RadioButton rb=new RadioButton(TopMovies.this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(stringList.get(i));
            rg.addView(rb);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int childCount = group.getChildCount();
                for (int x = 0; x < childCount; x++) {
                    RadioButton btn = (RadioButton) group.getChildAt(x);
                    if (btn.getId() == checkedId) {
                        Intent intent = new Intent(TopMovies.this, GenreMovies.class);
                        intent.putExtra("gen", btn.getText().toString());
                        startActivity(intent);
                    }
                }
            }
        });

        dialog.show();

    }

    private static class TopMovieListTask extends AsyncTask<String, String, String> {
        Context context;
        LinearLayout linearLayout;

        TopMovieListTask(Context context, LinearLayout linearLayout) {
            this.context = context;
            this.linearLayout = linearLayout;
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

                    JSONArray genresList = result.getJSONObject(i).getJSONArray("genres");
                    StringBuilder g = new StringBuilder(genresList.getString(0));

                    for (int j=0; j<genresList.length(); j++)
                        g.append(", ").append(genresList.getString(j));

                    genres.setText("Genres: " + g);

                    linearLayout.addView(v);
                }
            } catch (Exception e) {
                Log.e("Url", e.toString());
            }
        }
    }

    private static class AllMoviesListTask extends AsyncTask<String, String, String> {
        private Context context;
        private ListView listView;
        private SearchView searchView;
        private List<String> arrayList= new ArrayList<>();
        ArrayAdapter<String > adapter;

        AllMoviesListTask(Context context, ListView listView, SearchView searchView) {
            this.context = context;
            this.listView = listView;
            this.searchView = searchView;
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

                for (int i=0; i<result.length(); i++) {
                    arrayList.add(result.getString(i));
                }

                adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(context, RecommendMovies.class);
                        intent.putExtra("title", (String) listView.getItemAtPosition(position));

                        context.startActivity(intent);
                    }
                });

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        if(arrayList.contains(query)){
                            adapter.getFilter().filter(query);
                        }else{
                            Toast.makeText(context, "No Match found",Toast.LENGTH_LONG).show();
                        }
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });
            } catch (Exception e) {
                Log.e("Url", e.toString());
            }
        }
    }
}
