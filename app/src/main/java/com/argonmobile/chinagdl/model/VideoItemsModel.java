package com.argonmobile.chinagdl.model;

import com.argonmobile.chinagdl.data.FetchVideoListTask;
import com.argonmobile.chinagdl.data.VideoItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class VideoItemsModel {

    private static VideoItemsModel mInstance;

    private ArrayList<VideoItem> mVideoItems = new ArrayList<VideoItem>();
    private LinkedHashMap<String, ArrayList<VideoItem>> mPlayLists = new LinkedHashMap<String, ArrayList<VideoItem>>();

    private ArrayList<OnRequestObserver> mOnRequestObservers = new ArrayList<OnRequestObserver>();

    private boolean mIsRequesting = false;

    public static VideoItemsModel getInstance() {
        if (mInstance == null) {
            mInstance = new VideoItemsModel();
            //new FetchVideoListTask().execute();
        }
        return mInstance;
    }

    public void registerOnRequestObserver(OnRequestObserver onRequestObserver) {
        mOnRequestObservers.add(onRequestObserver);
    }

    public void unRegisterOnRequestObserver(OnRequestObserver onRequestObserver) {
        mOnRequestObservers.remove(onRequestObserver);
    }

    public boolean isRequesting() {
        return mIsRequesting;
    }

    public void requestVideoList() {
        FetchVideoListTask fetchVideoListTask = new FetchVideoListTask();
        fetchVideoListTask.setOnFetchVideoListListener(new FetchVideoListTask.OnFetchVideoListListener() {
            @Override
            public void onFetchStart() {
                mIsRequesting = true;
                for (OnRequestObserver observer: mOnRequestObservers) {
                    observer.onRequestStart();
                }
            }

            @Override
            public void onFetchFailed() {
                mIsRequesting = false;
                for (OnRequestObserver observer: mOnRequestObservers) {
                    observer.onRequestFailed();
                }
            }

            @Override
            public void onFetchCancelled() {
                mIsRequesting = false;
                for (OnRequestObserver observer: mOnRequestObservers) {
                    observer.onRequestFailed();
                }
            }

            @Override
            public void onFetchSucceed(ArrayList<VideoItem> videoList, LinkedHashMap<String, ArrayList<VideoItem>> playList) {
                mIsRequesting = false;
                mPlayLists.clear();
                mPlayLists.putAll(playList);
                fetchVideoListSucceed(videoList);
            }
        });

        fetchVideoListTask.execute();
    }

    public void fetchVideoListSucceed(ArrayList<VideoItem> videoList) {
        mVideoItems.clear();
        mVideoItems.addAll(videoList);
        for (OnRequestObserver observer: mOnRequestObservers) {
            observer.onRequestSucceed();
        }
    }

    public ArrayList<VideoItem> getAllVideos() {
        return mVideoItems;
    }

    public LinkedHashMap<String, ArrayList<VideoItem>> getPlayLists() {
        return mPlayLists;
    }

    public interface OnRequestObserver {
        public void onRequestStart();
        public void onRequestFailed();
        public void onRequestSucceed();
    }
}
