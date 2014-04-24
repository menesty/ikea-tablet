package org.menesty.ikea.tablet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.EditText;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.util.NumberUtil;

public class NumberDialog extends DialogFragment implements DialogInterface.OnClickListener {
    public interface ProductWeightChangeListener {
        void onWeightChange(String productName, double weight);
    }

    private EditText editText;

    public NumberDialog() {
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Input new weight").setPositiveButton(R.string.save, this)
                .setNegativeButton(R.string.cancel, this);

        editText = new EditText(getActivity());
        editText.setId(-12);
        editText.setRawInputType(Configuration.KEYBOARD_12KEY);

        editText.setText(getArguments().getDouble("weight", 0) + "");

        adb.setView(editText);

        Dialog dialog = adb.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
       /* if (true)
            throw new RuntimeException("bla bal");*/

        if (i == DialogInterface.BUTTON_POSITIVE) {
            double result = NumberUtil.parse(editText.getText().toString());
            ((ProductWeightChangeListener) getActivity()).onWeightChange(getArguments().getString("productName"), result);
        }

        dismiss();

    }
}
