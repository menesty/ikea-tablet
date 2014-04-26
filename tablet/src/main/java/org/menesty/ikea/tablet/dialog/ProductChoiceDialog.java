package org.menesty.ikea.tablet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.addapter.ProductAvailableAdapter;
import org.menesty.ikea.tablet.domain.ProductItem;

/**
 * Created by Menesty on 12/8/13.
 */
public class ProductChoiceDialog extends DialogFragment {
    private ProductItem[] products = new ProductItem[0];
    private EditText editText;

    private ItemSelectListener listener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("product_item_dialog", products);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            products = (ProductItem[]) (savedInstanceState.getParcelableArray("product_item_dialog"));

        final ProductAvailableAdapter adapter = new ProductAvailableAdapter(getActivity());
        adapter.addAll(products);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        editText = new EditText(getActivity());

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                Configuration config = getResources().getConfiguration();
                boolean keyBordPresent = config.keyboard != Configuration.KEYBOARD_NOKEYS;

                if (keyBordPresent && focus)
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .showSoftInput(editText, InputMethodManager.SHOW_FORCED);

            }
        });


        final ListView listview = new ListView(getActivity());

        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.discoverseed_larg, 0, 0, 0);
        editText.setRawInputType(Configuration.KEYBOARD_12KEY);
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
        return builder.create();
    }

    public void setListener(ItemSelectListener listener) {
        this.listener = listener;

    }

    public void setAvailableProductItem(ProductItem[] products) {
        this.products = products;
    }

    public static interface ItemSelectListener {
        void onItemSelect(ProductItem item);
    }


}
