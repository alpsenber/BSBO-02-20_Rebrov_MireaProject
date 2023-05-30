package ru.mirea.rebrov.mireaproject;
import static android.app.Activity.RESULT_OK;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import android.provider.MediaStore;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import ru.mirea.rebrov.mireaproject.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private String imagePixels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        String mainKeyAlias;
        try
        {
            mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
        }
        catch (GeneralSecurityException | IOException e)
        {
            throw new RuntimeException(e);
        }
        SharedPreferences secureSharedPreferences = null;
        try
        {
            secureSharedPreferences = EncryptedSharedPreferences.create(
                    "rebrovSharedPreferences",
                    mainKeyAlias,
                    getActivity().getBaseContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        }
        catch (GeneralSecurityException | IOException e)
        {
            throw new RuntimeException(e);
        }

        if(!secureSharedPreferences.getString("name", "null").equals("null") && !secureSharedPreferences.getString("photo", "null").equals("null")
                && !secureSharedPreferences.getString("surname", "null").equals("null"))
        {
            binding.editTextTextPersonName.setText(secureSharedPreferences.getString("name", "null"));
            binding.editTextTextPersonName2.setText(secureSharedPreferences.getString("surname", "null"));
            byte[] b = Base64.decode(secureSharedPreferences.getString("photo", "null"), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            binding.imageView2.setImageBitmap(bitmap);
        }
        SharedPreferences finalSecureSharedPreferences = secureSharedPreferences;
        binding.button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finalSecureSharedPreferences.edit().putString("name", binding.editTextTextPersonName.getText().toString()).apply();
                finalSecureSharedPreferences.edit().putString("surname", binding.editTextTextPersonName2.getText().toString()).apply();
                finalSecureSharedPreferences.edit().putString("photo", imagePixels).apply();
            }
        });
        binding.imageView2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 101);
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && null != data)
        {
            Uri selectedImage = data.getData();
            binding.imageView2.setImageURI(selectedImage);
            Bitmap bitmap;
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();

            imagePixels = Base64.encodeToString(b, Base64.DEFAULT);
        }
    }
}