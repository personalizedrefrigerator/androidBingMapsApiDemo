package com.example.bingmapsdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

// Bitmap ref: https://stackoverflow.com/questions/11831188/how-to-get-bitmap-from-a-url-in-android
// Bing API ref: https://docs.microsoft.com/en-us/bingmaps/rest-services/imagery/get-a-static-map
// Maps portal: https://www.bingmapsportal.com/Application#
// !!! IN PRODUCTION, https://www.microsoft.com/en-us/maps/product/terms-april-2011, MUST BE
//     PROVIDED TO THE END USER !!!

public class MapLoader implements Runnable
{
    // Formatting [(float latitudeCenter), (float longitudeCenter), ([unsigned] int zoomLevel),
    //              ([unsigned] int mapWidth), ([unsigned] int mapHeight),
    //              (float latitudePushpin), (float longitudePushpin), (string key)]
    private static final String MAP_REQUEST_TEMPLATE =
            "https://dev.virtualearth.net/REST/v1/Imagery/Map/Road/%.4f,%.4f/%d?mapSize=%d,%d&pushpin=%.4f,%.4f&key=%s";
    private static final int MAP_ZOOM_LEVEL = 17;

    private final double mLat;
    private final double mLong;
    private final MapSettings mOptions;
    private final MapLoader.Callback mCallback;

    public MapLoader(double lat, double lon, MapSettings options, Callback callback)
    {
        mLat = lat;
        mLong = lon;
        mOptions = options;
        mCallback = callback;

        checkRep();
    }

    @Override
    public void run()
    {
        checkRep();

        try
        {
            URL url = new URL(String.format(Locale.ENGLISH, MAP_REQUEST_TEMPLATE, mLat, mLong, MAP_ZOOM_LEVEL,
                    mOptions.getOutputWidth(),
                    mOptions.getOutputHeight(),
                    mLat, mLong, mOptions.getApiKey()));
            Bitmap result = BitmapFactory.decodeStream(url.openStream());

            mCallback.onLoaded(result);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            mCallback.onExcept(e.getCause());
        }

        checkRep();
    }

    public interface Callback
    {
        void onLoaded(Bitmap result);
        void onExcept(Throwable cause);
    }

    private void checkRep()
    {
        if (Double.isNaN(mLat)) throw new AssertionError("Latitude must be a number.");
        if (Double.isNaN(mLong)) throw new AssertionError("Longitude must be a number.");
        if (Double.isInfinite(mLat)) throw new AssertionError("Latitude must be finite.");
        if (Double.isInfinite(mLong)) throw new AssertionError("Longitude must be finite.");
        if (mOptions == null) throw new AssertionError("Options cannot be null.");
        if (mCallback == null) throw new AssertionError("Callback cannot be null.");
    }
}
