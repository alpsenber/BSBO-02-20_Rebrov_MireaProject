package ru.mirea.rebrov.mireaproject;

import static android.Manifest.permission.FOREGROUND_SERVICE;
import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.mirea.rebrov.mireaproject.databinding.FragmentMusicPlayerBinding;


public class MusicPlayerFragment extends Fragment {

    private FragmentMusicPlayerBinding binding;
    private int play_status = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMusicPlayerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (ContextCompat.checkSelfPermission(root.getContext(), POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            Log.d(MainActivity.class.getSimpleName().toString(), "Разрешения получены");
        } else {
            Log.d(MainActivity.class.getSimpleName().toString(), "Нет разрешений!");

            ActivityCompat.requestPermissions(getActivity(), new String[]{POST_NOTIFICATIONS, FOREGROUND_SERVICE}, 200);

        }
        binding.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(play_status == 0)
                {
                    Intent serviceIntent = new Intent(root.getContext(), MusicService.class);
                    ContextCompat.startForegroundService(root.getContext(), serviceIntent);
                    play_status = 1;
                    binding.imageButton.setImageResource(R.drawable.baseline_pause_24);
                }
                else
                {
                    root.getContext().stopService(new Intent(root.getContext(), MusicService.class));
                    play_status = 0;
                    binding.imageButton.setImageResource(R.drawable.baseline_play_arrow_24);
                }
            }
        });
        return root;
    }
}