package org.menesty.ikea.tablet.task;

public interface TaskCallbacks {

    public void onPreExecute();

    public void onProgressUpdate(int percent);

    public void onCancelled();

    public void onPostExecute();
}