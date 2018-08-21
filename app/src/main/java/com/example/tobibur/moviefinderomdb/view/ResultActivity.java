package com.example.tobibur.moviefinderomdb.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.tobibur.moviefinderomdb.R;
import com.example.tobibur.moviefinderomdb.model.ApiResponse;
import com.example.tobibur.moviefinderomdb.model.Movie;
import com.example.tobibur.moviefinderomdb.viewmodel.ResultViewModel;
import com.jetradar.desertplaceholder.DesertPlaceholder;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class ResultActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
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
    @BindView(R.id.main_layout) RelativeLayout mRelativeLayout;
    @BindView(R.id.placeholder) DesertPlaceholder desertPlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(ResultActivity.this);

        setSupportActionBar(toolbar);

        desertPlaceholder.setVisibility(View.VISIBLE);
        desertPlaceholder.setOnButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do stuff
                searchClicked();
            }
        });

        searchClicked();
    }

    private void searchClicked() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                //BuildMovieUrl(query);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                findMovie(query);

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

    private void findMovie(String query) {

        ResultViewModel model = ViewModelProviders.of(this).get(ResultViewModel.class);
        model.getData(query).observe(this, new Observer<ApiResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(@Nullable ApiResponse apiResponse) {
                mRelativeLayout.setVisibility(View.VISIBLE);
                desertPlaceholder.setVisibility(View.GONE);
                if(apiResponse == null){
                    Log.d(TAG, "onChanged: Handle Error");
                    mRelativeLayout.setVisibility(View.GONE);
                    desertPlaceholder.setVisibility(View.VISIBLE);
                }

                assert apiResponse != null;
                if(apiResponse.getError() == null) {
                    Movie result = apiResponse.getPosts();
                    assert result != null;
                    title_view.setText(result.getTitle());
                    rate_view.setText(getString(R.string.imdb) + " " + result.getImdbRating());
                    release_view.setText("Released: " + result.getReleased());
                    run_view.setText("Runtime: " + result.getRuntime());
                    genre_view.setText("Genre: " + result.getGenre());
                    actor_view.setText("Actors: " + result.getActors());
                    plot_view.setText("Plot: " + result.getPlot());
                    Glide.with(getApplicationContext()).load(result.getPoster()).into(mNetworkImageView);
                }else {
                    Throwable throwable = apiResponse.getError();
                    Toast.makeText(ResultActivity.this, "Error is "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressDialog.hide();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.search_menu_btn);
        searchView.setMenuItem(item);
        return true;
    }
}
