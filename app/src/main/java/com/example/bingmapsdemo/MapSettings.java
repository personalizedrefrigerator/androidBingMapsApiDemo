package com.example.bingmapsdemo;

public class MapSettings
{
    private final String mApiKey;
    private final int mOutputWidth;
    private final int mOutputHeight;

    public MapSettings(String apiKey, int outputWidth,
                       int outputHeight)
    {
        mApiKey = apiKey;
        mOutputWidth = outputWidth;
        mOutputHeight = outputHeight;

        checkRep();
    }

    public String getApiKey()
    {
        return mApiKey;
    }

    public int getOutputWidth()
    {
        return mOutputWidth;
    }

    public int getOutputHeight()
    {
        return mOutputHeight;
    }

    private void checkRep()
    {
        if (mApiKey == null) throw new AssertionError("API Key cannot be null");
        if (mOutputWidth <= 0) throw new AssertionError("Output width must be > 0");
        if (mOutputHeight <= 0) throw new AssertionError("Output height must be > 0");
    }
}
