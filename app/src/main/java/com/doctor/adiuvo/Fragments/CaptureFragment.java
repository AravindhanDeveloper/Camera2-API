package com.doctor.adiuvo.Fragments;

import static com.doctor.adiuvo.Activity.PhotoVideoRedirectActivity.mediaItems;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doctor.adiuvo.Adapter.MediaAdapter;
import com.doctor.adiuvo.Model.MediaItem;
import com.doctor.adiuvo.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;


public class CaptureFragment extends Fragment {

    private RecyclerView recyclerView;
    private MediaAdapter mediaAdapter;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_capture, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        if(mediaItems!=null){
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mediaAdapter = new MediaAdapter(getActivity(), mediaItems);

        recyclerView.setAdapter(mediaAdapter);}
        return view;
    }
}