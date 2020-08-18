package com.wealoha.social.view.custom.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wealoha.social.BaseFragAct;
import com.wealoha.social.R;

public class LogoutAndClearActDialogBuilder extends AlertDialog.Builder {

	private TextView contentView;
	private TextView titleView;

	public LogoutAndClearActDialogBuilder(Context context, String str) {
		super(context);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_with_content, null, false);
		if (!(context instanceof BaseFragAct)) {
			return;
		}
		final BaseFragAct baseAct = (BaseFragAct) context;
		contentView = (TextView) view.findViewById(R.id.content);
		contentView.setText(str);
		setView(view);
		setPositiveButton(R.string.confirm, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				baseAct.doLogout(baseAct);
				baseAct.closeAllAct(baseAct);
			}
		});
		setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	}

	public LogoutAndClearActDialogBuilder(Context context, String title, String content) {
		super(context);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_with_content, null, false);
		if (!(context instanceof BaseFragAct)) {
			return;
		}
		final BaseFragAct baseAct = (BaseFragAct) context;
		contentView = (TextView) view.findViewById(R.id.content);
		titleView = (TextView) view.findViewById(R.id.title);
		titleView.setVisibility(View.VISIBLE);
		contentView.setText(content);
		titleView.setText(title);
		setView(view);
		setPositiveButton(R.string.confirm, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				baseAct.doLogout(baseAct);
				baseAct.closeAllAct(baseAct);
			}
		});
		setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	}
	
	public static void firstEnterFeed2FragDialogBuilder(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.is_first_enter_160v, null, false);
		final BaseFragAct baseAct = (BaseFragAct) context;
		Button iKnow = (Button) view.findViewById(R.id.content);
		iKnow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		
	}
}
