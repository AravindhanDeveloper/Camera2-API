package com.doctor.adiuvo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.doctor.adiuvo.Adapter.MediaAdapter;
import com.doctor.adiuvo.Model.MediaItem;
import com.doctor.adiuvo.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<MediaItem> mediaItems = scanMediaFiles(); // Implement media scanning method
        mediaAdapter = new MediaAdapter(this, mediaItems);

        recyclerView.setAdapter(mediaAdapter);
    }

    // Implement a method to scan media files and create a list of MediaItem objects
    private List<MediaItem> scanMediaFiles() {
        List<MediaItem> mediaItems = new ArrayList<>();

        // Define the projection to retrieve specific columns from the MediaStore
        String[] projection = {
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE
        };

        // Specify the query to fetch both images and videos
        Uri mediaUri = MediaStore.Files.getContentUri("external");

        // Define the selection criteria (for both images and videos)
        String selection = "(" +
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = ? OR " +
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?) AND " +
                MediaStore.MediaColumns.SIZE + " > 0"; // Exclude zero-sized files

        String[] selectionArgs = new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        };

        // Perform a query on the MediaStore
        try (Cursor cursor = this.getContentResolver().query(
                mediaUri,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                int mimeTypeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE);

                do {
                    // Get the media details from the cursor
                    String path = cursor.getString(dataColumnIndex);
                    String mimeType = cursor.getString(mimeTypeColumnIndex);
                    // Determine if it's an image or video based on MIME type
                    boolean isImage = mimeType.startsWith("image");

                    // Create a MediaItem object

                    MediaItem mediaItem = new MediaItem(path, isImage);
                    mediaItems.add(mediaItem);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mediaItems;
    }
}

