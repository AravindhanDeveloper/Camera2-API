package com.doctor.adiuvo.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.doctor.adiuvo.Model.MediaItem;
import com.doctor.adiuvo.R;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private Context context;
    private List<MediaItem> mediaItems;

    public MediaAdapter(Context context, List<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaItem mediaItem = mediaItems.get(position);

        if (mediaItem.isImage()) {
            // Load image using Glide

            Glide.with(context)
                    .load(Uri.parse(mediaItem.getPath())) // Use Uri.parse to create a Uri from the path
                    .into(holder.imageView);
        } else {
            // Play video
            Uri videoUri = Uri.parse(mediaItem.getPath());
            holder.videoView.setVideoURI(videoUri);
            holder.videoView.start();
        }
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mediaItems.get(position).isImage() ? 0 : 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView); // Make sure the view IDs match your layout
            videoView = itemView.findViewById(R.id.videoView); // Make sure the view IDs match your layout
        }
    }
}
