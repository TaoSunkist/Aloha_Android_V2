package com.wealoha.social.fragment;

import javax.inject.Inject;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;

import com.wealoha.social.AsyncLoader;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.PushSettingResult;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.view.custom.SlideSwitch;
import com.wealoha.social.view.custom.SlideSwitch.OnSwitchChangedListener;

public class SettingNotificationFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Result<PushSettingResult>>, OnSwitchChangedListener, OnClickListener {

    @Inject
    ServerApi settingService;

    @InjectView(R.id.switch_ring)
    SlideSwitch switchRing;

    @InjectView(R.id.switch_vibration)
    SlideSwitch switchVibration;

    @InjectView(R.id.switch_notify_show_detail)
    SlideSwitch switchNotifyShowDetail;

    @InjectView(R.id.switch_ring_tv)
    TextView mRingTv;
    @InjectView(R.id.switch_notify_show_detail_tv)
    TextView mDetailTv;
    @InjectView(R.id.switch_vibration_tv)
    TextView mVibrationTv;
    @InjectView(R.id.prefer_notify_setting_tv)
    TextView mPreferTv;

    @InjectView(R.id.setting_notification_back_tv)
    ImageView back_tv;
    @InjectView(R.id.prefer_notify_setting_rl)
    RelativeLayout mOpenPreferSetting;
    @InjectView(R.id.menu_bar)
    RelativeLayout mMenuBar;

    @Inject
    Context context;

    private static final int REQUEST_CODE_LOAD_SETTING = 0;

    private static final int REQUEST_CODE_SAVE_SETTING = 1;

    // TODO 把logout从MeFragment放到这里来，logout以后回到首页

    Boolean pushSound;
    Boolean pushVibration;
    Boolean pushShowDetail; // 接口里是show，界面是notShow，注意哟

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchRing.setOnSwitchChangedListener(this);
        switchVibration.setOnSwitchChangedListener(this);
        switchNotifyShowDetail.setOnSwitchChangedListener(this);

        // 加载配置
        getLoaderManager().restartLoader(REQUEST_CODE_LOAD_SETTING, null, SettingNotificationFragment.this);

    }

    @Override
    protected void initTypeFace() {
        fontUtil.changeFonts(mMenuBar, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        fontUtil.changeViewFont(mRingTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        fontUtil.changeViewFont(mDetailTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        fontUtil.changeViewFont(mVibrationTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        fontUtil.changeViewFont(mPreferTv, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);

    }

    @Override
    public Loader<Result<PushSettingResult>> onCreateLoader(int i, Bundle bundle) {
        if (i == REQUEST_CODE_LOAD_SETTING) {
            return new AsyncLoader<Result<PushSettingResult>>(context) {

                @Override
                public Result<PushSettingResult> loadInBackground() {
                    return settingService.getPushSetting();
                }
            };
        } else if (i == REQUEST_CODE_SAVE_SETTING) {
            return new AsyncLoader<Result<PushSettingResult>>(context) {

                @Override
                public Result<PushSettingResult> loadInBackground() {
                    try {
                        return settingService.savePushSetting(pushSound, pushVibration, pushShowDetail);
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                    return null;
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Result<PushSettingResult>> resultLoader, Result<PushSettingResult> result) {
        if (result == null || !result.isOk()) {
            return;
        }
        int loader = resultLoader.getId();
        if (loader == REQUEST_CODE_LOAD_SETTING) {
            // 加载完，更新按钮状态
            PushSettingResult r = (PushSettingResult) result.getData();
            if (!r.pushEnable) {
                switchRing.setChecked(false);
                switchVibration.setChecked(false);
            } else {
                switchRing.setChecked(r.pushSound);
                switchVibration.setChecked(r.pushVibration);
            }
            // 初始化贊 和留言 提示
            ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.PUSH_SOUND, r.pushSound);
            ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.PUSH_VIBRATION, r.pushVibration);
            ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.PUSH_SHOW_DETAIL, r.pushShowDetail);
            ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.PUSH_ENABLE, r.pushEnable);
            // ContextConfig.getInstance().putPushSetConfig(NoticeBarController.mPushSettingResult);
            switchNotifyShowDetail.setChecked(r.pushShowDetail);
        } else if (loader == REQUEST_CODE_SAVE_SETTING) {
            // 保存完了，更新下看看
            getLoaderManager().restartLoader(REQUEST_CODE_LOAD_SETTING, null, this);
        }

    }

    @Override
    public void onLoaderReset(Loader<Result<PushSettingResult>> resultLoader) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSwitchChanged(SlideSwitch view, boolean isChecked) {
        int id = view.getId();
        // 只修改

        Log.i("SLIDE_SWITCH", "ISCHECKED:" + isChecked);
        switch (id) {
            case R.id.switch_ring:
                Log.i("SLIDE_SWITCH", "switch_ring ISCHECKED:" + isChecked);
                pushSound = isChecked;
                pushVibration = switchVibration.isChecked();
                pushShowDetail = switchNotifyShowDetail.isChecked();
                break;
            case R.id.switch_vibration:
                Log.i("SLIDE_SWITCH", "switch_vibration ISCHECKED:" + isChecked);
                pushVibration = isChecked;
                pushSound = switchRing.isChecked();
                pushShowDetail = switchNotifyShowDetail.isChecked();
                break;
            case R.id.switch_notify_show_detail:
                Log.i("SLIDE_SWITCH", "switch_notify_show_detail ISCHECKED:" + isChecked);
                pushShowDetail = isChecked;
                pushSound = switchRing.isChecked();
                pushVibration = switchVibration.isChecked();
                break;

        }
        // 保存
        getLoaderManager().restartLoader(REQUEST_CODE_SAVE_SETTING, null, this);
    }

    @OnClick({R.id.setting_notification_back_tv, R.id.prefer_notify_setting_rl})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_notification_back_tv:
                getActivity().finish();
                break;
            case R.id.prefer_notify_setting_rl:
                ((BaseFragAct) getActivity()).startFragment(PreferSettingNotificationFragment.class, null, true);
                break;
            default:
                break;
        }
    }

}
