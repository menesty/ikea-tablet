package org.menesty.ikea.tablet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.addapter.ProductAvailableAdapter;
import org.menesty.ikea.tablet.domain.AvailableProductItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Menesty on 12/8/13.
 */
public abstract class ProductChoiceDialog extends DialogFragment {
    private List<AvailableProductItem> products;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final ProductAvailableAdapter adapter = new ProductAvailableAdapter(getActivity());
        adapter.addAll(products);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText editText = new EditText(getActivity());
        final ListView listview = new ListView(getActivity());
        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.discoverseed_larg, 0, 0, 0);
        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                adapter.getFilter().filter(s);
            }
        });

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(editText);
        layout.addView(listview);


        builder.setView(layout);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onItemSelect(adapter.getItem(i));
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public abstract void onItemSelect(AvailableProductItem item);

    public void setAvailableProductItem(List<AvailableProductItem> products) {
        this.products = products;
    }

}
