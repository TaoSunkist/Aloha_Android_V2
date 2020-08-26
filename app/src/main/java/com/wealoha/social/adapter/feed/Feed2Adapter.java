package com.wealoha.social.adapter.feed;

import javax.inject.Inject;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.wealoha.social.adapter.feed.BaseFeedHolder.Holder2FragCallback;
import com.wealoha.social.api.Feed2ListApiService;
import com.wealoha.social.beans.FeedType;
import com.wealoha.social.beans.Post;
import com.wealoha.social.ui.feeds.Feed2Fragment;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.widget.BaseListApiAdapter;
import com.wealoha.social.widget.MultiListViewType;

public class Feed2Adapter extends BaseListApiAdapter<Post, String> implements
        BaseFeedHolder.Holder2AdtCallback {

    @Inject
    Picasso picasso;
    public static boolean isPlayerMedia = true;
    private Fragment mFrag;
    private final int mScreenWidth;
    private Holder2FragCallback holder2FragCallback;
    private Adapter2FragmentCallback adt2FragCallback;

    public Feed2Adapter(Fragment frag, Feed2ListApiService service) {
        super(service);
        mFrag = frag;
        mScreenWidth = UiUtils.getScreenWidth(frag.getActivity().getApplicationContext());
    }

    public void setAdt2FragCallback(Adapter2FragmentCallback adt2FragCallback) {
        this.adt2FragCallback = adt2FragCallback;
    }

    @Override
    protected AbsViewHolder newViewHolder(MultiListViewType type, Post item, LayoutInflater inflater, ViewGroup parent) {
        return getFeedHolder((FeedType) type, inflater, parent);
    }

    /***
     * 获取feed 类别的holder，包括imagefeed 和videofeed
     *
     * @param type
     *            feed类型
     * @param inflater
     * @param parent
     *            父容器
     * @return BaseFeedHolder
     */
    protected BaseFeedHolder getFeedHolder(FeedType type, LayoutInflater inflater, ViewGroup parent) {
        BaseFeedHolder feedcontentHolder;
        FeedHolder feedHolder = new FeedHolder(mFrag, inflater, parent);
        switch ((FeedType) type) {
            case VideoPost:
                feedcontentHolder = new VideoFeedHolder(inflater, parent, mFrag);
                break;
            default:
                feedcontentHolder = new ImageFeedHolder(inflater, parent, mFrag);
                break;
        }
        feedcontentHolder.setHolder2FragCallback(holder2FragCallback);// 设置holder
        // 的回调监听
        feedHolder.setHolder2FragCallback(holder2FragCallback);
        feedHolder.setHolder2AdtCallback(this);// 设置holder 的回调监听
        feedHolder.addChildHolder(feedcontentHolder);
        return feedHolder;
    }

    @Override
    protected MultiListViewType getItemMultiViewType(Post item) {
        return item.getType();
    }

    @Override
    protected boolean isMultiListView() {
        return true;
    }

    @Override
    protected int getMultiListViewTypeCount() {
        return FeedType.values().length;
    }

    public static boolean isPlaying = true;
    MediaPlayer mMediaPlayer;

    @Override
    protected void fillView(AbsViewHolder holder, Post post, int position, View convertView) {
        fillFeedView(holder, post, position, convertView);
        XL.i("REMOVE_TAG", "FEED:" + position);
    }

    /***
     * 填充feed 类型的视图，包括imageView 和videoView
     *
     * @param holder
     * @param post
     *            数据
     * @param position
     *            item位置
     * @param convertView
     * @return void
     */
    protected void fillFeedView(AbsViewHolder holder, Post post, int position, View convertView) {
        FeedHolder feedHolder = (FeedHolder) holder;
        if (!Feed2Fragment.views.containsKey(Integer.valueOf(position))) {
            Feed2Fragment.views.put(Integer.valueOf(position), convertView);
        }
        if (feedHolder.getContentHolder() instanceof VideoFeedHolder) {
            VideoFeedHolder videoFeedHolder = (VideoFeedHolder) feedHolder.getContentHolder();
            videoFeedHolder.resetViewData(post, position);
        }
        feedHolder.resetViewData(post, position);
    }

    @Override
    public void deletePostCallback(int position) {
        removeItem(position);
        if (adt2FragCallback != null) {
            adt2FragCallback.deletePostCallback();
        }
    }

    static public int firstItemIndex = 0;

    public void isPlayerMedia(int i) {
        firstItemIndex = i;
        notifyDataSetChanged();
    }

    /***
     * holder 回调frag 的callback，应该在listview setadapter 之前调用
     *
     * @param callback
     */
    @Override
    public void setHolder2FragCallback(Holder2FragCallback callback) {
        holder2FragCallback = callback;
    }

    public void getCurrentHolder(int i) {

    }

    public interface Adapter2FragmentCallback {

        public void deletePostCallback();
    }
}
