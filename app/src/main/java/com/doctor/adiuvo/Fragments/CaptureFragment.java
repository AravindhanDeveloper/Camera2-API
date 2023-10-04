package com.doctor.adiuvo.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doctor.adiuvo.R;
import com.google.android.material.button.MaterialButton;


public class CaptureFragment extends Fragment {


    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_capture, container, false);

        return view;
    }
}