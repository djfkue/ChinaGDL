package com.argonmobile.chinagdl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.argonmobile.chinagdl.data.VideoItem;
import com.argonmobile.chinagdl.model.VideoItemsModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements VideoItemsModel.OnRequestObserver {

    private final static String LOG_TAG = HomeFragment.class.getSimpleName();

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private VideoListAdapter mVideoListAdapter;
    private RecyclerView mRecyclerView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VideoItemsModel.getInstance().registerOnRequestObserver(this);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        VideoItemsModel.getInstance().unRegisterOnRequestObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        // Set the color scheme of the SwipeRefreshLayout by providing 4 color resource ids
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setClickable(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVideoListAdapter = new VideoListAdapter(VideoItemsModel.getInstance().getAllVideos());

        mRecyclerView.setAdapter(mVideoListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");
                requestVideoList();
            }
        });

        //mSwipeRefreshLayout.setRefreshing(true);
        if (VideoItemsModel.getInstance().isRequesting()) {
            // workaround to let the swipe refresh layout to show progress
            mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
            mSwipeRefreshLayout.setRefreshing(true);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        VideoItemsModel.getInstance();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void requestVideoList() {
        VideoItemsModel.getInstance().requestVideoList();
    }

    @Override
    public void onRequestStart() {

    }

    @Override
    public void onRequestFailed() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRequestSucceed() {
        mSwipeRefreshLayout.setRefreshing(false);
        mVideoListAdapter.notifyDataSetChanged();
    }

    private class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {
        private ArrayList<VideoItem> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public ImageView mVideoThumb;
            public TextView mTitle;
            public TextView mPublishTime;
            public TextView mDuration;

            // each data item is just a string in this case
            public ViewHolder(View v) {
                super(v);
                mVideoThumb = (ImageView) v.findViewById(R.id.video_thumb);
                mTitle = (TextView) v.findViewById(R.id.video_title);
                mPublishTime = (TextView) v.findViewById(R.id.video_publish_time);
                mDuration = (TextView) v.findViewById(R.id.video_duration);
            }

            @Override
            public void onClick(View v) {
                VideoItem item = mDataset.get(getPosition());
                if ( null != item ) {
                    Intent intent = new Intent(getActivity(), VideoViewBuffer.class);
                    intent.putExtra(VideoViewBuffer.PATH,item.player);
                    startActivity(intent);
                }
            }

            public void bindData(VideoItem item) {
                mTitle.setText(item.title);
                mPublishTime.setText(item.published);

                double totalSecs = item.duration;

                int hours = (int) (totalSecs / 3600);
                int minutes = (int) ((totalSecs % 3600) / 60);
                int seconds = (int) (totalSecs % 60);

                String duration;
                if (hours > 0) {
                    duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                } else {
                    duration = String.format("%02d:%02d", minutes, seconds);
                }

                mDuration.setText(duration);

                ImageLoader.getInstance().displayImage(item.bigThumbnail, mVideoThumb,
                        ChinaGDLApplication.sDisplayOptions);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public VideoListAdapter(ArrayList<VideoItem> dataSet) {
            mDataset = dataSet;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public VideoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            v.setOnClickListener(vh);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.bindData(mDataset.get(position));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
