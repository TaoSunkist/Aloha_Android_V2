package com.wealoha.social.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import androidx.loader.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import androidx.loader.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;

import com.squareup.otto.Bus;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.wealoha.social.AsyncLoader;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.ImageUploadResult;
import com.wealoha.social.beans.ApiResponse;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.ProfileData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.ui.dialogue.DialogueActivity;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ImageUtil;
import com.wealoha.social.utils.ImageUtil.CropMode;
import com.wealoha.social.utils.RegionNodeUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.view.custom.dialog.BasePickerDialog;
import com.wealoha.social.view.custom.dialog.BasePickerDialog.ReturnSomethingListener;
import com.wealoha.social.view.custom.dialog.FlippingLoadingDialog;
import com.wealoha.social.view.custom.dialog.TimePickerDialog;

/**
 * 设置 -> 资料设定
 *
 * @author superman
 * @author javamonk
 * @date 2014-11-04 14:54:25
 * @see
 * @since
 */
public class ConfigDetailsAct extends BaseFragAct implements LoaderManager.LoaderCallbacks<ApiResponse<ProfileData>> {

    public static final String TAG = ConfigDetailsAct.class.getSimpleName();
    /**
     * 头像
     */
    @InjectView(R.id.config_details_userphoto_civ)
    CircleImageView mUserPhoto;

    /**
     * ID
     */
    @InjectView(R.id.config_details_username_et)
    EditText mUserName;

    @InjectView(R.id.config_details_username_rl)
    RelativeLayout mUserNameRl;
    private Handler mHandler;
    /** 年龄 */
    // @InjectView(R.id.config_details_age)
    // RelativeLayout mAge;

    /**
     * 年龄数值
     */
    @InjectView(R.id.config_details_age_tv)
    TextView mAgeTv;

    /** 地区 */
    // @InjectView(R.id.config_details_area)
    // RelativeLayout mArea;

    /** 地区 */
    // @InjectView(R.id.config_details_area_tv)
    // TextView mAreaTv;

    /**
     * 身高
     */
    @InjectView(R.id.config_details_height)
    RelativeLayout mHeight;

    /**
     * 身高数值
     */
    @InjectView(R.id.config_details_height_tv)
    TextView mHeightTv;

    /**
     * 身材
     */
    @InjectView(R.id.config_details_stature)
    RelativeLayout mStature;

    /**
     * 身材
     */
    @InjectView(R.id.config_details_stature_rg)
    RadioGroup mStatureRg;

    /**
     * 体重
     */
    @InjectView(R.id.config_details_weight)
    RelativeLayout mWeight;

    /**
     * 体重数值
     */
    @InjectView(R.id.config_details_weight_tv)
    TextView mWeightTv;

    /**
     * 目的
     */
    @InjectView(R.id.config_details_goal_cb1)
    CheckBox mGoal01;
    @InjectView(R.id.config_details_goal_cb2)
    CheckBox mGoal02;
    @InjectView(R.id.config_details_goal_cb3)
    CheckBox mGoal03;

    /**
     * 介绍
     */
    @InjectView(R.id.config_details_introduction)
    RelativeLayout mIntroduction;

    @InjectView(R.id.config_details_introduction_tv)
    TextView mIntroductionTv;

    @InjectView(R.id.config_details_container)
    LinearLayout mContainerLayout;

    @InjectView(R.id.config_save_tv)
    TextView mSave;

    @InjectView(R.id.config_details_back)
    ImageView configDetailsBack;
    @InjectView(R.id.radio_bear)
    RadioButton radioBear;
    @InjectView(R.id.radio_average)
    RadioButton radioAverage;
    @InjectView(R.id.radio_muscle)
    RadioButton radioMuscle;
    @InjectView(R.id.radio_slim)
    RadioButton radioSlim;
    @InjectView(R.id.menu_bar)
    RelativeLayout mMenuBar;


    @Inject
    ContextUtil contextUtil;
    @Inject
    RegionNodeUtil regionNodeUtil;
    @Inject
    FontUtil fontutil;
    @Inject
    ServerApi userService;

    @Inject
    ServerApi profileService;
    @Inject
    protected Bus bus;

    private BasePickerDialog pikerDialog;

    public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
    // private static final int FLAG_CHOOSE_PHONE = 6;
    // private static final int FLAG_CHOOSE_IMG = 0x000005;
    // private static final int FLAG_MODIFY_FINISH = 0x000007;
    private static final int LOACTION_TAG = 1;

    private String mImgid;

    private final int LOADER_GET_PROFILE = 0;

    // 如果数据不是从服务器端拉回来填充的，不能提交
    private boolean dataLoaded = false;
    private User mUser;
    /**
     * 用于对比数据是否被修改
     */
    private User mTempUser;
    private Dialog alertDialog;
    private Dialog openCarmeraDialog;
    /**
     * 当前activity的识别码 注意：为了在各个act之间取得返回值，应当为每个act设置识别码并整合 但是现在还没有这么做
     */
    private static int FLAG = 1;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_config_details);
        com.wealoha.social.ActivityManager.push(this);
        mContext = this;
        mLoadingDialog = new FlippingLoadingDialog(mContext, R.layout.popup_prompt, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        LoaderManager.getInstance(this).restartLoader(LOADER_GET_PROFILE, null, this);
        initView();
    }

    @Override
    protected void changeFont(ViewGroup root) {
        fontutil.changeFonts(root, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
    }

    public void initView() {
        mUserName.setClickable(true);
        mUserName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mUserName.getText())) {
                    mUserName.setSelection(mUserName.getText().length());
                }
            }
        });

        radioBear.setOnCheckedChangeListener(checkChangeLis);
        radioAverage.setOnCheckedChangeListener(checkChangeLis);
        radioMuscle.setOnCheckedChangeListener(checkChangeLis);
        radioSlim.setOnCheckedChangeListener(checkChangeLis);
        mGoal01.setOnCheckedChangeListener(checkChangeLis);
        mGoal02.setOnCheckedChangeListener(checkChangeLis);
        mGoal03.setOnCheckedChangeListener(checkChangeLis);
    }

    protected void initTypeFace() {
        fontutil.changeFonts(mMenuBar, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
    }

    ;

    CompoundButton.OnCheckedChangeListener checkChangeLis = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == null) {
                return;
            }
            if (isChecked) {
                buttonView.setTextColor(Color.WHITE);
            } else {
                buttonView.setTextColor(getResources().getColor(R.color.gray_text));
            }
        }
    };

    public void onWindowFocusChanged(boolean hasFocus) {
    }

    ;

    void inputEnd() {
        CharSequence text = mUserName.getText();
        // Debug.asserts(text instanceof Spannable);
        Spannable spanText = (Spannable) text;
        Selection.setSelection(spanText, text.length());
    }

    @OnClick({R.id.config_details_userphoto_civ, R.id.config_save_tv, R.id.config_details_username_rl, R.id.config_details_introduction, R.id.config_details_age, /*
     * R
     * .
     * id
     * .
     * config_details_area
     * ,
     */R.id.config_details_height, R.id.config_details_stature, R.id.config_details_weight, R.id.config_details_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.config_details_height:
                // 身高
                pikerDialog = new BasePickerDialog(this, 140, 250);
                pikerDialog.show(getSupportFragmentManager(), "");
                String value = (String) mHeightTv.getTag();
                if (value != null && value.trim().length() > 0) {
                    pikerDialog.setCurrentValue(Integer.parseInt(value));
                } else {
                    // 默认
                    pikerDialog.setCurrentValue(180);
                }
                pikerDialog.setOnReturnSomethingListener(new ReturnSomethingListener() {

                    @Override
                    public void returnSomething(String result) {
                        mHeightTv.setText(result + "cm");
                        mHeightTv.setTag(result);
                    }
                });
                break;
            case R.id.config_details_weight:
                // 体重
                pikerDialog = new BasePickerDialog(this, 40, 150);
                pikerDialog.show(getSupportFragmentManager(), "");
                value = (String) mWeightTv.getTag();
                if (value != null && value.trim().length() > 0) {
                    pikerDialog.setCurrentValue(Integer.parseInt(value.trim()));
                } else {
                    pikerDialog.setCurrentValue(60);
                }
                pikerDialog.setOnReturnSomethingListener(new ReturnSomethingListener() {

                    @Override
                    public void returnSomething(String result) {
                        mWeightTv.setText(result + "kg");
                        mWeightTv.setTag(result);
                    }
                });
                break;
            case R.id.config_details_age:
                // 年龄
                TimePickerDialog tp = new TimePickerDialog();
                tp.show(getSupportFragmentManager(), "d");
                String birthday = (String) mAgeTv.getTag();
                if (birthday != null) {
                    // 设置当前日期
                    tp.setCurrentDate(birthday);
                } else {
                    tp.setCurrentDate("2000-01-01");
                }
                tp.setOnReturnSomethingListener(new ReturnSomethingListener() {

                    @Override
                    public void returnSomething(String result) {
                        // result: yyyy-MM-dd
                        // 计算出年龄
                        mAgeTv.setText(getAge(result) + "");
                        mAgeTv.setTag(result);
                    }
                });
                break;
            case R.id.config_details_introduction:
                // 介绍
                Intent intent = new Intent();
                intent.putExtra("content", contextUtil.getCurrentUser() != null ? contextUtil.getCurrentUser().getSummary() : "");
                intent.setData(GlobalConstants.IntentAction.INTENT_URI_INTRODUCTION);
                startActivityForResult(intent, FLAG);
                // startActivity(GlobalConstants.IntentAction.INTENT_URI_INTRODUCTION);
                break;
            case R.id.config_details_username_rl:
                break;
            case R.id.config_save_tv:
                save();
                break;
            case R.id.config_details_back:
                finishFrag();
                break;
            case R.id.config_details_userphoto_civ:
                openPhoto();
                break;
            // case R.id.config_details_area:
            // Intent intentArea = new Intent();
            // intentArea.setData(GlobalConstants.IntentAction.INTENT_URI_LOCATION);
            // startActivityForResult(intentArea, LOACTION_TAG);
            // break;
            default:
                break;
        }
    }

    private void finishFrag() {
        fillParams();

        if (mTempUser != null) {
            if (!checkInfoChange()) {
                areYouSureGiveUpTheChange();
                return;
            }
        }
        finish();
    }

    public void areYouSureGiveUpTheChange() {
        // ToastUtil.longToast(mContext, "--------------");
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_first_aloha_time, new LinearLayout(mContext), false);

        TextView title = (TextView) view.findViewById(R.id.first_aloha_title);
        TextView save = (TextView) view.findViewById(R.id.close_tv);
        TextView close = (TextView) view.findViewById(R.id.close_tv_02);
        TextView first_aloha_message = ((TextView) view.findViewById(R.id.first_aloha_message));
        FontUtil.setRegulartypeFace(this, title);
        FontUtil.setRegulartypeFace(this, save);
        FontUtil.setRegulartypeFace(this, close);
        FontUtil.setRegulartypeFace(this, first_aloha_message);

        title.setText(R.string.give_up_all_change);
        first_aloha_message.setVisibility(View.GONE);
        save.setText(R.string.cancel);
        View verticalLine = view.findViewById(R.id.vertical_line);
        verticalLine.setVisibility(View.VISIBLE);
        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
        close.setText(R.string.give_up_data_chage);
        close.setVisibility(View.VISIBLE);
        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                onBackPressed();
            }
        });
        alertDialog = new AlertDialog.Builder(this)//
                .setView(view)//
                .setCancelable(false) //
                .create();
        alertDialog.show();

    }

    private boolean checkInfoChange() {
        if (mUser == null || mTempUser == null) {
            return true;
        }
        mUser.getName();
        if (!mUser.getName().equals(mTempUser.getName())) {
            return false;
        }

        if (mUser.getWeight() != 0 && mUser.getWeight() != (mTempUser.getWeight())) {
            return false;
        }
        if (mUser.getHeight() != 0 && mUser.getHeight() != (mTempUser.getHeight())) {
            return false;
        }
        // if (mUser.regionCode != null && !mUser.regionCode.equals(mTempUser.regionCode)) {
        // return false;
        // }
        // if (mUser.region != null && !mUser.region.equals(mTempUser.region)) {
        // return false;
        // }
        if (!mUser.getBirthday().equals(mTempUser.getBirthday())) {
            return false;
        }
        if (mUser.getSelfTag() != null && !mUser.getSelfTag().equals(mTempUser.getSelfTag())) {
            // ToastUtil.longToast(mContext, "-----selfTag---------");
            return false;
        }
        if (mUser.getSelfPurposes() != null && mTempUser.getSelfPurposes() == null) {
            // ToastUtil.longToast(mContext,
            // "-----mUser.selfPurposes---------");
            return false;
        } else if (mUser.getSelfPurposes() == null && mTempUser.getSelfPurposes() != null) {
            // ToastUtil.longToast(mContext,
            // "-----mTempUser.selfPurposes---------");
            return false;
        }

        if (!StringUtil.join("-", mUser.getSelfPurposes()).equals(StringUtil.join("-", mTempUser.getSelfPurposes()))) {
            return false;
        }

        // String temp = StringUtil.join("-", selfPurposes);
        // if (mUser.selfPurposes != null && mTempUser.selfPurposes != null) {
        // if (mUser.selfPurposes.size() != mTempUser.selfPurposes.size()) {
        // ToastUtil.longToast(mContext, mUser.selfPurposes.size() +
        // "-----mTempUser.size---------" +
        // mTempUser.selfPurposes.size());
        // return false;
        // }
        // for (int i = 0; i < mUser.selfPurposes.size(); i++) {
        // if (!mTempUser.selfPurposes.contains(mUser.selfPurposes.get(i))) {
        // ToastUtil.longToast(mContext, "-----contains---------");
        // return false;
        // }
        // }
        // }
        if (mUser.getAvatarImageId() != null && !TextUtils.isEmpty(mTempUser.getAvatarImageId())) {
            if (!mUser.getAvatarImageId().equals(mTempUser.getAvatarImageId())) {
                return false;
            }
        }

        return true;
    }

    private int getAge(String b) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date birthday = df.parse(b);
            Date now = new Date();
            if (birthday.after(now)) {
                return 0;
            }
            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);
            int birthYear = birth.get(Calendar.YEAR);
            int age = thisYear - birthYear;
            Date birthWithSameYear = DateUtils.addYears(birthday, age);
            // 到生日了
            if (birthWithSameYear.before(now) || DateUtils.isSameDay(birthWithSameYear, now)) {
                return age;
            } else {
                age = age - 1;
                return age > 0 ? age : 0;
            }

            // int year = Calendar.getInstance().get(Calendar.YEAR);
            // Calendar c = Calendar.getInstance();
            // c.setTime(birthday);
            // int birthYear = c.get(Calendar.YEAR);
            // if (birthYear >= year) {
            // // 您是来自未来的？
            // return 0;
            // }
            // return year - birthYear;
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: save
     * @Description: 保存到网络
     */
    public void save() {
        if (!dataLoaded) {
            // 不是后台拉回来的，不能保存！！
            return;
        }

        User user = fillParams();

        if (user == null) {
            return;
        }

        // 为了和ios兼容，使用『-』拼起来
        String selfPurpose = StringUtil.join("-", user.getSelfPurposes());
        userService.reqAlertUserInfo(user.getName(), user.getBirthday(), user.getHeight(), user.getWeight(), user.getSelfTag(), user.getRegionCode(), selfPurpose, user.getAvatarImageId(), user.getSummary(), new Callback<ApiResponse<AuthData>>() {

            @Override
            public void success(ApiResponse<AuthData> apiResponse, Response arg1) {
                mLoadingDialog.dismiss();
                if (apiResponse == null) {
                    XL.d(TAG, "连接服务器失败");
                    ToastUtil.longToast(mContext, getString(R.string.network_error));
                    return;
                }
                if (apiResponse.isOk()) {
                    // Log.i("CONFIG_USER", "user:" + result.getData().user);
                    ToastUtil.shortToast(mContext, getString(R.string.successfully_saved));
                    contextUtil.setCurrentUser(apiResponse.getData().getUser());
                    // Log.i("CONFIG_USER", "user current:" +
                    // contextUtil.getCurrentUser());
                    isRefreshHeadIcon = true;
                    setResult();
                    finish();
                } else if (apiResponse.getData().getError() == ResultData.ERROR_USERNAME_USED) {
                    ToastUtil.shortToast(mContext, getString(R.string.username_unavailable));
                } else if (apiResponse.getData().getError() == ResultData.ERROR_INVALID_SUMMARY) {
                    ToastUtil.shortToast(mContext, getString(R.string.username_unavailable));
                }
            }

            @Override
            public void failure(RetrofitError arg0) {
                mLoadingDialog.dismiss();
                ToastUtil.shortToast(mContext, getString(R.string.network_error));
            }
        });

        mLoadingDialog.show();
    }

    /**
     * @return 一个临时用户，保存了需要修改的属性
     */
    private User fillParams() {
        mTempUser = User.Companion.fake(true, false);
        // 姓名
        String username = mUserName.getText().toString();
        if (!TextUtils.isEmpty(username) && username.length() >= 2) {
            mTempUser.setName(username);
            // XL.d(TAG, username);
        } else {
            ToastUtil.shortToast(this, R.string.the_user_name_lenght_not_correct);
            // showSingleAlohaDialog(mContext,
            // R.string.the_user_name_lenght_not_correct, null);
            return null;
        }

        // 生日 yyyy-MM-dd
        String birthday = (String) mAgeTv.getTag();
        if (!TextUtils.isEmpty(birthday)) {
            mTempUser.setBirthday(birthday);
        }

        // 身高
        String height = (String) mHeightTv.getTag();
        if (!TextUtils.isEmpty(height)) {
            mTempUser.setHeight(Integer.parseInt(height));
        }
        // 体重
        String weight = (String) mWeightTv.getTag();
        if (!TextUtils.isEmpty(height)) {
            mTempUser.setWeight(Integer.parseInt(weight));
        }

        // 已经是从tag里取的了
        String selfTag = null;
        for (int i = 0; i < mStatureRg.getChildCount(); i++) {
            RadioButton raido = (RadioButton) mStatureRg.getChildAt(i);
            if (raido.isChecked()) {
                selfTag = (String) raido.getTag();
                break;
            }
        }
        if (!TextUtils.isEmpty(selfTag)) {
            mTempUser.setSelfTag(selfTag);
        }
        // 地区码
        // String regionCode = (String) mAreaTv.getTag();
        // if (!TextUtils.isEmpty(regionCode)) {
        // mTempUser.regionCode = regionCode;
        // }
        // 目的
        List<String> selfPurposes = new ArrayList<String>();
        for (CheckBox cb : new CheckBox[]{mGoal01, mGoal02, mGoal03}) {
            if (cb.isChecked()) {
                selfPurposes.add((String) cb.getTag());
            }
        }
        if (selfPurposes.size() > 0) {
            mTempUser.setSelfPurposes(selfPurposes);
            // XL.d(TAG, "目的: " + selfPurpose);
        }

        if (!TextUtils.isEmpty(mImgid)) {
            mTempUser.setAvatarImageId(mImgid);
        }

        String summary = mIntroductionTv.getText().toString();
        if (!TextUtils.isEmpty(summary)) {
            mTempUser.setSummary(summary);
        }

        return mTempUser;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // PromptPopup.hidePrompt();
    }

    /**
     * @Description: 拍照
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-1
     */
    public void openPhoto() {
        View view = getLayoutInflater().inflate(R.layout.open_carmera_dialog, new LinearLayout(mContext), false);
        TextView openCarmera = (TextView) view.findViewById(R.id.open_carmera);
        TextView openLocaPics = (TextView) view.findViewById(R.id.open_location_photo);
        FontUtil.setRegulartypeFace(this, openCarmera);
        FontUtil.setRegulartypeFace(this, openLocaPics);
        openCarmera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissDialog();
                openCamera(ConfigDetailsAct.this);
            }
        });
        openLocaPics.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissDialog();
                openImgPick(ConfigDetailsAct.this);
            }
        });
        openCarmeraDialog = new AlertDialog.Builder(this).setView(view).show();
    }

    private void dismissDialog() {
        if (openCarmeraDialog != null && openCarmeraDialog.isShowing()) {
            openCarmeraDialog.dismiss();
        }
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: saveIntroduction
     * @Description: 返回当前ACT的各个子ACT的返回值
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {

        // if (requestCode == LOACTION_TAG && resultCode == RESULT_OK && result
        // != null) {
        //
        // String[] area =
        // result.getExtras().getString("regionName").split(",");
        // String areaStr = "";
        // if (area.length > 2) {
        // for (int i = area.length - 2; i < area.length; i++) {
        // areaStr = areaStr + ", " + area[i];
        // }
        // if (!TextUtils.isEmpty(areaStr)) {
        // areaStr = areaStr.substring(1, areaStr.length());
        // }
        // } else {
        // areaStr = result.getExtras().getString("regionName");
        // }
        //
        // // mAreaTv.setText(areaStr);
        // // mAreaTv.setTag(result.getExtras().getString("region"));
        // }
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == FLAG && resultCode == 0 && result != null) {
            mIntroductionTv.setText(result.getExtras().getString("introduction"));
        }
        // 改善了照片选择的代码阅读
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GlobalConstants.AppConstact.CAMERA_WITH_DATA:
                    if (mCameraImgFile == null) {// NullPointerException
                        return;
                    }
                    goImgCropAct(mCameraImgFile.getAbsolutePath(), GlobalConstants.AppConstact.IMG_PATH_FROM_CAMERA_WITH_DATA, TAG);
                    break;
                case GlobalConstants.AppConstact.PHOTO_PICKED_WITH_DATA: {
                    if (result == null) {// NullPointerException
                        return;
                    }
                    getSelectedImgPath(result.getData(), GlobalConstants.AppConstact.IMG_PATH_FROM_PHOTO_PICKED_WITH_DATA, TAG);
                }
                break;
                case GlobalConstants.AppConstact.FLAG_MODIFY_FINISH: {// 进入裁剪界面
                    if (result == null) {// NullPointerException
                        return;
                    }
                    String path = result.getStringExtra("path");
                    if (path != null) {
                        uploadPhotos(path);
                    } else {
                        // showSingleAlohaDialog(mContext, R.string.failed,
                        // View.GONE);
                        // ToastUtil.longToast(this, R.string.failed);
                        ToastUtil.shortToast(this, R.string.failed);

                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     * @param path
     * @Description: 图片上传
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-3
     */
    private void uploadPhotos(String path) {
        File file = new File(path);
        if (file.exists()) {
            uploadMethod(file, path);
        }
    }

    public void uploadMethod(File file, final String path) {
        if (file == null) {
            return;
        }
        mLoadingDialog.show();
        TypedFile usericon = new TypedFile("application/octet-stream", file);
        userService.sendSingleFeed(usericon, new Callback<ApiResponse<ImageUploadResult>>() {

            @Override
            public void failure(RetrofitError arg0) {
                mLoadingDialog.dismiss();
                ToastUtil.shortToast(ConfigDetailsAct.this, R.string.Unkown_Error);
                // showSingleAlohaDialog(mContext, R.string.Unkown_Error,
                // View.GONE);

            }

            @Override
            public void success(ApiResponse<ImageUploadResult> apiResponse, Response arg1) {
                mLoadingDialog.dismiss();
                if (apiResponse != null && apiResponse.isOk()) {
                    mImgid = apiResponse.getData().imageId;
                    mHandler.sendMessage(sendMsgToHandler(GlobalConstants.AppConstact.DISPLAY_HAND_PIC, path));
                    setResult();
                } else {
                    ToastUtil.shortToast(ConfigDetailsAct.this, R.string.Unkown_Error);
                    // showSingleAlohaDialog(mContext, R.string.Unkown_Error,
                    // View.GONE);
                }
            }
        });
    }

    /***
     *
     * 设置返回结果，刷新profile 界面的头像
     *
     * @param
     * @return void
     */
    public void setResult() {
        // Intent result = new Intent();
        // result.putExtra(GlobalConstants.AppConstact.REFRESH_RESULT_KEY,
        // path);
        setResult(RESULT_OK);
    }

    @Override
    public Loader<ApiResponse<ProfileData>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_GET_PROFILE:
                mLoadingDialog.show();
                return new AsyncLoader<ApiResponse<ProfileData>>(this) {

                    @Override
                    public ApiResponse<ProfileData> loadInBackground() {
                        try {
                            return profileService.view(contextUtil.getCurrentUser().getId());
                        } catch (Exception e) {
                            return null;
                        }
                    }
                };

            default:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<ApiResponse<ProfileData>> loader, ApiResponse<ProfileData> apiResponse) {
        mLoadingDialog.hide();
        if (apiResponse == null) {
            return;
        }
        switch (loader.getId()) {
            case LOADER_GET_PROFILE:
                mUser = apiResponse.getData().user;
                initData();
                initHandler();
                break;
            default:
                break;
        }
    }

    /**
     * 当一个已创建的装载器被重置,从而使其数据无效时，此方法被调用．此回调使你能发现什么时候数据将被釋放,那时你可以釋放对它的引用
     */
    @Override
    public void onLoaderReset(Loader<ApiResponse<ProfileData>> arg0) {

    }

    private void initHandler() {
        mHandler = new RefreshPhotoHandler(this);
    }

    /**
     * 避免内存溢出
     *
     * @author Yng
     * @date 2015年2月27日 下午1:52:46
     * @see
     * @since
     */
    private static class RefreshPhotoHandler extends Handler {

        private WeakReference<ConfigDetailsAct> mAcitivity;

        public RefreshPhotoHandler(ConfigDetailsAct baseAct) {
            mAcitivity = new WeakReference<ConfigDetailsAct>(baseAct);
        }

        @Override
        public void handleMessage(Message msg) {
            ConfigDetailsAct baseAct = mAcitivity.get();
            if (baseAct != null) {
                switch (msg.what) {
                    case GlobalConstants.AppConstact.DISPLAY_HAND_PIC:
                        baseAct.refreshPhoto((String) msg.obj);
                        break;
                }
            }
        }
    }

    /**
     * @Title: RefreshPhoto
     * @Description: 更新头像
     */
    public void refreshPhoto(String url) {
        Picasso.get().load(new File(url)).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(R.drawable.default_photo).into(mUserPhoto);
        mUserPhoto.invalidate();
    }

    public void initData() {
        dataLoaded = true;
        // 身高，体重，年龄都保存在tag上
        // 头像
        // ImageSize.AVATAR_ROUND_SMALL, CropMode.ScaleCenterCrop));
        Picasso.get().load(ImageUtil.getImageUrl(mUser.getAvatarImage().getId(), ImageSize.CHAT_THUMB, CropMode.ScaleCenterCrop)).placeholder(R.drawable.default_photo).into(mUserPhoto);
        mUserName.setText(TextUtils.isEmpty(mUser.getName()) ? "" : mUser.getName());

        // 年龄
        mAgeTv.setText(String.valueOf(mUser.getAge()));
        mAgeTv.setTag(TextUtils.isEmpty(mUser.getBirthday()) ? "" : mUser.getBirthday());
        // 身高
        mHeightTv.setText(mUser.getHeight() + "cm");
        mHeightTv.setTag(mUser.getHeight() + "");
        // 体重
        mWeightTv.setText(mUser.getWeight() + "kg");
        mWeightTv.setTag(mUser.getWeight());
        // introduction
        mIntroductionTv.setText(TextUtils.isEmpty(mUser.getSummary()) ? "" : mUser.getSummary());
        // if (!TextUtils.isEmpty(mUser.regionCode)) {
        // List<String> regionNames =
        // regionNodeUtil.getRegionNames(mUser.regionCode, 10);
        // Collections.reverse(regionNames);
        // // FIXME 地區 算法要改。。
        // if (regionNames.size() > 2) {
        // regionNames.remove(0);
        // }
        // mAreaTv.setText(StringUtil.join(", ", regionNames));
        // mAreaTv.setTag(mUser.regionCode);
        // }
        // selfPurpose, ios用-分开的
        if (mUser.getSelfPurposes() != null && mUser.getSelfPurposes().size() > 0) {
            CheckBox[] cbs = new CheckBox[]{mGoal01, mGoal02, mGoal03};
            for (String purposes : mUser.getSelfPurposes()) {
                String[] ps = StringUtil.split(purposes, "-");
                if (ps != null && ps.length > 0) {
                    ONE_PURPOSE:
                    for (String p : ps) {
                        XL.d(TAG, "purpose: " + p);
                        for (CheckBox cb : cbs) {
                            if (cb.getTag().equals(p)) {
                                cb.setChecked(true);
                                cb.setTextColor(Color.WHITE);
                                continue ONE_PURPOSE;
                            }
                        }
                    }

                }
            }
        }
        // selftag
        if (!TextUtils.isEmpty(mUser.getSelfTag())) {
            for (int i = 0; i < mStatureRg.getChildCount(); i++) {
                RadioButton radio = ((RadioButton) mStatureRg.getChildAt(i));
                if (radio.getTag().equals(mUser.getSelfTag())) {
                    radio.setChecked(true);
                    radio.setTextColor(Color.WHITE);
                } else {
                    radio.setTextColor(getResources().getColor(R.color.gray_text));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        com.wealoha.social.ActivityManager.pop(this);
        super.onDestroy();
    }

}
