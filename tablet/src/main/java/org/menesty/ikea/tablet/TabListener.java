package org.menesty.ikea.tablet;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import org.menesty.ikea.tablet.component.ParagonViewFragment;

public class TabListener implements ActionBar.TabListener {
    private final ParagonViewFragment fragment;

    public TabListener(ParagonViewFragment fragment) {
        this.fragment = fragment;
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.fragment_container, fragment);
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        fragment.persistState();
        ft.remove(fragment);
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }
}