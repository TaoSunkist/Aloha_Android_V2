package com.wealoha.social;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import io.reactivex.disposables.CompositeDisposable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupWindow;

import androidx.fragment.app.FragmentActivity;

import butterknife.ButterKnife;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;
import com.wealoha.social.activity.FeedNoticeAct;
import com.wealoha.social.activity.FragmentWrapperActivity;
import com.wealoha.social.activity.FragmentWrapperActivity.ActivityResultCallback;
import com.wealoha.social.activity.ImgCropingActivity;
import com.wealoha.social.activity.LauncherImgAct;
import com.wealoha.social.activity.MainAct;
import com.wealoha.social.activity.NaviIntroActivity;
import com.wealoha.social.activity.WelcomeAct;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.AccessTokenKeeper;
import com.wealoha.social.callback.IBackKeyCallback;
import com.wealoha.social.commons.AlohaThreadPool;
import com.wealoha.social.commons.AlohaThreadPool.ENUM_Thread_Level;
import com.wealoha.social.commons.CacheManager;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.CacheKey;
import com.wealoha.social.commons.HasCache;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.fragment.Profile2Fragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.push.NoticeBarController;
import com.wealoha.social.receive.AlarmReceiver;
import com.wealoha.social.store.PopupStore;
import com.wealoha.social.store.SyncEntProtocol;
import com.wealoha.social.ui.attestation.AttestationActivity;
import com.wealoha.social.ui.dialogue.DialogueActivity;
import com.wealoha.social.ui.lock.GestureLockAct;
import com.wealoha.social.ui.topic.TopicDetailActivity;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.DockingBeanUtils;
import com.wealoha.social.utils.FileTools;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.PushUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.UiUtils;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.MatchPopupWindow;
import com.wealoha.social.view.custom.dialog.FlippingLoadingDialog;
import com.wealoha.social.view.custom.popup.LoadingPopup;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description 基类
 * @copyright wealoha.com
 * @Date:2014-10-23
 */
public abstract class BaseFragAct extends FragmentActivity implements HasCache, IBackKeyCallback {

    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * 通过点击通知去聊天列表页面.
     */
    public static final String ACTION_BY_NOTICE_TO_CHATLIST = "by_notice_to_chatlist";
    /**
     * 通过点击通知去会话页面.
     */
    public static final String ACTION_BY_NOTICE_TO_DIALOGUE = "by_notice_to_dialogue";
    @Inject
    public ServerApi mClientLogService;
    public static String TAG = TopicDetailActivity.class.getSimpleName();
    @Inject
    public JsonController mJsonController;
    @Inject
    public FontUtil fontUtil;
    @Inject
    ContextUtil contextUtil;
    @Inject
    Picasso picasso;
    @Inject
    CacheManager cacheManager;
    @Inject
    PushUtil pushUtil;
    @Inject
    ServerApi authService;
    @Inject
    public Bus bus;
    public static boolean isRefreshHeadIcon;
    /* @Deprecated */
    protected Context mContext;
    public int mScreenWidth;
    public int mScreenHeight;
    public FlippingLoadingDialog mLoadingDialog;
    public static FlippingLoadingDialog mLoading;
    public Handler mHandler;
    protected View container;
    protected LoadingPopup popup;
    public static PopupWindow popupJoinEwm;
    /**
     * 用 start..forresult 方法开启act 时，用这个callback 将result 的结果返回给fragment
     */
    private ActivityResultCallback mActivityResultCallback;
    protected Resources mResources;
    protected Dialog mAlertDialog;
    protected PopupStore mPopupStore;

    /**
     * 是否是从后台来到前台
     */
    protected boolean isbackground;
    /**
     * 是否要开启手势锁
     */
    protected boolean openLock;
    /**
     * 是否要开启开机画面
     */
    protected boolean openLaunchImg;
    /**
     * 进入后台的时间点
     */
//	private long onStopTime = 0;
    public final static String STOP_TIME_KEY = "STOP_TIME_KEY";

    /**
     * 开启手势锁界面
     */
    private int lockRequestCode = 0x1111;
    /**
     * 开启相机或者图片选择器，这时候返回主界面时不要开启手势锁
     */
    protected boolean isOpenCamera;

    public static final String FINISH_BROADCAST_FILTER = "finishBroadcastFilter";
    protected View.OnClickListener mDialogClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mAlertDialog != null && mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
        }
    };

    private void doApiLogout(final Context context) {
        MiPushClient.unregisterPush(this);
        // FIXME 添加登出的Loading框;
        authService.unbind(pushUtil.getPushToken(), new Callback<ApiResponse<ResultData>>() {

            @Override
            public void success(ApiResponse<ResultData> apiResponse, Response response) {
                doRealApiLogout(context);
            }

            @Override
            public void failure(RetrofitError error) {
                doRealApiLogout(context);
            }
        });
    }

    private void doRealApiLogout(final Context context) {
        // FIXME 添加登出的Loading框;
        authService.unauth(new Callback<ApiResponse<ResultData>>() {

            @Override
            public void success(ApiResponse<ResultData> arg0, Response arg1) {
                popup.hide();
                contextUtil.cleanLoginStatus();
                Intent intent = new Intent(context, WelcomeAct.class);
                startActivity(intent);
            }

            @Override
            public void failure(RetrofitError error) {
                ToastUtil.shortToast(BaseFragAct.this, R.string.is_not_work);
                popup.hide();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        mContext = this;
        mResources = getResources();
        mPopupStore = new PopupStore();
        calcScreenSize();
        showCurrent();
        Injector.inject(this);

        String cid = ContextConfig.getInstance().getStringWithFilename(GlobalConstants.AppConstact.GETUI_PUSH_TOKEN, "");
        if (!TextUtils.isEmpty(cid)) {
            pushUtil.tryBindGetuiPushToken(cid);
        }

        contextUtil.setForegroundAct(this);
        popup = new LoadingPopup(this);
        // initBroadcastReceiver();
    }

    // private void initBroadcastReceiver() {
    // IntentFilter intentFilter = new IntentFilter();
    // // intentFilter.addAction(ACTION);
    // // registerReceiver(receiver, intentFilter);
    // }

    private void showCurrent() {
    }

    @Override
    public void setContentView(int layoutResID) {
        ViewGroup view = (ViewGroup) getLayoutInflater().inflate(layoutResID, null);
        super.setContentView(view);
        ButterKnife.inject(this);
        changeFont(view);
        container = view;
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: changeFont
     * @Description: 设置字体
     */
    protected void changeFont(ViewGroup root) {
        fontUtil.changeFonts(root, Font.ENCODESANSCOMPRESSED_400_REGULAR);
        initTypeFace();
    }

    protected void initTypeFace() {
    }

    @Override
    public void setContentView(View view) {
        // container = view;
    }

    /**
     * @Description:
     * @see:
     * @since:
     * @description 计算手机屏幕尺寸
     * @author: sunkist
     * @date:2014-10-24
     */
    void calcScreenSize() {
        mScreenWidth = UiUtils.getScreenWidth(getApplicationContext());
        mScreenHeight = UiUtils.getScreenHeight(getApplicationContext());
    }

    public void startActivity(Uri uri) {
        // FIXME 后续：建立一个stack用来统一管理activity
        startActivity(uri, null);
    }

    /**
     * 含有Bundle通过Action跳转界面 <br/>
     * 打开并请除栈中的activity，请使用如下方法<br/>
     * {@link #startActivityAndCleanTask(Uri, Bundle)}
     */
    public void startActivity(Uri uri, Bundle bundle) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        overridePendingTransition(R.anim.left_in, R.anim.stop);
        startActivity(intent);
    }

    /**
     * 含有Bundle通过Action跳转界面 <br/>
     * 打开并请除栈中的activity，请使用如下方法<br/>
     * {@link #startActivityAndCleanTask(Uri, Bundle)}
     */
    public void startActivity(Uri uri, Bundle bundle, int enterAnimId, int outAnimId) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        super.finish();
        startActivity(intent);
        overridePendingTransition(enterAnimId, outAnimId);
    }

    public void startActivityNotFinish(Uri uri, Bundle bundle, int enterAnimId, int outAnimId) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(enterAnimId, outAnimId);
    }

    /**
     * 含有Bundle通过Action跳转界面 <br/>
     * 打开并请除栈中的activity，请使用如下方法<br/>
     * {@link #startActivityAndCleanTask(Uri, Bundle)}
     */
    public void startActivityForResult(Uri uri, Bundle bundle, int enterAnimId, int outAnimId, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(enterAnimId, outAnimId);
    }

    /**
     * @param uri
     * @param bundle
     * @Description: 开启activity并清除task中的任务栈
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-19
     */
    public void startActivityAndCleanTask(Uri uri, Bundle bundle) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setData(uri);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        super.finish();
    }

    /**
     * @param uri
     * @param bundle
     * @Description: 开启activity并清除task中的任务栈
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-19
     */
    public void startActivityAndCleanTask(Uri uri, Bundle bundle, int enterAnim, int outAnim) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setData(uri);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.stop);
        super.finish();
    }

    public void finish(int enterAnim, int outAnim) {
        super.finish();
        overridePendingTransition(enterAnim, outAnim);
    }

    public void changePhoneArea(int position) {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (contextUtil.getForegroundAct() == null) {
            contextUtil.setForegroundAct(this);
        }
        contextUtil.setForeground(true);
    }

    /**
     * http://stackoverflow.com/a/10094553/3368344
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (contextUtil.getForegroundAct() == null) {
            contextUtil.setForegroundAct(this);
        }
        NoticeBarController.getInstance(this).cleanAllSession();
        AlarmReceiver.clearNotice();
        contextUtil.setForeground(true);
        // isLoadLauncherImg();
        XL.i("isLoadLauncherImg", this + "---onResume---" + openLaunchImg);
        isActOpenByPush();
        openGestureLock();
        isOpenCamera = false;
    }

    protected boolean isActOpenByPush() {
        if (this instanceof FragmentWrapperActivity || this instanceof DialogueActivity || this instanceof FeedNoticeAct || this instanceof TopicDetailActivity) {
            if (ContextConfig.getInstance().getBooleanWithFilename(MainAct.OPEN_GESTURE_FROM_PUSH_KEY)) {
                openLock = true;
                openLaunchImg = true;
                ContextConfig.getInstance().putBooleanWithFilename(MainAct.OPEN_GESTURE_FROM_PUSH_KEY, false);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        contextUtil.setForeground(false);
        contextUtil.deleteForegroundAct();
        popup.hide();
    }

    public void showMatchPopup(User user) {
        View v = getWindow().getDecorView();
        Bitmap b = ImageUtil.getScreenshot(v);
        final MatchPopupWindow matchPopup = new MatchPopupWindow(mContext);
        matchPopup.init(b, user);
        matchPopup.show(container);
    }

    @Override
    public <T> void save(CacheKey key, T object) {
        cacheManager.save(getClass(), key, object);
    }

    @Override
    public <T> void saveAppend(CacheKey key, T object) {
        cacheManager.save(getClass(), key, object);
    }

    public <T> void clearAppend(CacheKey cacheKey) {
        // cacheManager.
        cacheManager.clearAppend(getClass(), cacheKey);
    }

    ;

    @Override
    public <T> T restore(CacheKey key) {
        return cacheManager.restore(getClass(), key);
    }

    @Override
    public <T> void globalSave(CacheKey key, T object) {
        cacheManager.globalSave(key, object);
    }

    @Override
    public <T> T globalRestore(CacheKey key) {
        return cacheManager.globalRestore(key);
    }

    /**
     * @param user 设定文件
     * @Title: setCrashlyticesInfo
     * @Description: 收集崩溃信息
     */
    public void setCrashlyticesInfo(User user) {
        if (user != null) {
            // 崩溃多一些信息
            XL.i("CRASHLY_TEST", "------------------" + user.getId());
        }
    }

    private void setUserAndTicket(User user, String ticket) {
        setCrashlyticesInfo(user);
        // contextUtil.setPassportPhoneNumber(true);
        // 保存当前用户
        contextUtil.setCurrentUser(user);
        contextUtil.setCurrentTicket(ticket);
        if (user == null) {
            ToastUtil.shortToast(this, R.string.Unkown_Error);
        } else if (user.getProfileIncomplete()) {
            startActivity(GlobalConstants.IntentAction.INTENT_URI_USER_DATA);
        } else {
            startActivity(GlobalConstants.IntentAction.INTENT_URI_MAIN);
        }
    }

    /**
     * 手机号登录成功后调用
     */
    protected void afterMobileLoginSuccess(String number, User user, String ticket) {
        contextUtil.setPassportPhoneNumber(true);
        contextUtil.setPassportFacebook(false);
        contextUtil.setPassportSinaWeibo(false);

        contextUtil.setAccountPhoneNumber(number);
        if (user == null) {
            user = contextUtil.getCurrentUser();
        }
        setUserAndTicket(user, ticket);
    }

    /**
     * 微博登录成功后调用
     */
    public void afterSinaLoginSuccess(User user, String ticket) {
        // TODO 设置用户是weibo的
        if (user.getProfileIncomplete()) {
            // 可以输入邀请了
            ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.SHOW_INVITATION_CODE_INPUT, true);
        }
        contextUtil.setPassportSinaWeibo(true);
        contextUtil.setPassportFacebook(false);
        contextUtil.setPassportPhoneNumber(false);
        setUserAndTicket(user, ticket);
    }

    /**
     * 微博Facebook成功后调用
     */
    public void afterFacebookLoginSuccess(User user, String ticket) {
        // TODO 设置用户是weibo的
        if (user.getProfileIncomplete()) {
            // 可以输入邀请了
            ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.SHOW_INVITATION_CODE_INPUT, true);
        }
        // contextUtil.setPassportSinaWeibo(true);
        contextUtil.setPassportFacebook(true);
        contextUtil.setPassportPhoneNumber(false);
        contextUtil.setPassportSinaWeibo(false);
        setUserAndTicket(user, ticket);
    }

    /**
     * 退出登录
     */
    public void doLogout(Context context) {
        popup.show();
        String pushToken = pushUtil.getPushToken();
        NoticeBarController.getInstance(this).cleanAllSession();
        // 删除文件夹,
        AlohaThreadPool.getInstance(ENUM_Thread_Level.TL_AtOnce).execute(new Runnable() {

            @Override
            public void run() {
                // 不删除图片的cache
                FileTools.cleanDirectory(FileTools.ROOT_PATH, Collections.singleton("cache"));
            }
        });
        if (StringUtil.isNotEmpty(pushToken)) {
            XL.d("doLogout", "先解绑Token，然后再触发退出");
            // 有push，先解绑Token，然后再触发退出
            doApiLogout(context);
        } else {
            // 直接退出
            XL.d("doLogout", "直接退出");
            doRealApiLogout(context);
        }
        ContextConfig.getInstance().cleanUserConfig();
        try {
            Oauth2AccessToken oaToken = AccessTokenKeeper.readAccessToken(this);
            if (oaToken != null) {
                // XL.d(TAG, "清理本地微博Token");
                SyncEntProtocol.getInstance().reqSinaTokenRecove(oaToken.getToken());
                AccessTokenKeeper.clear(this);
            }
        } catch (Throwable t) {
            XL.w(TAG, "清理微博Token失败", t);
        }
        // 清理未完的match
        AppApplication.mUserList.clear();
    }

    public Message sendMsgToHandler(int tag, Object obj) {
        Message msg = Message.obtain();
        msg.what = tag;
        msg.obj = obj;
        return msg;
    }

    /**
     * 在主界面上启动新的Fragment<br/>
     * <p>
     * 使用 {@link ContextUtil#getMainAct()#startFragment(Class, Bundle, boolean)}
     * <p>
     * Fragment类型
     *
     * @param bundle   要传给Fragment的参数
     * @param backable 是否可以回退(仅当在Fragment容器内部切换时生效，Activity时忽略)
     */
    public void startFragment(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean backable) {
        startFragment(fragmentClass, bundle, backable, 0, 0);
    }

    public void startFragment(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean backable, int animIn, int animOut) {
        Bundle b = new Bundle();

        b.putSerializable(FragmentWrapperActivity.BUNDLE_KEY_FRAGMENT_CLASS, fragmentClass);
        if (bundle != null) {
            b.putAll(bundle);
        }
        XL.i("USER_PROFILE", "---startFragment:" + b);
        // 使用Activity打开Fragment: " + fragmentClass.getName());
        startActivity(GlobalConstants.IntentAction.INTENT_URI_FRAGMENT_TO_ACTIVITY, b);
        overridePendingTransition(animIn, animOut);
    }

    /**
     * 在主界面上启动新的Fragment<br/>
     * <p>
     * 使用 {@link ContextUtil#getMainAct()#startFragment(Class, Bundle, boolean)}
     * <p>
     * Fragment类型
     *
     * @param bundle   要传给Fragment的参数
     * @param backable 是否可以回退(仅当在Fragment容器内部切换时生效，Activity时忽略)
     */
    public void startFragmentForResult(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean backable, int requestCode, int enterAnimId, int outAnimId) {

        Bundle b = new Bundle();

        b.putSerializable(FragmentWrapperActivity.BUNDLE_KEY_FRAGMENT_CLASS, fragmentClass);
        if (bundle != null) {
            b.putAll(bundle);
        }

        startActivityForResult(GlobalConstants.IntentAction.INTENT_URI_FRAGMENT_TO_ACTIVITY, b, enterAnimId, outAnimId, requestCode);
    }

    /**
     * 在主界面上启动新的Fragment<br/>
     * <p>
     * 使用 {@link ContextUtil#getMainAct()#startFragment(Class, Bundle, boolean)}
     * <p>
     * Fragment类型
     *
     * @param bundle   要传给Fragment的参数
     * @param backable 是否可以回退(仅当在Fragment容器内部切换时生效，Activity时忽略)
     */
    public void startFragmentForResult(Uri uri, Class<? extends Fragment> fragmentClass, Bundle bundle, boolean backable, int requestCode, int enterAnimId, int outAnimId) {

        Bundle b = new Bundle();

        b.putSerializable(FragmentWrapperActivity.BUNDLE_KEY_FRAGMENT_CLASS, fragmentClass);
        if (bundle != null) {
            b.putAll(bundle);
        }

        startActivityForResult(uri, b, enterAnimId, outAnimId, requestCode);
    }

    /**
     * @param context
     * @return
     */
    public static boolean isBackground(Context context) {

        android.app.ActivityManager activityManager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public File mCameraImgFile;// 相机返回的图片的路径

    /**
     * @param baseFragAct
     * @Description:打开照相机.
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-29
     */
    public void openCamera(BaseFragAct baseFragAct) {

        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 如果媒体存在
            try {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                mCameraImgFile = new File(FileTools.getFileImgNameHasDir(contextUtil.getCurrentUser()));
                Uri u = Uri.fromFile(mCameraImgFile);
                intent.putExtra("orientation", 0);
                intent.putExtra("output", u);
                baseFragAct.startActivityForResult(intent, GlobalConstants.AppConstact.CAMERA_WITH_DATA);
                isOpenCamera = true;// 开启了相机
            } catch (ActivityNotFoundException e) {
                ToastUtil.shortToast(mContext, R.string.Unkown_Error);
            }
        } else {
            ToastUtil.shortToast(mContext, R.string.failed);
            // ToastUtil.shortToastCenter(this, "未檢測到存儲卡");
        }

    }

    /**
     * @param baseFragAct
     * @Description: 打开图片浏览器.
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-29
     */
    public void openImgPick(BaseFragAct baseFragAct) {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            i.setType("image/*");
            baseFragAct.startActivityForResult(i, GlobalConstants.AppConstact.PHOTO_PICKED_WITH_DATA);
            isOpenCamera = true;// 开启了照片选择器
        } catch (ActivityNotFoundException e) {
            ToastUtil.shortToast(this, R.string.Unkown_Error);
        }
    }

    public void goImgCropAct(String absolutePath, String imgPathFromCameraWithData, String tag) {
        if (TextUtils.isEmpty(absolutePath)) {
            return;
        }
        Intent intent = new Intent(this, ImgCropingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("path", absolutePath);
        bundle.putString("openType", tag);
        bundle.putString("openMethod", imgPathFromCameraWithData);
        intent.putExtras(bundle);
        startActivityForResult(intent, GlobalConstants.AppConstact.FLAG_MODIFY_FINISH);
        overridePendingTransition(R.anim.left_in, 0);
    }

    public void getSelectedImgPath(Uri uri, String imgPathFromPhotoPickedWithData, String tag) {
        String gotPhotoPath = null;
        try {
            String alamPath = (uri != null ? uri.toString() : "");
            if (alamPath != null) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                gotPhotoPath = (cursor != null ? cursor.getString(column_index) : "");
            } else {
                gotPhotoPath = (uri != null ? uri.getPath() : "");
            }
            if (gotPhotoPath == null) {
                getPhoto(uri, imgPathFromPhotoPickedWithData, tag);
            } else {
                goImgCropAct(gotPhotoPath, imgPathFromPhotoPickedWithData, tag);
            }
        } catch (Exception e) {
            getPhoto(uri, imgPathFromPhotoPickedWithData, tag);
        }
    }

    private void getPhoto(Uri uri, String imgPathFromPhotoPickedWithData, String tag) {
        InputStream is = null;
        try {
            is = this.getContentResolver().openInputStream(uri);
            String imgFilePath = FileTools.getAlohaImgPath() + "/" + //
                    System.currentTimeMillis() + ".jpg";
            IOUtils.copy(is, new FileOutputStream(imgFilePath));
            goImgCropAct(imgFilePath, imgPathFromPhotoPickedWithData, tag);
        } catch (IOException e1) {
            ToastUtil.shortToast(this, R.string.is_not_work);
            e1.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    public View getContainerView() {
        return container;
    }

    /**
     * @param fragmentClass Fragment类型
     * @param bundle        要传给Fragment的参数
     * @param backable      是否可以回退(仅当在Fragment容器内部切换时生效，Activity时忽略)
     * @Description: 在主界面上以往左滑动的动画启动新的Fragment<br />
     * 使用 {@link ContextUtil#getMainAct()#startFragment(Class, Bundle, boolean)}
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-14
     */
    public void startFragmentHasAnim(Class<? extends Fragment> fragmentClass, Bundle bundle, boolean backable, int animEnter, int animOut) {

        Bundle b = new Bundle();

        b.putSerializable(FragmentWrapperActivity.BUNDLE_KEY_FRAGMENT_CLASS, fragmentClass);
        if (bundle != null) {
            b.putAll(bundle);
        }

        // 使用Activity打开Fragment: " + fragmentClass.getName());
        startActivityHasAnim(GlobalConstants.IntentAction.INTENT_URI_FRAGMENT_TO_ACTIVITY, b, animEnter, animOut);
    }

    /**
     * @param uri
     * @param bundle
     * @param animOut
     * @param animEnter
     * @Description: 以向左滑入含有Bundle通过Action跳转界面 <br/>
     * 打开并请除栈中的activity，请使用如下方法<br/>
     * {@link #startActivityAndCleanTask(Uri, Bundle)}
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-14
     */
    public void startActivityHasAnim(Uri uri, Bundle bundle, int animEnter, int animOut) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(animEnter, animOut);
    }

    public Bundle getWhereComeFromToProfile() {
        if (getIntent() != null && TextUtils.isEmpty(getIntent().getStringExtra("refer_key"))) {
            Bundle bundle = getIntent().getExtras();
            return bundle;
        }
        return new Bundle();
    }

    /**
     * @Title: setViewBack
     * @Description: 兼容处理view 的背景
     */
    public void setViewBack(View v, Bitmap backimg) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            setBackgroundV16Plus(v, backimg);
        } else {
            setBackgroundV16Minus(v, backimg);
        }
    }

    @TargetApi(16)
    public void setBackgroundV16Plus(View v, Bitmap backimg) {
        v.setBackground(new BitmapDrawable(mContext.getResources(), backimg));
    }

    @SuppressWarnings("deprecation")
    public void setBackgroundV16Minus(View v, Bitmap backimg) {
        v.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), backimg));
    }

    /**
     * @param context title
     *                body
     * @Description:用户提示的对话框,不想显示title请传入空的字符串,body同理
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015年3月6日
     */
    protected void showSingleAlohaDialog(Context context, Integer titleResId, Integer bodyResId) {
        try {
            mAlertDialog = mPopupStore.showAlohaDialogSingleBtn(context,//
                    titleResId, bodyResId, mDialogClickListener);
            mAlertDialog.show();
        } catch (Throwable e) {// 界面销毁后调出对话框,可能会有异常,直接忽视掉

        }
    }

    /**
     * @Description:清传入：com.wealoha.social.api.user.bean.User
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-12-5
     */
    public void toProfileLogin(User user) {
        com.wealoha.social.beans.User user2 = DockingBeanUtils.transUser(user);
        Bundle bundle = new Bundle();
        bundle.putSerializable(User.TAG, user2);
        startFragment(Profile2Fragment.class, bundle, true);
    }

    /**
     * @Title: onActivityResultCallback
     * @Description: 方便这个包装类返回activity result 的结果给它包装的frag {@link #onActivityResult(int, int, Intent)}
     */
    public void setActivityResultCallback(ActivityResultCallback activityResultCallback) {
        mActivityResultCallback = activityResultCallback;
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent result) {
        super.onActivityResult(requestcode, resultcode, result);
        if (mActivityResultCallback != null) {
            mActivityResultCallback.onActivityResultCallback(requestcode, resultcode, result);
        }
        if (resultcode == RESULT_OK) {
            if (requestcode == lockRequestCode) {
                openLaunchImg = true;
                isLoadLauncherImg();
            }
        } else if (requestcode == lockRequestCode) {
            backToBeck();
        }
    }

    /**
     * 退出到桌面
     */
    public void backToBeck() {
        Intent MyIntent = new Intent(Intent.ACTION_MAIN);
        MyIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(MyIntent);
    }

    @Override
    protected void onDestroy() {
        // unregisterReceiver(receiver);
        compositeDisposable.clear();
        super.onDestroy();
    }

    /**
     * @author:sunkist
     * @see:
     * @since:
     * @description 静态Handler内部类，抽取成一份代码，
     * @copyright wealoha.com
     * @Date:2015年7月15日
     */
    public static class AlohaHandler<T extends BaseFragAct> extends Handler {

        private final WeakReference<T> mActivity;

        public AlohaHandler(T t) {
            mActivity = new WeakReference<T>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseFragAct activity = mActivity.get();
            if (activity != null) {

            }
        }
    }

    private void isLoadLauncherImg() {
        XL.i("isLoadLauncherImg", this + "---isLoadLauncherImg11111:" + openLaunchImg);
        if (this instanceof NaviIntroActivity || this instanceof WelcomeAct || !openLaunchImg) {
            return;
        }
        XL.i("isLoadLauncherImg", this + "---isLoadLauncherImg");
        openLaunchImg = false;
        if (checkLaunchImagePage()) {
            XL.i("isLoadLauncherImg", "true");
            startActivity(new Intent(this, LauncherImgAct.class));
        }
    }

    protected void openGestureLock() {
        if (isOpenCamera) {
            return;
        }
        if (this instanceof NaviIntroActivity) {
            return;
        }

        if (this instanceof GestureLockAct) {// 如果当前界面是手势锁的解锁界面，那么也不开启
            switch (((GestureLockAct) this).getGestureActType()) {
                case GestureLockAct.LOCK_UNLOCK:
                    return;
                case GestureLockAct.LOCK_CREATE:
                case GestureLockAct.LOCK_CHANGE:
                    if (isbackground) {
                        finish();
                        if (!checkGestureLockEnable()) {
                            startGestureLock();
                        }
                        return;
                    }
                    break;
                default:
                    break;
            }
        }

        if (this instanceof AttestationActivity && isbackground) {
            finish();
            return;
        }
        if (checkGestureLockEnable()) {
            isLoadLauncherImg();
        } else if (openLock) {
            openLock = false;
            long time = System.currentTimeMillis();
            if (time - getOnStopTime() > 0) {
                startGestureLock();
            }
        }
    }

    protected void startGestureLock() {
        Intent intent = new Intent(this, GestureLockAct.class);
        intent.putExtra(GestureLockAct.LOCK_TYPE, GestureLockAct.LOCK_UNLOCK);
        startActivityForResult(intent, lockRequestCode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isOpenCamera) {
            return;
        }
        isbackground = !isAppOnForeground();
        openLock = isbackground;
        openLaunchImg = isbackground;
        if (isbackground) {
            // 记录本次程序退出到后台的时间
//			onStopTime = System.currentTimeMillis();
            setOnStopTime(System.currentTimeMillis());
        }
    }

    public boolean isAppOnForeground() {
        // Returns a list of application processes that are running on the
        // device

        android.app.ActivityManager activityManager = (android.app.ActivityManager) (getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE));
        String packageName = getApplicationContext().getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (onBackKeyPressed()) {
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 是否设置了手势解锁
     *
     * @return boolean
     */
    public boolean checkLockEnable() {
        String lockPaw = ContextConfig.getInstance().getStringWithFilename(GestureLockAct.LOCK_PASSWORD, null);
        if (TextUtils.isEmpty(lockPaw)) {
            return false;
        }
        return true;
    }

    /**
     *
     */
    @Override
    public boolean onBackKeyPressed() {
        finish();
        overridePendingTransition(R.anim.stop, R.anim.right_out);
        return true;
    }

    /**
     * 关闭所有Actvity 栈内的act
     *
     * @return void
     */
    public void closeAllAct(Activity act) {
        Intent intent = new Intent();
        intent.setClass(act, MainAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清空MainAct 到该act之间所有界面
        intent.putExtra(MainAct.CLEAR_ACTION, 1);
        startActivity(intent);
        finish();
    }

    private Dialog wisperDialog;

    public void showmPrivacyCommentSign() {
        if (wisperDialog != null) {
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_for_wisper_guide, null, false);
        wisperDialog = new AlertDialog.Builder(this)//
                .setView(view)//
                .setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        wisperDialog.show();
    }

    public long getOnStopTime() {
        return ContextConfig.getInstance().getLongWithFilename(STOP_TIME_KEY, 0);
    }

    public void setOnStopTime(long stopTime) {
        ContextConfig.getInstance().putLongWithFilename(STOP_TIME_KEY, stopTime);
    }

    public boolean checkLaunchImagePage() {
        long time = System.currentTimeMillis();
        if (time - getOnStopTime() > contextUtil.getStartImageIntervalMinutes() * 1000 * 60) {
            setOnStopTime(0);
            return true;
        }
        return false;
    }

    /**
     * 是否设置了手势锁
     *
     * @return
     */
    private boolean checkGestureLockEnable() {
        return TextUtils.isEmpty(ContextConfig.getInstance().getStringWithFilename(GestureLockAct.LOCK_PASSWORD, null));
    }
}
