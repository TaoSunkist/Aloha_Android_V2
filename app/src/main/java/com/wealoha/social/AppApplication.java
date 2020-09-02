package com.wealoha.social;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.mooveit.library.Fakeit;
import com.wealoha.social.beans.User;
import com.wealoha.social.endpoint.ApiEndpointSelector;
import com.wealoha.social.fragment.BaseFragment;
import com.wealoha.social.inject.Injector;
import com.wealoha.social.inject.RootModule;
import com.wealoha.social.launch.LaunchBroadcastReceiver;
import com.wealoha.social.launch.LaunchService;
import com.wealoha.social.utils.ContextHolder;
import com.wealoha.social.utils.XL;

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2014-10-23
 */
public class AppApplication extends Application implements UncaughtExceptionHandler {

    /**
     * 如果用户修改了头像，则为true,在profile界面需要更新
     */
    @Deprecated
    public static boolean isUpdate = false;
    /**
     * 当前回话的sessionId
     *
     * @deprecated
     */
    public static List<User> mUserList = new ArrayList();
    public static AppApplication mInstance = new AppApplication();
    // 用户维护多个profile界面，需要手动进行维护和操作
    static public HashMap<String, BaseFragment> mFragmentMaps = new HashMap<String, BaseFragment>();
    @Deprecated
    public static User user;
    public static final String TAG = AppApplication.class.getSimpleName();
    public static ContextConfig config;
    public static ContextHolder contextHolder;
    public Double[] locationXY = {0.0, 0.0};

    private ApiEndpointSelector apiEndpointSelector;

    @Override
    public void onCreate() {
        super.onCreate();
        Fakeit.init();
        mInstance = this;
        config = ContextConfig.getInstance();
        config.init(mInstance);
        user = User.Companion.fake(true, false);
        config.getDefaultAccount(mInstance, user);
        Injector.init(new RootModule());
        // startService(service)
        // 全局异常捕获
        // CrashHandler crashHandler = CrashHandler.getInstance();
        // crashHandler.init(getApplicationContext());
        // 注意！！不要轻易启用这个全局异常捕获
        // CrashHandler crashHandler = CrashHandler.getInstance();
        // crashHandler.init(getApplicationContext());
        // 初始化push推送服务
        // app呼起服务
        startLaunchService();
        // 找个可用的api入口
        selectApiEndpoint();
        // 定位服务
        // startLocationService();
        // 测试图片地址
        testImageEndpoint();
    }

    /**
     * 伪单例
     *
     * @return
     */
    public static AppApplication getInstance() {
        return mInstance;
    }

    public String taskTopActvityName() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void startLaunchService() {
        XL.i(TAG, "start LaunchService");
        Intent serviceIntent = new Intent(this, LaunchService.class);
        // Starts the IntentService
        startService(serviceIntent);

        // 接收App呼起服务发出的广播
        IntentFilter intentFilter = new IntentFilter(LaunchService.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new LaunchBroadcastReceiver(), intentFilter);
    }

    public void selectApiEndpoint() {
        if (apiEndpointSelector == null) {
            apiEndpointSelector = new ApiEndpointSelector();
        }
        apiEndpointSelector.testAndSelectEndpoint();
    }

    public void testImageEndpoint() {
        if (apiEndpointSelector == null) {
            apiEndpointSelector = new ApiEndpointSelector();
        }
        apiEndpointSelector.testImageEndpoint();
    }

}
