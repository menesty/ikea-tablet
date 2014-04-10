package org.menesty.ikea.tablet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.TabletActivity;

/**
 * Created by Menesty on 3/4/14.
 */
public class InternetConnectionDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Problem connecting to internet").setPositiveButton(R.string.reloadData, this)
                .setMessage(R.string.reloadDataInfo)
                .setNegativeButton(R.string.settings, this);
        return adb.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dismiss();

        if (which == -1)
            ((TabletActivity) getActivity()).loadData();
        else
            TabletActivity.showApplicationSettings(getActivity(), true);
    }
}
