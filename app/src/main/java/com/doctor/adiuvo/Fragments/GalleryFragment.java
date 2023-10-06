package com.doctor.adiuvo.Fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.doctor.adiuvo.Adapter.MediaAdapter;
import com.doctor.adiuvo.Model.MediaItem;
import com.doctor.adiuvo.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;


public class GalleryFragment extends Fragment {
    View view;
    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    private final static int MY_READ_PERMISSION_CODE = 101;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        //Check For Permission
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, MY_READ_PERMISSION_CODE);

        }else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            List<MediaItem> mediaItems = scanMediaFiles(); // Implement media scanning method
            mediaAdapter = new MediaAdapter(getActivity(), mediaItems);

            recyclerView.setAdapter(mediaAdapter);        }


        return view;
    }

    private List<MediaItem> scanMediaFiles() {
        List<MediaItem> mediaItems = new ArrayList<>();

        String[] projection = {
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.MEDIA_TYPE // Added to distinguish between image and video
        };

        Uri mediaUri = MediaStore.Files.getContentUri("external");

        String selection = "(" +
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?) AND " +
                MediaStore.MediaColumns.SIZE + " > 0";

        String[] selectionArgs = new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        };

        try (Cursor cursor = getActivity().getContentResolver().query(
                mediaUri,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                int mediaTypeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);

                do {
                    String path = cursor.getString(dataColumnIndex);
                    int mediaType = cursor.getInt(mediaTypeColumnIndex);

                    // Determine if it's an image or video based on MEDIA_TYPE
                    boolean isImage = (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);

                    MediaItem mediaItem = new MediaItem(path, isImage);
                    mediaItems.add(mediaItem);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mediaItems;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_READ_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getActivity(), "Read External Storage Permission granted", Toast.LENGTH_SHORT).show();
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                List<MediaItem> mediaItems = scanMediaFiles(); // Implement media scanning method
                mediaAdapter = new MediaAdapter(getActivity(), mediaItems);

                recyclerView.setAdapter(mediaAdapter);            }else {
                Toast.makeText(getActivity(), "Read External Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}