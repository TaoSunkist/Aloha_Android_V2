package com.wealoha.social.view.custom.dialog;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Bus;
import com.wealoha.social.AppApplication;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.adapter.ChatMsgViewAdapter;
import com.wealoha.social.adapter.ProfileListAdapter;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.feed.FeedService;
import com.wealoha.social.beans.message.ImageMessage;
import com.wealoha.social.beans.message.Message;
import com.wealoha.social.beans.user.UserService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.event.AnginSendSmsEvent;
import com.wealoha.social.event.ControlUserEvent;
import com.wealoha.social.event.DeleteCommentEvent;
import com.wealoha.social.event.DeleteSessionEvent;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;

public class ReportBlackAlohaPopup implements OnClickListener {

	private String TAG = getClass().getName();
	@Inject
	Context context;
	@Inject
	ContextUtil contextUtil;
	@Inject
	FeedService mFeedService;
	@Inject
	UserService mUserService;
	@Inject
	FontUtil mFontUtil;
	@Inject
	protected Bus bus;
	@InjectView(R.id.popup_delete)
	TextView mDelete;
	@InjectView(R.id.popup_delete_line)
	View mDeleteLine;
	@InjectView(R.id.popup_logout)
	TextView mLogout;
	@InjectView(R.id.popup_logout_line)
	View mLogoutLine;
	@InjectView(R.id.popup_title)
	TextView mTitle;
	@InjectView(R.id.popup_title_line)
	View mTitleLineOne;
	@InjectView(R.id.report_user)
	TextView mReport;
	@InjectView(R.id.report_user_line)
	View mReportLine;
	@InjectView(R.id.black_user)
	TextView mBlack;
	@InjectView(R.id.black_user_line)
	View mBlackLine;
	@InjectView(R.id.dislike_user)
	TextView mDislike;
	@InjectView(R.id.remove_tag_line)
	View mRemoveTagLine;
	@InjectView(R.id.remove_tag)
	TextView mRemoveTag;
	private Context mContext;
	private ViewGroup container;
	private PopupWindow popup;
	private PostType mPostType;
	private String mId;

	private Fragment mFrag;
	private BaseAdapter baseAdapter;
	private AlertDialog superDialog;
	private User mUser;
	private LoadDataSuccess loadSuccess;

	public static enum PopupType {
		REPORT, REPORTBLACK, REPORTBLACKALOHA, LOGOUT, DELETE, CHAT_DELETE, //
		COMMENT_DELETE, LISTTYPE_LIKE, LISTTYPE_BLACK, RESEND_CHAT_TEXT, CONTROL_CHAT_SMS, //
		FIRST_NOPE_SHOW_DIALOG;
	}

	public static enum PostType {
		USER, FEED, SESSION, COMMENT_DEL, LISTTYPE_LIKE, LISTTYPE_BLACK, RESEND_CHAT_TEXT;
	}

	public ReportBlackAlohaPopup() {
		Injector.inject(this);
		container = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.report_black_aloha_layout, new LinearLayout(context), false);
		ButterKnife.inject(this, container);
	}

	public ReportBlackAlohaPopup(Fragment frag) {
		this();
		this.mFrag = frag;
	}

	public ReportBlackAlohaPopup(BaseFragAct mBaseFragAct, Fragment frag) {
		this();
		mContext = mBaseFragAct;
		this.mFrag = frag;
	}

	public ReportBlackAlohaPopup(BaseAdapter baseAdapter) {
		this();
		this.baseAdapter = baseAdapter;
	}

	public void showPopup(PopupType popuptype, PostType posttype, String id, String title, User user) {
		showPopup(popuptype, posttype, id, title, user, null);
	}

	public void showPopup(PopupType popuptype, PostType posttype, String id, String title, User user, LoadDataSuccess loadSuccess) {

		mId = id;
		// 标题
		if (TextUtils.isEmpty(title)) {
			mTitleLineOne.setVisibility(View.GONE);
		} else {
			mTitle.setText(title);
		}

		this.loadSuccess = loadSuccess;
		// 举报
		if (popuptype == PopupType.REPORT) {
			mLogout.setVisibility(View.GONE);
			mBlack.setVisibility(View.GONE);
			mDislike.setVisibility(View.GONE);
			mDelete.setVisibility(View.GONE);

			mDeleteLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mReportLine.setVisibility(View.GONE);
			mBlackLine.setVisibility(View.GONE);
			// 举报+黑名单
		} else if (popuptype == PopupType.REPORTBLACK) {
			mLogout.setVisibility(View.GONE);
			mDislike.setVisibility(View.GONE);
			mLogout.setVisibility(View.GONE);
			mDelete.setVisibility(View.GONE);

			mDeleteLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mBlackLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			// 等出
		} else if (popuptype == PopupType.LOGOUT) {
			mReport.setVisibility(View.GONE);
			mBlack.setVisibility(View.GONE);
			mDislike.setVisibility(View.GONE);
			mDelete.setVisibility(View.GONE);

			mDeleteLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mReportLine.setVisibility(View.GONE);
			mBlackLine.setVisibility(View.GONE);
			// 删除
		} else if (popuptype == PopupType.DELETE) {
			mLogout.setVisibility(View.GONE);
			mBlack.setVisibility(View.GONE);
			mDislike.setVisibility(View.GONE);
			mReport.setVisibility(View.GONE);

			mDeleteLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mReportLine.setVisibility(View.GONE);
			mBlackLine.setVisibility(View.GONE);
			// 举报+黑名单+解除aloha
		} else if (popuptype == PopupType.CHAT_DELETE) {
			mLogout.setVisibility(View.GONE);
			mBlack.setVisibility(View.GONE);
			mDislike.setVisibility(View.GONE);
			mReport.setVisibility(View.GONE);
			mDeleteLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mReportLine.setVisibility(View.GONE);
			mBlackLine.setVisibility(View.GONE);
		} else if (popuptype == PopupType.COMMENT_DELETE) {
			mPostType = PostType.COMMENT_DEL;
			mLogout.setVisibility(View.GONE);
			mBlack.setVisibility(View.GONE);
			mDislike.setVisibility(View.GONE);
			mTitle.setVisibility(View.GONE);
			mReport.setVisibility(View.GONE);
			mDeleteLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mReportLine.setVisibility(View.GONE);
			mBlackLine.setVisibility(View.GONE);
		} else if (popuptype == PopupType.LISTTYPE_LIKE) {
			mPostType = PostType.LISTTYPE_LIKE;
			mLogout.setVisibility(View.GONE);
			mBlack.setVisibility(View.GONE);
			mDislike.setVisibility(View.GONE);
			mTitle.setVisibility(View.GONE);
			mReport.setVisibility(View.GONE);
			mDeleteLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mReportLine.setVisibility(View.GONE);
			mBlackLine.setVisibility(View.GONE);
		} else if (popuptype == PopupType.LISTTYPE_BLACK) {
			mPostType = PostType.LISTTYPE_BLACK;
			mLogout.setVisibility(View.GONE);
			mBlack.setVisibility(View.GONE);
			mDislike.setVisibility(View.GONE);
			mTitle.setVisibility(View.GONE);
			mReport.setVisibility(View.GONE);
			mDeleteLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mReportLine.setVisibility(View.GONE);
			mBlackLine.setVisibility(View.GONE);
		} else if (popuptype == PopupType.RESEND_CHAT_TEXT) {
			mPostType = PostType.RESEND_CHAT_TEXT;
			mLogout.setVisibility(View.GONE);
			mBlack.setVisibility(View.GONE);
			mDislike.setVisibility(View.GONE);
			mReport.setVisibility(View.GONE);
			mRemoveTag.setVisibility(View.GONE);
			mDeleteLine.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mReportLine.setVisibility(View.GONE);
			mBlackLine.setVisibility(View.GONE);
			mTitle.setText(R.string.restart_post);
			mDelete.setText(R.string.confirm);
		} else {
			mLogout.setVisibility(View.GONE);
			mLogoutLine.setVisibility(View.GONE);
			mDelete.setVisibility(View.GONE);
			mDeleteLine.setVisibility(View.GONE);
		}

		if (user != null) {
			mUser = user;
			if (user.block) {
				mBlack.setText(R.string.remove_from_black_list);
			} else {
				mBlack.setText(R.string.add_to_black_list);
			}
		} else {
			mUser = new User();
		}

		// 举报user
		if (posttype == PostType.USER) {
			mPostType = PostType.USER;
			// serverUrl = GlobalConstants.ServerUrl.REPORT_USER;
			// 举报feed
		} else if (posttype == PostType.FEED) {
			// serverUrl = GlobalConstants.ServerUrl.REPORT_FEED;
			mPostType = PostType.FEED;
		} else if (posttype == PostType.SESSION) {
			mPostType = PostType.SESSION;
		} else if (posttype == PostType.LISTTYPE_LIKE) {// 人气列表
			mPostType = PostType.LISTTYPE_LIKE;
		} else if (posttype == PostType.LISTTYPE_BLACK) {// 黑名单列表
			mPostType = PostType.LISTTYPE_BLACK;
		} else if (posttype == PostType.RESEND_CHAT_TEXT) {
			mPostType = PostType.RESEND_CHAT_TEXT;
		}
		show();
	}

	private void show() {
		if (contextUtil.getForegroundAct() != null) {
			superDialog = new AlertDialog.Builder(contextUtil.getForegroundAct())//
					.setView(container)//
					.create();
			superDialog.show();
		}
	}

	@OnClick({ R.id.report_user, R.id.black_user, R.id.dislike_user, R.id.popup_delete, R.id.popup_logout })
	@Override
	public void onClick(View v) {
		if (superDialog == null || !superDialog.isShowing()) {
			return;
		}
		switch (v.getId()) {
		case R.id.report_user:
			report();
			break;
		case R.id.black_user:
			if (mUser.block) {
				removeFromBlack();
			} else {
				black();
			}

			break;
		case R.id.dislike_user:
			dislike();
			break;
		case R.id.popup_delete:
			if (mPostType == PostType.SESSION) {
				deleteChat(mId);
			} else if (mPostType == PostType.FEED) {
				deleteFeed();
			} else if (mPostType == PostType.COMMENT_DEL) {
				deleteComment(mId);
			} else if (mPostType == PostType.LISTTYPE_BLACK || mPostType == PostType.LISTTYPE_LIKE) {
				controlUser(mId);
			} else if (mPostType == PostType.RESEND_CHAT_TEXT) {
				resendChatSms();
			}
			break;
		case R.id.popup_logout:
			((BaseFragAct) contextUtil.getForegroundAct()).doLogout(context);
			break;
		default:
			break;
		}
		if (superDialog != null && superDialog.isShowing()) {
			superDialog.dismiss();
		}
	}

	/**
	 * @Description:重新发送信息
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-30
	 */
	private void resendChatSms() {
		// 请求再次发送信息
		try {
			if (mViewHolder != null) {
				bus.post(new AnginSendSmsEvent(mViewHolder));
			}
		} catch (Throwable e) {
		}
	}

	// FIXME 这个地方用一个简单的callback即可，不用bus这么麻烦
	private void controlUser(String id) {
		bus.post(new ControlUserEvent(Integer.valueOf(id)));
		XL.d("controlUser", id);
	}

	/**
	 * @Description:传Id删COmment
	 * @param id
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-16
	 */
	private void deleteComment(String id) {
		bus.post(new DeleteCommentEvent(id));
	}

	/**
	 * @Description: 删除会话
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-11-22
	 */
	private void deleteChat(String id) {
		bus.post(new DeleteSessionEvent(id));

	}

	/**
	 * @Title: report
	 * @Description: 举报用户或FEED
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void report() {
		if (mPostType == PostType.FEED) {
			mFeedService.reportFeed(mId, null, null, new Callback<Result<ResultData>>() {

				@Override
				public void success(Result<ResultData> arg0, Response arg1) {
					dismissPopup();
				}

				@Override
				public void failure(RetrofitError arg0) {
					dismissPopup();
				}
			});
			// FIXME 举报成功后没有提示,
		} else if (mPostType == PostType.USER) {
			mUserService.reportUser(mId, null, new Callback<Result<ResultData>>() {

				@Override
				public void success(Result<ResultData> arg0, Response arg1) {
					ToastUtil.shortToast(AppApplication.getInstance(), R.string.report_inappropriate_success);
				}

				@Override
				public void failure(RetrofitError arg0) {
					XL.i(TAG, "report user:failure");
				}
			});
		}
	}

	public void dismissPopup() {
		if (popup != null && popup.isShowing()) {
			popup.dismiss();
		}
	}

	/**
	 * @Title: black
	 * @Description: 加入黑名单
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void black() {
		mUserService.blackUser(mId, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> arg0, Response arg1) {
				XL.i(TAG, "black user:success");
				ToastUtil.shortToast(AppApplication.getInstance(), R.string.add_to_black_list_success);
				mUser.block = true;
			}

			@Override
			public void failure(RetrofitError arg0) {
				XL.i(TAG, "black user:failure");
			}
		});
	}

	/**
	 * @Title: black
	 * @Description: 移除黑名单
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void removeFromBlack() {
		mUserService.unblock(mId, new Callback<Result<ResultData>>() {

			@Override
			public void failure(RetrofitError arg0) {
				XL.i(TAG, "移除黑名單失敗");
				// ((ProfileTestFragment) mFrag).loadFeedList();
			}

			@Override
			public void success(Result<ResultData> result, Response arg1) {
				mUser.block = false;
				ToastUtil.shortToast(context, R.string.remove_from_black_list_success);
				XL.i(TAG, "移除黑名單成功");
			}
		});
	}

	private void dislike() {
		mUserService.dislikeUser(mId, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> arg0, Response arg1) {
				XL.i("ALOHA_ALOHA", "dislike user:success");
				// 刷新数据
				((RefreshData) mFrag).refreshData();
				if (loadSuccess != null) {
					loadSuccess.success();
				}
			}

			@Override
			public void failure(RetrofitError arg0) {
				XL.i("ALOHA_ALOHA", "dislike user:failure");
			}
		});
	}

	private void deleteFeed() {
		mFeedService.deleteFeed(mId, new Callback<Result<ResultData>>() {

			@Override
			public void success(Result<ResultData> arg0, Response arg1) {
				XL.i("ALOHA_ALOHA", "delete feed:success");
				// 更新feed list
				GlobalConstants.AppConstact.mDelPostId = mId;
				((ProfileListAdapter) baseAdapter).notifyFeedDataSetChanged(mId);
			}

			@Override
			public void failure(RetrofitError arg0) {
				XL.i("ALOHA_ALOHA", "delete feed:failure");
			}
		});
	}

	// 刷新数据
	public interface RefreshData {

		public void refreshData();
	}

	public interface LoadDataSuccess {

		public void success();
	}

	ChatMsgViewAdapter.ViewHolder mViewHolder;

	/**
	 * @Description: 来自聊天的请求再次发送消息
	 * @param resendChatText
	 * @param resendChatText2
	 * @param id
	 * @param title
	 * @param user
	 * @param loadSuccess
	 * @param viewHolder
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-30
	 */
	public void showPopup(PopupType resendChatText, PostType resendChatText2, String id, String title, User user, LoadDataSuccess loadSuccess, ChatMsgViewAdapter.ViewHolder viewHolder) {
		mViewHolder = viewHolder;
		showPopup(resendChatText, resendChatText2, id, title, user, null);
	}

	/**************************************************************************************************************************************************************************/
	TextView mTvBoxOne;
	TextView mTvBoxTwo;
	View mPopupLineOne;
	View mPopupLineTwo;
	private View contentView;
	/**
	 * Nope界面的对话框
	 */
	private RelativeLayout mAlohaNopeDialog;
	private TextView mNopeTitle;
	private TextView mNopeContent;

	/**
	 * @author Sunkist
	 * @param context
	 */
	public ReportBlackAlohaPopup(Context context) {
		mContext = context;
		Injector.inject(this);
		contentView = (View) LayoutInflater.from(context).inflate(R.layout.popup_aloha_alert, new LinearLayout(context), false);
		findViewById(contentView);
	}

	/**
	 * @Description:初始化控件
	 * @param contentView
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-9
	 */
	private void findViewById(View contentView) {
		mTvBoxOne = (TextView) contentView.findViewById(R.id.popup_box_one);
		mTvBoxTwo = (TextView) contentView.findViewById(R.id.popup_box_two);
		mPopupLineTwo = (View) contentView.findViewById(R.id.popup_line_two);
		mTitle = (TextView) contentView.findViewById(R.id.popup_aloha_title);
		mFontUtil.changeViewFont(mTitle, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
		mTitleLineOne = (View) contentView.findViewById(R.id.popup_line_one);
		mAlohaNopeDialog = (RelativeLayout) contentView.findViewById(R.id.aloha_nope_dialog);
		mNopeTitle = (TextView) contentView.findViewById(R.id.reset_title);
		mNopeContent = (TextView) contentView.findViewById(R.id.reset_content);
	}

	/**
	 * @Description:
	 * @param popuptype
	 * @param posttype
	 * @param title
	 * @param bundle
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @param <T>
	 * @return
	 * @date:2015-1-6
	 */
	public <T> AlertDialog showPopup(T t, PopupType popuptype, String title, Bundle bundle, OnClickListener onClickListener) {
		mTitleLineOne.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
		mTitle.setText(mTitleLineOne.getVisibility() == View.GONE ? "" : title);
		switch (popuptype) {
		case CONTROL_CHAT_SMS: {
			Message message = ((ChatMsgViewAdapter.ViewHolder) t).getMessage();
			if (message instanceof ImageMessage) {
				mTitleLineOne.setVisibility(View.VISIBLE);
				mTvBoxTwo.setVisibility(View.VISIBLE);
				mPopupLineTwo.setVisibility(View.VISIBLE);
				mTvBoxTwo.setOnClickListener(onClickListener);
				mTvBoxTwo.setText(R.string.delete);
				showDialog();
			} else {
				mTvBoxOne.setVisibility(View.VISIBLE);
				mTitleLineOne.setVisibility(View.VISIBLE);
				mTvBoxTwo.setVisibility(View.VISIBLE);
				mPopupLineTwo.setVisibility(View.VISIBLE);
				mTvBoxOne.setOnClickListener(onClickListener);
				mTvBoxTwo.setOnClickListener(onClickListener);
				mTvBoxOne.setText(R.string.copy);
				mTvBoxTwo.setText(R.string.delete);
				showDialog();
			}
		}
			return superDialog;
		case FIRST_NOPE_SHOW_DIALOG: {
			mAlohaNopeDialog.setVisibility(View.VISIBLE);
			mNopeTitle.setText(R.string.no_continue_cancel);
			mNopeContent.setText("");
			showDialog();
		}
			return superDialog;
		default:
			ToastUtil.shortToast(context, R.string.is_not_work);
			return superDialog;
		}
	}

	/**
	 * @Description:
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-6
	 */
	private void showDialog() {
		superDialog = new AlertDialog.Builder(mContext)//
				.setView(contentView)//
				.create();
		superDialog.show();
	}

	/**
	 * @Description:
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2015-1-6
	 */
	public void closeDialog() {
		if (superDialog != null && superDialog.isShowing())
			superDialog.dismiss();
	}
}
