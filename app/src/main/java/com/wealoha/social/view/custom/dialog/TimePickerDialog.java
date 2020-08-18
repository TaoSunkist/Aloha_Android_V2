package com.wealoha.social.view.custom.dialog;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.wealoha.social.R;
import com.wealoha.social.utils.TimeUtil;
import com.wealoha.social.utils.XL;

@SuppressLint("ValidFragment")
public class TimePickerDialog extends BasePickerDialog {

	public Button confirmation;

	public DatePicker datePicker;

	private Button canceled;

	private View view;
	private long maxDate = 0;
	private long minDate = 0;;
	private int currentDay = 1;
	private int currentMonth = 0;
	private int currentYear = 1970;

	public TimePickerDialog() {
	}

	/**
	 * Description: 最大日期和最小日期
	 * 
	 * @param maxDate
	 * @param minDate
	 */
	public TimePickerDialog(String maxDate, String minDate) {
		this.maxDate = TimeUtil.timeStringToLong(maxDate, "yyyy-MM-dd");
		this.minDate = TimeUtil.timeStringToLong(minDate, "yyyy-MM-dd");
	}

	/**
	 * Description: 最大日期、最小日期 还有当前年月日
	 * 
	 * @param maxDate
	 * @param minDate
	 */
	public TimePickerDialog(String maxDate, String minDate, int currentYear, int currentMonth, int currentDay) {
		this(maxDate, minDate);
		this.currentYear = currentYear;
		this.currentDay = currentDay;
		this.currentMonth = currentMonth;
	}

	/**
	 * Description: 当前年月日
	 * 
	 * @param maxDate
	 * @param minDate
	 */
	public TimePickerDialog(String date) {
		String[] dates = date.split("-");
		this.currentYear = Integer.parseInt(dates[0]);
		this.currentMonth = Integer.parseInt(dates[1]) - 1;
		this.currentDay = Integer.parseInt(dates[2]);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.dialog_time_picker, container, false);
		confirmation = (Button) view.findViewById(R.id.confirmation);
		canceled = (Button) view.findViewById(R.id.cancle);
		datePicker = (DatePicker) view.findViewById(R.id.dialog_picker);
		try {
			setDatePickerDividerColor(datePicker);
		} catch (ClassCastException e) {
			XL.d(getTag(), e.getMessage());
		}
		if (this.maxDate <= 0) {
			this.maxDate = TimeUtil.timeStringToLong("2005-01-01", "yyyy-MM-dd");
		}
		if (this.minDate <= 0) {
			this.minDate = TimeUtil.timeStringToLong("1950-01-01", "yyyy-MM-dd");
		}

		datePicker.setMaxDate(maxDate);
		datePicker.setMinDate(minDate);
		datePicker.init(currentYear, currentMonth, currentDay, null);

		confirmation = (Button) view.findViewById(R.id.confirmation);
		confirmation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 返回年龄
				// rsl.returnSomething(cal.get(Calendar.YEAR) -
				// datePicker.getYear() + "");
				rsl.returnSomething(datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth());
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

	/**
	 * @Description: 更改datepicker的分割线的颜色
	 * @param datePicker
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-12-13
	 */
	private void setDatePickerDividerColor(DatePicker datePicker) {
		// Divider changing:

		// 获取 mSpinners
		LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

		// 获取 NumberPicker
		LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
		for (int i = 0; i < mSpinners.getChildCount(); i++) {
			NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);
			Field[] pickerFields = NumberPicker.class.getDeclaredFields();

			for (int j = 0; j < picker.getChildCount(); j++) {
				// picker.getChildAt(j).setEnabled(false);这样做是无效的
				// picker.getChildAt(j).setFocusable(false);
				// View v = picker.getChildAt(j);
				if (picker.getChildAt(j) instanceof EditText) {
					EditText et = (EditText) picker.getChildAt(j);
					et.setFocusable(false);
					et.setFocusableInTouchMode(false);
				}
				// Log.i("PICKER", "picker name:" +
				// picker.getChildAt(j).setEnabled(false););
			}
			for (Field pf : pickerFields) {
				if (pf.getName().equals("mSelectionDivider")) {
					pf.setAccessible(true);
					try {
						pf.set(picker, new ColorDrawable(getResources().getColor(R.color.numpicker_gray_line)));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	/**
	 * @Title: setCurrentDate
	 * @Description: 格式 yyyy-MM-dd
	 * @param @param date 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void setCurrentDate(String date) {
		if (TextUtils.isEmpty(date)) {
			return;
		}
		String[] dates = date.split("-");
		this.currentYear = Integer.parseInt(dates[0]);
		this.currentMonth = Integer.parseInt(dates[1]) - 1;
		this.currentDay = Integer.parseInt(dates[2]);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
		setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
		return dialog;
	}
}
