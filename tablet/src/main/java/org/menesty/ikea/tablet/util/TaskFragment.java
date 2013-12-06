package org.menesty.ikea.tablet.util;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import org.menesty.ikea.tablet.task.BaseAsyncTask;
import org.menesty.ikea.tablet.task.TaskCallbacks;
import org.menesty.ikea.tablet.task.TaskStatusListener;

public class TaskFragment extends Fragment {

    private BaseAsyncTask<?, ?, ?> task;


    private TaskCallbacks callbacks;

    private boolean mRunning;

    public TaskFragment(TaskCallbacks callbacks) {
        this.callbacks = callbacks;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof TaskCallbacks)) {
            throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
        }

        callbacks = (TaskCallbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancel();
    }

    public void start(BaseAsyncTask<?, ?, ?> task, Object... params) {
        if (!mRunning) {
            this.task = task;
            task.setStatusListener(new TaskStatusListener() {
                @Override
                public void setRunning(boolean value) {
                    mRunning = value;
                }
            });
            task.setTaskCallbacks(callbacks);
            task.execute(params);

            mRunning = true;
        }
    }

    public void cancel() {
        if (mRunning) {
            task.cancel(false);
            task = null;
            mRunning = false;
        }
    }

    public boolean isRunning() {
        return mRunning;
    }
}
