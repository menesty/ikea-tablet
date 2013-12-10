package org.menesty.ikea.tablet;

import android.os.Handler;

/**
 * Created by Menesty on 12/7/13.
 */
public abstract class ProductIdKeyboardHandler {
    private Handler handler;
    private Runnable currentRunnable;

    private StringBuffer sb;

    public ProductIdKeyboardHandler() {
        handler = new Handler();
    }

    public void handleChar(char value) {
        if (sb == null)
            sb = new StringBuffer();
        sb.append(value);

        cancel();

        handler.postDelayed(currentRunnable = new Runnable() {
            @Override
            public void run() {
                onProductId(sb.toString());
                sb = null;
            }
        }, 300);
    }

    public abstract void onProductId(String productId);

    public void cancel() {
        handler.removeCallbacks(currentRunnable);
    }
}
