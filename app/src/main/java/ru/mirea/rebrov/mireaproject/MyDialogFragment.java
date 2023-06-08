package ru.mirea.rebrov.mireaproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment
{
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Файлы").setMessage("Что сделать с файлом?").setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Открыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((fileFragment) getParentFragment()).onClickOpen();
                        dialog.cancel();
                    }
                })
                .setNeutralButton("Сохранить", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        ((fileFragment) getParentFragment()).onClickSave();
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
