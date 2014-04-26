package org.menesty.ikea.tablet.util;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import org.menesty.ikea.tablet.R;
import org.menesty.ikea.tablet.task.BaseAsyncTask;
import org.menesty.ikea.tablet.task.TaskCallbacks;
import org.menesty.ikea.tablet.task.TaskListener;

public class TaskFragment<Result> extends Fragment implements TaskListener<Result> {

    private BaseAsyncTask<?, ?, Result> task;

    private ProgressDialog progressDialog;

    private boolean isTaskRunning = false;

    private boolean showDialog;

    private boolean lockScreen;

    private boolean dialogShowed;


    public TaskFragment(boolean showDialog, boolean lockScreen) {
        this.showDialog = showDialog;
        this.lockScreen = lockScreen;
    }

    public TaskFragment() {
        this(true, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (showDialog && isTaskRunning && !dialogShowed)
            showDialog();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void start(BaseAsyncTask<?, ?, Result> task, Object... params) {
        if (!isTaskRunning) {
            this.task = task;
            task.setTaskListener(this);
            task.execute(params);
        }
    }

    @Override
    public void onTaskStarted() {
        isTaskRunning = true;

        if (lockScreen)
            lockScreenOrientation();

        if (isAdded() && showDialog && isTaskRunning && !dialogShowed)
            showDialog();
    }

    private void showDialog() {
        progressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.loading),
                getResources().getString(R.string.wait_msg));
        dialogShowed = true;
    }

    @Override
    public void onTaskFinished(Result result) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            dialogShowed = false;
        }

        if (lockScreen)
            unlockScreenOrientation();

        isTaskRunning = false;

        ((TaskCallbacks) getActivity()).onPostExecute(task, result);
    }

    @Override
    public void onDetach() {
        // All dialogs should be closed before leaving the activity in order to avoid
        // the: Activity has leaked window com.android.internal.policy... exception
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            dialogShowed = false;
        }

        super.onDetach();
    }

    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;

        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT)
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    private void unlockScreenOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public boolean isRunning() {
        return isTaskRunning;
    }
}
