package com.wealoha.social.store;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.api.feed.bean.FeedType;
import com.wealoha.social.api.post.bean.Post;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.api.FeedService;
import com.wealoha.social.beans.user.UserService;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.Utils;
import com.wealoha.social.utils.XL;

public class PopupStore {

    public static enum PopupStoreEnum {

    }

    private static String TAG = "PopupStore";
    RegionNodeUtil regionNodeUtil;
    @Inject
    static RegionNodeUtil mRegionNodeUtil;
    @Inject
    FontUtil fontUtil;
    @Inject
    UserService mUserService;
    @Inject
    Context mContext;
    @Inject
    FeedService feedShare;

    public PopupStore(RegionNodeUtil regionNodeUtil) {
        super();
        Injector.inject(this);
        mRegionNodeUtil = regionNodeUtil;
    }

    public PopupStore() {

    }

    /**
     * baseFragAct
     * meUser      用户的User对象
     * toUser      查看的User对象
     *
     * @Description:分享profile的弹出层
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-21
     */
    public void showShareProfilePopup(final BaseFragAct baseFragAct, final User meUser, final User toUser) {

        int viewId = 0;
        LayoutInflater lInflater;
        final PopupWindow sharePopup;
        // FontUtil.setRegulartypeFace(mFrag.getActivity(), shareText);
        /** 关闭弹出层 */
        lInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newsMoreView = lInflater.inflate(R.layout.popup_news_detailed_article_share, new LinearLayout(baseFragAct), false);
        TextView titleTv = (TextView) newsMoreView.findViewById(R.id.popup_title);
        Button mCancelShareBtn = (Button) newsMoreView.findViewById(R.id.share_gv_btn);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);
        LinearLayout iconsLlTwo = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_two);
        LinearLayout popu_outside_ll = (LinearLayout) newsMoreView.findViewById(R.id.popu_outside_ll);
        HorizontalScrollView hsvLineTwo = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_two);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);
        View line = newsMoreView.findViewById(R.id.line);
        sharePopup = new PopupWindow(newsMoreView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        ColorDrawable cDrawable = new ColorDrawable(00000000);
        sharePopup.setAnimationStyle(R.style.popwin_anim_style);
        sharePopup.setBackgroundDrawable(cDrawable);
        sharePopup.setTouchable(true);
        sharePopup.setFocusable(true);
        sharePopup.setOutsideTouchable(true);
        sharePopup.setContentView(newsMoreView);
        int[] shareIconsTwo = new int[]{R.drawable.wechat_icon, R.drawable.circle_of_friends, R.drawable.copy, R.drawable.qq_icon, R.drawable.qzone_icon};
        int[] shareTextsTwo = new int[]{R.string.wechat, R.string.circle_of_friends, R.string.copy_link, R.string.circle_of_qq, R.string.circle_of_qqzone};
        final TextView[] tViewsOne = new TextView[shareTextsTwo.length];

        hsvLineTwo.setVisibility(View.VISIBLE);
        OnClickListener sharePopOnClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = null;
                String title = null;
                String body = null;
                switch (v.getId()) {
                    case R.id.popu_outside_ll:
                        break;
                    case 0:
                        title = baseFragAct.getString(toUser == null ? R.string.share_to_wx_title : R.string.share_to_other_wx_title, toUser == null ? meUser.getName() : toUser.getName());
                        body = StringUtil.shareDescription(toUser == null ? meUser : toUser, baseFragAct, mRegionNodeUtil);
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, toUser == null ? StringUtil.ME_PROFILE_TO_SHARE_WX : StringUtil.OTHER_PROFILE_TO_SHARE_WX);
                        ShareStore.shareToWXHasUrl(baseFragAct, url, title, body, toUser == null ? ImageUtil.getImageUrl(meUser.getAvatarImage().getId(), ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop) : ImageUtil.getImageUrl(toUser.getAvatarImage().getId(), ImageSize.AVATAR, CropMode.ScaleCenterCrop));
                        break;
                    case 1:
                        title = baseFragAct.getString(toUser == null ? R.string.share_to_wx_title : R.string.share_to_other_wx_title, toUser == null ? meUser.getName() : toUser.getName());
                        body = StringUtil.shareDescription(toUser == null ? meUser : toUser, baseFragAct, mRegionNodeUtil);
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, toUser == null ? StringUtil.ME_PROFILE_TO_SHARE_WXF : StringUtil.OTHER_PROFILE_TO_SHARE_WXF);
                        ShareStore.shareToFriendHasUrl(baseFragAct, url, title, body, toUser == null ? ImageUtil.getImageUrl(meUser.getAvatarImage().getId(), ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop) : ImageUtil.getImageUrl(toUser.getAvatarImage().getId(), ImageSize.AVATAR, CropMode.ScaleCenterCrop));
                        break;
                    case 2:
                        title = baseFragAct.getString(R.string.share_to_other_wx_title, toUser == null ? meUser.getName() : toUser.getName());
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, toUser == null ? StringUtil.ME_PROFILE_COPY_LINK : StringUtil.OTHER_PROFILE_COPY_LINK);
                        StringUtil.copyStringToClipboard(baseFragAct, url);
                        break;
                    case 3:
                        // 分享到qq
                        User user = null;
                        String username = null;
                        String imgid = null;
                        int titleid = 0;
                        int urlid = 0;

                        if (toUser == null) {
                            user = meUser;
                            username = meUser.getName();
                            imgid = meUser.getAvatarImage().getId();
                            titleid = R.string.share_to_wx_title;
                            urlid = StringUtil.ME_PROFILE_TO_SHARE_WX;
                        } else {
                            user = toUser;
                            username = toUser.getName();
                            imgid = toUser.getAvatarImage().getId();
                            titleid = R.string.share_to_other_wx_title;
                            urlid = StringUtil.OTHER_PROFILE_TO_SHARE_WX;
                        }
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, urlid);
                        body = StringUtil.shareDescription(user, baseFragAct, mRegionNodeUtil);
                        title = baseFragAct.getString(titleid, username);
                        String imgUrl = ImageUtil.getImageUrl(imgid, ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop);
                        ShareStore.shareToQQ(baseFragAct, url, title, body, imgUrl);
                        break;
                    case 4:
                        // 分享到qq空间
                        User user2 = null;
                        String username2 = null;
                        String imgid2 = null;
                        int titleid2 = 0;
                        int urlid2 = 0;

                        if (toUser == null) {
                            user2 = meUser;
                            username2 = meUser.getName();
                            imgid2 = meUser.getAvatarImage().getId();
                            titleid2 = R.string.share_to_wx_title;
                            urlid2 = StringUtil.ME_PROFILE_TO_SHARE_WXF;
                        } else {
                            user2 = toUser;
                            username2 = toUser.getName();
                            imgid2 = toUser.getAvatarImage().getId();
                            titleid2 = R.string.share_to_other_wx_title;
                            urlid2 = StringUtil.OTHER_PROFILE_TO_SHARE_WXF;
                        }
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, urlid2);
                        body = StringUtil.shareDescription(user2, baseFragAct, mRegionNodeUtil);
                        title = baseFragAct.getString(titleid2, username2);
                        String imgUrl2 = ImageUtil.getImageUrl(imgid2, ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop);
                        ArrayList<String> imgurls = new ArrayList<String>();
                        imgurls.add(imgUrl2);
                        ShareStore.shareToQzone(baseFragAct, url, title, body, imgurls);
                        break;
                    case R.drawable.report:
                        report(toUser);
                        break;
                    case R.drawable.blacklist:
                        if (toUser.getBlock()) {
                            removeFromBlack(toUser);
                        } else {
                            openGuideDialog(baseFragAct, toUser, sharePopup);
                        }
                        break;
                }
                if (sharePopup != null && sharePopup.isShowing()) {
                    sharePopup.dismiss();
                }
            }
        };
        mCancelShareBtn.setOnClickListener(sharePopOnClick);
        popu_outside_ll.setOnClickListener(sharePopOnClick);
        mCancelShareBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (baseFragAct.mScreenHeight * 0.07 + 0.5f)));
        LinearLayout.LayoutParams lLParams = new LinearLayout.LayoutParams((int) (baseFragAct.mScreenHeight * 0.16 + 0.5f), LinearLayout.LayoutParams.WRAP_CONTENT);
        int[] shareIconsOne = new int[]{R.drawable.report, R.drawable.blacklist};
        int[] shareTextsOne = new int[]{R.string.report, R.string.add_to_black_list};
        float textSize = 12f;
        // float textSize = (float) (baseFragAct.mScreenWidth * 0.013 + 0.5f);
        Resources resources = baseFragAct.getResources();
        int shareTextColor = resources.getColor(R.color.black_text);
        LinearLayout iconsLlOne = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_one);

        int leftPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int rightPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int bottomPadding = (int) (baseFragAct.mScreenHeight * 0.001 + 0.5f);
        titleTv.setText(baseFragAct.getString(R.string.share_my_profile));

        boolean isHaveLine = true;
        if (toUser == null || !toUser.getHasPrivacy()) {
            for (int i = 0; i < shareIconsTwo.length; i++) {
                Drawable drawable = resources.getDrawable(shareIconsTwo[i]);
                String shareTvStr = resources.getString(shareTextsTwo[i]);
                TextView shareTv = new TextView(baseFragAct);
                shareTv.setSingleLine(true);
                shareTv.setLayoutParams(lLParams);
                shareTv.setGravity(Gravity.CENTER);
                shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
                shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                shareTv.setText(shareTvStr);
                shareTv.setTextColor(shareTextColor);
                shareTv.setClickable(true);
                shareTv.setId(viewId++);
                shareTv.setOnClickListener(sharePopOnClick);
                drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
                shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
                shareTv.setCompoundDrawables(null, drawable, null, null);
                FontUtil.setRegulartypeFace(baseFragAct, shareTv);
                iconsLlOne.addView(shareTv);
                tViewsOne[i] = shareTv;
            }
        } else {
            titleTv.setVisibility(View.GONE);
            isHaveLine = false;
        }

        HorizontalScrollView hsvLineOne = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_one);
        hsvLineOne.setVisibility(View.VISIBLE);
        if (toUser != null) {// 显示举报、、
            titleTv.setText(R.string.share_my_profile);
            line.setVisibility(View.VISIBLE);
            for (int i = 0; i < shareIconsOne.length; i++) {
                Drawable drawable = resources.getDrawable(shareIconsOne[i]);
                String shareTvStr = resources.getString(shareTextsOne[i]);
                TextView shareTv = new TextView(baseFragAct);
                shareTv.setLayoutParams(lLParams);
                shareTv.setSingleLine(true);
                shareTv.setGravity(Gravity.CENTER);
                shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
                shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

                // 如果是加入黑名单（这的逻辑太乱）
                if (i == 1 && toUser.getBlock()) {
                    shareTv.setText(resources.getString(R.string.remove_from_black_list));
                } else {
                    shareTv.setText(shareTvStr);
                }

                shareTv.setTextColor(shareTextColor);
                shareTv.setClickable(true);
                shareTv.setId(shareIconsOne[i]);
                shareTv.setOnClickListener(sharePopOnClick);
                drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
                shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
                shareTv.setCompoundDrawables(null, drawable, null, null);
                FontUtil.setRegulartypeFace(baseFragAct, shareTv);
                iconsLlTwo.addView(shareTv);
                tViewsOne[i] = shareTv;
            }
        } else {
            isHaveLine = false;
        }

        if (isHaveLine) {
            line.setVisibility(View.VISIBLE);
        } else {
            line.setVisibility(View.GONE);

        }
        sharePopup.showAtLocation(newsMoreView, Gravity.BOTTOM, 0, 0);
        sharePopup.update();
    }

    /**
     * baseFragAct
     * meUser      用户的User对象
     * toUser      查看的User对象
     *
     * @Description:分享profile的弹出层
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-21
     */
    public void showShareProfilePopup(final BaseFragAct baseFragAct, int resId, final User meUser, final User toUser) {

        int viewId = 0;
        LayoutInflater lInflater;
        final PopupWindow sharePopup;
        // FontUtil.setRegulartypeFace(mFrag.getActivity(), shareText);
        /** 关闭弹出层 */
        lInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newsMoreView = lInflater.inflate(R.layout.popup_news_detailed_article_share, new LinearLayout(baseFragAct), false);
        TextView titleTv = (TextView) newsMoreView.findViewById(R.id.popup_title);
        Button mCancelShareBtn = (Button) newsMoreView.findViewById(R.id.share_gv_btn);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);
        LinearLayout iconsLlTwo = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_two);
        LinearLayout popu_outside_ll = (LinearLayout) newsMoreView.findViewById(R.id.popu_outside_ll);
        HorizontalScrollView hsvLineTwo = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_two);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);
        View line = newsMoreView.findViewById(R.id.line);
        sharePopup = new PopupWindow(newsMoreView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        ColorDrawable cDrawable = new ColorDrawable(00000000);
        sharePopup.setAnimationStyle(R.style.popwin_anim_style);
        sharePopup.setBackgroundDrawable(cDrawable);
        sharePopup.setTouchable(true);
        sharePopup.setFocusable(true);
        sharePopup.setOutsideTouchable(true);
        sharePopup.setContentView(newsMoreView);
        int[] shareIconsTwo = new int[]{R.drawable.wechat_icon, R.drawable.circle_of_friends, R.drawable.copy, R.drawable.qq_icon, R.drawable.qzone_icon};
        int[] shareTextsTwo = new int[]{R.string.wechat, R.string.circle_of_friends, R.string.copy_link, R.string.circle_of_qq, R.string.circle_of_qqzone};
        final TextView[] tViewsOne = new TextView[shareTextsTwo.length];

        hsvLineTwo.setVisibility(View.VISIBLE);
        OnClickListener sharePopOnClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = null;
                String title = null;
                String body = null;
                switch (v.getId()) {
                    case R.id.popu_outside_ll:
                        break;
                    case 0:
                        title = baseFragAct.getString(toUser == null ? R.string.share_to_wx_title : R.string.share_to_other_wx_title, toUser == null ? meUser.getName() : toUser.getName());
                        body = StringUtil.shareDescription(toUser == null ? meUser : toUser, baseFragAct, mRegionNodeUtil);
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, toUser == null ? StringUtil.ME_PROFILE_TO_SHARE_WX : StringUtil.OTHER_PROFILE_TO_SHARE_WX);
                        ShareStore.shareToWXHasUrl(baseFragAct, url, title, body, toUser == null ? ImageUtil.getImageUrl(meUser.getAvatarImage().getId(), ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop) : ImageUtil.getImageUrl(toUser.getAvatarImage().getId(), ImageSize.AVATAR, CropMode.ScaleCenterCrop));
                        break;
                    case 1:
                        title = baseFragAct.getString(toUser == null ? R.string.share_to_wx_title : R.string.share_to_other_wx_title, toUser == null ? meUser.getName() : toUser.getName());
                        body = StringUtil.shareDescription(toUser == null ? meUser : toUser, baseFragAct, mRegionNodeUtil);
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, toUser == null ? StringUtil.ME_PROFILE_TO_SHARE_WXF : StringUtil.OTHER_PROFILE_TO_SHARE_WXF);
                        ShareStore.shareToFriendHasUrl(baseFragAct, url, title, body, toUser == null ? ImageUtil.getImageUrl(meUser.getAvatarImage().getId(), ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop) : ImageUtil.getImageUrl(toUser.getAvatarImage().getId(), ImageSize.AVATAR, CropMode.ScaleCenterCrop));
                        break;
                    case 2:
                        title = baseFragAct.getString(R.string.share_to_other_wx_title, toUser == null ? meUser.getName() : toUser.getName());
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, toUser == null ? StringUtil.ME_PROFILE_COPY_LINK : StringUtil.OTHER_PROFILE_COPY_LINK);
                        StringUtil.copyStringToClipboard(baseFragAct, url);
                        break;
                    case 3:
                        // 分享到qq
                        User user = null;
                        String username = null;
                        String imgid = null;
                        int titleid = 0;
                        int urlid = 0;

                        if (toUser == null) {
                            user = meUser;
                            username = meUser.getName();
                            imgid = meUser.getAvatarImage().getId();
                            titleid = R.string.share_to_wx_title;
                            urlid = StringUtil.ME_PROFILE_TO_SHARE_WX;
                        } else {
                            user = toUser;
                            username = toUser.getName();
                            imgid = toUser.getAvatarImage().getId();
                            titleid = R.string.share_to_other_wx_title;
                            urlid = StringUtil.OTHER_PROFILE_TO_SHARE_WX;
                        }
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, urlid);
                        body = StringUtil.shareDescription(user, baseFragAct, mRegionNodeUtil);
                        title = baseFragAct.getString(titleid, username);
                        String imgUrl = ImageUtil.getImageUrl(imgid, ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop);
                        ShareStore.shareToQQ(baseFragAct, url, title, body, imgUrl);
                        break;
                    case 4:
                        // 分享到qq空间
                        User user2 = null;
                        String username2 = null;
                        String imgid2 = null;
                        int titleid2 = 0;
                        int urlid2 = 0;

                        if (toUser == null) {
                            user2 = meUser;
                            username2 = meUser.getName();
                            imgid2 = meUser.getAvatarImage().getId();
                            titleid2 = R.string.share_to_wx_title;
                            urlid2 = StringUtil.ME_PROFILE_TO_SHARE_WXF;
                        } else {
                            user2 = toUser;
                            username2 = toUser.getName();
                            imgid2 = toUser.getAvatarImage().getId();
                            titleid2 = R.string.share_to_other_wx_title;
                            urlid2 = StringUtil.OTHER_PROFILE_TO_SHARE_WXF;
                        }
                        url = StringUtil.shareWebPagerUrl(meUser, toUser, urlid2);
                        body = StringUtil.shareDescription(user2, baseFragAct, mRegionNodeUtil);
                        title = baseFragAct.getString(titleid2, username2);
                        String imgUrl2 = ImageUtil.getImageUrl(imgid2, ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop);
                        ArrayList<String> imgurls = new ArrayList<String>();
                        imgurls.add(imgUrl2);
                        ShareStore.shareToQzone(baseFragAct, url, title, body, imgurls);
                        break;
                    case R.drawable.report:
                        report(toUser);
                        break;
                    case R.drawable.blacklist:
                        if (toUser.getBlock()) {
                            removeFromBlack(toUser);
                        } else {
                            openGuideDialog(baseFragAct, toUser, sharePopup);
                        }
                        break;
                }
                if (sharePopup != null && sharePopup.isShowing()) {
                    sharePopup.dismiss();
                }
            }
        };
        mCancelShareBtn.setOnClickListener(sharePopOnClick);
        popu_outside_ll.setOnClickListener(sharePopOnClick);
        mCancelShareBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (baseFragAct.mScreenHeight * 0.07 + 0.5f)));
        LinearLayout.LayoutParams lLParams = new LinearLayout.LayoutParams((int) (baseFragAct.mScreenHeight * 0.16 + 0.5f), LinearLayout.LayoutParams.WRAP_CONTENT);
        int[] shareIconsOne = new int[]{R.drawable.report, R.drawable.blacklist};
        int[] shareTextsOne = new int[]{R.string.report, R.string.add_to_black_list};
        float textSize = 12f;
        // float textSize = (float) (baseFragAct.mScreenWidth * 0.013 + 0.5f);
        Resources resources = baseFragAct.getResources();
        int shareTextColor = resources.getColor(R.color.black_text);
        LinearLayout iconsLlOne = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_one);

        int leftPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int rightPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int bottomPadding = (int) (baseFragAct.mScreenHeight * 0.001 + 0.5f);
        titleTv.setText(resId);

        boolean isHaveLine = true;
        if (toUser == null || !toUser.getHasPrivacy()) {
            for (int i = 0; i < shareIconsTwo.length; i++) {
                Drawable drawable = resources.getDrawable(shareIconsTwo[i]);
                String shareTvStr = resources.getString(shareTextsTwo[i]);
                TextView shareTv = new TextView(baseFragAct);
                shareTv.setSingleLine(true);
                shareTv.setLayoutParams(lLParams);
                shareTv.setGravity(Gravity.CENTER);
                shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
                shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                shareTv.setText(shareTvStr);
                shareTv.setTextColor(shareTextColor);
                shareTv.setClickable(true);
                shareTv.setId(viewId++);
                shareTv.setOnClickListener(sharePopOnClick);
                drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
                shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
                shareTv.setCompoundDrawables(null, drawable, null, null);
                FontUtil.setRegulartypeFace(baseFragAct, shareTv);
                iconsLlOne.addView(shareTv);
                tViewsOne[i] = shareTv;
            }
        } else {
            titleTv.setVisibility(View.GONE);
            isHaveLine = false;
        }

        HorizontalScrollView hsvLineOne = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_one);
        hsvLineOne.setVisibility(View.VISIBLE);
        if (toUser != null) {// 显示举报、、
            titleTv.setText(R.string.share_my_profile);
            line.setVisibility(View.VISIBLE);
            for (int i = 0; i < shareIconsOne.length; i++) {
                Drawable drawable = resources.getDrawable(shareIconsOne[i]);
                String shareTvStr = resources.getString(shareTextsOne[i]);
                TextView shareTv = new TextView(baseFragAct);
                shareTv.setLayoutParams(lLParams);
                shareTv.setSingleLine(true);
                shareTv.setGravity(Gravity.CENTER);
                shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
                shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

                // 如果是加入黑名单（这的逻辑太乱）
                if (i == 1 && toUser.getBlock()) {
                    shareTv.setText(resources.getString(R.string.remove_from_black_list));
                } else {
                    shareTv.setText(shareTvStr);
                }

                shareTv.setTextColor(shareTextColor);
                shareTv.setClickable(true);
                shareTv.setId(shareIconsOne[i]);
                shareTv.setOnClickListener(sharePopOnClick);
                drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
                shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
                shareTv.setCompoundDrawables(null, drawable, null, null);
                FontUtil.setRegulartypeFace(baseFragAct, shareTv);
                iconsLlTwo.addView(shareTv);
                tViewsOne[i] = shareTv;
            }
        } else {
            isHaveLine = false;
        }

        if (isHaveLine) {
            line.setVisibility(View.VISIBLE);
        } else {
            line.setVisibility(View.GONE);

        }
        sharePopup.showAtLocation(newsMoreView, Gravity.BOTTOM, 0, 0);
        sharePopup.update();
    }

    /**
     * baseFragAct
     * meUser      用户的User对象
     * toUser      查看的User对象
     *
     * @Description:分享profile的弹出层
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-21
     */
    public void showShareProfilePopup(final BaseFragAct baseFragAct, final com.wealoha.social.api.user.bean.User meUser, final com.wealoha.social.api.user.bean.User toUser) {

        int viewId = 0;
        LayoutInflater lInflater;
        final PopupWindow sharePopup;
        /** 关闭弹出层 */
        lInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newsMoreView = lInflater.inflate(R.layout.popup_news_detailed_article_share, new LinearLayout(mContext), false);
        View line = newsMoreView.findViewById(R.id.line);
        sharePopup = new PopupWindow(newsMoreView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        ColorDrawable cDrawable = new ColorDrawable(00000000);
        // sharePopup.setAnimationStyle(android.R.style.Animation_Toast);
        sharePopup.setAnimationStyle(R.style.popwin_anim_style);
        sharePopup.setBackgroundDrawable(cDrawable);
        sharePopup.setTouchable(true);
        sharePopup.setFocusable(true);
        sharePopup.setOutsideTouchable(true);
        sharePopup.setContentView(newsMoreView);
        int[] shareIconsTwo = new int[]{R.drawable.wechat_icon, R.drawable.circle_of_friends, R.drawable.copy, R.drawable.qq_icon, R.drawable.qzone_icon};
        int[] shareTextsTwo = new int[]{R.string.wechat, R.string.circle_of_friends, R.string.copy_link, R.string.circle_of_qq, R.string.circle_of_qqzone};
        final TextView[] tViewsOne = new TextView[shareTextsTwo.length];
        TextView titleTv = (TextView) newsMoreView.findViewById(R.id.popup_title);
        Button mCancelShareBtn = (Button) newsMoreView.findViewById(R.id.share_gv_btn);
        LinearLayout iconsLlTwo = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_two);
        LinearLayout popu_outside_ll = (LinearLayout) newsMoreView.findViewById(R.id.popu_outside_ll);
        HorizontalScrollView hsvLineTwo = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_two);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);

        hsvLineTwo.setVisibility(View.VISIBLE);
        OnClickListener sharePopOnClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = null;
                String title = null;
                String body = null;
                switch (v.getId()) {
                    case R.id.popu_outside_ll:
                        break;
                    case 0:
                        title = baseFragAct.getString(toUser == null ? R.string.share_to_wx_title : R.string.share_to_other_wx_title, toUser == null ? meUser.getName() : toUser.getName());
                        body = StringUtil.shareDescription(toUser == null ? meUser : toUser, baseFragAct, mRegionNodeUtil);
                        url = StringUtil.shareUserWebUrl(meUser.getId(), toUser.getId(), toUser == null ? StringUtil.ME_PROFILE_TO_SHARE_WX : StringUtil.OTHER_PROFILE_TO_SHARE_WX);
                        ShareStore.shareToWXHasUrl(baseFragAct, url, title, body, toUser == null ? meUser.getAvatarImage().getUrlSquare(ImageSize.AVATAR) : toUser.getAvatarImage().getUrlSquare(ImageSize.AVATAR));
                        break;
                    case 1:
                        title = baseFragAct.getString(toUser == null ? R.string.share_to_wx_title : R.string.share_to_other_wx_title, toUser == null ? meUser.getName() : toUser.getName());
                        body = StringUtil.shareDescription(toUser == null ? meUser : toUser, baseFragAct, mRegionNodeUtil);
                        url = StringUtil.shareUserWebUrl(meUser.getId(), toUser.getId(), toUser == null ? StringUtil.ME_PROFILE_TO_SHARE_WXF : StringUtil.OTHER_PROFILE_TO_SHARE_WXF);
                        ShareStore.shareToFriendHasUrl(baseFragAct, url, title, body, toUser == null ? meUser.getAvatarImage().getUrlSquare(ImageSize.AVATAR) : toUser.getAvatarImage().getUrlSquare(ImageSize.AVATAR));
                        break;
                    case 2:
                        title = baseFragAct.getString(R.string.share_to_other_wx_title, toUser == null ? meUser.getName() : toUser.getName());
                        url = StringUtil.shareUserWebUrl(meUser.getId(), toUser.getId(), toUser == null ? StringUtil.ME_PROFILE_COPY_LINK : StringUtil.OTHER_PROFILE_COPY_LINK);
                        StringUtil.copyStringToClipboard(baseFragAct, url);
                        break;
                    case 3:
                        // 分享到qq
                        String username = null;
                        String imgid = null;
                        int titleid = 0;
                        int urlid = 0;

                        if (toUser == null) {
                            username = meUser.getName();
                            titleid = R.string.share_to_wx_title;
                            urlid = StringUtil.ME_PROFILE_TO_SHARE_WX;
                        } else {
                            username = toUser.getName();
                            titleid = R.string.share_to_other_wx_title;
                            urlid = StringUtil.OTHER_PROFILE_TO_SHARE_WX;
                        }
                        url = StringUtil.shareUserWebUrl(meUser.getId(), toUser.getId(), urlid);
                        body = StringUtil.shareDescription(toUser == null ? meUser : toUser, baseFragAct, mRegionNodeUtil);
                        title = baseFragAct.getString(titleid, username);
                        String imgUrl = ImageUtil.getImageUrl(imgid, ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop);
                        ShareStore.shareToQQ(baseFragAct, url, title, body, imgUrl);
                        break;
                    case 4:
                        // 分享到qq空间
                        User user2 = null;
                        String username2 = null;
                        String imgid2 = null;
                        int titleid2 = 0;
                        int urlid2 = 0;

                        if (toUser == null) {
                            username2 = meUser.getName();
                            titleid2 = R.string.share_to_wx_title;
                            urlid2 = StringUtil.ME_PROFILE_TO_SHARE_WXF;
                        } else {
                            username2 = toUser.getName();
                            titleid2 = R.string.share_to_other_wx_title;
                            urlid2 = StringUtil.OTHER_PROFILE_TO_SHARE_WXF;
                        }
                        url = StringUtil.shareUserWebUrl(meUser.getId(), toUser.getId(), urlid2);
                        body = StringUtil.shareDescription(user2, baseFragAct, mRegionNodeUtil);
                        title = baseFragAct.getString(titleid2, username2);
                        String imgUrl2 = ImageUtil.getImageUrl(imgid2, ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop);
                        ArrayList<String> imgurls = new ArrayList<String>();
                        imgurls.add(imgUrl2);
                        ShareStore.shareToQzone(baseFragAct, url, title, body, imgurls);
                        break;
                    case R.drawable.report:
                        report(toUser);
                        break;
                    case R.drawable.blacklist:
                        if (toUser.isBlock()) {
                            removeFromBlack(toUser);
                        } else {
                            openGuideDialog(baseFragAct, toUser, sharePopup);
                        }
                        break;
                }
                if (sharePopup != null && sharePopup.isShowing()) {
                    sharePopup.dismiss();
                }
            }
        };
        mCancelShareBtn.setOnClickListener(sharePopOnClick);
        popu_outside_ll.setOnClickListener(sharePopOnClick);
        mCancelShareBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (baseFragAct.mScreenHeight * 0.07 + 0.5f)));
        LinearLayout.LayoutParams lLParams = new LinearLayout.LayoutParams((int) (baseFragAct.mScreenHeight * 0.16 + 0.5f), LinearLayout.LayoutParams.WRAP_CONTENT);
        int[] shareIconsOne = new int[]{R.drawable.report, R.drawable.blacklist};
        int[] shareTextsOne = new int[]{R.string.report, R.string.add_to_black_list};
        float textSize = 12f;
        // float textSize = (float) (baseFragAct.mScreenWidth * 0.013 + 0.5f);
        Resources resources = baseFragAct.getResources();
        int shareTextColor = resources.getColor(R.color.black_text);
        LinearLayout iconsLlOne = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_one);

        int leftPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int rightPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int bottomPadding = (int) (baseFragAct.mScreenHeight * 0.001 + 0.5f);
        titleTv.setText(baseFragAct.getString(R.string.share_my_profile));

        boolean isHaveLine = true;
        if (toUser == null || !toUser.hasPrivacy()) {
            for (int i = 0; i < shareIconsTwo.length; i++) {
                Drawable drawable = resources.getDrawable(shareIconsTwo[i]);
                String shareTvStr = resources.getString(shareTextsTwo[i]);
                TextView shareTv = new TextView(baseFragAct);
                shareTv.setSingleLine(true);
                shareTv.setLayoutParams(lLParams);
                shareTv.setGravity(Gravity.CENTER);
                shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
                shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                shareTv.setText(shareTvStr);
                shareTv.setTextColor(shareTextColor);
                shareTv.setClickable(true);
                shareTv.setId(viewId++);
                shareTv.setOnClickListener(sharePopOnClick);
                drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
                shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
                shareTv.setCompoundDrawables(null, drawable, null, null);
                FontUtil.setRegulartypeFace(baseFragAct, shareTv);
                iconsLlOne.addView(shareTv);
                tViewsOne[i] = shareTv;
            }
        } else {
            titleTv.setVisibility(View.GONE);
            isHaveLine = false;
        }

        HorizontalScrollView hsvLineOne = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_one);
        hsvLineOne.setVisibility(View.VISIBLE);
        if (toUser != null) {// 显示举报、、
            titleTv.setText(R.string.share_my_profile);
            line.setVisibility(View.VISIBLE);
            for (int i = 0; i < shareIconsOne.length; i++) {
                Drawable drawable = resources.getDrawable(shareIconsOne[i]);
                String shareTvStr = resources.getString(shareTextsOne[i]);
                TextView shareTv = new TextView(baseFragAct);
                shareTv.setLayoutParams(lLParams);
                shareTv.setSingleLine(true);
                shareTv.setGravity(Gravity.CENTER);
                shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
                shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

                // 如果是加入黑名单（这的逻辑太乱）
                if (i == 1 && toUser.isBlock()) {
                    shareTv.setText(resources.getString(R.string.remove_from_black_list));
                } else {
                    shareTv.setText(shareTvStr);
                }

                shareTv.setTextColor(shareTextColor);
                shareTv.setClickable(true);
                shareTv.setId(shareIconsOne[i]);
                shareTv.setOnClickListener(sharePopOnClick);
                drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
                shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
                shareTv.setCompoundDrawables(null, drawable, null, null);
                FontUtil.setRegulartypeFace(baseFragAct, shareTv);
                iconsLlTwo.addView(shareTv);
                tViewsOne[i] = shareTv;
            }
        } else {
            isHaveLine = false;
        }

        if (isHaveLine) {
            line.setVisibility(View.VISIBLE);
        } else {
            line.setVisibility(View.GONE);

        }
        sharePopup.showAtLocation(newsMoreView, Gravity.BOTTOM, 0, 0);
        sharePopup.update();
    }

    /**
     * baseFragAct
     *
     * @ meUser      用户的User对象
     * toUser      查看的User对象
     * @Description:分享profile的弹出层
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-21
     */
    public void showSharePostPopup(final BaseFragAct baseFragAct, final Post post, final User toUser) {

        int viewId = 0;
        LayoutInflater lInflater;
        final PopupWindow sharePopup;
        /** 关闭弹出层 */
        lInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newsMoreView = lInflater.inflate(R.layout.popup_news_detailed_article_share, new LinearLayout(mContext), false);
        View line = newsMoreView.findViewById(R.id.line);
        sharePopup = new PopupWindow(newsMoreView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        ColorDrawable cDrawable = new ColorDrawable(00000000);
        // sharePopup.setAnimationStyle(android.R.style.Animation_Toast);
        sharePopup.setAnimationStyle(R.style.popwin_anim_style);
        sharePopup.setBackgroundDrawable(cDrawable);
        sharePopup.setTouchable(true);
        sharePopup.setFocusable(true);
        sharePopup.setOutsideTouchable(true);
        sharePopup.setContentView(newsMoreView);
        int[] shareIconsTwo = new int[]{R.drawable.wechat_icon, R.drawable.circle_of_friends, R.drawable.copy, R.drawable.qq_icon, R.drawable.qzone_icon};
        int[] shareTextsTwo = new int[]{R.string.wechat, R.string.circle_of_friends, R.string.copy_link, R.string.circle_of_qq, R.string.circle_of_qqzone};
        final TextView[] tViewsOne = new TextView[shareTextsTwo.length];
        TextView titleTv = (TextView) newsMoreView.findViewById(R.id.popup_title);
        Button mCancelShareBtn = (Button) newsMoreView.findViewById(R.id.share_gv_btn);
        LinearLayout iconsLlTwo = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_two);
        LinearLayout popu_outside_ll = (LinearLayout) newsMoreView.findViewById(R.id.popu_outside_ll);
        HorizontalScrollView hsvLineTwo = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_two);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        hsvLineTwo.setVisibility(View.VISIBLE);
        OnClickListener sharePopOnClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = null;
                String title = null;
                String body = null;
                switch (v.getId()) {
                    case R.id.popu_outside_ll:
                        break;
                    case 0:
                        // title = baseFragAct.getString(toUser == null ?
                        // R.string.share_to_wx_title :
                        // R.string.share_to_other_wx_title, toUser == null ?
                        // meUser.getName() : toUser.getName());
                        // body = StringUtil.shareDescription(toUser == null ?
                        // meUser : toUser, baseFragAct,
                        // mRegionNodeUtil);
                        // url = StringUtil.shareWebPagerUrl(meUser, toUser, toUser
                        // == null ?
                        // StringUtil.ME_PROFILE_TO_SHARE_WX :
                        // StringUtil.OTHER_PROFILE_TO_SHARE_WX);
                        // ShareStore.shareToWXHasUrl(baseFragAct, url, title, body,
                        // toUser == null ?
                        // ImageUtil.getImageUrl(meUser.getAvatarImage().getId(),
                        // ImageSize.CHAT_THUMB,
                        // CropMode.ScaleCenterCrop) :
                        // ImageUtil.getImageUrl(toUser.getAvatarImage().getId(),
                        // ImageSize.AVATAR, CropMode.ScaleCenterCrop));
                        break;
                    case 1:
                        // title = baseFragAct.getString(toUser == null ?
                        // R.string.share_to_wx_title :
                        // R.string.share_to_other_wx_title, toUser == null ?
                        // meUser.getName() : toUser.getName());
                        // body = StringUtil.shareDescription(toUser == null ?
                        // meUser : toUser, baseFragAct,
                        // mRegionNodeUtil);
                        // url = StringUtil.shareWebPagerUrl(meUser, toUser, toUser
                        // == null ?
                        // StringUtil.ME_PROFILE_TO_SHARE_WXF :
                        // StringUtil.OTHER_PROFILE_TO_SHARE_WXF);
                        // ShareStore.shareToFriendHasUrl(baseFragAct, url, title,
                        // body, toUser == null ?
                        // ImageUtil.getImageUrl(meUser.getAvatarImage().getId(),
                        // ImageSize.CHAT_THUMB,
                        // CropMode.ScaleCenterCrop) :
                        // ImageUtil.getImageUrl(toUser.getAvatarImage().getId(),
                        // ImageSize.AVATAR, CropMode.ScaleCenterCrop));
                        break;
                    case 2:
                        // title =
                        // baseFragAct.getString(R.string.share_to_other_wx_title,
                        // toUser == null ?
                        // meUser.getName() : toUser.getName());
                        // url = StringUtil.shareWebPagerUrl(meUser, toUser, toUser
                        // == null ?
                        // StringUtil.ME_PROFILE_COPY_LINK :
                        // StringUtil.OTHER_PROFILE_COPY_LINK);
                        // StringUtil.copyStringToClipboard(baseFragAct, url);
                        break;
                    case 3:
                        // 分享到qq

                        url = StringUtil.sharePostWebUrl(post.getUser().getId(), toUser.getId(), post, StringUtil.OTHER_PROFILE_TO_SHARE_WXF);
                        String imgUrl = ImageUtil.getImageUrl(post.getImage().getImageId(), ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop);
                        ShareStore.shareToQQ(baseFragAct, url, "1", "2", imgUrl);
                        // ShareStore.shareToWX(baseFragAct, null, imgUrl);
                        break;
                    case 4:
                        // 分享到qq空间
                        // User user2 = null;
                        // String username2 = null;
                        // String imgid2 = null;
                        // int titleid2 = 0;
                        // int urlid2 = 0;
                        //
                        // if (toUser == null) {
                        // user2 = meUser;
                        // username2 = meUser.getName();
                        // imgid2 = meUser.getAvatarImage().getId();
                        // titleid2 = R.string.share_to_wx_title;
                        // urlid2 = StringUtil.ME_PROFILE_TO_SHARE_WXF;
                        // } else {
                        // user2 = toUser;
                        // username2 = toUser.getName();
                        // imgid2 = toUser.getAvatarImage().getId();
                        // titleid2 = R.string.share_to_other_wx_title;
                        // urlid2 = StringUtil.OTHER_PROFILE_TO_SHARE_WXF;
                        // }
                        // url = StringUtil.shareWebPagerUrl(meUser, toUser,
                        // urlid2);
                        // body = StringUtil.shareDescription(user2, baseFragAct,
                        // mRegionNodeUtil);
                        // title = baseFragAct.getString(titleid2, username2);
                        // String imgUrl2 = ImageUtil.getImageUrl(imgid2,
                        // ImageSize.CHAT_THUMB,
                        // CropMode.ScaleCenterCrop);
                        // ArrayList<String> imgurls = new ArrayList<String>();
                        // imgurls.add(imgUrl2);
                        // ShareStore.shareToQzone(baseFragAct, url, title, body,
                        // imgurls);
                        break;
                    case R.drawable.report:
                        report(toUser);
                        break;
                    case R.drawable.blacklist:
                        if (toUser.getBlock()) {
                            removeFromBlack(toUser);
                        } else {
                            openGuideDialog(baseFragAct, toUser, sharePopup);
                        }
                        break;
                }
                if (sharePopup != null && sharePopup.isShowing()) {
                    sharePopup.dismiss();
                }
            }
        };
        mCancelShareBtn.setOnClickListener(sharePopOnClick);
        popu_outside_ll.setOnClickListener(sharePopOnClick);
        mCancelShareBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (baseFragAct.mScreenHeight * 0.07 + 0.5f)));
        LinearLayout.LayoutParams lLParams = new LinearLayout.LayoutParams((int) (baseFragAct.mScreenHeight * 0.16 + 0.5f), LinearLayout.LayoutParams.WRAP_CONTENT);
        int[] shareIconsOne = new int[]{R.drawable.report, R.drawable.blacklist};
        int[] shareTextsOne = new int[]{R.string.report, R.string.add_to_black_list};
        float textSize = 12f;
        // float textSize = (float) (baseFragAct.mScreenWidth * 0.013 + 0.5f);
        Resources resources = baseFragAct.getResources();
        int shareTextColor = resources.getColor(R.color.black_text);
        LinearLayout iconsLlOne = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_one);

        int leftPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int rightPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int bottomPadding = (int) (baseFragAct.mScreenHeight * 0.001 + 0.5f);
        titleTv.setText(baseFragAct.getString(R.string.share_my_profile));

        boolean isHaveLine = true;
        if (toUser == null || !toUser.getHasPrivacy()) {
            for (int i = 0; i < shareIconsTwo.length; i++) {
                Drawable drawable = resources.getDrawable(shareIconsTwo[i]);
                String shareTvStr = resources.getString(shareTextsTwo[i]);
                TextView shareTv = new TextView(baseFragAct);
                shareTv.setSingleLine(true);
                shareTv.setLayoutParams(lLParams);
                shareTv.setGravity(Gravity.CENTER);
                shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
                shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                shareTv.setText(shareTvStr);
                shareTv.setTextColor(shareTextColor);
                shareTv.setClickable(true);
                FontUtil.setRegulartypeFace(baseFragAct, shareTv);
                shareTv.setId(viewId++);
                shareTv.setOnClickListener(sharePopOnClick);
                drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
                shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
                shareTv.setCompoundDrawables(null, drawable, null, null);
                iconsLlOne.addView(shareTv);
                tViewsOne[i] = shareTv;
            }
        } else {
            titleTv.setVisibility(View.GONE);
            isHaveLine = false;
        }

        HorizontalScrollView hsvLineOne = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_one);
        hsvLineOne.setVisibility(View.VISIBLE);
        if (toUser != null) {// 显示举报、、
            titleTv.setText(R.string.share_my_profile);
            line.setVisibility(View.VISIBLE);
            for (int i = 0; i < shareIconsOne.length; i++) {
                Drawable drawable = resources.getDrawable(shareIconsOne[i]);
                String shareTvStr = resources.getString(shareTextsOne[i]);
                TextView shareTv = new TextView(baseFragAct);
                shareTv.setLayoutParams(lLParams);
                shareTv.setSingleLine(true);
                shareTv.setGravity(Gravity.CENTER);
                shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
                shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

                // 如果是加入黑名单（这的逻辑太乱）
                if (i == 1 && toUser.getBlock()) {
                    shareTv.setText(resources.getString(R.string.remove_from_black_list));
                } else {
                    shareTv.setText(shareTvStr);
                }

                shareTv.setTextColor(shareTextColor);
                shareTv.setClickable(true);
                shareTv.setId(shareIconsOne[i]);
                FontUtil.setRegulartypeFace(baseFragAct, shareTv);
                shareTv.setOnClickListener(sharePopOnClick);
                drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
                shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
                shareTv.setCompoundDrawables(null, drawable, null, null);
                iconsLlTwo.addView(shareTv);
                tViewsOne[i] = shareTv;
            }
        } else {
            isHaveLine = false;
        }

        if (isHaveLine) {
            line.setVisibility(View.VISIBLE);
        } else {
            line.setVisibility(View.GONE);

        }
        sharePopup.showAtLocation(newsMoreView, Gravity.BOTTOM, 0, 0);
        sharePopup.update();
    }

    /**
     * @ mBaseFragAct
     * string
     * @Description: 分享WEB的URL，需要整合到上面的方法中
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-21
     */
    public void showShareProfileUrl02(String string, final BaseFragAct baseFragAct, final String title, final String url, final String imgurl) {

        int viewId = 0;
        LayoutInflater lInflater;
        final PopupWindow sharePopup;
        /** 关闭弹出层 */
        lInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newsMoreView = lInflater.inflate(R.layout.popup_news_detailed_article_share, new LinearLayout(baseFragAct), false);
        TextView titleTv = (TextView) newsMoreView.findViewById(R.id.popup_title);
        Button mCancelShareBtn = (Button) newsMoreView.findViewById(R.id.share_gv_btn);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        sharePopup = new PopupWindow(newsMoreView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        ColorDrawable cDrawable = new ColorDrawable(00000000);
        sharePopup.setAnimationStyle(R.style.popwin_anim_style);
        sharePopup.setBackgroundDrawable(cDrawable);
        sharePopup.setTouchable(true);
        sharePopup.setFocusable(true);
        sharePopup.setOutsideTouchable(true);
        sharePopup.setContentView(newsMoreView);
        titleTv.setText(string);
        int[] shareIconsTwo = new int[]{R.drawable.wechat_icon, R.drawable.circle_of_friends, R.drawable.copy, R.drawable.qq_icon, R.drawable.qzone_icon};
        int[] shareTextsTwo = new int[]{R.string.wechat, R.string.circle_of_friends, R.string.copy_link, R.string.circle_of_qq, R.string.circle_of_qqzone};
        final TextView[] tViewsOne = new TextView[shareTextsTwo.length];
        LinearLayout popu_outside_ll = (LinearLayout) newsMoreView.findViewById(R.id.popu_outside_ll);
        HorizontalScrollView hsvLineTwo = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_two);

        hsvLineTwo.setVisibility(View.VISIBLE);
        OnClickListener sharePopOnClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.share_gv_btn:
                        break;
                    case R.id.popu_outside_ll:
                        break;
                    case 0:
                        // ShareStore.shareToWX(baseFragAct, title, url);
                        ShareStore.shareImgToWXHasUrl(baseFragAct, url, title, "", imgurl);
                        break;
                    case 1:
                        ShareStore.shareImgToFriendHasUrl(baseFragAct, url, title, "", imgurl);
                        // ShareStore.shareToWXF(baseFragAct, title, url);
                        break;
                    case 2:
                        StringUtil.copyStringToClipboard(baseFragAct, url);
                        break;
                    case 3:
                        // Utils.openBrowser(baseFragAct, url);
                        ShareStore.shareToQQ(baseFragAct, url, title, "", imgurl);
                        break;
                    case 4:
                        // ShareStore.shareToSms(baseFragAct, url);
                        ArrayList<String> urls = new ArrayList<String>();
                        urls.add(imgurl);
                        ShareStore.shareToQzone(baseFragAct, url, title, "", urls);
                        break;
                }
                if (sharePopup != null && sharePopup.isShowing()) {
                    sharePopup.dismiss();
                }
            }
        };
        mCancelShareBtn.setOnClickListener(sharePopOnClick);
        popu_outside_ll.setOnClickListener(sharePopOnClick);
        mCancelShareBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (baseFragAct.mScreenHeight * 0.07 + 0.5f)));
        LinearLayout.LayoutParams lLParams = new LinearLayout.LayoutParams((int) (baseFragAct.mScreenHeight * 0.16 + 0.5f), LinearLayout.LayoutParams.WRAP_CONTENT);
        float textSize = (float) (baseFragAct.mScreenWidth * 0.013 + 0.5f);
        Resources resources = baseFragAct.getResources();
        int shareTextColor = resources.getColor(R.color.black_text);
        LinearLayout iconsLlOne = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_one);

        int leftPadding = (int) (baseFragAct.mScreenHeight * 0.01 + 0.5f);
        int rightPadding = (int) (baseFragAct.mScreenHeight * 0.01 + 0.5f);
        int bottomPadding = (int) (baseFragAct.mScreenHeight * 0.001 + 0.5f);

        for (int i = 0; i < shareIconsTwo.length; i++) {
            Drawable drawable = resources.getDrawable(shareIconsTwo[i]);
            String shareTvStr = resources.getString(shareTextsTwo[i]);
            TextView shareTv = new TextView(baseFragAct);
            shareTv.setLayoutParams(lLParams);
            shareTv.setSingleLine(true);
            shareTv.setGravity(Gravity.CENTER);
            shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
            shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            shareTv.setText(shareTvStr);
            shareTv.setTextColor(shareTextColor);
            shareTv.setClickable(true);
            shareTv.setId(viewId++);
            shareTv.setOnClickListener(sharePopOnClick);
            drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
            shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
            shareTv.setCompoundDrawables(null, drawable, null, null);
            FontUtil.setRegulartypeFace(baseFragAct, shareTv);
            iconsLlOne.addView(shareTv);
            tViewsOne[i] = shareTv;
        }

        HorizontalScrollView hsvLineOne = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_one);
        hsvLineOne.setVisibility(View.VISIBLE);
        sharePopup.showAtLocation(newsMoreView, Gravity.BOTTOM, 0, 0);
        sharePopup.update();
    }

    /**
     * mBaseFragAct
     * string
     *
     * @Description: 分享WEB的URL，需要整合到上面的方法中
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-21
     */
    public void showShareProfileUrl(String string, final BaseFragAct baseFragAct, final String title, final String url) {

        int viewId = 0;
        LayoutInflater lInflater;
        final PopupWindow sharePopup;
        /** 关闭弹出层 */
        lInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newsMoreView = lInflater.inflate(R.layout.popup_news_detailed_article_share, new LinearLayout(baseFragAct), false);
        TextView titleTv = (TextView) newsMoreView.findViewById(R.id.popup_title);
        Button mCancelShareBtn = (Button) newsMoreView.findViewById(R.id.share_gv_btn);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        sharePopup = new PopupWindow(newsMoreView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        ColorDrawable cDrawable = new ColorDrawable(00000000);
        sharePopup.setAnimationStyle(R.style.popwin_anim_style);
        sharePopup.setBackgroundDrawable(cDrawable);
        sharePopup.setTouchable(true);
        sharePopup.setFocusable(true);
        sharePopup.setOutsideTouchable(true);
        sharePopup.setContentView(newsMoreView);
        titleTv.setText(string);
        int[] shareIconsTwo = new int[]{R.drawable.wechat_icon, R.drawable.circle_of_friends, R.drawable.copy, R.drawable.safari_icon, R.drawable.message_popup};
        int[] shareTextsTwo = new int[]{R.string.wechat, R.string.circle_of_friends, R.string.copy_link, R.string.safari, R.string.message_share_url};
        final TextView[] tViewsOne = new TextView[shareTextsTwo.length];
        LinearLayout popu_outside_ll = (LinearLayout) newsMoreView.findViewById(R.id.popu_outside_ll);
        HorizontalScrollView hsvLineTwo = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_two);

        hsvLineTwo.setVisibility(View.VISIBLE);
        OnClickListener sharePopOnClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.share_gv_btn:
                        break;
                    case R.id.popu_outside_ll:
                        break;
                    case 0:
                        ShareStore.shareToWX(baseFragAct, title, url);
                        break;
                    case 1:
                        ShareStore.shareToWXF(baseFragAct, title, url);
                        break;
                    case 2:
                        StringUtil.copyStringToClipboard(baseFragAct, url);
                        break;
                    case 3:
                        Utils.openBrowser(baseFragAct, url);
                        break;
                    case 4:
                        ShareStore.shareToSms(baseFragAct, url);
                        break;
                }
                if (sharePopup != null && sharePopup.isShowing()) {
                    sharePopup.dismiss();
                }
            }
        };
        mCancelShareBtn.setOnClickListener(sharePopOnClick);
        popu_outside_ll.setOnClickListener(sharePopOnClick);
        mCancelShareBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (baseFragAct.mScreenHeight * 0.07 + 0.5f)));
        LinearLayout.LayoutParams lLParams = new LinearLayout.LayoutParams((int) (baseFragAct.mScreenHeight * 0.16 + 0.5f), LinearLayout.LayoutParams.WRAP_CONTENT);
        float textSize = (float) (baseFragAct.mScreenWidth * 0.013 + 0.5f);
        Resources resources = baseFragAct.getResources();
        int shareTextColor = resources.getColor(R.color.black_text);
        LinearLayout iconsLlOne = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_one);

        int leftPadding = (int) (baseFragAct.mScreenHeight * 0.01 + 0.5f);
        int rightPadding = (int) (baseFragAct.mScreenHeight * 0.01 + 0.5f);
        int bottomPadding = (int) (baseFragAct.mScreenHeight * 0.001 + 0.5f);

        for (int i = 0; i < shareIconsTwo.length; i++) {
            Drawable drawable = resources.getDrawable(shareIconsTwo[i]);
            String shareTvStr = resources.getString(shareTextsTwo[i]);
            TextView shareTv = new TextView(baseFragAct);
            shareTv.setLayoutParams(lLParams);
            shareTv.setSingleLine(true);
            shareTv.setGravity(Gravity.CENTER);
            shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
            shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            shareTv.setText(shareTvStr);
            shareTv.setTextColor(shareTextColor);
            shareTv.setClickable(true);
            shareTv.setId(viewId++);
            shareTv.setOnClickListener(sharePopOnClick);
            drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
            shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
            shareTv.setCompoundDrawables(null, drawable, null, null);
            FontUtil.setRegulartypeFace(baseFragAct, shareTv);
            iconsLlOne.addView(shareTv);
            tViewsOne[i] = shareTv;
        }

        HorizontalScrollView hsvLineOne = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_one);
        hsvLineOne.setVisibility(View.VISIBLE);
        sharePopup.showAtLocation(newsMoreView, Gravity.BOTTOM, 0, 0);
        sharePopup.update();
    }

    /**
     * 设定文件
     *
     * @return void 返回类型
     * @throws
     * @Title: black
     * @Description: 加入黑名单
     */
    private void black(final User user) {
        mUserService.blackUser(user.getId(), new Callback<Result<ResultData>>() {

            @Override
            public void success(Result<ResultData> arg0, Response arg1) {
                // ToastUtil.shortToast(context, "成功加入");
                XL.i("PopupStore", "black success");
                user.setBlock(true);
                ToastUtil.longToast(mContext, R.string.add_to_black_list_success);
            }

            @Override
            public void failure(RetrofitError arg0) {
                XL.i("PopupStore", "black user:failure-----" + arg0.getMessage());
                ToastUtil.longToast(mContext, R.string.network_error);
            }
        });
    }

    /**
     * 设定文件
     *
     * @return void 返回类型
     * @throws
     * @Title: black
     * @Description: 加入黑名单
     */
    private void black(final com.wealoha.social.api.user.bean.User user) {
        mUserService.blackUser(user.getId(), new Callback<Result<ResultData>>() {

            @Override
            public void success(Result<ResultData> arg0, Response arg1) {
                // ToastUtil.shortToast(context, "成功加入");
                XL.i("PopupStore", "black success");
                user.block();
                ToastUtil.longToast(mContext, R.string.add_to_black_list_success);
            }

            @Override
            public void failure(RetrofitError arg0) {
                XL.i("PopupStore", "black user:failure-----" + arg0.getMessage());
                ToastUtil.longToast(mContext, R.string.network_error);
            }
        });
    }

    /**
     * context
     *
     * @return
     */
    public Dialog showFirstEnterFeedsPopDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.is_first_enter_160v, null, false);
        Button close = (Button) view.findViewById(R.id.cancel_btn);
        FontUtil fontUtil = new FontUtil(context);
        fontUtil.changeViewFont(((TextView) view.findViewById(R.id.title)), Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        fontUtil.changeViewFont(((TextView) view.findViewById(R.id.video_post)), Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        fontUtil.changeViewFont(((TextView) view.findViewById(R.id.public_comments)), Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        fontUtil.changeViewFont(((TextView) view.findViewById(R.id.privacy)), Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        FontUtil.setRegulartypeFace(context, close);
        FontUtil.setRegulartypeFace(context, ((TextView) view.findViewById(R.id.video_post_des)));
        FontUtil.setRegulartypeFace(context, ((TextView) view.findViewById(R.id.public_comments_str)));
        FontUtil.setRegulartypeFace(context, ((TextView) view.findViewById(R.id.privacy_des)));

        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.cancel();
                }
            }
        });

        alertDialog = new AlertDialog.Builder(context)//
                .setView(view)//
                .setCancelable(false) //
                .create();
        alertDialog.show();
        alertDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.IS_FIRST_ENTER_160V, false);
            }
        });
        return alertDialog;
    }

    private Dialog alertDialog;

    public void openGuideDialog(BaseFragAct baseAct, final User user, PopupWindow sharePopup) {
        if (sharePopup != null && sharePopup.isShowing()) {
            sharePopup.dismiss();
        }
        View view = LayoutInflater.from(baseAct).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(baseAct), false);
        TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
        TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
        TextView close = (TextView) view.findViewById(R.id.close_tv);
        TextView close02 = (TextView) view.findViewById(R.id.close_tv_02);
        FontUtil.setRegulartypeFace(baseAct, message);
        FontUtil.setRegulartypeFace(baseAct, close02);
        FontUtil.setRegulartypeFace(baseAct, title);
        FontUtil.setRegulartypeFace(baseAct, close);

        title.setText(R.string.add_to_black_confirm);
        message.setText(R.string.add_to_black_rule);
        message.setGravity(Gravity.CENTER);
        close.setText(R.string.cancel);
        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });

        close02.setVisibility(View.VISIBLE);
        close02.setText(R.string.add);
        close02.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                black(user);
            }
        });

        alertDialog = new AlertDialog.Builder(baseAct)//
                .setView(view)//
                .setCancelable(false) //
                .create();
        alertDialog.show();

    }

    public void openGuideDialog(BaseFragAct baseAct, final com.wealoha.social.api.user.bean.User user, PopupWindow sharePopup) {
        if (sharePopup != null && sharePopup.isShowing()) {
            sharePopup.dismiss();
        }
        View view = LayoutInflater.from(baseAct).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(baseAct), false);
        TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
        TextView message = (TextView) view.findViewById(R.id.first_aloha_message);
        TextView close = (TextView) view.findViewById(R.id.close_tv);
        TextView close02 = (TextView) view.findViewById(R.id.close_tv_02);
        FontUtil.setRegulartypeFace(baseAct, message);
        FontUtil.setRegulartypeFace(baseAct, close02);
        FontUtil.setRegulartypeFace(baseAct, title);
        FontUtil.setRegulartypeFace(baseAct, close);

        title.setText(R.string.add_to_black_confirm);
        message.setText(R.string.add_to_black_rule);
        message.setGravity(Gravity.CENTER);
        close.setText(R.string.cancel);
        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });

        close02.setVisibility(View.VISIBLE);
        close02.setText(R.string.add);

        close02.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                black(user);
            }
        });

        alertDialog = new AlertDialog.Builder(baseAct)//
                .setView(view)//
                .setCancelable(false) //
                .create();
        alertDialog.show();

    }

    /**
     * 设定文件
     *
     * @return void 返回类型
     * @throws
     * @Title: black
     * @Description: 移除黑名单
     */
    private void removeFromBlack(final User user) {
        mUserService.unblock(user.getId(), new Callback<Result<ResultData>>() {

            @Override
            public void failure(RetrofitError arg0) {
                XL.i(TAG, "移除黑名單失敗");
                ToastUtil.longToast(mContext, R.string.network_error);
            }

            @Override
            public void success(Result<ResultData> result, Response arg1) {
                user.setBlock(false);
                ToastUtil.longToast(mContext, R.string.remove_from_black_list_success);
                XL.i(TAG, "移除黑名單成功");
            }
        });
    }

    /**
     * 设定文件
     *
     * @return void 返回类型
     * @throws
     * @Title: black
     * @Description: 移除黑名单
     */
    private void removeFromBlack(final com.wealoha.social.api.user.bean.User user) {
        mUserService.unblock(user.getId(), new Callback<Result<ResultData>>() {

            @Override
            public void failure(RetrofitError arg0) {
                XL.i(TAG, "移除黑名單失敗");
                ToastUtil.longToast(mContext, R.string.network_error);
            }

            @Override
            public void success(Result<ResultData> result, Response arg1) {
                user.removeBlock();
                ;
                ToastUtil.longToast(mContext, R.string.remove_from_black_list_success);
                XL.i(TAG, "移除黑名單成功");
            }
        });
    }

    /**
     * 设定文件
     *
     * @return void 返回类型
     * @throws
     * @Title: report
     * @Description: 举报用户或FEED
     */
    private void report(User user) {
        mUserService.reportUser(user.getId(), null, new Callback<Result<ResultData>>() {

            @Override
            public void success(Result<ResultData> arg0, Response arg1) {
                ToastUtil.longToast(mContext, R.string.report_inappropriate_success);
            }

            @Override
            public void failure(RetrofitError arg0) {
                ToastUtil.longToast(mContext, R.string.network_error);
            }
        });
    }

    /**
     * 设定文件
     *
     * @return void 返回类型
     * @throws
     * @Title: report
     * @Description: 举报用户或FEED
     */
    private void report(com.wealoha.social.api.user.bean.User user) {
        mUserService.reportUser(user.getId(), null, new Callback<Result<ResultData>>() {

            @Override
            public void success(Result<ResultData> arg0, Response arg1) {
                ToastUtil.longToast(mContext, R.string.report_inappropriate_success);
            }

            @Override
            public void failure(RetrofitError arg0) {
                ToastUtil.longToast(mContext, R.string.network_error);
            }
        });
    }

    /**
     * @Description:显示单个按钮的对话框
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年3月5日
     */
    public Dialog showAlohaDialogSingleBtn(Context context, Integer titleResId, Integer bodyResId, View.OnClickListener onClickListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_aloha_prompt, new LinearLayout(context), false);
        TextView close = (TextView) view.findViewById(R.id.close_tv);
        TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
        TextView body = (TextView) view.findViewById(R.id.first_aloha_message);
        FontUtil.setRegulartypeFace(context, body);
        FontUtil.setRegulartypeFace(context, title);
        FontUtil.setRegulartypeFace(context, close);
        if (titleResId == null) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(context.getResources().getString(titleResId));
        }
        if (bodyResId == null) {
            body.setVisibility(View.GONE);
        } else {
            body.setText(context.getResources().getString(bodyResId));
        }
        if (onClickListener == null) {// 没有设置监听，那么点击关闭按钮就关闭对话框
            close.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.cancel();
                    }
                }
            });
        } else {
            close.setOnClickListener(onClickListener);
        }

        alertDialog = new AlertDialog.Builder(context)//
                .setView(view)//
                .setCancelable(false) //
                .create();
        return alertDialog;
    }

    public void swithcDialg() {
        if (alertDialog != null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            } else {
                alertDialog.show();
            }
        }
    }

    public void show() {
        if (alertDialog != null) {
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    public void showSharePostUrl(String string, final BaseFragAct baseFragAct, final String title, final String url) {

        int viewId = 0;
        LayoutInflater lInflater;
        final PopupWindow sharePopup;
        /** 关闭弹出层 */
        lInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newsMoreView = lInflater.inflate(R.layout.popup_news_detailed_article_share, new LinearLayout(baseFragAct), false);
        sharePopup = new PopupWindow(newsMoreView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        ColorDrawable cDrawable = new ColorDrawable(00000000);
        sharePopup.setAnimationStyle(R.style.popwin_anim_style);
        sharePopup.setBackgroundDrawable(cDrawable);
        sharePopup.setTouchable(true);
        sharePopup.setFocusable(true);
        sharePopup.setOutsideTouchable(true);
        sharePopup.setContentView(newsMoreView);
        TextView titleTv = (TextView) newsMoreView.findViewById(R.id.popup_title);
        titleTv.setText(string);
        int[] shareIconsTwo = new int[]{R.drawable.wechat_icon, R.drawable.circle_of_friends, R.drawable.copy, R.drawable.safari_icon, R.drawable.message_popup};
        int[] shareTextsTwo = new int[]{R.string.wechat, R.string.circle_of_friends, R.string.copy_link, R.string.safari, R.string.message_share_url};
        final TextView[] tViewsOne = new TextView[shareTextsTwo.length];
        Button mCancelShareBtn = (Button) newsMoreView.findViewById(R.id.share_gv_btn);
        LinearLayout popu_outside_ll = (LinearLayout) newsMoreView.findViewById(R.id.popu_outside_ll);
        HorizontalScrollView hsvLineTwo = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_two);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);
        hsvLineTwo.setVisibility(View.VISIBLE);
        OnClickListener sharePopOnClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.share_gv_btn:
                        break;
                    case R.id.popu_outside_ll:
                        break;
                    case 0:
                        ShareStore.shareToWX(baseFragAct, title, url);
                        break;
                    case 1:
                        ShareStore.shareToWXF(baseFragAct, title, url);
                        break;
                    case 2:
                        StringUtil.copyStringToClipboard(baseFragAct, url);
                        break;
                    case 3:
                        Utils.openBrowser(baseFragAct, url);
                        break;
                    case 4:
                        ShareStore.shareToSms(baseFragAct, url);
                        break;
                }
                if (sharePopup != null && sharePopup.isShowing()) {
                    sharePopup.dismiss();
                }
            }
        };
        mCancelShareBtn.setOnClickListener(sharePopOnClick);
        popu_outside_ll.setOnClickListener(sharePopOnClick);
        mCancelShareBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (baseFragAct.mScreenHeight * 0.07 + 0.5f)));
        LinearLayout.LayoutParams lLParams = new LinearLayout.LayoutParams((int) (baseFragAct.mScreenHeight * 0.16 + 0.5f), LinearLayout.LayoutParams.WRAP_CONTENT);
        float textSize = (float) (baseFragAct.mScreenWidth * 0.013 + 0.5f);
        Resources resources = baseFragAct.getResources();
        int shareTextColor = resources.getColor(R.color.black_text);
        LinearLayout iconsLlOne = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_one);

        int leftPadding = (int) (baseFragAct.mScreenHeight * 0.01 + 0.5f);
        int rightPadding = (int) (baseFragAct.mScreenHeight * 0.01 + 0.5f);
        int bottomPadding = (int) (baseFragAct.mScreenHeight * 0.001 + 0.5f);

        for (int i = 0; i < shareIconsTwo.length; i++) {
            Drawable drawable = resources.getDrawable(shareIconsTwo[i]);
            String shareTvStr = resources.getString(shareTextsTwo[i]);
            TextView shareTv = new TextView(baseFragAct);
            shareTv.setLayoutParams(lLParams);
            shareTv.setSingleLine(true);
            shareTv.setGravity(Gravity.CENTER);
            shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
            shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            shareTv.setText(shareTvStr);
            shareTv.setTextColor(shareTextColor);
            shareTv.setClickable(true);
            shareTv.setId(viewId++);
            shareTv.setOnClickListener(sharePopOnClick);
            drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
            shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
            shareTv.setCompoundDrawables(null, drawable, null, null);
            FontUtil.setRegulartypeFace(baseFragAct, shareTv);
            iconsLlOne.addView(shareTv);
            tViewsOne[i] = shareTv;
        }

        HorizontalScrollView hsvLineOne = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_one);
        hsvLineOne.setVisibility(View.VISIBLE);
        sharePopup.showAtLocation(newsMoreView, Gravity.BOTTOM, 0, 0);
        sharePopup.update();
    }

    private Context mCtx;

    public PopupStore(Context context) {
        mCtx = context;
    }

    public static final int VERTICAL_ONE = 0x000001;

    public void showShareDialog() {

    }

    ContextUtil contextUtil = new ContextUtil();

    public void showShareProfilePopup(final BaseFragAct baseFragAct, final Post mPost, Bitmap bitmap) {
        showShareProfilePopup(baseFragAct, mPost);
    }

    public void showShareProfilePopup(final BaseFragAct baseFragAct, final Post mPost) {

        int viewId = 0;
        LayoutInflater lInflater;
        final PopupWindow sharePopup;
        /** 关闭弹出层 */
        lInflater = (LayoutInflater) baseFragAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newsMoreView = lInflater.inflate(R.layout.popup_news_detailed_article_share, new LinearLayout(baseFragAct), false);
        View line = newsMoreView.findViewById(R.id.line);
        sharePopup = new PopupWindow(newsMoreView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        ColorDrawable cDrawable = new ColorDrawable(00000000);
        sharePopup.setAnimationStyle(R.style.popwin_anim_style);
        sharePopup.setBackgroundDrawable(cDrawable);
        sharePopup.setTouchable(true);
        sharePopup.setFocusable(true);
        sharePopup.setOutsideTouchable(true);
        sharePopup.setContentView(newsMoreView);
        int[] shareIconsTwo = new int[]{R.drawable.wechat_icon, R.drawable.circle_of_friends, R.drawable.copy, R.drawable.qq_icon, R.drawable.qzone_icon};
        int[] shareTextsTwo = new int[]{R.string.wechat, R.string.circle_of_friends, R.string.copy_link, R.string.circle_of_qq, R.string.circle_of_qqzone};
        final TextView[] tViewsOne = new TextView[shareTextsTwo.length];
        TextView titleTv = (TextView) newsMoreView.findViewById(R.id.popup_title);
        Button mCancelShareBtn = (Button) newsMoreView.findViewById(R.id.share_gv_btn);
        LinearLayout popu_outside_ll = (LinearLayout) newsMoreView.findViewById(R.id.popu_outside_ll);
        HorizontalScrollView hsvLineTwo = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_two);
        FontUtil.setRegulartypeFace(baseFragAct, titleTv);
        FontUtil.setRegulartypeFace(baseFragAct, mCancelShareBtn);
        hsvLineTwo.setVisibility(View.VISIBLE);
        OnClickListener sharePopOnClick = new OnClickListener() {

            @Override
            public void onClick(View v) {
                String imgUrl = mPost.getImage().getUrlSquare(ImageSize.CHAT_THUMB);
                switch (v.getId()) {
                    case R.id.popu_outside_ll:
                        break;
                    case 0:
                        if (mPost.getType() == FeedType.ImagePost) {
                            if (mPost.isMine()) {// 分享自己的
                                ShareStore.shareImgToWXHasUrl(baseFragAct, StringUtil.sharePostWebUrl(mPost.getUser().getId(), //
                                        mPost.getUser().getId(), mPost, StringUtil.ME_PROFILE_TO_SHARE_WX), "Aloha", "", imgUrl);
                            } else {// 分享別人的
                                ShareStore.shareImgToWXHasUrl(baseFragAct, StringUtil.sharePostWebUrl(contextUtil.getCurrentUser().getId(),//
                                        mPost.getUser().getId(), mPost, StringUtil.OTHER_PROFILE_TO_SHARE_WX), "Aloha", "", imgUrl);
                            }
                        } else if (mPost.getType() == FeedType.VideoPost) {// 分享視頻
                            if (mPost.isMine()) {// 分享自己的
                                ShareStore.shareVideoToWXHasUrl(baseFragAct, StringUtil.sharePostWebUrl(mPost.getUser().getId(), //
                                        mPost.getUser().getId(), mPost, StringUtil.ME_PROFILE_TO_SHARE_WX), "Aloha", "", imgUrl);
                            } else {// 分享別人的
                                ShareStore.shareVideoToWXHasUrl(baseFragAct, StringUtil.sharePostWebUrl(contextUtil.getCurrentUser().getId(),//
                                        mPost.getUser().getId(), mPost, StringUtil.OTHER_PROFILE_TO_SHARE_WX), "Aloha", "", imgUrl);
                            }
                        }
                        feedShare(mPost);
                        break;
                    case 1:
                        String urlwx = null;
                        if (mPost.getType() == FeedType.ImagePost) {
                            if (mPost.isMine()) {// 分享自己的
                                urlwx = StringUtil.sharePostWebUrl(mPost.getUser().getId(), //
                                        mPost.getUser().getId(), mPost, StringUtil.ME_PROFILE_TO_SHARE_WX);
                                ShareStore.shareImgToFriendHasUrl(baseFragAct, urlwx, "Aloha", "", imgUrl);
                            } else {// 分享別人的
                                urlwx = StringUtil.sharePostWebUrl(contextUtil.getCurrentUser().getId(), //
                                        mPost.getUser().getId(), mPost, StringUtil.OTHER_PROFILE_TO_SHARE_WX);
                                ShareStore.shareImgToFriendHasUrl(baseFragAct, urlwx, "Aloha", "", imgUrl);
                            }
                        } else if (mPost.getType() == FeedType.VideoPost) {// 分享視頻
                            if (mPost.isMine()) {// 分享自己的
                                urlwx = StringUtil.sharePostWebUrl(mPost.getUser().getId(), //
                                        mPost.getUser().getId(), mPost, StringUtil.ME_PROFILE_TO_SHARE_WXF);

                                ShareStore.shareVideoToFriendHasUrl(baseFragAct, urlwx, "Aloha", "", imgUrl);
                            } else {// 分享別人的
                                urlwx = StringUtil.sharePostWebUrl(contextUtil.getCurrentUser().getId(), //
                                        mPost.getUser().getId(), mPost, StringUtil.OTHER_PROFILE_TO_SHARE_WXF);
                                ShareStore.shareVideoToFriendHasUrl(baseFragAct, urlwx, "Aloha", "", imgUrl);
                            }
                        }
                        feedShare(mPost);
                        break;
                    case 2:
                        StringUtil.copyStringToClipboard(baseFragAct, imgUrl);
                        break;
                    case 3:
                        String url = null;
                        // 分享到qq
                        if (mPost.isMine()) {
                            url = StringUtil.sharePostWebUrl(mPost.getUser().getId(), "", mPost, StringUtil.ME_PROFILE_TO_SHARE_WX);
                        } else {
                            url = StringUtil.sharePostWebUrl("", mPost.getUser().getId(), mPost, StringUtil.OTHER_PROFILE_TO_SHARE_WX);
                        }
                        XL.d("ParamsShareToQQ", "baseFragAct:" + baseFragAct + "-"//
                                + "url:" + url + "-mPost.getUser().getName()" + //
                                mPost.getUser().getName() + "-mPost.getDescription()" + mPost.getDescription()//
                                + "-imgUrl" + imgUrl);
                        ShareStore.shareToQQ(baseFragAct, url, mPost.getUser().getName(), mPost.getDescription(), imgUrl);
                        feedShare(mPost);
                        break;
                    case 4:
                        ArrayList<String> imgurls = new ArrayList<String>();
                        imgurls.add(imgUrl);

                        String urlZone = null;
                        if (mPost.isMine()) {
                            urlZone = StringUtil.sharePostWebUrl(mPost.getUser().getId(), null, mPost, StringUtil.ME_PROFILE_TO_SHARE_WX);
                        } else {
                            urlZone = StringUtil.sharePostWebUrl(null, mPost.getUser().getId(), mPost, StringUtil.OTHER_PROFILE_TO_SHARE_WX);
                        }
                        ShareStore.shareToQzone(baseFragAct, urlZone, mPost.getUser().getName(), mPost.getDescription(), imgurls);
                        XL.i("QQ_ZONE_SHARE", "qq_zone:" + urlZone);
                        feedShare(mPost);
                        break;
                }
                if (sharePopup != null && sharePopup.isShowing()) {
                    sharePopup.dismiss();
                }
            }

            private void feedShare(Post mPost) {
                if (mPost != null)
                    feedShare.feedShare(mPost.getPostId(), new Callback<ResultData>() {

                        @Override
                        public void failure(RetrofitError arg0) {

                        }

                        @Override
                        public void success(ResultData arg0, Response arg1) {

                        }
                    });
            }
        };
        mCancelShareBtn.setOnClickListener(sharePopOnClick);
        popu_outside_ll.setOnClickListener(sharePopOnClick);
        mCancelShareBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (baseFragAct.mScreenHeight * 0.07 + 0.5f)));
        LinearLayout.LayoutParams lLParams = new LinearLayout.LayoutParams((int) (baseFragAct.mScreenHeight * 0.16 + 0.5f), LinearLayout.LayoutParams.WRAP_CONTENT);
        float textSize = 12f;
        // float textSize = (float) (baseFragAct.mScreenWidth * 0.013 + 0.5f);
        Resources resources = baseFragAct.getResources();
        int shareTextColor = resources.getColor(R.color.black_text);
        LinearLayout iconsLlOne = (LinearLayout) newsMoreView.findViewById(R.id.icons_ll_one);

        int leftPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int rightPadding = (int) (baseFragAct.mScreenHeight * 0.008 + 0.5f);
        int bottomPadding = (int) (baseFragAct.mScreenHeight * 0.001 + 0.5f);
        titleTv.setText(baseFragAct.getString(R.string.share_my_profile));

        for (int i = 0; i < shareIconsTwo.length; i++) {
            Drawable drawable = resources.getDrawable(shareIconsTwo[i]);
            String shareTvStr = resources.getString(shareTextsTwo[i]);
            TextView shareTv = new TextView(baseFragAct);
            shareTv.setSingleLine(true);
            shareTv.setLayoutParams(lLParams);
            shareTv.setGravity(Gravity.CENTER);
            shareTv.setPadding(leftPadding, 0, rightPadding, bottomPadding);
            shareTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            shareTv.setText(shareTvStr);
            shareTv.setTextColor(shareTextColor);
            shareTv.setClickable(true);
            shareTv.setId(viewId++);
            shareTv.setOnClickListener(sharePopOnClick);
            drawable.setBounds(0, 0, (int) (drawable.getMinimumWidth() / 1.0), (int) (drawable.getMinimumHeight() / 1.0));
            shareTv.setCompoundDrawablePadding((int) (baseFragAct.mScreenHeight * 0.01 + 0.5f));
            shareTv.setCompoundDrawables(null, drawable, null, null);
            FontUtil.setRegulartypeFace(baseFragAct, shareTv);
            iconsLlOne.addView(shareTv);
            tViewsOne[i] = shareTv;
        }

        HorizontalScrollView hsvLineOne = (HorizontalScrollView) newsMoreView.findViewById(R.id.hsv_line_one);
        hsvLineOne.setVisibility(View.VISIBLE);
        sharePopup.showAtLocation(newsMoreView, Gravity.BOTTOM, 0, 0);
        sharePopup.update();

    }

}
