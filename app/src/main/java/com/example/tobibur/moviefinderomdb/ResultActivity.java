package com.example.tobibur.moviefinderomdb;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class ResultActivity extends AppCompatActivity {

    ContentLoadingProgressBar progressDialog;

    @BindView(R.id.networkImageView)
    ImageView mNetworkImageView;
    @BindView(R.id.title) TextView title_view;
    @BindView(R.id.rating) TextView rate_view;
    @BindView(R.id.released) TextView release_view;
    @BindView(R.id.runtime) TextView run_view;
    @BindView(R.id.genre) TextView genre_view;
    @BindView(R.id.actor) TextView actor_view;
    @BindView(R.id.plot) TextView plot_view;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.search_view) MaterialSearchView searchView;

    SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mSessionManager = new SessionManager(this);
        progressDialog = new ContentLoadingProgressBar(ResultActivity.this);

        if(mSessionManager.isLoggedIn()){
            HashMap<String, String> map = mSessionManager.getUserDetails();
            String value = map.get(SessionManager.KEY_RESULT);
            fillData(value);
        }else {
            customDialogCall();
        }

        searchClicked();
    }

    private void customDialogCall() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog, null);
        final EditText mEditText = alertLayout.findViewById(R.id.dMessage);
        new AlertDialog.Builder(this)
                .setTitle("Search Movie")
                .setView(alertLayout)
                .setCancelable(false)
                .setNegativeButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BuildMovieUrl(mEditText.getText().toString());
                    }
                })
                .setPositiveButton("Close", null)
                .show();
    }

    private void searchClicked() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                BuildMovieUrl(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        searchView.setVoiceSearch(true); //or false
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void BuildMovieUrl(String movieName) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("www.omdbapi.com")
                .appendPath("")
                .appendQueryParameter("t", movieName)
                .appendQueryParameter("apikey", "35ba1dc9");
        String myUrl = builder.build().toString();
        Log.d(TAG, "BuildMovieUrl: "+myUrl);
        volleyStringRequest(myUrl);
    }

    public void volleyStringRequest(String url){

        String  REQUEST_TAG = "com.example.tobibur.moviefinderomdb";
        //progressDialog.st("Loading...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);
                mSessionManager.createMovieSession(response);
                fillData(response);

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

    @SuppressLint("SetTextI18n")
    private void fillData(String response) {
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

            Glide.with(this).load(img_url).into(mNetworkImageView);

            String titleYear=title+" ("+year+")";
            String rated =getString(R.string.imdb)+" "+rating;
            title_view.setText(titleYear);
            rate_view.setText(rated);
            release_view.setText("Released: "+released);
            run_view.setText("Runtime: "+runtime);
            genre_view.setText("Genre: "+genre);
            actor_view.setText("Actors: "+actor);
            plot_view.setText("Plot: "+plot);

        } catch (JSONException e) {
            Toast.makeText(ResultActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onResponse: JsonException triggered");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.search_menu_btn);
        searchView.setMenuItem(item);
        return true;
    }
}
