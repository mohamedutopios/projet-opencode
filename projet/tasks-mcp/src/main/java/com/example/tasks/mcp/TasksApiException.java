package com.example.tasks.mcp;

/** Erreur renvoyée par tasks-api, ou API injoignable (status 0). */
public class TasksApiException extends RuntimeException {

    private final int status;

    public TasksApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int status() {
        return status;
    }
}
