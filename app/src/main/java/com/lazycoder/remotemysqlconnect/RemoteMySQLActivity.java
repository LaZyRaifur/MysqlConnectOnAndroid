package com.lazycoder.remotemysqlconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lazycoder.remotemysqlconnect.AddMovie.AddMovieActivity;
import com.lazycoder.remotemysqlconnect.MovingListActivity.MovieListingActivity;
import com.lazycoder.remotemysqlconnect.helper.CheckNetworkStatus;

public class RemoteMySQLActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_my_sql);

        Button viewAllBtn= (Button)findViewById(R.id.viewAllBtn);
        Button addNewBtn = (Button)findViewById(R.id.addNewBtn);

        viewAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext(),
                            MovieListingActivity.class);
                    startActivity(intent);
                }else {
                    //Display error message if not connected to internet
                    Toast.makeText(RemoteMySQLActivity.this, "Unable to connect to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check for network connectivity
                if (CheckNetworkStatus.isNetworkAvailable(getApplicationContext()))
                {
                    Intent intent = new Intent(getApplicationContext(),
                            AddMovieActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
