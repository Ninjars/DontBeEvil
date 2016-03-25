package com.projects.jez.dontbeevil.errors;

/**
 * Created by Jez on 25/03/2016.
 */
public class UnknownLoopTaskRuntimeError extends RuntimeException {
    public UnknownLoopTaskRuntimeError(String id) {
        super("Could not find task id " + id);
    }
}
