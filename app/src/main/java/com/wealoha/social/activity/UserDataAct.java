package com.wealoha.social.activity;

import java.io.File;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.InjectView;
import butterknife.OnClick;

import com.lidroid.xutils.ViewUtils;
import com.squareup.picasso.Picasso;
import com.wealoha.social.ActivityManager;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.ContextConfig;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Image;
import com.wealoha.social.beans.ImageUploadResult;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.beans.User;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.GlobalConstants.ImageSize;
import com.wealoha.social.utils.ContextUtil;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.FontUtil.Font;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.view.custom.CircleImageView;
import com.wealoha.social.view.custom.dialog.BasePickerDialog;
import com.wealoha.social.view.custom.dialog.BasePickerDialog.ReturnSomethingListener;
import com.wealoha.social.view.custom.dialog.TimePickerDialog;

public class UserDataAct extends BaseFragAct implements OnClickListener {

    public static final String TAG = UserDataAct.class.getSimpleName();
    /**
     * 包裹头像的相对布局
     */
    private String mHeadIconPath;
    public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
    private User user;
    public MHandler mHandler = new MHandler();
    @InjectView(R.id.usercenter_rl)
    LinearLayout layout;
    @InjectView(R.id.usercenter_weight_rl)
    RelativeLayout mWeight;
    @InjectView(R.id.usercenter_height_rl)
    RelativeLayout mHeight;
    // @InjectView(R.id.usercenter_area_rl)
    // RelativeLayout mArea;
    @InjectView(R.id.usercenter_birthday_rl)
    RelativeLayout mbirthday;
    @InjectView(R.id.usercenter_weight_tv)
    TextView mWeightTv;
    @InjectView(R.id.usercenter_height_tv)
    TextView mHeightTv;
    // @InjectView(R.id.usercenter_area_tv)
    // TextView mAreaTv;
    @InjectView(R.id.usercenter_birthday_tv)
    TextView mBirthdayTv;
    @InjectView(R.id.save_user_data)
    TextView mSave;
    @InjectView(R.id.user_data_nick_box)
    EditText mUsername;
    @InjectView(R.id.usermanager_head_icon)
    CircleImageView mUserPhoto;
    @Inject
    Picasso picasso;
    @Inject
    FontUtil fontUtil;
    @Inject
    Context context;
    @Inject
    ServerApi userService;
    private BasePickerDialog mPickerDialog;
    private TimePickerDialog mTimePicdialog;
    private static final int LOACTION_TAG = 1;
    private String username;
    private String height;
    private String weight;
    private String regionCode;
    private String birthday;
    private String defaultBir = "1989-06-15";
    private int defaultWeight = 75;
    private int defaultHeight = 180;
    private Dialog openCarmeraDialog;

    @Inject
    ContextUtil contextUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_user_data);
        ActivityManager.push(this);
        ViewUtils.inject(this);
        user = (User) getIntent().getParcelableExtra(User.TAG);

        if (user == null) {
            user = User.Companion.fake();
        }
        fontUtil.changeFonts(layout, Font.ENCODESANSCOMPRESSED_600_SEMIBOLD);
        // 可以输入邀请了
        ContextConfig.getInstance().putBooleanWithFilename(GlobalConstants.AppConstact.SHOW_INVITATION_CODE_INPUT, true);
        collectMsgUpdateUI();
    }

    @OnClick({ /* R.id.usercenter_area_rl, *///
            R.id.usermanager_head_icon,//
            R.id.usercenter_weight_rl,//
            R.id.usercenter_birthday_rl,//
            R.id.usercenter_height_rl,//
            R.id.save_user_data})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.usermanager_head_icon:
                openHeadSelect();
                break;
            case R.id.usercenter_weight_rl:
                mPickerDialog = new BasePickerDialog(this, 40, 150);
                mPickerDialog.setCurrentValue(defaultWeight);
                mPickerDialog.show(getFragmentManager(), "");
                mPickerDialog.setOnReturnSomethingListener(new ReturnSomethingListener() {

                    @Override
                    public void returnSomething(String result) {
                        mWeightTv.setText(result + "kg");
                        weight = result;
                    }
                });
                break;
            case R.id.usercenter_height_rl:
                mPickerDialog = new BasePickerDialog(this, 140, 250);
                mPickerDialog.setCurrentValue(defaultHeight);
                mPickerDialog.show(getFragmentManager(), "");
                mPickerDialog.setOnReturnSomethingListener(new ReturnSomethingListener() {

                    @Override
                    public void returnSomething(String result) {
                        mHeightTv.setText(result + "cm");
                        height = result;
                    }
                });
                break;
            case R.id.usercenter_birthday_rl:
                mTimePicdialog = new TimePickerDialog();
                mTimePicdialog.setCurrentDate(defaultBir);
                mTimePicdialog.show(getFragmentManager(), "");
                mTimePicdialog.setOnReturnSomethingListener(new ReturnSomethingListener() {

                    @Override
                    public void returnSomething(String result) {
                        mBirthdayTv.setText(result);
                        birthday = result;
                    }
                });
                break;
            case R.id.save_user_data:
                save();
                break;
        }
    }

    private void openHeadSelect() {
        View view = getLayoutInflater().inflate(R.layout.open_carmera_dialog, null);
        TextView openCarmera = (TextView) view.findViewById(R.id.open_carmera);
        TextView openLocaPics = (TextView) view.findViewById(R.id.open_location_photo);
        openCarmera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openCamera(UserDataAct.this);
                if (openCarmeraDialog != null && openCarmeraDialog.isShowing()) {
                    openCarmeraDialog.dismiss();
                }
            }
        });
        openLocaPics.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openImgPick(UserDataAct.this);
                if (openCarmeraDialog != null && openCarmeraDialog.isShowing()) {
                    openCarmeraDialog.dismiss();
                }
            }
        });
        openCarmeraDialog = new AlertDialog.Builder(this).setView(view).show();
    }

    public int titleHeight;
    public static final int DISPLAY_HAND_PIC = 0x000004;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
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
                case GlobalConstants.AppConstact.FLAG_MODIFY_FINISH: {// 进入裁剪界面后返回
                    final String path = result.getStringExtra("path");
                    if (!TextUtils.isEmpty(path)) {
                        mHandler.sendMessage(sendMsgToHandler(DISPLAY_HAND_PIC, path));
                    } else {
                        ToastUtil.shortToast(this, R.string.failed);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    public void save() {
        username = mUsername.getText().toString().trim().replaceAll(" ", "");
        if (TextUtils.isEmpty(username) || username.length() < 2 || username.length() > 20) {
            ToastUtil.shortToast(mContext, R.string.the_user_name_lenght_not_correct);
            return;
        }
        // 身高
        if (TextUtils.isEmpty(height)) {
            ToastUtil.shortToast(this, R.string.please_input_your_height_and_weight);
            return;
        }
        // 体重
        if (TextUtils.isEmpty(weight)) {
            ToastUtil.shortToast(this, R.string.please_input_your_height_and_weight);
            return;
        }
        if (TextUtils.isEmpty(birthday)) {
            ToastUtil.shortToast(this, R.string.please_input_your_birthday);
            return;
        }
        if (TextUtils.isEmpty(mHeadIconPath)) {
            ToastUtil.shortToast(this, R.string.please_upload_your_avatar_image);
            return;
        }

        user.setName(username);
        user.setHeight(Integer.parseInt(height));
        user.setWeight(Integer.parseInt(weight));
        user.setBirthday(birthday);
        user.setRegionCode(regionCode);
        userDataPerfect();
    }

    public void uploadMethod(String imgPath) {
        if (TextUtils.isEmpty(imgPath)) {
            return;
        }
        showDialog(true);
        TypedFile usericon = new TypedFile("application/octet-stream", new File(imgPath));
        userService.sendSingleFeed(usericon, new Callback<Result<ImageUploadResult>>() {

            @Override
            public void failure(RetrofitError arg0) {
                showDialog(false);
                ToastUtil.shortToast(UserDataAct.this, R.string.network_error);

            }

            @Override
            public void success(Result<ImageUploadResult> result, Response arg1) {
                if (result != null && result.isOk()) {
                    user.setAvatarImage(Image.Companion.fake());
                    user.getAvatarImage().setId(result.getData().imageId);
                    user.getAvatarImage().setUrl(result.getData().imageUrl);
                } else {
                    ToastUtil.shortToast(UserDataAct.this, R.string.failed);
                }
                showDialog(false);
            }
        });
    }

    /**
     * @Description: 上传用户的其他资料
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-11-19
     */
    private void userDataPerfect() {
        showDialog(true);

        userService.uploadUserData(username, height, weight, user.getBirthday(), user.getRegionCode(), user.getAvatarImage().getId(), new Callback<Result<AuthData>>() {

            @Override
            public void success(Result<AuthData> result, Response paramResponse) {
                showDialog(false);
                if (result != null) {
                    if (result.isOk()) {
                        // 成功，进入邀请码页面
                        contextUtil.setCurrentUser(result.getData().getUser());
                        startActivity(GlobalConstants.IntentAction.INTENT_URI_INVITATION);
                        finish();
                    } else if (result.getData().getError() == ResultData.ERROR_USERNAME_USED) {
                        ToastUtil.longToast(UserDataAct.this, getString(R.string.username_unavailable));
                    } else if (result.getData().getError() == ResultData.ERROR_INVALID_SUMMARY) {
                        ToastUtil.longToast(UserDataAct.this, getString(R.string.intro_has_illegalword));
                    } else {
                        ToastUtil.shortToast(UserDataAct.this, R.string.is_not_work);
                    }
                } else {
                    ToastUtil.longToast(UserDataAct.this, getString(R.string.network_error));
                }
            }

            @Override
            public void failure(RetrofitError paramRetrofitError) {
                showDialog(false);
                ToastUtil.longToast(UserDataAct.this, getString(R.string.network_error));
            }
        });
    }

    /***
     * loading 弹层
     *
     * @param isShow
     * @return void
     */
    public void showDialog(boolean isShow) {
        if (isShow) {
            if (container != null) {
                if (popup != null) {
                    popup.show(container);
                }
            }
        } else {
            if (popup != null) {
                popup.hide();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        layout.requestFocus();
    }

    private void collectMsgUpdateUI() {
        mHandler.sendEmptyMessage(DISPLAY_HAND_PIC);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            exit();
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private static final int EXIT_BACK_NOTICE = 111;
    private boolean isExit;

    public void exit() {
        if (!isExit) {
            isExit = true;
            ToastUtil.longToast(UserDataAct.this, getString(R.string.back_to_exit_notice));
            mHandler.sendEmptyMessageDelayed(EXIT_BACK_NOTICE, 2000);
        } else {
            finish();
            ActivityManager.popAll();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    public class MHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EXIT_BACK_NOTICE:
                    isExit = false;
                    break;

                case DISPLAY_HAND_PIC:
                    try {
                        mHeadIconPath = (String) msg.obj;
                        uploadMethod(mHeadIconPath);
                        picasso.load(new File(mHeadIconPath)).resize(ImageSize.CHAT_THUMB, ImageSize.CHAT_THUMB).skipMemoryCache().placeholder(R.drawable.default_photo).into(mUserPhoto);
                    } catch (Exception e) {
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        ActivityManager.pop(this);
        super.onDestroy();
    }

}
