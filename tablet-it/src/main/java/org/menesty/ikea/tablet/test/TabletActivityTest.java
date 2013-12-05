package org.menesty.ikea.tablet.test;

import android.test.ActivityInstrumentationTestCase2;
import org.menesty.ikea.tablet.*;

public class TabletActivityTest extends ActivityInstrumentationTestCase2<TabletActivity> {

    public TabletActivityTest() {
        super(TabletActivity.class);
    }

    public void testActivity() {
        TabletActivity activity = getActivity();
        assertNotNull(activity);
    }
}

