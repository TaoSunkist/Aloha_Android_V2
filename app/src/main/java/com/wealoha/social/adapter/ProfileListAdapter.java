package com.wealoha.social.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.app.Fragment;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.wealoha.social.R;
import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.FeedResult;
import com.wealoha.social.beans.User;
import com.wealoha.social.fragment.FeedFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.listitem.FeedItemHolder;
import com.wealoha.social.view.custom.listitem.ProfileHeaderHolder;
import com.wealoha.social.view.custom.listitem.ProfileImagesHolder;
import com.wealoha.social.view.custom.listitem.ProfileInfoHolder;

/**
 * Profile数据<br/>
 * <p>
 * 结构: 同时只有两个类型的view存在<br/>
 * 0: header 1...xx: 内容
 *
 * @author superman
 * @author javamonk
 * @date 2014-11-19 12:39:14
 * @see
 * @since
 */
public class ProfileListAdapter extends BaseAdapter implements OnClickListener {

    private final String TAG = getClass().getSimpleName();

    @Inject
    Context context;

    @Inject
    ContextUtil contextUtil;
    private List<Feed> mList; // 数据
    private Map<String, User> mUserMap;
    private Map<String, Integer> mCommentCountMap;
    private Map<String, Integer> mLikeCountMap;
    private ViewType mViewType; // 当前显示的类型
    private boolean showHeader; // 是否显示header
    private User mUser;
    private boolean isMe;
    private ViewGroup mHeader;
    private ProfileHeaderHolder mHeaderHolder;
    private ListView mParentList;
    private Fragment fragment;

    private int mFeedItemType = 0;

    /**
     * 视图类型
     */
    public static enum ViewType {
        Single, Triple, Profile, Black;
    }

    public ProfileListAdapter(ListView listView, Fragment parentFragment,//
                              User user, ProfileHeaderHolder headerHolder, ViewType viewType, int feedItemType) {
        fragment = parentFragment;
        mParentList = listView;
        mFeedItemType = feedItemType;
        Injector.inject(this);
        mUser = user;
        if (user == null) {
            mUser = User.Companion.fake(true, false);
        }

        isMe = mUser.getId().equals(contextUtil.getCurrentUser().getId());

        mViewType = viewType;
        if (headerHolder == null) {
            showHeader = false;
        } else {
            showHeader = true;
            this.mHeader = headerHolder.getView(this, mUser, isMe);
            if (mUser.getHasPrivacy()) {
                mViewType = ViewType.Black;
            } else if (mUser.getPostCount() < 1 && !mUser.getMe()) {
                mViewType = ViewType.Profile;
            }
            mHeaderHolder = headerHolder;
            mHeaderHolder.setCurrentView(mViewType);
            mHeaderHolder.changeViewType(mViewType);
        }

        mList = new ArrayList<Feed>();
        mUserMap = new HashMap<String, User>();
        mLikeCountMap = new HashMap<String, Integer>();
        mCommentCountMap = new HashMap<String, Integer>();
        // this.mHeader = header;
    }

    /**
     * 修改显示的方式
     *
     *  viewType
     */
    public void changeViewType(ViewType viewType) {
        if (mViewType == viewType) {
            return;
        }
        mViewType = viewType;
        if (mHeaderHolder != null) {
            Log.i("CHECKBOX", "mHeaderHolder");
            // mHeaderHolder.setCurrentView(mViewType);
        }

        notifyDataSetChanged();
    }

    /**
     * 更新header
     */
    public void notifyHeaderDataSetChanged(ViewGroup header) {
        if (header != null) {
            mHeader = header;
            notifyDataSetChanged();
        }
    }

    /**
     * 更新feed（删除）
     */
    public void notifyFeedDataSetChanged(String feedid) {
        if (!TextUtils.isEmpty(feedid)) {
            for (int i = 0; i < mList.size(); i++) {
                Feed feed = mList.get(i);
                if (feedid.equals(feed.postId)) {
                    if (fragment != null && fragment instanceof FeedFragment) {
                        mList.remove(i);
                        ((FeedFragment) fragment).deleteCallback(mList);
                    }
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    /**
     * 拿到了新数据，更新(在底部更新数据，不清空之前的数据，新旧数据叠加)
     *
     *  result
     */
    public void notifyDataSetChanged(FeedResult result) {
        if (result != null && result.getList() != null) {
            mList.addAll(result.getList());
            mUserMap.putAll(result.getUserMap());
            mLikeCountMap.putAll(result.getLikeCountMap());
            mCommentCountMap.putAll(result.getCommentCountMap());
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        if (mList != null && mUserMap != null && mLikeCountMap != null && mCommentCountMap != null) {
            mList.clear();
            mUserMap.clear();
            mLikeCountMap.clear();
            mCommentCountMap.clear();
        }
    }

    /**
     * 拿到了新数据，更新(在顶部更新数据，清空之前的数据)
     *
     *  result
     */
    public void notifyTopDataSetChanged(FeedResult result) {
        if (result.getList() != null && result.getList().size() >= 0) {
            mList.addAll(result.getList());
            mUserMap.putAll(result.getUserMap());
            mLikeCountMap.putAll(result.getLikeCountMap());
            mCommentCountMap.putAll(result.getCommentCountMap());
            // 清空赞
            FeedItemHolder.clearPraiseMap();
            notifyDataSetChanged();
        }
    }

    public void notifyTopDataSetChangedByList(List<Feed> list, Map<String, User> userMap, Map<String, Integer> commentCountMap, Map<String, Integer> likeCountMap) {
        mList = list;
        mUserMap = userMap;
        mLikeCountMap = likeCountMap;
        mCommentCountMap = commentCountMap;
        notifyDataSetChanged();
    }

    public void notifyTopDataSetChangedByList(FeedResult result) {
        mList = result.getList();
        mUserMap = result.getUserMap();
        mLikeCountMap = result.getLikeCountMap();
        mCommentCountMap = result.getCommentCountMap();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int rows = 0;
        switch (mViewType) {
            case Single:
                rows = mList.size();
                break;
            case Triple:
                rows = mList.size() / 3;
                if (mList.size() % 3 > 0) {
                    rows++;
                }
                break;
            case Profile:
            case Black:
                rows = 1;
            default:
                break;
        }
        if (showHeader) {
            // 有header多一行
            rows++;
        }

        XL.d(TAG, "List行数: " + rows);
        return rows;
    }

    @Override
    public Object getItem(int position) {
        if (showHeader) {
            // 有header的情况下，position从1开始
            position--;
        }
        switch (mViewType) {
            case Single:
                return mList.get(position);
            case Triple:
                // 三个一行的模式
                if (mList.size() == 0) {
                    return Collections.emptyList();
                }
                int start = position * 3;
                int to = start + 3;
                if (to > mList.size()) {
                    to = mList.size();
                }
                XL.d(TAG, "返回数据: " + start + " " + to);
                return mList.subList(start, to);
            case Profile:
                return mUser;
            default:
                break;
        }
        return null;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (showHeader && position == 0) {
            // 第一个位置的类型永远是header
            XL.d(TAG, "返回View类型: " + 0);
            return 0;
        } else {
            // 第二行开始，类型就是当前的
            // 三个一行，一个一行，profile三种
            XL.d(TAG, "返回View类型: " + mViewType);
            return showHeader ? 1 : 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        if (showHeader) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (showHeader && position == 0) {
            return mHeader;
        } else {
            if (mViewType == ViewType.Single) {
                // 先处理下视图切换带来的不兼容
                convertView = clearConvertView(convertView, FeedItemHolder.class);
                Feed item = (Feed) getItem(position);
                User user = mUserMap.get(item.userId);
                int commentCount = mCommentCountMap.get(item.postId);
                FeedItemHolder fth;
                if (convertView == null) {
                    fth = new FeedItemHolder(this, parent);
                    convertView = fth.getView(user, item, commentCount, mLikeCountMap, mFeedItemType);
                    convertView.setTag(fth);
                    // XL.d(TAG, "获取到缓存的视图: " +
                    // convertView.getTag().getClass().getName());
                } else {
                    fth = (FeedItemHolder) convertView.getTag();
                    convertView = fth.getView(user, item, commentCount, mLikeCountMap, mFeedItemType);
                    // XL.d(TAG, "获取到缓存的视图: " + feedItem);
                }
                // 展示单个的
            } else if (mViewType == ViewType.Triple) {
                ProfileImagesHolder contentPics;
                @SuppressWarnings("unchecked")
                List<Feed> item = (List<Feed>) getItem(position);
                // 先处理下视图切换带来的不兼容
                convertView = clearConvertView(convertView, ProfileImagesHolder.class);
                // 缓存处理
                if (convertView == null) {
                    contentPics = new ProfileImagesHolder(item, parent);
                    convertView = contentPics.getView();
                    convertView.setTag(contentPics);
                } else {
                    contentPics = (ProfileImagesHolder) convertView.getTag();
                    contentPics.updateData(item);
                }
            } else if (mViewType == ViewType.Profile) {
                // 展示 profile
                convertView = clearConvertView(convertView, ProfileInfoHolder.class);
                return new ProfileInfoHolder(mUser, isMe, parent).getView();
            } else if (mViewType == ViewType.Black) {
                XL.i("PROFILE_LIST_ADAPTER", "------");
                // 在对方的黑名单中
                ViewGroup viewgroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_profile_block, parent, false);
                viewgroup.getLayoutParams().height = UiUtils.getScreenHeight(context) - UiUtils.dip2px(context, 290);
                return viewgroup;
            }
        }
        return convertView;
    }

    /**
     *  设定文件
     * @return void 返回类型
     * @throws
     * @Title: clearConvertView
     * @Description: 处理下视图切换带来的不兼容
     */
    private View clearConvertView(View convertView, @SuppressWarnings("rawtypes") Class clazz) {
        if (convertView != null) {
            Object view = convertView.getTag();
            if (view == null || !(view.getClass().equals(clazz))) {
                // 视图转换，丢弃
                convertView = null;
                XL.d(TAG, "丢弃上个布局方式的视图: " + view);
            }
        }
        return convertView;
    }

    /**
     *  设定文件
     * @return void 返回类型
     * @throws
     * @Title: changePraiseCount
     * @Description: 点赞之后回调，修改赞数
     */
    public void changePraiseCount(String postId) {
        // +1
        if (mLikeCountMap != null && mLikeCountMap.get(postId) != null && !TextUtils.isEmpty(postId)) {
            mLikeCountMap.put(postId, mLikeCountMap.get(postId) + 1);
        }
    }

    public void changeListDividerHeight(int divider) {
        Log.i("LIST_DIVIDER", "divider:" + divider);
        mParentList.setDividerHeight(divider);
        // mParentList.setDivider(new ColorDrawable(0xffffff));
    }

    @Override
    public void onClick(View v) {
    }
}
