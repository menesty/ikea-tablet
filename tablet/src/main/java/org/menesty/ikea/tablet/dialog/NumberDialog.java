package org.menesty.ikea.tablet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.EditText;
import org.menesty.ikea.tablet.R;

public class NumberDialog extends DialogFragment implements DialogInterface.OnClickListener {
    public NumberDialog() {
          setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Problem connecting to internet").setPositiveButton("", this)
                .setMessage(R.string.reloadDataInfo)
                .setNegativeButton(R.string.settings, this);

        EditText editText = new EditText(getActivity());
        editText.setId(-12);
        editText.setRawInputType(Configuration.KEYBOARD_12KEY);

        editText.setText(savedInstanceState.getDouble("weight", 0) + "");

        adb.setView(editText);

        Dialog dialog = adb.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dismiss();
    }
}
