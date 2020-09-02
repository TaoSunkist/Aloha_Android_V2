package com.wealoha.social.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.FindYouResult;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.view.custom.CircleImageView;

public class FindYouAdapter extends BaseAdapter {

    @Inject
    FontUtil font;

    @Inject
    Context context;

    @Inject
    Context mContext;
    private List<User> mUsers;
    private Map<String, String> mHighlightMap;

    public FindYouAdapter() {
        Injector.inject(this);
        mUsers = new ArrayList<User>();
        mHighlightMap = new HashMap<String, String>();
    }

    public void clearData() {
        if (mUsers != null) {
            mUsers.clear();
        }

        if (mHighlightMap != null) {
            mHighlightMap.clear();
        }
    }

    public void setData(FindYouResult result) {
        if (result != null) {
            if (result.getList() != null) {
                mUsers.addAll(result.getList());
            }
            if (result.getHighlightMap() != null) {
                mHighlightMap.putAll(result.getHighlightMap());
            }
        }
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_findyou_userlist, parent, false);
            font.changeFonts((ViewGroup) convertView, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
            holder.userPhoto = (CircleImageView) convertView.findViewById(R.id.user_photo);
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.matchOrNot = (TextView) convertView.findViewById(R.id.macht_or_not);
            convertView.setTag(holder);
        } else {
            // mListItem = (UserListItem) convertView.getTag();
            // convertView = mListItem.initView(user);
            holder = (ViewHolder) convertView.getTag();
        }
        Picasso.get().load(ImageUtil.getImageUrl(user.getAvatarImage().getId(), 100, CropMode.ScaleCenterCrop)).placeholder(R.drawable.default_photo).into(holder.userPhoto);
        // 标出高亮
        if (mHighlightMap == null || mHighlightMap.size() <= 0) {
            holder.userName.setText(user.getName());
        } else {
            holder.userName.setText(StringUtil.foregroundHight(user.getName(), mHighlightMap.get(user.getId())));
        }

        if (user.getMatch()) {
            holder.matchOrNot.setVisibility(View.VISIBLE);
        } else {
            holder.matchOrNot.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {

        CircleImageView userPhoto;
        TextView userName;
        TextView matchOrNot;
    }
}
