package com.wealoha.social.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.activity.LeaveCommentAct;
import com.wealoha.social.beans.Comment;
import com.wealoha.social.beans.CommentResult;
import com.wealoha.social.beans.Feed;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.TimeUtil;
import com.wealoha.social.view.custom.CircleImageView;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 评论列表
 * @copyright wealoha.com
 * @Date:2014-11-21
 */
public class CommentAdapter extends BaseAdapter/* implements OnSlideListener */{

	private LeaveCommentAct leaveCommentAct;
	private List<Comment> mComments = new ArrayList<Comment>();
	private Map<String, User> mUsers = new HashMap<String, User>();
	private Date d;
	@Inject
	Picasso picasso;
	@Inject
	ContextUtil contextUtil;
	@Inject
	Context context;
	@Inject
	FontUtil font;

	public CommentAdapter(LeaveCommentAct leaveCommentAct, List<Comment> mComments, Map<String, User> mUsers, Feed feed) {
		this.leaveCommentAct = leaveCommentAct;
		this.mComments = mComments;
		this.mUsers = mUsers;
		Injector.inject(this);
	}

	public void notifyDataSetChangedByDelete(List<Comment> comment) {
		if (comment != null) {
			// this.mUsers.putAll(result.getData().getUserMap());
			this.mComments = comment;
			this.notifyDataSetChanged();
		}
	}

	public void notifyDataSetChanged(ApiResponse<CommentResult> apiResponse) {
		if (apiResponse != null) {
			this.mUsers.putAll(apiResponse.getData().getUserMap());
			// 放到數據開頭
			this.mComments.addAll(apiResponse.getData().getList());
			notifyDataSetChanged();
		}
	}

	public void notifyDataSetChanged(List<Comment> comment, Map<String, User> userMap) {
		if (comment != null) {
			// 放到數據開頭
			this.mUsers = userMap;
			this.mComments = comment;
			notifyDataSetChanged();
		}
	}

	public void notifyDataSetChangedOnTop(ApiResponse<CommentResult> apiResponse) {
		if (apiResponse != null && apiResponse.getData().getList().size() > 0) {
			this.mUsers.putAll(apiResponse.getData().getUserMap());
			// 放到數據開頭
			this.mComments.addAll(0, apiResponse.getData().getList());
			notifyDataSetChanged();
		}
	}

	/**
	 * @Title: notifyDataSetAllChanged
	 * @Description: 清空旧数据，刷新新数据
	 * @param @param result 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void notifyDataSetAllChanged(ApiResponse<CommentResult> apiResponse) {
		if (apiResponse != null && apiResponse.getData().getList().size() > 0) {
			this.mUsers = apiResponse.getData().getUserMap();
			this.mComments = apiResponse.getData().getList();
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		if (mComments != null && mComments.size() > 0) {
			return mComments.size();
		} else {
			return 0;
		}
	}

	@Override
	public Comment getItem(int position) {
		return mComments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		d = new Date();
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = leaveCommentAct.getLayoutInflater().inflate(R.layout.item_comment_content, parent, false);
			font.changeFonts((ViewGroup) convertView, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
			viewHolder = new ViewHolder(convertView);
			font.changeViewFont(viewHolder.content, Font.ENCODESANSCOMPRESSED_400_REGULAR);
			convertView.setTag(viewHolder);
		} else {
			// 获得缓存
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Comment comment = mComments.get(position);
		picasso.load(ImageUtil.getImageUrl(mUsers.get(comment.userId).getAvatarImage().getId(), viewHolder.userPhoto.getWidth(), CropMode.ScaleCenterCrop)).placeholder(R.drawable.default_photo).into(viewHolder.userPhoto);
		viewHolder.userName.setText(mUsers.get(comment.userId).getName());
		viewHolder.timeAgo.setText(TimeUtil.getDistanceTimeForApp(leaveCommentAct, d.getTime(), comment.createTimeMillis));

		String commentText = comment.comment;
		if (comment.replyUserId != null) {
			// 如果是回覆
			String replyUser = mUsers.get(comment.replyUserId).getName();
			commentText = context.getString(R.string.comment_in_reply, replyUser, commentText);
		}
		viewHolder.content.setText(commentText);

		final User userinfo = mUsers.get(comment.userId);
		viewHolder.userPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putParcelable(User.TAG, userinfo);
				bundle.putString("refer_key", GlobalConstants.WhereIsComeFrom.FEED_COMMENT_TO_PROFILE);
				((BaseFragAct) contextUtil.getForegroundAct()).startFragment(Profile2Fragment.class, bundle, true);
			}
		});
		// final boolean flag = comment.mine;
		// if (!mFeed.mine) {
		// if (!comment.mine) {
		// // viewHolder.comment_content_rl.setEnabled(false);
		// viewHolder.comment_content_rl.setOnTouchListener(new
		// OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// Log.i("COMMENT_FLAG", "v.getY:" + v.getY());
		// if (event.getAction() == MotionEvent.ACTION_MOVE) {
		// return true;
		// }
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// leaveCommentAct.onItemClick(p);
		// }
		// return true;
		// }
		// });
		// } else {
		// viewHolder.comment_content_rl.setOnTouchListener(new
		// OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// Log.i("COMMENT_FLAG", "flag:" + flag + "---feedflag:" + mFeed.mine);
		// return false;
		// }
		// });
		// }
		// }
		// if (!(mFeed.mine || mComments.get(position).mine)) {
		// viewHolder.comment_content_rl.setOnTouchListener(new
		// OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// return true;
		// }
		// });
		// }
		return convertView;
	}

	static class ViewHolder {

		CircleImageView userPhoto;
		TextView userName;
		TextView content;
		TextView timeAgo;

		public ViewHolder(View view) {
			userPhoto = (CircleImageView) view.findViewById(R.id.comments_user_photo);
			userName = (TextView) view.findViewById(R.id.comments_user_name);
			content = (TextView) view.findViewById(R.id.comments_content_tv);
			timeAgo = (TextView) view.findViewById(R.id.comments_time_ago);

			// slideView布局中的删除控件
		}

	}
}
