package com.doctor.adiuvo.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doctor.adiuvo.Activity.CameraActivity;
import com.doctor.adiuvo.R;
import com.google.android.material.button.MaterialButton;


public class GalleryFragment extends Fragment {
    View view;
    MaterialButton uploadBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_gallery, container, false);
        uploadBtn=view.findViewById(R.id.upload_btn);

uploadBtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(getActivity(), CameraActivity.class));

    }
});
        return view;
}}