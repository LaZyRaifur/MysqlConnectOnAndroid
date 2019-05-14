package com.lazycoder.remotemysqlconnect.MovingListActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lazycoder.remotemysqlconnect.MovieDeleteUpdate.MovieUpdateDeleteActivity;
import com.lazycoder.remotemysqlconnect.R;
import com.lazycoder.remotemysqlconnect.helper.CheckNetworkStatus;
import com.lazycoder.remotemysqlconnect.helper.HttpJsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MovieListingActivity extends AppCompatActivity {


    private static final String KEY_SUCCESS = "success";
    private static final String KEY_DATA = "data";
    private static final String KEY_MOVIE_ID = "movie_id";
    private static final String KEY_MOVIE_NAME = "movie_name";
    private static final String BASE_URL = "http://192.168.0.102/movies/";
    private ArrayList<HashMap<String,String>> movieList;
    private ListView movieListView;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_listing);

        movieListView = (ListView)findViewById(R.id.movieList);
        new FetchMoviesAsynTask().execute();
    }

    /**
     * Fetches the list of movies from the server
     */
    private class FetchMoviesAsynTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Display progress bar
            pDialog = new ProgressDialog(MovieListingActivity.this);
            pDialog.setMessage("Loading Movies,Please wait!!!!");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpJsonParser httpJsonParser = new HttpJsonParser();
            JSONObject jsonObject = httpJsonParser.makeHttpRequest(BASE_URL+"fetch_all_movies.php","GET",null);

            try {
                int success = jsonObject.getInt(KEY_SUCCESS);
                JSONArray movies;

                if (success == 1){
                    movieList = new ArrayList<>();
                    movies = jsonObject.getJSONArray(KEY_DATA);

                    //Iterate through the response and populate movie list
                    for (int i = 0;i<movies.length();i++){
                        JSONObject movie = movies.getJSONObject(i);
                        Integer movieId = movie.getInt(KEY_MOVIE_ID);
                        String movieName = movie.getString(KEY_MOVIE_NAME);
                        HashMap<String,String>map = new HashMap<String, String>();
                        map.put(KEY_MOVIE_ID,movieId.toString());
                        map.put(KEY_MOVIE_NAME,movieName);
                        movieList.add(map);

                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
                    }

                    protected void onPostExecute(String result){
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateMovieList();
                }
            });
                    }
    }

    /**
     * Updating parsed JSON data into ListView
     * */

    private void populateMovieList() {
        ListAdapter adapter = new SimpleAdapter(MovieListingActivity.this
        ,movieList,R.layout.list_item,new String[]{KEY_MOVIE_ID,KEY_MOVIE_NAME},
                new int[]{R.id.movieId,R.id.movieName});


        //updating listView
        movieListView.setAdapter(adapter);

        //Call MovieAdapterDeleteActivity when a movie is clicked
        movieListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())){
                    String movieId = ((TextView)view.findViewById(R.id.movieId)).getText().toString();
                    Intent intent = new Intent(getApplicationContext(), MovieUpdateDeleteActivity.class);
                    intent.putExtra(KEY_MOVIE_ID,movieId);
                    startActivityForResult(intent,20);

                }else {
                    Toast.makeText(MovieListingActivity.this,"Unable to connect to internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20){
            //if the result code is 20 that means that the user
            // has deleted/updated the movie
            //so refresh the movie listing
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}
