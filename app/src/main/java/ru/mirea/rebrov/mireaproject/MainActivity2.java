package ru.mirea.rebrov.mireaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.provider.Settings.Secure;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

import ru.mirea.rebrov.mireaproject.databinding.ActivityMain2Binding;
import android.os.Handler;



public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMain2Binding binding;
    private FirebaseAuth mAuth;
    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        String android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        //binding.textView7.setText(android_id);
        binding.button3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signIn(binding.editTextTextPersonName.getText().toString(), binding.editTextTextPersonName2.getText().toString());
            }
        });
        binding.button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createAccount(binding.editTextTextPersonName.getText().toString(), binding.editTextTextPersonName2.getText().toString());
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(!checkRemote())
        {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
        else
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    System.exit(0);
                }
            }, 2000); // задержка в 2 секунды

        }
    }

    private boolean checkRemote() {

        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);

        for(int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if(Objects.equals(p.packageName, "com.anydesk.anydeskandroid"))
            {
                DialogRemote fragment = new DialogRemote();
                fragment.show(getSupportFragmentManager(), "mirea");
                return true;
            }
        }
        return false;
    }
    private void updateUI(FirebaseUser user)
    {
        if (user != null) {
            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
        }
        else
        {

            binding.editTextTextPersonName.setVisibility(View.VISIBLE);
            binding.editTextTextPersonName2.setVisibility(View.VISIBLE);
            binding.button.setVisibility(View.VISIBLE);
            binding.button3.setVisibility(View.VISIBLE);
        }
    }

    private void createAccount(String email, String password)
    {
        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm())
//        {
//            return;
//        }

        String hashedPassword = shaHash.hashSHA256(password);
        //Log.d("tag", hashedPassword);
        mAuth.createUserWithEmailAndPassword(email, hashedPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else
                        {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private boolean validateForm()
    {
        if(binding.editTextTextPersonName2.getText().toString().length() <6)
        {
            return false;
        }
        return !TextUtils.isEmpty(binding.editTextTextPersonName.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(binding.editTextTextPersonName.getText().toString()).matches();
    }

    private void signIn(String email, String password)
    {
        Log.d(TAG, "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }
                        else
                        {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void onOkClicked() {
        finish();
        System.exit(0);
    }

    public void onContinueClicked() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
}