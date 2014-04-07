package org.menesty.ikea.tablet;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import org.menesty.ikea.tablet.dialog.SettingFragment;

/**
 * Created by Menesty on 4/7/14.
 */
public class PrefActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new SettingFragment());
        fragmentTransaction.commit();
    }
}
