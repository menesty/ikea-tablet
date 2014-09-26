package org.menesty.ikea.tablet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.menesty.ikea.tablet.BaseActivity;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.addapter.HistoryAdapter;
import org.menesty.ikea.tablet.domain.History;

/**
 * Created by Menesty on
 * 9/22/14.
 * 20:42.
 */
public class HistoryChoiceDialog extends DialogFragment {
    private History[] histories = new History[0];

    private ItemSelectListener listener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("history_item_dialog", histories);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            histories = BaseActivity.convert(History.class, savedInstanceState.getParcelableArray("history_item_dialog"));

        final HistoryAdapter adapter = new HistoryAdapter(getActivity());
        adapter.addAll(histories);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final ListView listview = new ListView(getActivity());

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(listview);


        builder.setView(layout);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listener.onItemSelect(adapter.getItem(i));
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Create the AlertDialog object and return it
        Dialog d = builder.create();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(d.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        d.getWindow().setAttributes(lp);

        return d;

    }

    public void setAvailableHistories(History[] histories) {
        this.histories = histories;
    }

    public void setListener(ItemSelectListener listener) {
        this.listener = listener;
    }

    public static interface ItemSelectListener {
        void onItemSelect(History item);
    }

}
