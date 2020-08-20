package com.wealoha.social.activity;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.api.ServerApi;
import com.wealoha.social.beans.AuthData;
import com.wealoha.social.beans.Result;
import com.wealoha.social.beans.ResultData;
import com.wealoha.social.commons.GlobalConstants;
import com.wealoha.social.commons.JsonController;
import com.wealoha.social.utils.FontUtil;
import com.wealoha.social.utils.StringUtil;
import com.wealoha.social.utils.ToastUtil;
import com.wealoha.social.utils.XL;

/**
 * 短信验证码发送和验证
 *
 * @author sunkist
 * @author superman
 * @author javamonk
 * @date 2014-10-29 10:20:05
 * @see
 * @since
 */
public class VerifyAct extends BaseFragAct implements OnClickListener {

    @Inject
    Context context;
    @Inject
    ServerApi userRegisterService;

    /**
     * 取消
     */
    @ViewInject(R.id.dialog_last)
    private Button mDialoglast;
    /**
     * 没有收到验证码
     */
    @ViewInject(R.id.dialog_get_sms_error)
    private TextView mDialogGetSmsError;
    /**
     * 验证码输入框
     */
    @ViewInject(R.id.dialog_input_auth_code_box)
    private EditText mInputAuthCodeBox;
    /**
     * 提示信息
     */
    @ViewInject(R.id.verify_code_mobile_num)
    private TextView mVerifyCodeMobileNum;
    /**
     * 取消
     */
    @ViewInject(R.id.dialog_cancle)
    private TextView mDialogCancle;
    /**
     * 取消
     */
    private String phoneNum;
    /**
     * 密码
     */
    private String pw;
    // private PopupWindow pWindow;
    public static final int ANEWREQ_CODE = 100;
    public static final int WAITREQCODE = 101;
    // 来自注册(0)还是忘记密码(1)
    private int from = -1;

    private InputMethodManager imm;

    private static int FROM_REGISTER = 0;
    private static int FROM_FORGOT = 1;
    /**
     * 计时专用
     */
    private TextView timerTextView;
    private TextView resetTitle;
    private Dialog forgotDialog;
    int i = 60;
    private Timer timer;
    /**
     * 計時專用handler
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                if (msg.what == 1) {
                    Log.i("TIMER_TIMER", "I:" + i);
                    if (i == 0) {
                        timer.cancel();
                        timer = null;
                        if (forgotDialog != null && forgotDialog.isShowing()) {
                            forgotDialog.dismiss();
                            reSendSms();
                        }
                    }
                    if (timerTextView != null) {
                        timerTextView.setText(getString(R.string.retry_send_msg_str, msg.arg1));
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_verify);
        ViewUtils.inject(this);
        startTimer();
        initData();
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: startTimer
     * @Description: 開始計時
     * @ 设定文件
     */
    public void startTimer() {
        i = 60;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                i--;
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = i;

                handler.sendMessage(msg);
            }
        }, 60, 1000);
    }

    public void initData() {

        Intent intent = getIntent();
        Bundle bundle = intent != null ? intent.getExtras() : null;
        if (bundle != null) {
            if (bundle.getInt("from") == FROM_REGISTER) {// 来自注册
                pw = bundle.getString(GlobalConstants.AppConstact.PASSWORD);
                from = FROM_REGISTER;
            } else if (bundle.getInt("from") == FROM_FORGOT) {// 来自忘记密码
                from = FROM_FORGOT;
            }
            phoneNum = bundle.getString(GlobalConstants.AppConstact.USERNAME);
            Log.i("VERFYact", "verifyact---" + phoneNum);
            mVerifyCodeMobileNum.setText(phoneNum);
        } else {
            Toast.makeText(this, R.string.procedural_error, Toast.LENGTH_LONG).show();
        }

    }

    @OnClick({R.id.dialog_last, R.id.dialog_get_sms_error, R.id.dialog_cancle})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_last:
                if (TextUtils.isEmpty(mInputAuthCodeBox.getText().toString())) {
                    ToastUtil.longToast(context, R.string.verification_code_cant_be_empty);
                    return;
                }

                if (from == FROM_FORGOT) {
                    lastForForgot();// 下一步 重置密码
                } else if (from == FROM_REGISTER) {
                    lastForRegister();// 下一步 完成注册
                }

                break;
            case R.id.dialog_get_sms_error:
                reSendSmsTimer();
                break;
            case R.id.dialog_cancle:
                cancelAuth();
                break;
        }
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: lastForForgot
     * @Description: 下一步忘记密码
     */
    public void lastForForgot() {
        final String code = mInputAuthCodeBox.getText().toString();
        XL.d(TAG, "验证码: " + code);
        if (TextUtils.isEmpty(code)) {
            ToastUtil.longToast(this, getString(R.string.verification_code_cant_be_empty));
            return;
        }
        HttpUtils httpUtils = new HttpUtils();
        RequestParams reqParams = new RequestParams();
        reqParams.addBodyParameter("number", phoneNum);
        reqParams.addBodyParameter("code", code);
        httpUtils.send(HttpMethod.POST, GlobalConstants.ServerUrl.RESET_PASSWORD_VERIFY_CODE, reqParams, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                /* 18210402175 */
                ToastUtil.shortToast(VerifyAct.this, getString(R.string.verification_code_cant_be_empty));
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                Result<AuthData> result = JsonController.parseJson(arg0.result, new TypeToken<Result<AuthData>>() {
                }.getType());
                ToastUtil.shortToast(VerifyAct.this, getString(R.string.verification_code_has_been_sent));
                if (result != null && result.isOk()) {
                    // afterMobileLoginSuccess(phoneNum, result.data.user,
                    // result.data.t);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", phoneNum);
                    bundle.putString("code", code);
                    startActivity(GlobalConstants.IntentAction.INTENT_URI_NEWPASSWORD, bundle);
                    finish();
                } else if (ResultData.MOBILE_ERROR == result.data.error) {
                    ToastUtil.longToast(VerifyAct.this, R.string.register_mobile_error);
                } else if (ResultData.REGISTERED_ERROR == result.data.error) {
                    ToastUtil.longToast(VerifyAct.this, R.string.mobile_error);
                } else {
                    ToastUtil.longToast(VerifyAct.this, R.string.verification_code_has_been_sent_but_faile);
                }
            }
        });
    }

    /**
     * @return void 返回类型
     * @throws
     * @
     * @Title: lastForRegister
     * @Description: 下一步注册
     */
    private void lastForRegister() {

        String code = mInputAuthCodeBox.getText().toString();
        Log.i("VERFYact", "last for register:" + phoneNum + "-----" + code);
        if (TextUtils.isEmpty(code)) {
            ToastUtil.longToast(this, getString(R.string.verification_code_cant_be_empty));
            return;
        }

        // 提交注册
        userRegisterService.register(phoneNum, code, StringUtil.md5(pw), new Callback<Result<AuthData>>() {

            @Override
            public void success(Result<AuthData> result, Response response) {
                if (result == null) {
                    ToastUtil.longToast(VerifyAct.this, R.string.Unkown_Error);
                    return;
                }
                if (result.isOk()) {
                    ToastUtil.shortToast(VerifyAct.this, getString(R.string.verification_code_has_been_sent));
                    afterMobileLoginSuccess(phoneNum, result.data.user, result.data.t);
                    finish();
                } else if (ResultData.ERROR_INVALID_MOBILE_VERIFY_CODE == result.data.error) {
                    ToastUtil.longToast(VerifyAct.this, R.string.verification_code_has_been_sent_but_faile);
                } else if (ResultData.ERROR_MOBILE_NUMBER_REGISTERED == result.data.error) {
                    ToastUtil.longToast(VerifyAct.this, R.string.Invalid_phone_number_str);
                } else {
                    ToastUtil.longToast(VerifyAct.this, R.string.Unkown_Error);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    public void reSendSmsTimer() {
        if (i == 0) {
            reSendSms();
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.dialog_sms_reset, null);
        timerTextView = (TextView) view.findViewById(R.id.reset_phone_num);
        resetTitle = (TextView) view.findViewById(R.id.reset_title);
        resetTitle.setText(R.string.resend_security_code);
        timerTextView.setText(getString(R.string.retry_send_msg_str, i));
        FontUtil.setRegulartypeFace(this, resetTitle);
        FontUtil.setRegulartypeFace(this, timerTextView);
        forgotDialog = new AlertDialog.Builder(this).setView(view).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        }).create();
        forgotDialog.show();
    }

    /**
     * @param @param url 设定文件
     * @return void 返回类型
     * @throws
     * @Title: reSendSms
     * @Description: 参数用来判断重新获取的是忘记密码的验证码还是注册的验证码
     */
    private void reSendSms() {
        View view = getLayoutInflater().inflate(R.layout.dialog_sms_reset, null);
        TextView pn = (TextView) view.findViewById(R.id.reset_phone_num);
        TextView rt = (TextView) view.findViewById(R.id.reset_title);
        rt.setText(R.string.resend_security_code);
        pn.setText(phoneNum);
        forgotDialog = new AlertDialog.Builder(this).setView(view).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        }).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 两个发送验证码的接口返回状态码类似
                Callback<Result<ResultData>> callback = new Callback<Result<ResultData>>() {

                    @Override
                    public void success(Result<ResultData> result, Response response) {

                        if (result != null) {
                            if (result.isOk()) {
                                ToastUtil.shortToast(VerifyAct.this, getString(R.string.verification_code_has_been_sent));
                                startTimer();
                            } else if (ResultData.MOBILE_ERROR == result.data.error) {
                                ToastUtil.longToast(VerifyAct.this, R.string.register_mobile_error);
                            } else if (ResultData.REGISTERED_ERROR == result.data.error) {
                                ToastUtil.longToast(VerifyAct.this, R.string.mobile_error);
                            } else if (Result.STATUS_CODE_THRESHOLD_HIT == result.status) {
                                ToastUtil.longToast(VerifyAct.this, R.string.get_code_frequent_req);
                            } else {
                                ToastUtil.longToast(VerifyAct.this, R.string.Unkown_Error);
                            }
                        } else {
                            ToastUtil.longToast(VerifyAct.this, R.string.Unkown_Error);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ToastUtil.longToast(VerifyAct.this, R.string.get_code_error);
                    }
                };

                if (from == FROM_FORGOT) {
                    // 重新发送验证码 ：重置密码
                    userRegisterService.getResetPasswordCode(phoneNum, callback);
                } else if (from == FROM_REGISTER) {
                    // 重新发送验证码 ：注册
                    userRegisterService.getCode(phoneNum, callback);
                }
            }
        }).create();
        forgotDialog.show();
    }

    private void cancelAuth() {
        View view = getLayoutInflater().inflate(R.layout.dialog_sms_reset, null);
        TextView pn = (TextView) view.findViewById(R.id.reset_phone_num);
        TextView rt = (TextView) view.findViewById(R.id.reset_title);
        FontUtil.setRegulartypeFace(this, pn);
        FontUtil.setRegulartypeFace(this, rt);
        rt.setText(R.string.verification_code_has_delay_be_sure_to_restart);
        // pn.setText(phoneNum);
        pn.setVisibility(View.GONE);
        new AlertDialog.Builder(this).setView(view).setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                openRegisterAct();
            }

        }).setNegativeButton(R.string.continue_to_wait, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
            openRegisterAct();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void openRegisterAct() {
        overridePendingTransition(R.anim.right_in, R.anim.stop);
        finish();
        // startActivity(intent);
    }

    /**
     * @param 设定文件
     * @return void 返回类型
     * @throws
     * @Title: popInputMethod
     * @Description: 自動彈出輸入法
     */
    public void popInputMethod() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mInputAuthCodeBox, 0);
            }
        }, 500);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        // 彈出輸入法
        popInputMethod();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (imm != null && mInputAuthCodeBox != null) {
            imm.hideSoftInputFromWindow(mInputAuthCodeBox.getWindowToken(), 0);
            // imm.hideSoftInputFromInputMethod(mEditTextContent.getWindowToken(),
            // 0);
        }
    }
}
