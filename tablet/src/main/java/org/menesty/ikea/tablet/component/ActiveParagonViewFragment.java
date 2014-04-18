package org.menesty.ikea.tablet.component;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.domain.ProductItem;

import java.util.List;

public class ActiveParagonViewFragment extends ParagonViewFragment {

    public ActiveParagonViewFragment(List<ProductItem[]> data) {
        super(data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main, container, false);
        init(view);
        return view;
    }
}
