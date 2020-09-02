package com.wealoha.social.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;
import com.wealoha.social.view.custom.MyNumberPicker;

public class PhoneAreaFragment extends DialogFragment {

	private String[] phoneArea;
	private View view;

	@ViewInject(R.id.confirmation)
	public Button confirmation;

	@ViewInject(R.id.phoneareaselect)
	public MyNumberPicker np;

	@ViewInject(R.id.canceled)
	private Button canceled;

	private int mPhoneAreaIndex = 0;

	public int[] mPhoneAreaRes = new int[] {//
	R.string.code_china_86,//
	R.string.code_taiwan_886,//
	R.string.code_macao_853,//
	R.string.code_hongkong_852,//
	R.string.code_US_1,//
	R.string.code_singapore_65,//
	R.string.code_malaysia_60,//
	R.string.code_thailand_66,//
	R.string.code_australia_61,//
	R.string.code_new_zealand_64,//
	R.string.code_italy_39,//
	R.string.code_english_44,//
	R.string.code_japan_81,//
	R.string.code_germany_49,//
	R.string.code_french_33 };

	// public PhoneAreaFragment(Context context) {
	// this.context = context;
	// }
	public interface ChangePhoneAreaCallback {

		public void changeAreaCallback(int postion);
	}

	private ChangePhoneAreaCallback changeCallback;

	public PhoneAreaFragment(int position) {
		Log.i("AREA_POSITINO", "position:" + position);
		if (position > -1) {
			mPhoneAreaIndex = position;
		}

	}

	public PhoneAreaFragment(int position, ChangePhoneAreaCallback callback) {
		this(position);
		changeCallback = callback;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frag_phonearea, container, false);
		// ViewUtils.inject(view);
		phoneArea = new String[] {//
		getResources().getString(R.string.china_86),//
		getResources().getString(R.string.taiwan_886),//
		getResources().getString(R.string.macao_853),//
		getResources().getString(R.string.hongkong_852),//
		getResources().getString(R.string.US_1),//
		getResources().getString(R.string.singapore_65),//
		getResources().getString(R.string.malaysia_60),//
		getResources().getString(R.string.thailand_66),//
		getResources().getString(R.string.australia_61),//
		getResources().getString(R.string.new_zealand_64),//
		getResources().getString(R.string.italy_39),//
		getResources().getString(R.string.english_44),//
		getResources().getString(R.string.japan_81),//
		getResources().getString(R.string.germany_49),//
		getResources().getString(R.string.french_33),//
		getResources().getString(R.string.austria_43),//
		getResources().getString(R.string.southkorea_82),//
		getResources().getString(R.string.canada_1) };

		np = (MyNumberPicker) view.findViewById(R.id.phoneareaselect);

		np.setDisplayedValues(phoneArea);
		np.setMinValue(0);
		np.setMaxValue(phoneArea.length - 1);

		if (mPhoneAreaIndex >= 0) {
			np.setValue(mPhoneAreaIndex);
		}
		np.setOnValueChangedListener(new com.wealoha.social.view.custom.MyNumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(MyNumberPicker picker, int oldVal, int newVal) {
				mPhoneAreaIndex = newVal;
			}
		});
		confirmation = (Button) view.findViewById(R.id.confirmation);
		confirmation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (changeCallback == null) {
					((BaseFragAct) getActivity()).changePhoneArea(mPhoneAreaIndex);
				} else {
					changeCallback.changeAreaCallback(mPhoneAreaIndex);
				}
				getDialog().dismiss();
			}
		});
		canceled = (Button) view.findViewById(R.id.canceled);
		canceled.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});
		return view;
	}

	public void setCurrentArea(int position) {
		if (phoneArea != null && phoneArea.length > 0 && position > 0 && position < phoneArea.length) {
			Log.i("AREA_POSITINO", "position:" + position);
			mPhoneAreaIndex = position;
		}
	}

	public String getCurrentArea(int position) {
		return phoneArea[position];
	}

	/***
	 * 返回（）
	 * 
	 * @return
	 * @return String
	 */
	public String getCurrentArea() {
		if (mPhoneAreaIndex >= 0 && mPhoneAreaIndex < phoneArea.length) {
			return phoneArea[mPhoneAreaIndex];
		}
		return phoneArea[0];
	}

	/***
	 * 返回 +86 格式的区号
	 * 
	 * @return
	 * @return String
	 */
	public String getCurrentAreaCode(Context context) {
		return getCurrentAreaCode(context, mPhoneAreaIndex);
	}

	/***
	 * 返回 +86 格式的区号
	 * 
	 * @return
	 * @return String
	 */
	public String getCurrentAreaCode(Context context, int position) {
		int res = 0;
		if (mPhoneAreaIndex >= 0 && position < phoneArea.length) {
			res = position;
		}
		return context.getString(mPhoneAreaRes[res]);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);

		return dialog;
	}

}
