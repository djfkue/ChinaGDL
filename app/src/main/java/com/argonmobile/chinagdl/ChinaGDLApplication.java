package com.argonmobile.chinagdl;


import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

public class ChinaGDLApplication extends Application {

    private static final String TAG = ChinaGDLApplication.class.getSimpleName();

    public static final int CACHE_MAX_SIZE = 60 * 1024 * 1024;
    public static DisplayImageOptions sDisplayOptions;
    public static DisplayImageOptions sProfileOptions;
    static {
        sDisplayOptions = new DisplayImageOptions.Builder()
                // .showImageOnLoading(0) // Nothing display when loading
                // .showImageForEmptyUri(R.drawable.ic_empty)
                // .showImageOnFail(R.drawable.ic_error)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        sProfileOptions = new DisplayImageOptions.Builder()
                // .showImageOnLoading(0) // Nothing display when loading
                // .showImageForEmptyUri(R.drawable.ic_empty)
                //.showImageOnFail(R.drawable.ic_user_empty_white_bg)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them, or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this) method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                        // .denyCacheImageMultipleSizesInMemory()
                .diskCacheSize(CACHE_MAX_SIZE)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //.writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    private File mCacheDir;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate ...");
        initCacheDir();

        super.onCreate();
        initImageLoader(getApplicationContext());
    }

    private void initCacheDir() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            mCacheDir = new File(Environment.getExternalStorageDirectory(), "chinagdl/cache");
        } else {
            mCacheDir = getCacheDir();
        }

        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
    }
}
