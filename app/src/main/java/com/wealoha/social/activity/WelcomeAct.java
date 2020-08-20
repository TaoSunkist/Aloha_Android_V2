package com.wealoha.social.activity;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import butterknife.InjectView;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.AccessTokenKeeper;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.NetworkUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.dialog.FlippingLoadingDialog;

/**
 * @description 首页： ①引导登陆注册、②初始化数据
 * @copyright wealoha.com
 * @Date:2014-10-23
 */
public class WelcomeAct extends BaseFragAct implements OnClickListener {

    protected static final int MSG_INSTALL_APK = 111;
    public static final String TAG = WelcomeAct.class.getSimpleName();
    private Handler mHandler;
    private View mViewStub;
    /**
     * sina账号登陆
     */
    private RelativeLayout mSinaLoginRl;
    /**
     * 创建新的账号
     */
    private RelativeLayout mCreateAccountRl;
    private RelativeLayout mLoginRl;
    /**
     * 开机动画
     */
    @InjectView(R.id.image_startup)
    ImageView mImageStartup;
    private Target startupTarget;
    @Inject
    ContextUtil contextUtil;
    @Inject
    Picasso picasso;
    @Inject
    ServerApi connectService;
    @Inject
    FontUtil fontUtil;
    private Context mContext;

    RelativeLayout mFacebookLogin;

    // private ConnectLoaderCallback connectLoaderCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_welcome);
        ActivityManager.popAll();
        ActivityManager.push(WelcomeAct.this);
        mHandler = new Handler();
        mContext = WelcomeAct.this;
        mLoadingDialog = new FlippingLoadingDialog(mContext, R.layout.popup_prompt, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (contextUtil.isTestingApi()) {
            ToastUtil.longToast(this, "您正在使用测试环境");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // uiHelper.onPause();
    }

    private void init() {
        ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.IS_FIRST_ENTER, true);
        initAfterStartupImage();

    }

    private void initAfterStartupImage() {
        User currentUser = contextUtil.getCurrentUser();
        String ticket = contextUtil.getCurrentTicket();
        if (currentUser != null && StringUtil.isNotEmpty(ticket)) {
            Uri uri = null;
            if (currentUser.getProfileIncomplete()) {
                uri = GlobalConstants.IntentAction.INTENT_URI_USER_DATA;
            } else {
                uri = GlobalConstants.IntentAction.INTENT_URI_MAIN;
            }
            joinMainUI(uri);
        } else {
            findId();
            initView();
        }
    }

    public void joinMainUI(final Uri uri) {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Bundle bundle = new Bundle();
                bundle.putString(TAG, TAG);
                startActivity(uri, bundle);
                finish();
            }
        }, 1000);
    }

    public void initView() {
        AlphaAnimation aa = new AlphaAnimation(0f, 1f);
        aa.setStartOffset(1000);
        aa.setDuration(1000);
        mViewStub.startAnimation(aa);
    }

    public void findId() {
        mViewStub = ((ViewStub) findViewById(R.id.viewsub)).inflate();
        mLoginRl = (RelativeLayout) mViewStub.findViewById(R.id.vb_login_rl);
        mCreateAccountRl = (RelativeLayout) mViewStub.findViewById(R.id.vb_create_account_rl);
        mSinaLoginRl = (RelativeLayout) mViewStub.findViewById(R.id.vb_sina_login_rl);
        mFacebookLogin = (RelativeLayout) mViewStub.findViewById(R.id.vb_facebook_login_rl);
        // mFacebookLogin = (LoginButton)
        // mViewStub.findViewById(R.id.vb_facebook_login_btn);
        // mFacebookLogin.setReadPermissions(Arrays.asList("email",
        // "user_likes", "user_status"));
        mFacebookLogin.setOnClickListener(this);
        mSinaLoginRl.setOnClickListener(this);
        mCreateAccountRl.setOnClickListener(this);
        mLoginRl.setOnClickListener(this);
        fontUtil.changeFonts((ViewGroup) mViewStub, Font.ENCODESANSCOMPRESSED_200_EXTRALIGHT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vb_sina_login_rl:
                otherUserLogin(R.id.vb_sina_login_rl);
                break;
            case R.id.vb_create_account_rl:
                startActivity(GlobalConstants.IntentAction.INTENT_URI_REGISTER);
                break;
            case R.id.vb_login_rl:
                startActivity(GlobalConstants.IntentAction.INTENT_URI_LOGIN);
                break;
            case R.id.vb_facebook_login_rl:
                otherUserLogin(R.id.vb_facebook_login_rl);
                break;
        }
    }

    public void otherUserLogin(int viewId) {
        if (NetworkUtil.isNetworkAvailable()) {
            if (viewId == R.id.vb_sina_login_rl)
                sinaUserLogin();
            else if (viewId == R.id.vb_facebook_login_rl) {
                facebookUserLogin();
            }
        } else {
            ToastUtil.shortToast(mContext, R.string.network_error);
        }
    }

    private static final String PERMISSION = "publish_actions";

    private void facebookUserLogin() {
    }

    private IWeiboShareAPI mShareWeiboAPI;
    /** sina微博分享的API */
    // WeiboAuth mWeiboAuth;
    /**
     * sina微博单点登录的API
     */
    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;
    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;
    WeiboAuth weiboAuth;

    void sinaUserLogin() {
        mShareWeiboAPI = WeiboShareSDK.createWeiboAPI(mContext, GlobalConstants.AppConstact.SINA_APP_KEY);
        weiboAuth = new WeiboAuth(this, GlobalConstants.AppConstact.SINA_APP_KEY, GlobalConstants.AppConstact.SINA_REDIRECT_URL, GlobalConstants.AppConstact.SINA_SCOPE);
        // mAuthInfo = new AuthInfo(this,
        // GlobalConstants.AppConstact.SINA_APP_KEY,
        // GlobalConstants.AppConstact.SINA_REDIRECT_URL,
        // GlobalConstants.AppConstact.SINA_SCOPE);
        mSsoHandler = new SsoHandler(WelcomeAct.this, weiboAuth);
        if (!mShareWeiboAPI.isWeiboAppInstalled()) {/* 如果没有安装 */
            startActivity(GlobalConstants.IntentAction.INTENT_URI_OAUTH_SINA);
            // mSsoHandler.authorizeWeb(new AuthListener());
            // mSsoHandler.authorizeWeb(new AuthListener());
        } else {/* 已安装微博客户端 */
            // mSsoHandler.authorizeClientSso(new AuthListener());
            mSsoHandler.authorize(new AuthListener());
        }
    }

    class AuthListener implements WeiboAuthListener {

        public void onComplete(Bundle values) {
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            XL.d("SinaAccess", mAccessToken.isSessionValid() + "");
            if (mAccessToken.isSessionValid()) {
                AccessTokenKeeper.writeAccessToken(WelcomeAct.this, mAccessToken);
                Bundle bundle = new Bundle();
                bundle.putString("uid", mAccessToken.getUid());
                bundle.putString("token", mAccessToken.getToken());
                XL.d("SinaAccess", mAccessToken.getToken());
                connectService.connectWeibo(mAccessToken.getUid(), mAccessToken.getToken(), new Callback<Result<AuthData>>() {

                    @Override
                    public void success(Result<AuthData> result, Response response) {
                        if (result != null) {
                            if (result.isOk()) {
                                result.data.user.setAccessToken(mAccessToken.getToken());
                                contextUtil.setCurrentSinaWbToken(result.data.user.getAccessToken());
                                afterSinaLoginSuccess(result.data.user, result.data.t);
                                XL.d("SinaAccess", mAccessToken.getToken());
                                if (result.data.user.getProfileIncomplete()) {
                                    startActivityAndCleanTask(GlobalConstants.IntentAction.INTENT_URI_USER_DATA, null);
                                } else {
                                    startActivityAndCleanTask(GlobalConstants.IntentAction.INTENT_URI_MAIN, null);
                                }
                                finish();
                            }
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ToastUtil.shortToast(WelcomeAct.this, R.string.login_failed_please_try_again);
                    }
                });
            } else {
                ToastUtil.shortToast(mContext, R.string.is_not_work);
            }
        }

        @Override
        public void onCancel() {
            ToastUtil.shortToast(mContext, R.string.cancel);
        }

        @Override
        public void onWeiboException(WeiboException arg0) {
            ToastUtil.shortToast(mContext, R.string.Unkown_Error);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
            return;
        }
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.pop(this);
    }

}
