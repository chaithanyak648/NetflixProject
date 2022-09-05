package com.example.netflix.Mainscreens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.netflix.R;

public class MovieDetails extends AppCompatActivity {
ImageView movieimage;
TextView moviename;
Button play;
String name,image,fileurl,moviesid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details2);
        getSupportActionBar().hide();
        movieimage=findViewById(R.id.imagedetails);
        moviename=findViewById(R.id.moviename);
        play=findViewById(R.id.playbutton);
        moviesid=getIntent().getStringExtra("movieId");
        name=getIntent().getStringExtra("movieName");
        image=getIntent().getStringExtra("movieImageUrl");
        fileurl=getIntent().getStringExtra("movieFile");
        Glide.with(this).load(image).into(movieimage);
        moviename.setText(name);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MovieDetails.this,VideoPlayer.class);
                i.putExtra("url",fileurl);
                startActivity(i);
            }
        });





    }
}