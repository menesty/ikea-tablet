package org.menesty.ikea.tablet;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.PopupMenu;
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

    public void onTabReselected(ActionBar.Tab tab, final FragmentTransaction ft) {
        if (fragment.isUploaded()) {
            //ContextMenu menu
            PopupMenu popup = new PopupMenu(fragment.getActivity(), fragment.getView());
            popup.inflate(R.menu.tab_actions);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    ((BaseActivity) fragment.getActivity()).cancel(fragment.getUUID());

                    return true;
                }
            });

            popup.show();
        }

    }
}