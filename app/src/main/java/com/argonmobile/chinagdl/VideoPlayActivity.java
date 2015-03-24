package com.argonmobile.chinagdl;

import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class VideoPlayActivity extends ActionBarActivity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    public final static String  VIDEO_PATH = "video_path";

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        // Tricky:
        // Youku video size is 512 x 288
        // Reset the video container through this ratio
        View videoContainer = findViewById(R.id.video_view_container);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        Log.e("SD_TRACE", "screen size: " + screenWidth + "x" + screenHeight);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) videoContainer.getLayoutParams();
        layoutParams.width = screenWidth;
        layoutParams.height = (int) (288 * (screenWidth/ 512.0f));
        videoContainer.setLayoutParams(layoutParams);

        mVideoView = (VideoView) findViewById(R.id.video_view);
        String path = null;
        Bundle extras = getIntent().getExtras();
        if ( null != extras )
            path = extras.getString(VIDEO_PATH);

        if (path == "") {
            // Tell the user to provide a media file URL/path.
            Toast.makeText(
                    VideoPlayActivity.this,
                    "Please edit VideoBuffer Activity, and set path"
                            + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
            return;
        } else {
            Uri uri = Uri.parse(path);
            mVideoView.setVideoURI(uri);
            mVideoView.setMediaController(null);
            mVideoView.setOnInfoListener(this);
            mVideoView.setOnBufferingUpdateListener(this);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                    Log.e("SD_TRACE", "video width: " + mediaPlayer.getVideoWidth());
                    Log.e("SD_TRACE", "video height: " + mediaPlayer.getVideoHeight());
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();

                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                break;
        }
        return true;
    }
}
