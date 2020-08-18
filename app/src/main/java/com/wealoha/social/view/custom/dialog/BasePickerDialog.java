package com.wealoha.social.view.custom.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.wealoha.social.R;
import com.wealoha.social.view.custom.MyNumberPicker;
import com.wealoha.social.view.custom.MyNumberPicker.OnValueChangeListener;

@SuppressLint("ValidFragment")
public class BasePickerDialog extends DialogFragment {

	private String[] strs;
	private View view;
	private int currentValue = 0;
	private int start = 0;
	private int end = 0;

	@ViewInject(R.id.confirmation)
	public Button confirmation;

	@ViewInject(R.id.dialog_picker)
	public MyNumberPicker np;

	@ViewInject(R.id.canceled)
	private Button canceled;

	private int strsIndex;

	ReturnSomethingListener rsl;

	public BasePickerDialog() {
	}

	@SuppressLint("ValidFragment")
	public BasePickerDialog(Context context, String[] strs) {
		this.strs = strs;
	}

	/**
	 * @Title: saveLogin
	 * @Description: 生成x~y之间的number picker
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public BasePickerDialog(Context context, int start, int end) {
		this.start = start;
		this.end = end;
		int count = end - start + 1;
		this.strs = new String[count];
		for (int i = 0; i < count; i++) {
			strs[i] = start + "";
			start++;
		}
	}

	/**
	 * 设定当前值
	 * 
	 * @param value
	 */
	public void setCurrentValue(int value) {

		if (value == 0 || value - start < 0 || value - end > 0) {
			return;
		}
		currentValue = value - start;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.dialog_picker, container, false);
		// ViewUtils.inject(view);
		np = (MyNumberPicker) view.findViewById(R.id.dialog_picker);
		np.setDisplayedValues(strs);
		np.setMinValue(0);
		np.setDividerPadding(100);
		np.setShowDividers(0);
		np.setMaxValue(strs.length - 1);
		np.setClickable(false);
		np.setWrapSelectorWheel(false);

		if (currentValue != 0) {
			np.setValue(currentValue);
			strsIndex = currentValue;
		}
		np.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(MyNumberPicker picker, int oldVal, int newVal) {
				strsIndex = newVal;
			}
		});
		confirmation = (Button) view.findViewById(R.id.confirmation);
		confirmation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rsl.returnSomething(strs[strsIndex]);
				getDialog().dismiss();
			}
		});
		canceled = (Button) view.findViewById(R.id.canceled);
		canceled.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDialog().dismiss();
			}
		});
		return view;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
		return dialog;
	}

	public void setOnReturnSomethingListener(ReturnSomethingListener rsl) {
		this.rsl = rsl;
	}

	public interface ReturnSomethingListener {

		public void returnSomething(String result);
	}

}
