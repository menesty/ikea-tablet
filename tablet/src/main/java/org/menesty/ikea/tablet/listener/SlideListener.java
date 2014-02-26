package org.menesty.ikea.tablet.listener;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Menesty on 2/26/14.
 */
public abstract class SlideListener implements View.OnTouchListener {
    private float fromPosition;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromPosition = motionEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float delta = fromPosition - motionEvent.getX();
                float percentage = view.getMeasuredWidth() > view.getMeasuredHeight() ? 0.2f : 0.3f;

                if (Math.abs(delta) > view.getMeasuredWidth() * percentage)
                    if (delta > 0)
                        next();
                    else
                        previous();
                break;

            default:
                break;
        }
        return false;
    }

    public abstract void next();

    public abstract void previous();
}
