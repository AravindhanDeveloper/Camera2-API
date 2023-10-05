package com.doctor.adiuvo.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.doctor.adiuvo.Model.MediaItem;
import com.doctor.adiuvo.R;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PhotoVideoRedirectActivity extends AppCompatActivity {
    public static  List<MediaItem> mediaItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photovideo_redirect);

        init();

    }

    VideoView videoView;

    private void init() {

        ImageView imgShow = findViewById(R.id.imgShow);
        MaterialButton discardBtn = findViewById(R.id.discard_btn);
        MaterialButton saveBtn = findViewById(R.id.save_btn);
        String path = getIntent().getStringExtra("PATH");

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getStringExtra("WHO").equalsIgnoreCase("Image")) {
                    MediaItem mediaItem = new MediaItem(path, true);
                    mediaItems.add(mediaItem);
                    Intent mIntent = new Intent(PhotoVideoRedirectActivity.this, MainActivity.class);
                    mIntent.putExtra("WHO", "photoView");

                    startActivity(mIntent);
                    finish();
                } else {
                    MediaItem mediaItem = new MediaItem(path, false);
                    mediaItems.add(mediaItem);
                    Intent mIntent = new Intent(PhotoVideoRedirectActivity.this, MainActivity.class);
                    mIntent.putExtra("WHO", "photoView");
                    startActivity(mIntent);
                }
            }
        });
        discardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                File fileToDelete = new File(path);
                Log.e("pathvalue", path);
                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        finish();
                        Toast.makeText(PhotoVideoRedirectActivity.this, "File discard successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PhotoVideoRedirectActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PhotoVideoRedirectActivity.this, "File does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });
        videoView = (VideoView) findViewById(R.id.vidShow);

        if (getIntent().getStringExtra("WHO").equalsIgnoreCase("Image")) {

            imgShow.setVisibility(View.VISIBLE);


            Glide.with(PhotoVideoRedirectActivity.this)
                    .load(getIntent().getStringExtra("PATH"))
                    .placeholder(R.drawable.ic_photo_cont)
                    .into(imgShow);


        } else {

            videoView.setVisibility(View.VISIBLE);
            try {
                videoView.setMediaController(null);
                videoView.setVideoURI(Uri.parse(getIntent().getStringExtra("PATH")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            videoView.requestFocus();
            //videoView.setZOrderOnTop(true);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {

                    videoView.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.start();
                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        super.onBackPressed();
    }
}
