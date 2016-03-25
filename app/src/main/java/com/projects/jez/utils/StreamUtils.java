package com.projects.jez.utils;

import java.io.Closeable;
import java.io.IOException;

public class StreamUtils {
    public static boolean closeQuietly(Closeable stream) {
        try {
            stream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}