package com.projects.jez.dontbeevil.content;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.projects.jez.utils.StreamUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jez on 25/03/2016.
 */
public class ContentLoader {
    private static final String TAG = ContentLoader.class.getSimpleName();
    private static final boolean DLOG = true;
    private static final Gson gson = new Gson();
    private static final JsonParser parser = new JsonParser();
    private final List<IncrementerScript> mIncrementers;

    public ContentLoader(Context context) {
        mIncrementers = loadAssetList(context, "incrementers.json", IncrementerScript.class);
    }

    public List<IncrementerScript> getIncrementers() {
        return mIncrementers;
    }

    private <T> List<T> loadAssetList(Context context, String assetPath, Class<T> scriptClass) {
        InputStream stream = null;
        try {
            stream = openAssetStream(context,assetPath );
            try {
                return parseJsonStreamToList(stream, scriptClass);
            } catch (IOException e) {
                Log.e(TAG, "Exception reading json stream");
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception opening json stream");
            e.printStackTrace();
        }
        if (stream != null) {
            StreamUtils.closeQuietly(stream);
        }
        return null;
    }

    private InputStream openAssetStream(Context context, String assetFileName) throws IOException {
        return context.getAssets().open(assetFileName);
    }

    private String streamJSON(InputStream stream) {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        StringBuilder stringBuilder = new StringBuilder();
        String str;
        try {
            while ((str = in.readLine()) != null) {
                stringBuilder.append(str);
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception reading json stream");
            e.printStackTrace();
        } finally {
            if (in != null) StreamUtils.closeQuietly(in);
        }
        return stringBuilder.toString();
    }

    /***
     * @return list of parsed objects. May contain nulls.
     * @throws IOException
     */
    private <T> List<T> parseJsonStreamToList(InputStream in, Class<T> scriptClass) throws IOException {
        String jsonString = streamJSON(in);
        JsonArray jArray = parser.parse(jsonString).getAsJsonArray();
        List<T> returnList = new ArrayList<>();
        for (JsonElement element : jArray) {
            returnList.add(gson.fromJson(element, scriptClass));
        }
        return returnList;
    }
}
