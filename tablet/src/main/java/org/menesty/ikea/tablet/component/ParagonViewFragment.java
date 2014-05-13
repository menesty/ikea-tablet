package org.menesty.ikea.tablet.component;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ParagonViewFragment extends Fragment {

    public final String UUID;

    private List<ProductItem[]> data;

    public ParagonViewFragment() {
        UUID = generateUUID();
    }

    public ParagonViewFragment(String uuid, List<ProductItem[]> data) {
        UUID = uuid;
        this.data = data;
    }

    public static String generateUUID(){
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.paragon_view, container, false);
        init(view);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    private int currentActiveParagonIndex = 0;

    private View.OnTouchListener listViewOnTouchListener;

    protected void init(final View view) {
        RadioGroup paragonGroup = (RadioGroup) (view.findViewById(R.id.paragon_group));
        paragonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int index = group.indexOfChild(group.findViewById(checkedId));

                if (currentActiveParagonIndex == index)
                    return;

                ViewFlipper flipper = (ViewFlipper) (view.findViewById(R.id.listViewContainer));

                if (currentActiveParagonIndex > index) {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.go_prev_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.go_prev_out));
                } else {
                    flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.go_next_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.go_next_out));
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

        if (data == null)
            createParagon(view);
        else
            for (ProductItem[] items : data) {
                ProductViewLayout listView = createParagon(view);
                listView.setItems(items);
            }

    }

    private void scrollFlipperView(int direction) {
        RadioGroup paragonGroup = (RadioGroup) (getView().findViewById(R.id.paragon_group));
        View radioBox = paragonGroup.getChildAt(checkedRadioButtonIndex(paragonGroup) + direction);

        if (radioBox != null)
            paragonGroup.check(radioBox.getId());
    }

    private int checkedRadioButtonIndex(RadioGroup group) {
        return group.indexOfChild(group.findViewById(group.getCheckedRadioButtonId()));
    }

    public ProductViewLayout createParagon() {
        return createParagon(getView());
    }

    public ProductViewLayout createParagon(final View view) {
        RadioGroup paragonGroup = (RadioGroup) (view.findViewById(R.id.paragon_group));

        RadioButton currentRadio = new RadioButton(getActivity());
        paragonGroup.addView(currentRadio);

        ViewFlipper flipper = (ViewFlipper) (view.findViewById(R.id.listViewContainer));

        ProductViewLayout productViewLayout = new ProductViewLayout(getActivity(), flipper) {
            @Override
            public void onItemLongClick(ProductItem item) {
                if (!showEditWeightDialog())
                    return;

                NumberDialog dialog = new NumberDialog();
                Bundle args = new Bundle();
                args.putDouble("weight", item.weight);
                args.putString("productName", item.productName);
                dialog.setArguments(args);

                dialog.show(getActivity().getFragmentManager(), "number-dialog");
            }
        };
        productViewLayout.setViewOnTouchListener(listViewOnTouchListener);

        flipper.addView(productViewLayout);
        paragonGroup.check(currentRadio.getId());

        return productViewLayout;
    }

    protected boolean showEditWeightDialog() {
        return false;
    }

    public ProductViewLayout getActiveView() {
        RadioGroup paragonGroup = (RadioGroup) (getView().findViewById(R.id.paragon_group));
        int index = checkedRadioButtonIndex(paragonGroup);

        if (index >= 0)
            return (ProductViewLayout) (((ViewFlipper) getView().findViewById(R.id.listViewContainer)).getChildAt(index));

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

    public void updateWeight(String productName, double weight) {
        ViewFlipper flipper = (ViewFlipper) getView().findViewById(R.id.listViewContainer);

        for (int i = 0; i < flipper.getChildCount(); i++) {
            ProductViewLayout layout = (ProductViewLayout) flipper.getChildAt(i);
            List<ProductItem> items = layout.findByProductName(productName);

            if (items.size() != 0)
                for (ProductItem item : items) {
                    item.weight = weight;
                    item.checked = true;
                    layout.update(item);
                }
        }
    }

    public void deleteParagon() {
        RadioGroup paragonGroup = (RadioGroup) (getView().findViewById(R.id.paragon_group));
        int index = checkedRadioButtonIndex(paragonGroup);

        if (index >= 0) {
            paragonGroup.removeViewAt(index);
            ViewFlipper flipper = (ViewFlipper) (getView().findViewById(R.id.listViewContainer));
            flipper.removeViewAt(index);

            if (paragonGroup.getChildCount() != 0)
                paragonGroup.check(paragonGroup.getChildAt(paragonGroup.getChildCount() == index ? 0 : index).getId());
        }
    }

    public List<ProductItem[]> getData() {
        if (!isAdded())
            return data;

        List<ProductItem[]> result = new ArrayList<ProductItem[]>();

        ViewFlipper flipper = (ViewFlipper) (getView().findViewById(R.id.listViewContainer));

        for (int i = 0; i < flipper.getChildCount(); i++)
            if (flipper.getChildAt(i) instanceof ProductViewLayout)
                result.add(((ProductViewLayout) flipper.getChildAt(i)).getItems());

        return result;
    }

    public void persistState() {
        this.data = getData();
    }

    public void reset() {
        data = null;

        if (getView() == null)
            return;

        ViewFlipper flipper = (ViewFlipper) (getView().findViewById(R.id.listViewContainer));
        flipper.removeAllViews();

        RadioGroup paragonGroup = (RadioGroup) (getView().findViewById(R.id.paragon_group));
        paragonGroup.removeAllViews();

        createParagon();
    }
}