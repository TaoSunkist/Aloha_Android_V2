package com.wealoha.social.store;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.squareup.picasso.Picasso;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage.IMediaObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.callback.CallbackImpl;
import com.wealoha.social.commons.AlohaThreadPool;
import com.wealoha.social.commons.AlohaThreadPool.ENUM_Thread_Level;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.impl.ShareCallbackImpl;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 分享的工具类
 * @copyright wealoha.com
 * @Date:2015年7月16日
 */
public class ShareStore {

    /**
     * @author:sunkist
     * @see:
     * @since:
     * @description 分享的状态监听回调.
     * @copyright wealoha.com
     * @Date:2015年7月16日
     */
    public static interface SetOnShareCallback {

        public void onSuccess();

        public void onFailure();
    }

    @Inject
    static Picasso mPicasso;

    /**
     * @Description:分享网络连接给微信
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-20
     */
    public static void shareToWXHasUrl(final BaseFragAct baseFragAct, String webpageUrl, String title, String body, final String imgUrl) {
        try {
            final IWXAPI iwxapi = WXAPIFactory.createWXAPI(baseFragAct, GlobalConstants.AppConstact.QQ_WX_APPID, true);
            if (iwxapi.isWXAppInstalled()) {
                iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);
                WXWebpageObject webpage = new WXWebpageObject();
                final WXMediaMessage msg = new WXMediaMessage(webpage);
                webpage.webpageUrl = webpageUrl;
                msg.title = title;
                msg.description = body;
                AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap thumb = ImageUtil.handPic(imgUrl);
                        if (thumb != null) {
                            msg.thumbData = ImageUtil.bmpToByteArray(thumb, true);
                        } else {
                            msg.thumbData = null;
                        }
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage" + System.currentTimeMillis();
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        iwxapi.sendReq(req);
                    }
                });
            } else {
                ToastUtil.shortToast(baseFragAct, R.string.uninstall_wxapp);
            }
        } catch (Exception e) {
            XL.e("share$QQweixin", e.getMessage());
            ToastUtil.shortToast(baseFragAct, R.string.is_not_work);
        }
    }

    public static void shareToFriendHasUrl(BaseFragAct baseFragAct, String webpageUrl, String title, String body, final String imgUrl) {
        try {
            final IWXAPI iwxapi = WXAPIFactory.createWXAPI(baseFragAct, GlobalConstants.AppConstact.QQ_WX_APPID, true);
            if (iwxapi.isWXAppInstalled()) {
                iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = webpageUrl;
                final WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                msg.description = body;
                AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap thumb = ImageUtil.handPic(imgUrl);
                        if (thumb != null) {
                            msg.thumbData = ImageUtil.bmpToByteArray(thumb, true);
                        } else {
                            msg.thumbData = null;
                        }
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage" + System.currentTimeMillis();
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        iwxapi.sendReq(req);
                    }
                });
            } else {
                ToastUtil.shortToast(baseFragAct, R.string.uninstall_wxapp);
            }
        } catch (Exception e) {
            ToastUtil.shortToast(baseFragAct, R.string.is_not_work);
        }
    }

    public static void shareToWX(BaseFragAct baseFragAct, String title, final String url) {

        try {
            final IWXAPI iwxapi = WXAPIFactory.createWXAPI(baseFragAct, GlobalConstants.AppConstact.QQ_WX_APPID, true);
            if (iwxapi.isWXAppInstalled()) {
                iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = "http://www.baidu.com";
                final WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = "Aloha";
                AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap thumb = ImageUtil.handPic(url);
                        // Bitmap bitmap = ImageUtil.compressImage(thumb);
                        if (thumb != null) {
                            msg.thumbData = ImageUtil.bmpToByteArray(thumb, true);
                        } else {
                            msg.thumbData = null;
                        }
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage" + System.currentTimeMillis();
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        iwxapi.sendReq(req);
                    }
                });
            } else {
                ToastUtil.shortToast(baseFragAct, R.string.uninstall_wxapp);
            }
        } catch (Exception e) {
            XL.e("share$QQweixin", e.getMessage());
            ToastUtil.shortToast(baseFragAct, R.string.is_not_work);
        }
    }

    public static void shareToWXF(BaseFragAct baseFragAct, String title, String url) {
        try {
            final IWXAPI iwxapi = WXAPIFactory.createWXAPI(baseFragAct, GlobalConstants.AppConstact.QQ_WX_APPID, true);
            if (iwxapi.isWXAppInstalled()) {
                iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);
                WXWebpageObject webpage = new WXWebpageObject();
                WXMediaMessage msg = new WXMediaMessage((IMediaObject) webpage);
                webpage.webpageUrl = url;
                msg.title = title;
                msg.thumbData = null;
                com.tencent.mm.sdk.modelmsg.SendMessageToWX.Req req = new com.tencent.mm.sdk.modelmsg.SendMessageToWX.Req();
                req.transaction = "webpage" + System.currentTimeMillis();
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                iwxapi.sendReq(req);
            } else {
                ToastUtil.shortToast(baseFragAct, R.string.uninstall_wxapp);
            }

        } catch (Throwable e) {
            ToastUtil.shortToast(baseFragAct, R.string.is_not_work);
        }

    }

    /**
     * @Description:分享到信息
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-21
     */
    public static void shareToSms(BaseFragAct baseFragAct, String text) {
        try {
            Uri smsToUri = Uri.parse("smsto:");
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
            sendIntent.putExtra("sms_body", text);
            sendIntent.setType("vnd.android-dir/mms-sms");
            baseFragAct.startActivityForResult(sendIntent, 1002);
        } catch (Exception e) {
            ToastUtil.shortToast(baseFragAct, R.string.is_not_work);
        }
    }

    public static void shareToQQ(BaseFragAct baseFragAct, String webpageUrl, String title, String body, final String imgUrl) {
        try {

            Tencent mTencent = Tencent.createInstance(baseFragAct.getString(R.string.qq_sdk_id), baseFragAct.getApplicationContext());
            QQAuth mQQAuth = QQAuth.createInstance(baseFragAct.getString(R.string.qq_sdk_id), baseFragAct);
            QQShare mQQShare = new QQShare(baseFragAct, mQQAuth.getQQToken());
            // ToastUtil.shortToast(baseFragAct, "shareToQQ");
            final Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            if (!TextUtils.isEmpty(body)) {
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, body);
            } else {
            }
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, webpageUrl);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);

            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "ALOHA");
            // params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,
            // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
            mQQShare.shareToQQ(baseFragAct, params, qqShareListener);
        } catch (Exception e) {
        }
    }

    // public static void sharePostToQQ(BaseFragAct baseFragAct, final String
    // imageUrl) {
    // Tencent mTencent =
    // Tencent.createInstance(baseFragAct.getString(R.string.qq_sdk_id),
    // baseFragAct.getApplicationContext());
    // QQAuth mQQAuth =
    // QQAuth.createInstance(baseFragAct.getString(R.string.qq_sdk_id),
    // baseFragAct);
    // QQShare mQQShare = new QQShare(baseFragAct, mQQAuth.getQQToken());
    //
    // Bundle params = new Bundle();
    // params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
    // params.putString(QQShare.SHARE_TO_QQ_APP_NAME,
    // baseFragAct.getString(R.string.app_name));
    // params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE,
    // QQShare.SHARE_TO_QQ_TYPE_IMAGE);
    // params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,
    // QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
    // mQQShare.shareToQQ(baseFragAct, params, qqShareListener);
    // }

    public static void shareToQzone(BaseFragAct baseFragAct, String webpageUrl, String title, String body, final ArrayList<String> imgurls) {
        Tencent mTencent = Tencent.createInstance(baseFragAct.getString(R.string.qq_sdk_id), baseFragAct.getApplicationContext());
        final Bundle params = new Bundle();
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);// 必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, body);// 选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, webpageUrl);// 必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgurls);
        mTencent.shareToQzone(baseFragAct, params, qqShareListener);
    }

    /**
     * 微信分享類型：圖片加鏈接
     */
    public static final int SHARE_IMG_TO_WX_HAS_URL = 0x01;
    /**
     * 微信分享類型：Web鏈接
     */
    public static final int SHARE_WEB_TO_WX_HAS_URL = 0x02;
    /**
     * 微信朋友圈分享類型：圖片加鏈接
     */

    public static final int SHARE_IMG_TO_WX_FRIEND_HAS_URL = 0x03;
    /**
     * 微信朋友圈分享類型：Web鏈接
     */
    public static final int SHARE_WEB_TO_WX_FRIEND_HAS_URL = 0x04;

    /**
     * @param ctx
     * @param webpageUrl
     * @param title
     * @param body
     * @param imgUrl
     * @param shareToWxType
     * @Description: 集成微信分享的方法
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年4月9日
     */
    public static void shareToWx(Context ctx, String webpageUrl, String title, String body, String imgUrl, int shareToWxType) {
        // FIXME 請完成！！！！！！！ 微信分享的jich
        try {
            final IWXAPI iwxapi = WXAPIFactory.createWXAPI(ctx, GlobalConstants.AppConstact.QQ_WX_APPID, true);
            iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);
            switch (shareToWxType) {

                case SHARE_IMG_TO_WX_HAS_URL:

                    break;
                case SHARE_WEB_TO_WX_HAS_URL:

                    break;
                case SHARE_IMG_TO_WX_FRIEND_HAS_URL:

                    break;
                case SHARE_WEB_TO_WX_FRIEND_HAS_URL:

                    break;
                default:
                    ToastUtil.shortToast(ctx, R.string.is_not_work);
                    break;
            }
        } catch (Exception e) {
            ToastUtil.shortToast(ctx, R.string.Unkown_Error);
        }
    }

    /**
     * @Description:分享网络连接给微信
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-20
     */
    public static void shareImgToWXHasUrl(final BaseFragAct baseFragAct, String webpageUrl, String title, String body, final String imgUrl) {
        try {
            final IWXAPI iwxapi = WXAPIFactory.createWXAPI(baseFragAct, GlobalConstants.AppConstact.QQ_WX_APPID, true);
            if (iwxapi.isWXAppInstalled()) {

                iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);

                final WXImageObject wxImageObject = new WXImageObject();
                final WXMediaMessage msg = new WXMediaMessage(wxImageObject);
                wxImageObject.imageUrl = webpageUrl;
                msg.title = "Aloha";
                AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap thumb = ImageUtil.handPic(imgUrl);
                        if (thumb != null) {
                            msg.setThumbImage(thumb);
                        } else {
                            wxImageObject.imageData = null;
                        }
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage" + System.currentTimeMillis();
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        iwxapi.sendReq(req);
                    }
                });
            } else {
                ToastUtil.shortToast(baseFragAct, R.string.uninstall_wxapp);
            }
        } catch (Exception e) {
            XL.e("share$QQweixin", e.getMessage());
            ToastUtil.shortToast(baseFragAct, R.string.is_not_work);
        }
    }

    public static void shareVideoToWXHasUrl(final BaseFragAct baseFragAct, String webpageUrl, String title, String body, final String imgUrl) {
        try {
            final IWXAPI iwxapi = WXAPIFactory.createWXAPI(baseFragAct, GlobalConstants.AppConstact.QQ_WX_APPID, true);
            if (iwxapi.isWXAppInstalled()) {

                iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);
                BitmapDrawable bitmapDrawable = (BitmapDrawable) baseFragAct.getResources().getDrawable(R.drawable.play_btn_s);
                final Bitmap iconBtn = bitmapDrawable.getBitmap();
                final WXImageObject wxImageObject = new WXImageObject();
                final WXMediaMessage msg = new WXMediaMessage(wxImageObject);
                wxImageObject.imageUrl = webpageUrl;
                msg.title = "Aloha";
                AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap thumb = ImageUtil.handPic(imgUrl);
                        Bitmap layerThumb = ImageUtil.getTwoDimentsionalCode(thumb, iconBtn);
                        if (thumb != null) {
                            msg.thumbData = ImageUtil.bmpToByteArray(layerThumb, true);
                        } else {
                            msg.thumbData = null;
                        }
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage" + System.currentTimeMillis();
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        iwxapi.sendReq(req);
                    }
                });
            } else {
                ToastUtil.shortToast(baseFragAct, R.string.uninstall_wxapp);
            }
        } catch (Exception e) {
            XL.e("share$QQweixin", e.getMessage());
            ToastUtil.shortToast(baseFragAct, R.string.is_not_work);
        }
    }

    public static void shareImgToFriendHasUrl(BaseFragAct baseFragAct, String webpageUrl, String title, String body, final String imgUrl) {
        try {
            final IWXAPI iwxapi = WXAPIFactory.createWXAPI(baseFragAct, GlobalConstants.AppConstact.QQ_WX_APPID, true);
            if (iwxapi.isWXAppInstalled()) {
                iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);

                WXWebpageObject webpage = new WXWebpageObject();
                final WXMediaMessage msg = new WXMediaMessage(webpage);
                webpage.webpageUrl = webpageUrl;
                msg.title = "Aloha";
                AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap thumb = ImageUtil.handPic(imgUrl);
                        if (thumb != null) {
                            msg.thumbData = ImageUtil.bmpToByteArray(thumb, true);
                        } else {
                            msg.thumbData = null;
                        }
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage" + System.currentTimeMillis();
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        iwxapi.sendReq(req);
                    }
                });
            } else {
                ToastUtil.shortToast(baseFragAct, R.string.uninstall_wxapp);
            }
        } catch (Exception e) {
            ToastUtil.shortToast(baseFragAct, R.string.is_not_work);
        }
    }

    public static void shareVideoToFriendHasUrl(BaseFragAct baseFragAct, String webpageUrl, String title, String body, final String imgUrl) {
        try {
            final IWXAPI iwxapi = WXAPIFactory.createWXAPI(baseFragAct, GlobalConstants.AppConstact.QQ_WX_APPID, true);
            if (iwxapi.isWXAppInstalled()) {
                iwxapi.registerApp(GlobalConstants.AppConstact.QQ_WX_APPID);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) baseFragAct.getResources().getDrawable(R.drawable.play_btn_s);
                final Bitmap iconBtn = bitmapDrawable.getBitmap();

                WXWebpageObject webpage = new WXWebpageObject();
                final WXMediaMessage msg = new WXMediaMessage(webpage);
                webpage.webpageUrl = webpageUrl;
                msg.title = title;
                msg.description = body;
                AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

                    @Override
                    public void run() {
                        Bitmap thumb = ImageUtil.handPic(imgUrl);
                        Bitmap layerThumb = ImageUtil.getTwoDimentsionalCode(thumb, iconBtn);
                        if (thumb != null) {
                            msg.thumbData = ImageUtil.bmpToByteArray(layerThumb, true);
                        } else {
                            msg.thumbData = null;
                        }
                        SendMessageToWX.Req req = new SendMessageToWX.Req();
                        req.transaction = "webpage" + System.currentTimeMillis();
                        req.message = msg;
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        iwxapi.sendReq(req);
                    }
                });
            } else {
                ToastUtil.shortToast(baseFragAct, R.string.uninstall_wxapp);
            }
        } catch (Exception e) {
            ToastUtil.shortToast(baseFragAct, R.string.is_not_work);
        }
    }

    static IUiListener qqShareListener = new IUiListener() {

        @Override
        public void onCancel() {
        }

        @Override
        public void onComplete(Object response) {
        }

        @Override
        public void onError(UiError e) {
        }
    };

    public static void shareSina(Activity activity, Bundle bundle, Response response, ShareCallbackImpl shareCallbackImpl) {
        IWeiboShareAPI mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(activity, GlobalConstants.AppConstact.SINA_APP_KEY);
        if (mWeiboShareAPI.isWeiboAppInstalled()) {
            boolean isRegisterApp = mWeiboShareAPI.registerApp();
            if (isRegisterApp) {
                if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                    int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
                    mWeiboShareAPI.handleWeiboResponse(activity.getIntent(), response);
                    if (supportApi >= 10351) {
                        sendMultiMessage(mWeiboShareAPI, bundle, activity, shareCallbackImpl);
                    } else {
                        sendSingleMessage(mWeiboShareAPI, bundle, activity, shareCallbackImpl);
                    }
                } else {
                    ToastUtil.shortToast(activity, R.string.weibosdk_demo_not_support_api_hint);
                }
            } else {
                ToastUtil.shortToast(activity, R.string.is_not_work);
            }
        } else {
            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {

                @Override
                public void onCancel() {
                }
            });
        }
    }

    private static void sendSingleMessage(final IWeiboShareAPI mWeiboShareAPI, final Bundle bundle, final Activity act, final ShareCallbackImpl shareCallbackImpl) {
        final WeiboMessage weiboMessage = new WeiboMessage();
        getImageObj(act, bundle, new CallbackImpl() {

            @Override
            public void success(ImageObject imageObject, Bitmap bitmap) {
                weiboMessage.mediaObject = new WebpageObject();
                weiboMessage.mediaObject.identify = com.sina.weibo.sdk.utils.Utility.generateGUID();

                String demoUrl = bundle.getString("permalink");

                String text = act.getString(R.string.advance_features_gay_aloha_Invitation, bundle.getString("code"),//
                        bundle.getString("permalink"));


                weiboMessage.mediaObject.title = text;
                weiboMessage.mediaObject.actionUrl = demoUrl;
                weiboMessage.mediaObject.description = act.getString(R.string.app_name);
                XL.d("shareSina", text);
                weiboMessage.mediaObject.setThumbImage(bitmap);
                weiboMessage.mediaObject.thumbData = ImageUtil.bmpToByteArray(bitmap, true);
                SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.message = weiboMessage;
                mWeiboShareAPI.sendRequest(request);
                shareCallbackImpl.failure();
            }

        });

    }

    /**
     * 创建图片消息对象。
     *
     * @param act
     * @param callbackImpl 异步回调网络上获取视频的封面
     * @return 图片消息对象。
     */
    private static void getImageObj(Activity act, final Bundle bundle, final CallbackImpl callbackImpl) {
        final ImageObject imageObject = new ImageObject();

        BitmapDrawable bitmapDrawable = (BitmapDrawable) act.getResources().getDrawable(R.drawable.share_facebook_code);
        final Bitmap bitmap = bitmapDrawable.getBitmap();
        AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

            @Override
            public void run() {
                Bitmap thumb = ImageUtil.handPic(bundle.getString("imgUrl"));
                Bitmap roundBitmap = ImageUtil.getRoundBitmap(thumb);
                String path = ImageUtil.saveToLocal(ImageUtil.getSuperpositionBitmap(bitmap, roundBitmap, 600, 600, 2, 4.8f, 0, 0),//
                        FileTools.getAlohaImgPath() + "/" + System.currentTimeMillis() + ".jpg", 50);
                Bitmap layerThumb = ImageUtil.createBitmap(path, bitmap.getWidth(), bitmap.getHeight());
                try {
                    FileTools.deleteFile(path);
                } catch (Throwable e) {
                }
                imageObject.setImageObject(layerThumb);
                callbackImpl.success(imageObject, layerThumb);
            }
        });
    }

    private static void sendMultiMessage(final IWeiboShareAPI mWeiboShareAPI, final Bundle bundle,//
                                         final Activity act, final ShareCallbackImpl shareCallbackImpl) {
        final WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        getImageObj(act, bundle, new CallbackImpl() {

            @Override
            public void success(ImageObject imageObject, Bitmap bitmap) {
                String demoUrl = bundle.getString("permalink");
                String text = act.getString(R.string.advance_features_gay_aloha_Invitation, bundle.getString("code"),//
                        demoUrl);
                weiboMessage.textObject = new TextObject();
                weiboMessage.textObject.text = text;
                weiboMessage.imageObject = imageObject;
                SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
                request.transaction = String.valueOf(System.currentTimeMillis());
                request.multiMessage = weiboMessage;
                mWeiboShareAPI.sendRequest(request);
                shareCallbackImpl.success();
            }
        });

    }

}
