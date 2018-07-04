package com.example.tobibur.moviefinderomdb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class ResultActivity extends AppCompatActivity {

    String MovieName,URL="http://www.omdbapi.com/?t=The%20Walk&apikey=35ba1dc9";
    ProgressDialog progressDialog;
    private NetworkImageView mNetworkImageView;
    private ImageLoader mImageLoader;
    TextView title_view,rate_view,release_view,run_view,genre_view,actor_view,plot_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        MovieName = getIntent().getStringExtra("movie");
        progressDialog = new ProgressDialog(ResultActivity.this);
        mNetworkImageView = findViewById(R.id
                .networkImageView);
        title_view = findViewById(R.id.title);
        rate_view = findViewById(R.id.rating);
        release_view = findViewById(R.id.released);
        run_view = findViewById(R.id.runtime);
        genre_view = findViewById(R.id.genre);
        plot_view = findViewById(R.id.plot);
        actor_view = findViewById(R.id.actor);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.omdbapi.com")
                .appendPath("")
                .appendQueryParameter("t", MovieName)
                .appendQueryParameter("apikey", "35ba1dc9");
        String myUrl = builder.build().toString();
        volleyStringRequst(myUrl);
    }

    public void volleyStringRequst(String url){

        String  REQUEST_TAG = "com.example.tobibur.moviefinderomdb";
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        // Instantiate the RequestQueue.
        mImageLoader = AppSingleton.getInstance(this.getApplicationContext())
                .getImageLoader();
        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String img_url = jsonObject.getString("Poster");
                    String title = jsonObject.getString("Title");
                    String year = jsonObject.getString("Year");
                    String rating = jsonObject.getString("imdbRating");
                    String released = jsonObject.getString("Released");
                    String runtime = jsonObject.getString("Runtime");
                    String genre = jsonObject.getString("Genre");
                    String actor = jsonObject.getString("Actors");
                    String plot = jsonObject.getString("Plot");
                    mImageLoader.get(img_url, ImageLoader.getImageListener(mNetworkImageView,R.mipmap.ic_launcher,R.mipmap.ic_launcher));
                    mNetworkImageView.setImageUrl(img_url, mImageLoader);
                    String titleYear=title+" ("+year+")";
                    String rated =getString(R.string.imdb)+rating;
                    title_view.setText(titleYear);
                    rate_view.setText(rated);
                    release_view.setText("Released: "+released);
                    run_view.setText("Runtime: "+runtime);
                    genre_view.setText("Genre: "+genre);
                    actor_view.setText("Actors: "+actor);
                    plot_view.setText("Plot: "+plot);

                } catch (JSONException e) {
                    Toast.makeText(ResultActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                progressDialog.hide();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressDialog.hide();
            }
        });
        // Adding String request to request queue
        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, REQUEST_TAG);
    }
}
