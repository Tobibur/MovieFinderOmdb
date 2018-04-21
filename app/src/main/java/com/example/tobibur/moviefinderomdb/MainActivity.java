package com.example.tobibur.moviefinderomdb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

    EditText movie;
    Button search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declaration
        movie = findViewById(R.id.movie);
        search = findViewById(R.id.search_bttn);

        //On Click Search Button
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String movie_name = movie.getText().toString();
                Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                intent.putExtra("movie",movie_name);
                startActivity(intent);
            }
        });
    }
}
