package com.argonmobile.chinagdl;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link com.argonmobile.chinagdl.PlayListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayListFragment extends Fragment implements VideoItemsModel.OnRequestObserver {

    private final static String LOG_TAG = PlayListFragment.class.getSimpleName();

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private PlayListAdapter mPlayListsAdapter;
    private RecyclerView mRecyclerView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoListFragment.
     */
    public static PlayListFragment newInstance() {
        PlayListFragment fragment = new PlayListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PlayListFragment() {
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
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
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

        mPlayListsAdapter = new PlayListAdapter(VideoItemsModel.getInstance().getPlayLists());

        mRecyclerView.setAdapter(mPlayListsAdapter);

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
        mPlayListsAdapter.notifyDataSetChanged();
    }

    private class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
        private LinkedHashMap<String, ArrayList<VideoItem>> mDataset;
        private ArrayList<String> mPlayListNames;
        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            public TextView mPlayListName;
            public TextView mPlayListCount;
            public ViewGroup mVideoItemContainer;
            public TextView mViewMore;
            // each data item is just a string in this case
            public ViewHolder(View v) {
                super(v);
                mPlayListName = (TextView) v.findViewById(R.id.playlist_name);
                mPlayListCount = (TextView) v.findViewById(R.id.playlist_count);
                mVideoItemContainer = (ViewGroup) v.findViewById(R.id.video_container);
                mViewMore = (TextView) v.findViewById(R.id.view_more);
            }

            @Override
            public void onClick(View v) {
            }

            public void bindData(String playListName, ArrayList<VideoItem> items) {

                int videoCount = items.size();
                if (videoCount <= mVideoItemContainer.getChildCount()) {
                    mViewMore.setVisibility(View.GONE);
                } else {
                    mViewMore.setVisibility(View.VISIBLE);
                }

                mPlayListCount.setText("" + videoCount + " videos");
                mPlayListName.setText(playListName);
                if (playListName.contains("Android") || playListName.contains("Mobile")) {
                    mPlayListName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_android_grey600_24dp,
                            0,
                            0,
                            0);
                } else {
                    mPlayListName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_whatshot_grey600_24dp,
                            0,
                            0,
                            0);
                }

                for (int i = 0; i < mVideoItemContainer.getChildCount(); i++) {
                    if (i < videoCount) {
                        bindVideoItem(mVideoItemContainer.getChildAt(i), items.get(i));
                        mVideoItemContainer.getChildAt(i).setVisibility(View.VISIBLE);
                    } else {
                        mVideoItemContainer.getChildAt(i).setVisibility(View.GONE);
                    }
                }

//
            }

            private void bindVideoItem(View videoItemView, VideoItem item) {

                ImageView videoThumb = (ImageView) videoItemView.findViewById(R.id.video_thumb);
                TextView title = (TextView) videoItemView.findViewById(R.id.video_title);
                TextView publishTime = (TextView) videoItemView.findViewById(R.id.video_publish_time);
                TextView durationView = (TextView) videoItemView.findViewById(R.id.video_duration);

                title.setText(item.title);
                publishTime.setText(item.published);

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

                durationView.setText(duration);

                ImageLoader.getInstance().displayImage(item.bigThumbnail, videoThumb,
                        ChinaGDLApplication.sDisplayOptions);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public PlayListAdapter(LinkedHashMap<String, ArrayList<VideoItem>> playLists) {
            mDataset = playLists;
            mPlayListNames = new ArrayList<String>(playLists.keySet());
        }

        // Create new views (invoked by the layout manager)
        @Override
        public PlayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.play_list_card_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            v.setOnClickListener(vh);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            mPlayListNames = new ArrayList<String>(mDataset.keySet());
            String playListName = mPlayListNames.get(position);
            holder.bindData(playListName, mDataset.get(playListName));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
