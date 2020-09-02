package com.wealoha.social.adapter;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.R;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.message.InboxSession;
import com.wealoha.social.beans.message.Message;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ChatUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.TimeUtil;
import com.wealoha.social.view.custom.CircleImageView;

public class ChatListAdapter extends BaseAdapter/* implements OnSlideListener */ {

    public static final String TAG = ChatListAdapter.class.getSimpleName();
    private MainAct mMainAct;
    private List<InboxSession> mSessionList;

    // 里面保存了信息的数量，来自noticebarcontroller
    private Map<String, Message> mMessageMap;
    private ListView mListView;

    @Inject
    LayoutInflater layoutInflater;
    @Inject
    FontUtil fontUtil;
    @Inject
    ChatUtil mChatUtil;
    // private RelativeLayout.LayoutParams mRelativeParams;
    // private RelativeLayout chat_bg_no_chat_data_rl;
    public int mContentWidth;
    private RelativeLayout chat_bg_no_chat_data_rl;

    public ChatListAdapter(MainAct mMainAct2, List<InboxSession> mSessionList2, Map<String, Message> mMessageMap2, Map<String, Integer> getmSessionMap, ListView listView, RelativeLayout chat_bg_no_chat_data_rl) {
        this.mMainAct = mMainAct2;
        this.mSessionList = mSessionList2;
        this.mMessageMap = mMessageMap2;
        mListView = listView;
        this.chat_bg_no_chat_data_rl = chat_bg_no_chat_data_rl;
        // mRelativeParams = new RelativeLayout.LayoutParams(,
        // RelativeLayout.LayoutParams.WRAP_CONTENT);
        mContentWidth = (int) (mMainAct2.mScreenWidth * 0.7 + 0.5f);
        Injector.inject(this);
    }

    @Override
    public int getCount() {
        if (mSessionList == null || mSessionList.size() <= 0) {
            return 0;
        } else {
            return mSessionList.size();
        }
    }

    @Override
    public InboxSession getItem(int position) {
        return mSessionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder chatListHolder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_chat_list, mListView, false);
            chatListHolder = new ViewHolder(convertView);
            convertView.setTag(chatListHolder);
        } else {
            chatListHolder = (ViewHolder) convertView.getTag();
        }
        InboxSession session = getItem(position);
        Image image = session.getUser().getAvatarImage();
        Picasso.get().load(ImageUtil.getImageUrl(image.getId(), GlobalConstants.ImageSize.AVATAR_ROUND_SMALL, CropMode.ScaleCenterCrop)).placeholder(R.drawable.default_photo).into(chatListHolder.item_chat_user_photo);
        chatListHolder.item_chat_user_name.setText(mSessionList.get(position).getUser().getName());
        chatListHolder.item_chat_time_stamp.setText(TimeUtil.getDistanceTimeForApp(mMainAct, new Date().getTime(), session.getUpdateTimeMillis()));
        String userid = mSessionList.get(position).getUser().getId();
        // 显示最新一条摘要
        String messageSummary = mChatUtil.getMessageSummary(mMessageMap.get(userid));
        chatListHolder.item_chat_content_tv.getLayoutParams().width = mContentWidth;
        chatListHolder.item_chat_content_tv.setText(messageSummary == null ? "" : messageSummary);
        // chatListHolder.item_chat_content_tv.setVisibility(View.VISIBLE);
        // 显示角标
        if (session.getUnread() > 0) {
            chatListHolder.item_chat_user_subscript.setVisibility(View.VISIBLE);
            chatListHolder.item_chat_user_subscript.setText(session.getUnread() > 99 ? "···" : session.getUnread() + "");
        } else {
            chatListHolder.item_chat_user_subscript.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {

        TextView item_chat_content_tv;
        TextView item_chat_time_stamp;
        TextView item_chat_sub;
        CircleImageView item_chat_user_photo;
        TextView item_chat_user_name;
        TextView item_chat_user_subscript;

        public ViewHolder(View view) {
            // FIXME 提取
            fontUtil.changeFonts((ViewGroup) view, Font.ENCODESANSCOMPRESSED_400_REGULAR);
            item_chat_user_photo = (CircleImageView) view.findViewById(R.id.item_chat_user_photo);
            item_chat_user_name = (TextView) view.findViewById(R.id.item_chat_user_name);
            fontUtil.changeViewFont(item_chat_user_name, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
            item_chat_content_tv = (TextView) view.findViewById(R.id.item_chat_content_tv);
            item_chat_time_stamp = (TextView) view.findViewById(R.id.item_chat_time_stamp);
            fontUtil.changeViewFont(item_chat_time_stamp, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
            item_chat_user_subscript = (TextView) view.findViewById(R.id.item_chat_user_subscript);
            item_chat_user_subscript.setTypeface(Typeface.DEFAULT);
            view.setTag(this);
        }
    }

    public void notifyDataChage(List<InboxSession> mSessionList, Map<String, Message> mMessageMap) {
        this.mSessionList = mSessionList;
        this.mMessageMap = mMessageMap;
        if (mSessionList == null || mSessionList.size() <= 0) {
            mListView.setVisibility(View.GONE);
            chat_bg_no_chat_data_rl.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            chat_bg_no_chat_data_rl.setVisibility(View.GONE);
        }
        this.notifyDataSetChanged();
    }

}
