package org.menesty.ikea.tablet.component;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.dialog.NumberDialog;
import org.menesty.ikea.tablet.domain.ProductItem;
import org.menesty.ikea.tablet.listener.SlideListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Menesty on 2/26/14.
 */
public class ParagonControlComponent {
    private int currentActiveParagonIndex = 0;

    private Activity context;

    private View.OnTouchListener listViewOnTouchListener;

    public ParagonControlComponent(Activity context) {
        this.context = context;
        init();
    }

    private void init() {
        RadioGroup paragonGroup = (RadioGroup) (context.findViewById(R.id.paragon_group));
        paragonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = group.indexOfChild(group.findViewById(checkedId));
                if (currentActiveParagonIndex == index)
                    return;

                ViewFlipper flipper = (ViewFlipper) (context.findViewById(R.id.listViewContainer));

                if (currentActiveParagonIndex > index) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_prev_out));
                } else {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.go_next_out));
                }

                flipper.setDisplayedChild(index);
                currentActiveParagonIndex = index;
            }
        });

        listViewOnTouchListener = new SlideListener() {
            @Override
            public void next() {
                scrollFlipperView(1);
            }

            @Override
            public void previous() {
                scrollFlipperView(-1);
            }
        };
    }

    private void scrollFlipperView(int direction) {
        RadioGroup paragonGroup = (RadioGroup) (context.findViewById(R.id.paragon_group));
        View radioBox = paragonGroup.getChildAt(checkedRadioButtonIndex(paragonGroup) + direction);

        if (radioBox != null)
            paragonGroup.check(radioBox.getId());
    }

    private int checkedRadioButtonIndex(RadioGroup group) {
        return group.indexOfChild(group.findViewById(group.getCheckedRadioButtonId()));
    }


    public ProductViewLayout createParagon() {
        RadioGroup paragonGroup = (RadioGroup) (context.findViewById(R.id.paragon_group));

        RadioButton currentRadio = new RadioButton(context);
        paragonGroup.addView(currentRadio);

        ViewFlipper flipper = (ViewFlipper) (context.findViewById(R.id.listViewContainer));

        ProductViewLayout productViewLayout = new ProductViewLayout(context, flipper) {
            @Override
            public void onItemLongClick(ProductItem item) {
                NumberDialog dialog = new NumberDialog();
                Bundle args = new Bundle();
                args.putDouble("weight", item.weight);
                args.putString("productName", item.productName);
                dialog.setArguments(args);

                dialog.show(context.getFragmentManager(), "number-dialog");
            }
        };
        productViewLayout.setViewOnTouchListener(listViewOnTouchListener);

        flipper.addView(productViewLayout);
        paragonGroup.check(currentRadio.getId());

        return productViewLayout;
    }

    public ProductViewLayout getActiveView() {
        RadioGroup paragonGroup = (RadioGroup) (context.findViewById(R.id.paragon_group));
        int index = checkedRadioButtonIndex(paragonGroup);

        if (index >= 0)
            return (ProductViewLayout) (((ViewFlipper) context.findViewById(R.id.listViewContainer)).getChildAt(index));

        return null;
    }

    public ProductItem deleteProductItem() {
        ProductViewLayout viewLayout = getActiveView();

        if (viewLayout == null || viewLayout.getSelected() == null)
            return null;

        ProductItem item = viewLayout.getSelected();
        item.count--;

        viewLayout.update(item);

        return item;
    }


    public boolean addProduct(ProductItem productItem) {
        ProductViewLayout listView = getActiveView();

        if (listView == null)
            return false;

        listView.add(productItem);
        listView.requestLayout();

        return true;
    }


    public void deleteParagon() {
        RadioGroup paragonGroup = (RadioGroup) (context.findViewById(R.id.paragon_group));
        int index = checkedRadioButtonIndex(paragonGroup);

        if (index >= 0) {
            paragonGroup.removeViewAt(index);
            ViewFlipper flipper = (ViewFlipper) (context.findViewById(R.id.listViewContainer));
            flipper.removeViewAt(index);

            if (paragonGroup.getChildCount() != 0)
                paragonGroup.check(paragonGroup.getChildAt(paragonGroup.getChildCount() == index ? 0 : index).getId());
        }
    }

    public List<ProductItem[]> getData() {
        List<ProductItem[]> result = new ArrayList<ProductItem[]>();

        ViewFlipper flipper = (ViewFlipper) (context.findViewById(R.id.listViewContainer));

        for (int i = 0; i < flipper.getChildCount(); i++)
            if (flipper.getChildAt(i) instanceof ProductViewLayout)
                result.add(((ProductViewLayout) flipper.getChildAt(i)).getItems());

        return result;
    }

    public void reset() {
        ViewFlipper flipper = (ViewFlipper) (context.findViewById(R.id.listViewContainer));
        flipper.removeAllViews();

        RadioGroup paragonGroup = (RadioGroup) (context.findViewById(R.id.paragon_group));
        paragonGroup.removeAllViews();

        createParagon();
    }
}
