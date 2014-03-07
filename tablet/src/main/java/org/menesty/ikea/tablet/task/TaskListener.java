package org.menesty.ikea.tablet.task;

public interface TaskListener<T> {
    void onTaskStarted();

    void onTaskFinished(T result);
}
