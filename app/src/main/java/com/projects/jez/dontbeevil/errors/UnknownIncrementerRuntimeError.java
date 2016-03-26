package com.projects.jez.dontbeevil.errors;

/**
 * Created by Jez on 25/03/2016.
 */
public class UnknownIncrementerRuntimeError extends RuntimeException {
    public UnknownIncrementerRuntimeError(String id) {
        super("Unknown incrementer for id " + id);
    }
}
