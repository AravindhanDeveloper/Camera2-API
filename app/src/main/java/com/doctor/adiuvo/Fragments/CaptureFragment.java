package com.doctor.adiuvo.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.doctor.adiuvo.Activity.PhotoVideoRedirectActivity;
import com.doctor.adiuvo.Activity.RunTimePermission;
import com.doctor.adiuvo.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class CaptureFragment extends Fragment implements ImageAnalysis.Analyzer, View.OnClickListener  {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private ImageView bRecord,flashlightButton,imgSwipeCamera,closeImg;
    private ImageView bCapture;
    private long recordingStartTimeMillis = 0;
    private Handler timerHandler = new Handler();
    private RunTimePermission runTimePermission;
    private MaterialTextView timerTextView;
    private boolean isFlashlightOn = false, isRecording = true;
    private Camera camera;
    private Button captureImageBtn,captureVideoBtn;
    private CameraSelector currentCameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_capture, container, false);
        timerTextView = view.findViewById(R.id.textCounter);

        runTimePermission = new RunTimePermission(getActivity());
        runTimePermission.requestPermission(new String[]{Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, new RunTimePermission.RunTimePermissionListener() {

            @Override
            public void permissionGranted() {
                // First we need to check availability of play services

            }

            @Override
            public void permissionDenied() {
            }
        });
        previewView = view.findViewById(R.id.previewView);
        bCapture = view.findViewById(R.id.imgCapture);
        closeImg = view.findViewById(R.id.close_img);

        bRecord = view.findViewById(R.id.vedCapture);
        captureVideoBtn= view.findViewById(R.id.captureVideoBtn);
        captureImageBtn= view.findViewById(R.id.captureImageBtn);
        bCapture.setOnClickListener(this);
        bRecord.setOnClickListener(this);
        captureImageBtn.setOnClickListener(this);
        captureVideoBtn.setOnClickListener(this);
        flashlightButton = view.findViewById(R.id.flashlightButton);
        imgSwipeCamera = view.findViewById(R.id.imgChangeCamera);
        imgSwipeCamera.setOnClickListener(this);
        flashlightButton.setOnClickListener(this);

        startCamera();
        cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        return view;
    }
    private void switchCamera() {
        if (currentCameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            currentCameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
        } else {
            currentCameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        }
        startCamera();

    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                    camera = cameraProvider.bindToLifecycle((LifecycleOwner) getActivity(), cameraSelector);

                } catch (ExecutionException | InterruptedException e) {
                    // Handle exceptions
                }
            }
        }, ContextCompat.getMainExecutor(getActivity()));
    }

    private void toggleFlashlight() {
        try {
            if (camera != null) {
                CameraInfo cameraInfo = camera.getCameraInfo();
                if (cameraInfo.hasFlashUnit()) {
                    if (isFlashlightOn) {
                        // Turn off the flashlight
                        flashlightButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_flash_off));
                        camera.getCameraControl().enableTorch(false);
                        isFlashlightOn = false;
                    } else {
                        // Turn on the flashlight
                        flashlightButton.setImageDrawable(getActivity().getDrawable(R.drawable.ic_flash_on));

                        camera.getCameraControl().enableTorch(true);
                        isFlashlightOn = true;
                    }
                }
            }
        } catch (Exception e) {
            // Handle exceptions
        }
    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(getActivity());
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Image capture use case
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // Video capture use case
        videoCapture = new VideoCapture.Builder()
                .setVideoFrameRate(30)
                .build();

        // Image analysis use case
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(getExecutor(), this);

        //bind to lifecycle:
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture, videoCapture);
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        // image processing here for the current frame
        Log.d("TAG", "analyze: got the frame at: " + image.getImageInfo().getTimestamp());
        image.close();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgCapture) {
            capturePhoto();

        } if (view.getId() == R.id.imgChangeCamera) {
            switchCamera();
            Toast.makeText(getActivity(), "Failed to retrieve saved photo URI.", Toast.LENGTH_SHORT).show();


        } if (view.getId() == R.id.flashlightButton) {
            toggleFlashlight();

        }
        if (view.getId() == R.id.captureImageBtn) {
            ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.theme));
            ColorStateList black = ColorStateList.valueOf(getResources().getColor(R.color.black));
            captureVideoBtn.setBackgroundTintList(black);
            captureImageBtn.setBackgroundTintList(colorStateList);
            bCapture.setVisibility(View.VISIBLE);
            bRecord.setVisibility(View.GONE);

        }   if (view.getId() == R.id.captureVideoBtn) {
            ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.theme));
            ColorStateList black = ColorStateList.valueOf(getResources().getColor(R.color.black));
            captureVideoBtn.setBackgroundTintList(colorStateList);
            captureImageBtn.setBackgroundTintList(black);
            bCapture.setVisibility(View.GONE);
            bRecord.setVisibility(View.VISIBLE);
        }
        if (view.getId() == R.id.vedCapture) {
            if (isRecording) {
                isRecording = false;
                recordVideo();
            } else {
                isRecording = true;
                stopTimer();
                videoCapture.stopRecording();
            }
        }

    }


    @SuppressLint("RestrictedApi")
    private void recordVideo() {
        if (videoCapture != null) {

            long timestamp = System.currentTimeMillis();

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");

            try {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                recordingStartTimeMillis = SystemClock.elapsedRealtime();
                startTimer();
                videoCapture.startRecording(
                        new VideoCapture.OutputFileOptions.Builder(
                                getActivity().getContentResolver(),
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                        ).build(),
                        getExecutor(),
                        new VideoCapture.OnVideoSavedCallback() {
                            @Override
                            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                                Uri savedImageUri = outputFileResults.getSavedUri();
                                if (savedImageUri != null) {
                                    // Create an intent to send the image path to another activity
                                    Intent mIntent = new Intent(getActivity(), PhotoVideoRedirectActivity.class);
                                    mIntent.putExtra("PATH", savedImageUri.toString()); // Pass the URI as a string
                                    mIntent.putExtra("THUMB", savedImageUri.toString()); // You can adjust this as needed
                                    mIntent.putExtra("WHO", "Video");
                                    startActivity(mIntent);
                                } else {
                                    Toast.makeText(getActivity(), "Failed to retrieve saved photo URI.", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                                Toast.makeText(getActivity(), "Error saving video: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void startTimer() {
        timerTextView.setVisibility(View.VISIBLE);
        timerHandler.postDelayed(updateTimerRunnable, 0);
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(updateTimerRunnable);
        timerTextView.setVisibility(View.GONE);
    }

    private Runnable updateTimerRunnable = new Runnable() {
        public void run() {
            long elapsedTimeMillis = SystemClock.elapsedRealtime() - recordingStartTimeMillis;
            int seconds = (int) (elapsedTimeMillis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            timerTextView.setText(timeFormatted);

            timerHandler.postDelayed(this, 1000); // Update the timer every second
        }
    };

    private void capturePhoto() {
        long timestamp = System.currentTimeMillis();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");


        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getActivity().getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri savedImageUri = outputFileResults.getSavedUri();
                        if (savedImageUri != null) {
                            Intent mIntent = new Intent(getActivity(), PhotoVideoRedirectActivity.class);
                            mIntent.putExtra("PATH", savedImageUri.toString()); // Pass the URI as a string
                            mIntent.putExtra("THUMB", savedImageUri.toString()); // You can adjust this as needed
                            mIntent.putExtra("WHO", "Image");
                            startActivity(mIntent);
                        } else {
                            Toast.makeText(getActivity(), "Failed to retrieve saved photo URI.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(getActivity(), "Error saving photo: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }
}