package org.menesty.ikea.tablet.dialog;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.menesty.ikea.tablet.R;

/**
 * Created by Menesty on 4/7/14.
 */
public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.pref_dialog);
    }

}
