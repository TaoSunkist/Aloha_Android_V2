package com.wealoha.social.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.wealoha.social.R;
import com.wealoha.social.commons.GlobalConstants;

/**
 * 时间类型相关的列
 * 
 * @Description:
 * @author:zhangqian
 * @see:
 * @since:
 * @copyright © jrzj.com
 * @Date:2014-4-1
 */
public class TimeUtil {

	/**
	 * 日期+时间的string 型转为long型
	 * 
	 * @Description:
	 * @param dateTime
	 *            (型如 ： 2013-03-08 13:51:00)[日期和时间之间有空格]
	 * @return
	 * @see:
	 * @since:
	 * @author: zhangqian
	 * @date:2014-4-1
	 */

	public static long timeStringToLong(String dateTime) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt2;
		try {
			dt2 = GlobalConstants.DateFormatYMDHMS.parse(dateTime + ":00");
			return dt2.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 
	 * 将指定格式的时间转换成long型
	 * 
	 * @Description:
	 * @param dateTime
	 * @param format
	 * @return
	 * @see:
	 * @since:
	 * @author:
	 * @date:2014-4-1
	 */

	public static long timeStringToLong(String dateTime, String format) {
		if (dateTime != null && dateTime.length() > 0) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				Date date = sdf.parse(dateTime);
				return date.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	/**
	 * 时间long转字符串 (24小时制)
	 * 
	 * @Description:
	 * @param millis
	 * @return
	 * @see:
	 * @since:
	 * @author: zhangqian
	 * @date:2014-4-1
	 */
	public static String timeLongToString(long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		return GlobalConstants.DateFormatHMS.format(cal.getTime());
	}

	/**
	 * 客户端时间戳转换 微秒 和秒
	 * 
	 * @Description:
	 * @param times
	 * @return
	 * @see:
	 * @since:
	 * @author: zhangqian
	 * @date:2014-5-15
	 */
	public static String serverToClientTime(String times) {
		try {
			if (times != null && times.length() == 10) {
				return times + "000";
			} else {
				return times;
			}
		} catch (Exception e) {
			new RuntimeException(e.toString());
			return times;
		}
	}

	public static String clientTimeToServer(String times) {
		try {
			if (times != null && times.length() == 13) {
				return times.substring(0, times.length() - 3);
			} else if (times != null && times.length() == 10) {
				return times;
			} else {
				return times;
			}
		} catch (Exception e) {
			new RuntimeException(e.toString());
			return times;
		}
	}

	/**
	 * @Description: 获取时间戳
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-6-16
	 */
	public static String getMySqlCurrentTimeStamp() {
		Date date = new Date();
		long time = date.getTime();
		String dateline = time + "";
		dateline = dateline.substring(0, 10);
		return dateline;
	}

	/**
	 * @Description: 时间前后的比较
	 * @param date1
	 * @param date2
	 * @return
	 * @see:
	 * @since:
	 * @description 传入的时间格式为：yyyy-MM-dd hh:mm
	 * @author: sunkist
	 * @date:2014-8-20
	 */
	public static int compareDate(String date1, String date2) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() > dt2.getTime()) {
				System.out.println("dt1 在dt2前");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				System.out.println("");
				return -1;
			} else {
				return 0;// 相等
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	/**
	 * @Title: getDistanceTimeForApp
	 * @Description: 计算时间差，返回 如 x天前、x小时前、x分钟前、x秒前
	 * @param @param time1 未来的时间
	 * @param @param time2 过去的时间
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String getDistanceTimeForApp(Context aContext, long time1, long time2) {
		long week;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		long diff;

		diff = time1 - time2;

		if (diff <= 0) {
			return aContext.getString(R.string.dynamic_content_rightnow);
		}
		day = diff / (24 * 60 * 60 * 1000);
		week = diff / (TimeUnit.DAYS.toMillis(7));
		hour = (diff / (60 * 60 * 1000) - day * 24);
		min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
		sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		String time = "";
		if (week != 0) {
			return StringUtil.toString(week) + aContext.getString(R.string.time_afterWeek);
		}
		if (day != 0) {
			return time += StringUtil.toString(day) + aContext.getString(R.string.time_afterDay);
		}
		if (hour != 0) {
			return time += StringUtil.toString(hour) + aContext.getString(R.string.time_afterhours);
		}
		if (min != 0) {
			return time += StringUtil.toString(min) + aContext.getString(R.string.time_afterminutes);
		}
		if (sec != 0) {
			return time += StringUtil.toString(sec) + aContext.getString(R.string.time_afterSeconds);
		}
		return aContext.getString(R.string.dynamic_content_rightnow);
	}

	public static String howLong(Context aContext, long time2) {
		return getDistanceTimeForApp(aContext, new Date().getTime(), time2);
	}

	/**
	 * @Description: 比较两个时间的相差天、小时、分
	 * @param str1
	 * @param str2
	 * @return
	 * @see:
	 * @since:
	 * @description
	 * @author: sunkist
	 * @date:2014-8-20
	 */
	@Deprecated
	public static String getDistanceTime(Context ctx,String str1, String str2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date one;
		Date two;
		long day = 0;
		long hour = 0;
		long min = 0;
		long sec = 0;
		try {
			one = df.parse(str1);
			two = df.parse(str2);
			long time1 = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (time1 < time2) {
				diff = time2 - time1;
			} else {
				diff = time1 - time2;
			}
			day = diff / (24 * 60 * 60 * 1000);
			hour = (diff / (60 * 60 * 1000) - day * 24);
			min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
			sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return day + "天" + hour + "小时" + min + "分" + sec + "秒";
	}

	public static String TimeStamp2String(String beginDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sd = sdf.format(new Date(Long.parseLong(beginDate)));
		return sd;
	}

	public static String GetTimeInfo(Context aContext, long aTimeStamp) {
		String ret = null;
		long currenttime = getCurrentTime();
		long offset = currenttime - aTimeStamp;
		offset /= 1000;
		if (aTimeStamp == 0) {
			ret = "";
		} else {
			if (offset > 0) {
				if (offset / 60 == 0) {
					ret = offset % 60 + aContext.getString(R.string.time_afterSeconds);
				} else if (offset / 60 / 60 == 0) {
					ret = offset / 60 % 60 + aContext.getString(R.string.time_afterminutes);
				} else if (offset / 60 / 60 / 24 == 0) {
					ret = offset / 60 / 60 % 24 + aContext.getString(R.string.time_afterhours);
				} else {
					ret = getStrdateyearmonthday(aTimeStamp);
				}
			} else {
				ret = aContext.getString(R.string.dynamic_content_rightnow);
			}
		}
		return ret + " ";
	}

	public static String getStrdateyearmonthday(long aTimeStamp) {
		// TimeZone DefaultTimeZone = TimeZone.getTimeZone("GMT+0:00");
		String currentyear = getYear(getCurrentTime());
		String getyear = getYear(aTimeStamp);
		String timeformat = "MM/dd HH:mm";

		if (!getyear.equals(currentyear)) {
			timeformat = "yyyy/MM/dd HH:mm";
		}
		String ret = null;
		SimpleDateFormat fm1 = new SimpleDateFormat(timeformat);
		// fm1.setTimeZone(DefaultTimeZone);
		ret = fm1.format(aTimeStamp);
		fm1 = null;
		return ret;
	}

	public static long getCurrentTime() {
		Calendar c = Calendar.getInstance();
		long ret = c.getTimeInMillis();
		c = null;
		return ret;
	}

	public static String getYear(long aTimeStamp) {
		String ret = null;
		SimpleDateFormat fm1 = new SimpleDateFormat("yyyy");
		ret = fm1.format(aTimeStamp);
		fm1 = null;
		return ret;
	}
}
