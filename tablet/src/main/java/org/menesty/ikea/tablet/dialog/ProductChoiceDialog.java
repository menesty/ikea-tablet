package org.menesty.ikea.tablet.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.TabletActivity;
import org.menesty.ikea.tablet.addapter.ProductAvailableAdapter;
import org.menesty.ikea.tablet.domain.ProductItem;

/**
 * Created by Menesty on 12/8/13.
 */
public class ProductChoiceDialog extends DialogFragment implements NumberPicker.OnValueChangeListener {
    private ProductItem[] products = new ProductItem[0];

    private ItemSelectListener listener;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("product_item_dialog", products);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        if (savedInstanceState != null)
            products = (ProductItem[]) (savedInstanceState.getParcelableArray("product_item_dialog"));

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
                ProductItem productItem = adapter.getItem(i);

                if (productItem.count > 1)
                    show(adapter.getItem(i));
                else
                    listener.onItemSelect(productItem);
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

    public void setListener(ItemSelectListener listener) {
        this.listener = listener;

    }

    public void setAvailableProductItem(ProductItem[] products) {
        this.products = products;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }


    public static interface ItemSelectListener {
        void onItemSelect(ProductItem item);
    }


    public void show(final ProductItem item) {

        final Dialog d = new Dialog(TabletActivity.get());
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.number_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue((int) item.count);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    item.count = np.getValue();
                    listener.onItemSelect(item);
                }
                d.dismiss();
            }
        });
        d.show();


    }
}
