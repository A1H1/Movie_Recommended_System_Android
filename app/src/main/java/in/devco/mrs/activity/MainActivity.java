package in.devco.mrs.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import in.devco.mrs.R;
import in.devco.mrs.config.Config;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText serverName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverName = findViewById(R.id.main_activity_server_name);

        findViewById(R.id.main_activity_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String server = serverName.getText().toString();

        if (v.getId() == R.id.main_activity_submit) {
            if (server.equals(""))
                Toast.makeText(this, "Server Name can't be empty", Toast.LENGTH_LONG).show();
            else {
                Config.SERVER_NAME = "http://" + server + ":8000/";
                startActivity(new Intent(this, TopMovies.class));
                finish();
            }
        }
    }
}
