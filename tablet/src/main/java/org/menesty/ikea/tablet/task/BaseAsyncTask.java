package org.menesty.ikea.tablet.task;

import android.os.AsyncTask;

public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private TaskListener<Result> listener;

    public void setTaskListener(TaskListener<Result> listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (listener != null)
            listener.onTaskStarted();
    }

    @Override
    protected void onPostExecute(Result result) {
        if (listener != null)
            listener.onTaskFinished(result);
    }
}
