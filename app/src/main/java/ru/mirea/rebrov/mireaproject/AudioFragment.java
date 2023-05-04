package ru.mirea.rebrov.mireaproject;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import ru.mirea.rebrov.mireaproject.databinding.FragmentAudioBinding;
import ru.mirea.rebrov.mireaproject.databinding.FragmentCameraBinding;


public class AudioFragment extends Fragment {
    private FragmentAudioBinding binding;
    private static final int REQUEST_CODE_PERMISSION = 200;
    private final String TAG = MainActivity.class.getSimpleName();
    private boolean isWork;
    private String fileName = null;
    private Button recordButton = null;
    private Button playButton = null;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private String recordFilePath = null;
    boolean isStartRecording = true;
    boolean isStartPlaying = true;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAudioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recordButton = binding.recordButton;
        playButton = binding.playButton;
        playButton.setEnabled(false);
        recordFilePath = (new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC), "/audiorecordtest.3gp")).getAbsolutePath();

        int audioRecordPermissionStatus = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO);
        int storagePermissionStatus = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (android.os.Build.VERSION.SDK_INT > 32) {
            if (audioRecordPermissionStatus == PackageManager.PERMISSION_GRANTED) {
                isWork = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);
            }
        } else {
            if (audioRecordPermissionStatus == PackageManager.PERMISSION_GRANTED && storagePermissionStatus == PackageManager.PERMISSION_GRANTED) {
                isWork = true;
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartRecording) {
                    recordButton.setText("Stop recording");
                    playButton.setEnabled(false);
                    startRecording();
                } else {
                    recordButton.setText("Start recording");
                    playButton.setEnabled(true);
                    stopRecording();
                }
                mStartRecording = !mStartRecording;
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStartPlaying) {
                    playButton.setText("Stop playing");
                    recordButton.setEnabled(false);
                    startPlaying();
                } else {
                    playButton.setText("Start playing");
                    recordButton.setEnabled(false);
                    stopPlaying();
                }
                mStartPlaying = !mStartPlaying;

            }

        });
        return root;
    }
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(recordFilePath);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }
}