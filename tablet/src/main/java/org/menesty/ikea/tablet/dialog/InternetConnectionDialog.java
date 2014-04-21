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
    private boolean serverError;

    public InternetConnectionDialog() {
        this(false);
    }

    public InternetConnectionDialog(boolean serverError) {
        this.serverError = serverError;
        setRetainInstance(true);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(serverError ? R.string.serverConnectionProblemTitle : R.string.internetConnectionProblemTitle).setPositiveButton(R.string.tryAgain, this)
                .setMessage(serverError ? R.string.serverConnectionProblemMessage : R.string.reloadDataInfo)
                .setNegativeButton(R.string.settings, this);
        Dialog dialog = adb.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
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
