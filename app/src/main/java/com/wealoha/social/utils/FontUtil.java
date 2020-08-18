package com.wealoha.social.utils;

import javax.inject.Inject;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wealoha.social.inject.Injector;

public class FontUtil {

	@Inject
	Context context;

	private String configuration;
	private boolean flag;
	private Typeface typeface;

	public FontUtil(Context context) {
		Injector.inject(this);
		configuration = context.getResources().getConfiguration().locale.getCountry();
		if ("CN".equals(configuration) || "TW".equals(configuration)) {
			flag = false;
		}
	}

	public static enum Font {
		ENCODESANSCOMPRESSED_100_THIN("EncodeSansCompressed-100-Thin.ttf"), //
		ENCODESANSCOMPRESSED_200_EXTRALIGHT("EncodeSansCompressed-200-ExtraLight.ttf"), //
		ENCODESANSCOMPRESSED_300_LIGHT("EncodeSansCompressed-300-Light.ttf"), //
		ENCODESANSCOMPRESSED_400_REGULAR("EncodeSansCompressed-400-Regular.ttf"), //
		ENCODESANSCOMPRESSED_500_MEDIUM("EncodeSansCompressed-500-Medium.ttf"), //
		ENCODESANSCOMPRESSED_600_SEMIBOLD("EncodeSansCompressed-600-SemiBold.ttf"), //
		ENCODESANSCOMPRESSED_700_BOLD("EncodeSansCompressed-700-Bold.ttf"), //
		ENCODESANSCOMPRESSED_800_EXTRABOLD("EncodeSansCompressed-800-ExtraBold.ttf"), //
		ENCODESANSCOMPRESSED_900_BLACK("EncodeSansCompressed_900_Black.ttf");//

		private String mFont;

		private Font(String font) {
			mFont = font;
		}

		public String getFont() {
			return mFont;
		}
	}

	public void changeFonts(ViewGroup root, Font font) {

		Log.i("FONT_UTIL", "configuration:" + configuration);
		if (flag) {
			return;
		}
		typeface = Typeface.createFromAsset(context.getAssets(), font.getFont());
		changeFonts(root, typeface);

		Log.i("FONT_UTIL", "for:end");

	}

	private void changeFonts(ViewGroup root, Typeface typeface) {
		for (int i = 0; i < root.getChildCount(); i++) {
			View v = root.getChildAt(i);
			if (v instanceof ViewGroup) {
				// 递归调用
				changeFonts((ViewGroup) v, typeface);
			} else {
				changeViewFont(v, typeface);
			}
		}
	}

	private void changeViewFont(View v, Typeface typeface) {
		if (v instanceof TextView) {
			((TextView) v).setTypeface(typeface);
		} else if (v instanceof Button) {
			((Button) v).setTypeface(typeface);
		} else if (v instanceof EditText) {
			((EditText) v).setTypeface(typeface);
		}
	}

	public void changeViewFont(View v, Font font) {
		typeface = Typeface.createFromAsset(context.getAssets(), font.getFont());
		if (v instanceof TextView) {
			((TextView) v).setTypeface(typeface);
		} else if (v instanceof Button) {
			((Button) v).setTypeface(typeface);
		} else if (v instanceof EditText) {
			((EditText) v).setTypeface(typeface);
		}

	}

	private static Typeface mSemiBold;

	public static void setSemiBoldTypeFace(Context context, View v) {
		if (mSemiBold == null) {
			mSemiBold = Typeface.createFromAsset(context.getAssets(), Font.ENCODESANSCOMPRESSED_600_SEMIBOLD.getFont());
		}
		changeTypeFace(v, mSemiBold);
	}

	public static void setSemiBoldTypeFace(Context context, ViewGroup root) {
		if (mSemiBold == null) {
			mSemiBold = Typeface.createFromAsset(context.getAssets(), Font.ENCODESANSCOMPRESSED_600_SEMIBOLD.getFont());
		}

		for (int i = 0; i < root.getChildCount(); i++) {
			View v = root.getChildAt(i);
			if (v instanceof ViewGroup) {
				// 递归调用
				setSemiBoldTypeFace(context, root);
			} else {
				changeTypeFace(v, mSemiBold);
			}
		}
	}

	private static Typeface mRegular;

	public static void setRegulartypeFace(Context context, View v) {
		if (mRegular == null) {
			mRegular = Typeface.createFromAsset(context.getAssets(), Font.ENCODESANSCOMPRESSED_400_REGULAR.getFont());
		}
		changeTypeFace(v, mRegular);
	}
	public static void setRegulartypeFace(Context context, ViewGroup root) {
		if (mRegular == null) {
			mRegular = Typeface.createFromAsset(context.getAssets(), Font.ENCODESANSCOMPRESSED_400_REGULAR.getFont());
		}

		for (int i = 0; i < root.getChildCount(); i++) {
			View v = root.getChildAt(i);
			if (v instanceof ViewGroup) {
				// 递归调用
				setRegulartypeFace(context, root);
			} else {
				changeTypeFace(v, mRegular);
			}
		}
	}
	
	private static Typeface medium;
	
	public static void setMediumFace(Context context, View v) {
		if (medium == null) {
			medium = Typeface.createFromAsset(context.getAssets(), Font.ENCODESANSCOMPRESSED_500_MEDIUM.getFont());
		}
		changeTypeFace(v, medium);
	}
	public static void setMediumFace(Context context, ViewGroup root) {
		if (medium == null) {
			medium = Typeface.createFromAsset(context.getAssets(), Font.ENCODESANSCOMPRESSED_500_MEDIUM.getFont());
		}

		for (int i = 0; i < root.getChildCount(); i++) {
			View v = root.getChildAt(i);
			if (v instanceof ViewGroup) {
				// 递归调用
				setRegulartypeFace(context, root);
			} else {
				changeTypeFace(v, medium);
			}
		}
	}

	public static void changeTypeFace(View v, Typeface face) {
		if (v instanceof TextView) {
			((TextView) v).setTypeface(face);
		} else if (v instanceof Button) {
			((Button) v).setTypeface(face);
		} else if (v instanceof EditText) {
			((EditText) v).setTypeface(face);
		}
	}

}
