package org.menesty.ikea.tablet.test;

import android.test.ActivityUnitTestCase;
import org.menesty.ikea.tablet.TabletActivity;
import org.menesty.ikea.tablet.data.DataJsonService;
import org.menesty.ikea.tablet.domain.ProductItem;

import java.util.ArrayList;
import java.util.List;

public class TabletActivityTest extends ActivityUnitTestCase<TabletActivity> {

    public TabletActivityTest() {
        super(TabletActivity.class);
    }

    public void testActivity() {
        /*TabletActivity activity = getActivity();
        assertNotNull(activity);*/
    }

    public void testParagonSerialize() {
        DataJsonService service = new DataJsonService();

        List<ProductItem[]> data = new ArrayList<ProductItem[]>();

        String result = service.serializeParagons(data);

        assertEquals("{}", result);
    }

}

