package com.projects.jez.dontbeevil.errors;

/**
 * Created by Jez on 25/03/2016.
 */
public class DuplicateLoopTaskRuntimeError extends RuntimeException {
    public DuplicateLoopTaskRuntimeError(String id) {
        super("Task already added for task id " + id);
    }
}
