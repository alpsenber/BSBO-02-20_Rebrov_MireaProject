package ru.mirea.rebrov.mireaproject;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogRemote extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Внимание!").setMessage("Установлено приложение удалённого доступа. Хакеры могут использовать это для несанкционированного доступа и кражи информации.").setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity2) getActivity()).onOkClicked();
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
}
