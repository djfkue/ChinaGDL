package com.argonmobile.chinagdl.util;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    public static <T> T parseJson(String jsonString, Class<T> valueClass) {
        try {
            return JsonUtils.defaultMapper().readValue(jsonString, valueClass);
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse JSON entity " + valueClass.getSimpleName(), e);
            throw new RuntimeException(e);
        }
    }

    // re-use a single ObjectMapper so we're not creating multiple object mappers
    private static ObjectMapper sObjectMapper = new ObjectMapper();
    public static ObjectMapper defaultMapper() {
        return sObjectMapper;
    }

}
