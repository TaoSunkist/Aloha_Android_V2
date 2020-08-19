package com.wealoha.social.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.UserListResult;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.view.custom.CircleImageView;

public class SwipeMenuAdapter extends BaseAdapter {

    @Inject
    Context context;
    @Inject
    Picasso picasso;
    @Inject
    FontUtil font;

    private List<User> mUsers;

    private boolean showMatch;

    /**
     * @param result
     * @param showMatch 是否显示右侧的已匹配
     */
    public SwipeMenuAdapter(UserListResult result, boolean showMatch) {
        Injector.inject(this);
        if (result.list != null && result.list.size() > 0) {
            this.mUsers = result.list;
        } else {
            this.mUsers = new ArrayList<User>();
        }
        // if (users != null && users.size() > 0) {
        // this.mUsers = users;
        // } else {
        // this.mUsers = new ArrayList<User>();
        // }
        this.showMatch = showMatch;

    }

    public void notifyDataSetChanged(Result<UserListResult> result) {
        if (result != null && result.data.list.size() > 0) {
            mUsers.addAll(result.data.list);
            super.notifyDataSetChanged();
        }
    }

    public void notifyDataSetChangedByList(List<User> users) {
        if (users != null) {
            mUsers.clear();
            mUsers.addAll(users);
            super.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (mUsers.size() < 0) {
            return 0;
        }
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = (User) getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            // mListItem = new UserListItem();
            // convertView = mListItem.initView(user);
            // convertView.setTag(mListItem);
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_swipe_userlist, parent, false);
            font.changeFonts((ViewGroup) convertView, Font.ENCODESANSCOMPRESSED_400_REGULAR);
            holder.userPhoto = (CircleImageView) convertView.findViewById(R.id.user_photo);
            holder.userPhotoCount = (TextView) convertView.findViewById(R.id.user_photo_count);
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            font.changeViewFont(holder.userName, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
            holder.userInfos = (TextView) convertView.findViewById(R.id.user_infos);
            holder.matchOrNot = (TextView) convertView.findViewById(R.id.macht_or_not);
            convertView.setTag(holder);
        } else {
            // mListItem = (UserListItem) convertView.getTag();
            // convertView = mListItem.initView(user);
            holder = (ViewHolder) convertView.getTag();
        }
        picasso.load(ImageUtil.getImageUrl(user.getAvatarImage().getId(), GlobalConstants.ImageSize.AVATAR_ROUND_SMALL, CropMode.ScaleCenterCrop)).placeholder(R.drawable.default_photo).into(holder.userPhoto);
        holder.userName.setText(user.getName());
        if (showMatch && user.getMatch()) {
            holder.matchOrNot.setVisibility(View.VISIBLE);
        } else {
            holder.matchOrNot.setVisibility(View.GONE);
        }
        holder.userPhotoCount.setText(context.getString(R.string.aloha_time_photo_count, String.valueOf(user.getPostCount())));
        holder.userInfos.setText(user.getAge() + "·" + user.getHeight() + "·" + user.getWeight() + "·" + StringUtil.getUserZodiac(user.getZodiac(), context));
        return convertView;
    }

    static class ViewHolder {

        CircleImageView userPhoto;
        TextView userName;
        TextView userPhotoCount;
        TextView matchOrNot;
        TextView userInfos;

    }
}
