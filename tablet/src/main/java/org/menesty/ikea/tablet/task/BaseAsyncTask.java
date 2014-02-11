package org.menesty.ikea.tablet.task;

import android.os.AsyncTask;

public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private TaskStatusListener statusListener;

    protected TaskCallbacks callbacks;

    public void setStatusListener(TaskStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    public void setTaskCallbacks(TaskCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    protected void setRunning(boolean value) {
        if (statusListener != null)
            statusListener.setRunning(value);
    }

    @Override
    protected void onPostExecute(Result result) {
        if (callbacks != null)
            callbacks.onPostExecute(this, result);

        if (statusListener != null)
            statusListener.setRunning(false);
    }
}
