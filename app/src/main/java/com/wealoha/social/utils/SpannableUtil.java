package com.wealoha.social.utils;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import com.wealoha.social.widget.NoUnderlineClickableSpan;

public class SpannableUtil {

	/**
	 * 返回部分字段可点击的字符串
	 * 
	 * @param content
	 *            完整的内容字符串
	 * @param spans
	 *            可点击的字符串数组
	 * @param clickListener
	 *            与spans中字符串对应的点击监听事件
	 * @return SpannableStringBuilder
	 */
	public static SpannableStringBuilder buidlerClickableStr(String content, String[] spans, int color, NoUnderlineClickableSpan... clickListener) {
		XL.i("SPANNABLE_COLOR_","content：" + content);
		XL.i("SPANNABLE_COLOR_","spans：" + spans);
		XL.i("SPANNABLE_COLOR_","spanslength：" + spans.length);
		if(spans == null || spans.length == 0 || TextUtils.isEmpty(content)){
			return null;
		}
		
		XL.i("SPANNABLE_COLOR_","---------------");
		SpannableStringBuilder contentBuilder = new SpannableStringBuilder(content);
		for (int i = 0; i < spans.length; i++) {
			if(TextUtils.isEmpty(spans[i])){
				continue;
			}
			int start = content.indexOf(spans[i]);
			int end = start + spans[i].length();
			
			ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
			if (start != -1) {
				SpannableStringBuilder ssb = buidlerClickableStr(spans[i], clickListener[i], colorSpan);
				contentBuilder.replace(start, end, ssb);
			}
		}
		
		return contentBuilder;
	}

	public static SpannableStringBuilder buidlerClickableStr(String spanStr, NoUnderlineClickableSpan clickSpan, ForegroundColorSpan colorBg) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
		ssb.setSpan(clickSpan, 0, spanStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		
		if (colorBg != null) {
			ssb.setSpan(colorBg, 0, spanStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		return ssb;
	}

	/**
	 * @author: sunkist
	 * @description:
	 * @param content
	 * @param bcs
	 * @param spans
	 * @param clickListener
	 * @return
	 * @date:2015年8月5日
	 */
	public static SpannableStringBuilder buidlerClickableStr(String content,//
			BackgroundColorSpan bcs,//
			ForegroundColorSpan fcs, //
			String[] spans, //
			NoUnderlineClickableSpan... clickListener) {
		// if (spans.length != clickListener.length) {
		// throw new Exception("spans length != clickListener length");
		// }
		SpannableStringBuilder contentBuilder = new SpannableStringBuilder(content);
		for (int i = 0; i < spans.length; i++) {
			int start = content.indexOf(spans[i]);
			int end = start + spans[i].length();

			if (start != -1) {
				SpannableStringBuilder ssb = buidlerClickableStr(spans[i], bcs, fcs, clickListener[i]);
				contentBuilder.replace(start, end, ssb);
			}
		}
		return contentBuilder;
	}

	public static SpannableStringBuilder buidlerClickableStr(String spanStr, BackgroundColorSpan bcs,//
			ForegroundColorSpan fcs, NoUnderlineClickableSpan clickSpan) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(spanStr);
		ssb.setSpan(clickSpan, 0, spanStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		if (fcs != null) {
			ssb.setSpan(fcs, 0, spanStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		if (bcs != null) {
			ssb.setSpan(bcs, 0, spanStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		}
		return ssb;
	}
}
