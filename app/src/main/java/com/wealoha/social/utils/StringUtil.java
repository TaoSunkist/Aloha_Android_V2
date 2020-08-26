package com.wealoha.social.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.wealoha.social.R;
import com.wealoha.social.beans.Post;
import com.wealoha.social.beans.User;
import com.wealoha.social.beans.User;
import com.wealoha.social.push.notification.InboxMessageNewNotification;

/**
 * String 工具类
 *
 * @Description:
 * @author:zhangqian
 * @see:
 * @since:
 * @copyright © jrzj.com
 * @Date:2014-3-22
 */
@SuppressLint("SimpleDateFormat")
public class StringUtil {

    @Inject
    static ContextUtil mContextUtil;

    // 应用的语言设置
    public static final String APP_LANGUAGE_AUTO = "auto";
    public static final String APP_LANGUAGE_CN = "zh_CN";
    public static final String APP_LANGUAGE_TW = "zh_TW";
    public static final String APP_LANGUAGE_US = "en_US";
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    // 编码
    private static final String ECODING = "UTF-8";
    // 获取img标签正则
    private static final String IMGURL_REG = "<img.*src=(.*?)[^>]*?>";
    // 获取src路径的正则
    private static final String IMGSRC_REG = "http:\"?(.*?)(\"|>|\\s+)";

    private static final String TAG = "StringUtil";

    public static String abbreviate(String string, int length) {
        if (string == null) {
            return string;
        }
        if (string.length() <= length) {
            return string;
        }
        return string.substring(0, length) + "...";
    }

    /**
     * 此方法处理传入的字符串，如果传入字符串需要按指定长度截取则返回一个以"..."结尾的字符串。 <span
     * style="color:red">截斷字符串盡量用 {@link #abbreviate(String, int)}
     * 這個方法非常慢</span>
     *
     * @param str    需要处理的字符串
     * @param length 指定长度 注：按照字符串中2个字母的长度为1，一个汉字的长度为1的规律，指定一个字符串的长度；
     * @return
     */
    public static String processString(String str, int length, Context context) {
        if (str == null) {
            return "";
        }
        long startTime = System.currentTimeMillis();
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager systemService = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        systemService.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        // Debug.d(TAG, "screenWidth = "+screenWidth );
        int screenHeight = dm.heightPixels;
        // Debug.d(TAG, "screenHeight = "+screenHeight );
        int gapPixels = screenWidth > screenHeight ? screenWidth - screenHeight : screenHeight - screenWidth;
        // Debug.d(TAG, "gapPixels = "+gapPixels );
        int gapLength = gapPixels / 13 + 1;
        // Debug.d(TAG, "gapLength = "+gapLength );

        String temp = str.trim();

        int cnt = 0, ent = 0, resultCount = 0, charAt = 0;
        // Debug.d(TAG, "length = "+length );
        if (screenWidth < screenHeight) {
            // Debug.d(TAG, "length = "+length );
            if (getStringLength(temp) > length) {
                for (int i = 0; i < temp.length(); i++) {
                    if (resultCount == length)
                        return temp.substring(0, charAt) + "...";
                    if (isChinese(temp.charAt(i))) {
                        cnt++;
                    } else {
                        ent++;
                    }
                    resultCount = cnt + ent / 2;
                    charAt = i;
                }
            } else {
                return temp;
            }

        } else {
            length += gapLength / 2;
            // Debug.d(TAG, "length = "+length );
            if (getStringLength(temp) > length) {
                for (int i = 0; i < temp.length(); i++) {
                    if (resultCount == length) {
                        // Debug.d(TAG, "result = "+temp.substring(0,
                        // charAt)+"..." );
                        return temp.substring(0, charAt) + "...";
                    }
                    if (isChinese(temp.charAt(i))) {
                        cnt++;
                    } else {
                        ent++;
                    }
                    resultCount = cnt + ent / 2;
                    charAt = i;
                }
            } else {
                return temp;
            }
        }
        // Debug.d(TAG, "length = "+length );
        long endTime = System.currentTimeMillis();
        XL.i(TAG, "processString cost " + (endTime - startTime) + "ms");

        return "";
    }

    /**
     * 判断一个字符是否是汉字
     *
     * @return
     */
    private static boolean isChinese(Character c) {
        // return ((int)a>=19968 && (int)a <=171941);
        return c.toString().length() != c.toString().getBytes().length;
    }

    /**
     * 按照字符串中2个字母的长度为1，一个汉字的长度为1的规律,获取一个字符串的长度
     *
     * @param str
     * @return
     */
    private static int getStringLength(String str) {
        int cnt = 0, ent = 0;
        for (int i = 0; i < str.length(); i++) {
            if (isChinese(str.charAt(i))) {
                cnt++;
            } else {
                ent++;
            }
        }
        return cnt + ent / 2;
    }

    /**
     * 判断当前时间是否在闲时内
     *
     * @param startTime 用户设置的闲时开始时间
     * @param endTime   用户设置的显示结束时间
     * @return true 闲时屏蔽开启，并且在当前在闲时需要屏蔽震动、铃声； false 闲时屏蔽关闭或者当前不在闲时；
     */
    public static boolean isInFreeTime(String startTime, String endTime, boolean isFreeTime) {
        boolean isIn = false;
        if (isFreeTime) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            String hourString = hour + "";
            String minString = min + "";
            if (hourString.length() == 1) {
                hourString = "0" + hourString;
            }
            if (minString.length() == 1) {
                minString = "0" + minString;
            }
            String current = hourString + ":" + minString;
            if (startTime.compareTo(endTime) > 0) {
                if (!(current.compareTo(endTime) > 0 && current.compareTo(startTime) < 0)) {
                    isIn = true;
                }
            } else {
                if (current.compareTo(startTime) > 0 && current.compareTo(endTime) < 0) {
                    isIn = true;
                }
            }
        }
        return isIn;
    }

    /**
     * @return boolean
     * @throws
     * @Title: noOneEmpty
     * @Description: 描述 判断一批字符串是否都是非空的
     */
    public static boolean noOneEmpty(Object... strArray) {
        for (Object s : strArray) {
            if (s instanceof String) {
                if (!isNotEmpty((String) s))
                    return false;
            } else {
                if (s == null)
                    return false;
            }
        }
        return true;

    }

    public static boolean isNotEmpty(String s) {
        return (s != null && s.trim().length() > 0);
    }

    /**
     * 去掉字符串中的换行；用于邮件列表中预览的显示
     *
     * @param str
     * @return
     * @Description:
     * @see:
     * @since:
     */
    public static String deleteEmptyLine(String str) {
        String dest = str;
        if (str != null) {
            Pattern p = Pattern.compile("\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * tab 换为一个空格，多个空格合并；用于邮件列表中预览的显示
     *
     * @param str
     * @return
     * @Description:
     * @see:
     * @since:
     */
    public static String combineBlank(String str) {
        String dest = str;
        if (str != null) {
            Pattern p = Pattern.compile("\t");
            Matcher m = p.matcher(str);
            dest = (m.replaceAll(" ")).trim().replaceAll(" +", " ");
        } else {
            dest = "";
        }
        return dest;
    }

    /**
     * 把一个输入流里面的内容 转化成文本 字符串
     *
     * @param is
     * @return
     */
    public static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            is.close();
            baos.close();
            byte[] result = baos.toByteArray();// 服务器端返回的0011 gb2312
            return new String(result, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 截取图片名称
     *
     * @param str
     * @return
     * @Description:
     * @see:
     * @since:
     * @author: zhangqian
     * @date:2014-3-19
     */
    public static String subPicName(String str) {
        if (str != null && !"".equals(str) && str.length() > 1) {
            return str.substring(str.lastIndexOf("/") + 1);
        } else {
            return "";
        }
    }

    /**
     * @param bodyData
     * @return
     * @Description:
     * @see:
     * @since:
     * @author: zhangqian
     * @date:2014-3-25
     */
    public static String getHtmnlByBody(String[] bodyData) {

        StringBuffer sb = new StringBuffer();
        sb.append("<HTML><body style=\"width=100%;height=100%\">");
        for (int i = 0; i < bodyData.length; i++) {
            String str = bodyData[i];
            if (bodyData[i].startsWith("http") && (bodyData[i].endsWith(".jpg") || bodyData[i].endsWith(".png") || bodyData[i].endsWith(".gif") || bodyData[i].endsWith(".jpeg"))) {
                sb.append("<img src=\"file:///sdcard/tencent/MobileQQ/photo/" + subPicName(str) + "\">");
            } else {
                sb.append("<p style=\"font-size:50px;word-break:break-all\"><font size=4>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp" + str + "</font></p>");
            }
        }
        return sb.toString();
    }

    // public static String makeBodyToHTML(String bodyData) {
    //
    // StringBuffer sb = new StringBuffer();
    // if (bodyData.startsWith("http") && (bodyData.endsWith(".jpg") ||
    // bodyData.endsWith(".png") || bodyData.endsWith(".gif") ||
    // bodyData.endsWith(".jpeg"))) {
    // sb.append("<img style=\"max-width: 100%;-ms-border-radius: 4px;-o-border-radius: 4px;border-radius: 4px;\" src=\"file:///"
    // +
    // AssignController.getInstance().getImagePathWithNewsHTML5(AppApplication.getInstance())
    // + subPicName(bodyData) + "\">");
    // } else {
    // sb.append("<span style=\"font-size:50px;word-break:break-all,text-justify:inter-word,text-align:justify;text-justify:inter-ideograph;\"><font size=4>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
    // + bodyData + "</font></span></br></br>");
    // //
    // sb.append("<p style=\"font-size:50px;word-break:break-all\"><font size=4>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
    // // + bodyData + "</font></p>");
    // }
    // return sb.toString();
    // }
    //
    // public static String makeBodyByTitleToHTML(String bodyData) {
    //
    // StringBuffer sb = new StringBuffer();
    // if (bodyData.startsWith("http") && (bodyData.endsWith(".jpg") ||
    // bodyData.endsWith(".png") || bodyData.endsWith(".gif") ||
    // bodyData.endsWith(".jpeg"))) {
    // sb.append("<img style=\"max-width: 100%;-ms-border-radius: 4px;-o-border-radius: 4px;border-radius: 4px;\" src=\"file:///"
    // +
    // AssignController.getInstance().getImagePathWithNewsHTML5(AppApplication.getInstance())
    // + subPicName(bodyData) + "\">");
    // } else {
    // sb.append("<span style=\"font-size:50px;word-break:break-all,text-justify:inter-word,text-align:justify;text-justify:inter-ideograph;\"><font size=4>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
    // + bodyData + "</font></span></br></br>");
    // //
    // sb.append("<p style=\"font-size:50px;word-break:break-all\"><font size=4>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"
    // // + bodyData + "</font></p>");
    // }
    // return sb.toString();
    // }

    /**
     * @param content
     * @param ch
     * @return String[]
     * @Description: 切掉
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-3-31
     */
    static public String[] split(String content, String ch) {
        String[] str = null;
        if (!("".equals(content))) {
            str = content.split(ch);
            return str;
        }
        return null;
    }

    /**
     * 处理时间
     *
     * @param timeStr
     * @return
     * @Description:
     * @see:
     * @since:
     * @author: zhangqian
     * @date:2014-4-1
     */
    @Deprecated
    public static String getStandardDate(Context ctx, String timeStr) {

        StringBuffer sb = new StringBuffer();

        long t = Long.parseLong(timeStr);
        long time = System.currentTimeMillis() - (t);
        long mill = (long) Math.ceil(time / 1000);// 秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day - 1 > 0) {
            sb.append(day + "天");
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                sb.append("1天");
            } else {
                sb.append(hour + "小时");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute + "分钟");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill + "秒");
            }
        } else {
            sb.append("刚刚");
        }
        if (!sb.toString().equals("刚刚")) {
            sb.append("前");
        }
        return sb.toString();
    }

    @Deprecated
    public static String friendly_time(Context ctx, String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater.get().format(cal.getTime());
        String paramDate = dateFormater.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 7) {
            ftime = days + "天前";
        } else if (days >= 8) {
            ftime = dateFormaterByList.get().format(time);
            // ftime = dateFormater.get().format(time);
        }
        return ftime;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     * @Description:
     * @see:
     * @since:
     * @author: zhangqian
     * @date:2014-4-1
     */

    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    private final static ThreadLocal<SimpleDateFormat> dateFormaterByList = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd HH:mm");
            // return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static String subName(String picname) {
        int lastIndexOf = picname.lastIndexOf("/");
        return picname.substring(lastIndexOf + 1, picname.length() - 4);
    }

    /**
     * 获取html内容
     *
     * @param url
     * @return
     * @throws Exception
     * @Description:
     * @see:
     * @since:
     * @author: zhangqian
     * @date:2014-5-7
     */
    public static String getHTML(String url) throws Exception {
        URL uri = new URL(url);
        URLConnection connection = uri.openConnection();
        InputStream in = connection.getInputStream();
        byte[] buf = new byte[1024];
        StringBuffer sb = new StringBuffer();
        while (in.read(buf, 0, buf.length) > 0) {
            sb.append(new String(buf, ECODING));
        }
        in.close();
        return sb.toString();
    }

    /**
     * 获取ImageUrl地址
     *
     * @param HTML
     * @return
     * @Description:
     * @see:
     * @since:
     * @author: zhangqian
     * @date:2014-5-7
     */
    public static List<String> getImageUrl(String HTML) {
        Matcher matcher = Pattern.compile(IMGURL_REG).matcher(HTML);
        List<String> listImgUrl = new ArrayList<String>();
        while (matcher.find()) {
            listImgUrl.add(matcher.group());
        }
        return listImgUrl;
    }

    /**
     * 获取ImageSrc地址
     *
     * @param listImageUrl
     * @return
     * @Description:
     * @see:
     * @since:
     * @author: zhangqian
     * @date:2014-5-7
     */
    public static List<String> getImageSrc(List<String> listImageUrl) {
        List<String> listImgSrc = new ArrayList<String>();
        for (String image : listImageUrl) {
            Matcher matcher = Pattern.compile(IMGSRC_REG).matcher(image);
            while (matcher.find()) {
                listImgSrc.add(matcher.group().substring(0, matcher.group().length() - 1));
            }
        }
        return listImgSrc;
    }

    /**
     * @param target
     * @return
     * @Description:
     * @see:
     * @since:
     * @description 是否是纯数字
     * @author: sunkist
     * @date:2014-5-9
     */
    static public boolean IsNumber(String target) {
        // String regEx = "^[0-9]*$";
        // Pattern p = Pattern.compile(regEx);
        // Matcher m = p.matcher(target);
        try {
            Double.valueOf(target);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static public String[] strSplit(String content) {
        String[] contents = null;
        if (content == null) {
            return contents;
        }
        String[] oneStrings = content.toString().split("-");
        return oneStrings;
    }

    public static String get32RandomString() {

        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random r = new Random();
        int range = buffer.length();
        for (int i = 0; i < 32; i++) {
            sb.append(buffer.charAt(r.nextInt(range)));
        }
        return sb.toString();
    }

    /**
     * @param mobiles
     * @return
     * @Description: 手机号验证
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-6-9
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * @return
     * @Description: 手机号验证
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-6-9
     */
    // ^(\\d+)$
    // /^0?(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/
    public static boolean matchPhone(String text) {
        if (Pattern.compile("^(\\d+)$").matcher(text).matches()) {
            return true;
        }
        return false;
    }

    /**
     * @param code
     * @return
     * @Description:從用戶選擇的地區碼中提取實際的地區碼
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-27
     */
    public static String getCallingCode(String code) {
        Pattern patternCallingCode = Pattern.compile("\\((\\+\\d+)\\)");
        XL.d(TAG, "raw calling code: " + code);
        Matcher m = patternCallingCode.matcher(code);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * @return
     * @Description:從用戶選擇的地區碼中提取實際的地區碼
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-27
     */
    public static int validatePwd(String password) {
        if (password.length() < 4)
            return -1;
        return 0;
    }

    /**
     * @param c
     * @return
     * @Description: 是否包含中文
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-6-18
     */
    public static boolean isChineseNo(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * @param str
     * @return
     * @Description: 去除字符串
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-6-18
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    // public static String TitletoJson(String jsonData) {
    // StringBuffer stringBuffer = new StringBuffer();
    // stringBuffer.append("{\"ct\":\"");
    // stringBuffer.append(ContextConfig.getInstance().getStringWithFilename(ContextConfig.TITLE_LASTTIME));
    // stringBuffer.append("\",\"data\":");
    // String result = (jsonData.replaceAll("titleDirectoryId",
    // "catid")).replaceAll("titleDirectoryName", "catname");
    // stringBuffer.append(result);
    // stringBuffer.append("}");
    // XL.d("TitleToJson", jsonData);
    // // StringBuffer stringBuffer = new StringBuffer();
    // // stringBuffer.append("{\"ct\":\"");
    // //
    // stringBuffer.append(ContextConfig.getInstance().getIntWithFilename(ContextConfig.TITLE_LASTTIME));
    // // stringBuffer.append("\",\"data\":[");
    // // for (NewsPagerTitle newsPagerTitle : nPagerTitles) {
    // // stringBuffer.append("{\"catid\":\"");
    // // stringBuffer.append(newsPagerTitle.getTitleDirectoryId());
    // // stringBuffer.append("\",\"catname\":\"");
    // // stringBuffer.append(newsPagerTitle.getTitleDirectoryName());
    // // stringBuffer.append("\"},");
    // // }
    // // stringBuffer.append("");
    // // return stringBuffer.toString();
    // return stringBuffer.toString();
    // }

    /**
     * Originally from RoboGuice:
     * https://github.com/roboguice/roboguice/blob/master
     * /roboguice/src/main/java/roboguice/util/Strings.java Like join, but
     * allows for a distinct final delimiter. For english sentences such as
     * "Alice, Bob and Charlie" use ", " and " and " as the delimiters.
     *
     * @param delimiter     usually ", "
     * @param lastDelimiter usually " and "
     * @param objs          the objects
     * @param <T>           the type
     * @return a string
     */
    public static <T> String joinAnd(final String delimiter, final String lastDelimiter, final Collection<T> objs) {
        if (objs == null || objs.isEmpty())
            return "";

        final Iterator<T> iter = objs.iterator();
        final StringBuilder buffer = new StringBuilder(StringUtil.toString(iter.next()));
        int i = 1;
        while (iter.hasNext()) {
            final T obj = iter.next();
            if (notEmpty(obj))
                buffer.append(++i == objs.size() ? lastDelimiter : delimiter).append(StringUtil.toString(obj));
        }
        return buffer.toString();
    }

    public static <T> String joinAnd(final String delimiter, final String lastDelimiter, final T... objs) {
        return joinAnd(delimiter, lastDelimiter, Arrays.asList(objs));
    }

    public static <T> String join(final String delimiter, final Collection<T> objs) {// 1
        if (objs == null || objs.isEmpty())
            return "";

        final Iterator<T> iter = objs.iterator();
        final StringBuilder buffer = new StringBuilder(StringUtil.toString(iter.next()));

        while (iter.hasNext()) {
            final T obj = iter.next();
            if (notEmpty(obj))
                buffer.append(delimiter).append(StringUtil.toString(obj));
        }
        return buffer.toString();
    }

    public static <T> String join(final String delimiter, final T... objects) {
        return join(delimiter, Arrays.asList(objects));
    }

    public static String toString(InputStream input) {
        StringWriter sw = new StringWriter();
        copy(new InputStreamReader(input), sw);
        return sw.toString();
    }

    public static String toString(Reader input) {
        StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static int copy(Reader input, Writer output) {
        long count = copyLarge(input, output);
        return count > Integer.MAX_VALUE ? -1 : (int) count;
    }

    public static long copyLarge(Reader input, Writer output) throws RuntimeException {
        try {
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            long count = 0;
            int n;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(final Object o) {
        return toString(o, "");
    }

    public static String toString(final Object o, final String def) {
        return o == null ? def : o instanceof InputStream ? toString((InputStream) o) : o instanceof Reader ? toString((Reader) o) : o instanceof Object[] ? StringUtil.join(", ", (Object[]) o) : o instanceof Collection ? StringUtil.join(", ", (Collection<?>) o) : o.toString();
    }

    public static boolean isEmpty(final Object o) {
        return toString(o).trim().length() == 0;
    }

    public static boolean notEmpty(final Object o) {
        return toString(o).trim().length() != 0;
    }

    public static String md5(String s) {
        // http://stackoverflow.com/questions/1057041/difference-between-java-and-php5-md5-hash
        // http://code.google.com/p/roboguice/issues/detail?id=89
        try {

            final byte[] hash = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"));
            final StringBuilder hashString = new StringBuilder();

            for (byte aHash : hash) {
                String hex = Integer.toHexString(aHash);

                if (hex.length() == 1) {
                    hashString.append('0');
                    hashString.append(hex.charAt(hex.length() - 1));
                } else {
                    hashString.append(hex.substring(hex.length() - 2));
                }
            }

            return hashString.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String capitalize(String s) {
        final String c = StringUtil.toString(s);
        return c.length() >= 2 ? c.substring(0, 1).toUpperCase() + c.substring(1) : c.length() >= 1 ? c.toUpperCase() : c;
    }

    public static boolean equals(Object a, Object b) {
        return StringUtil.toString(a).equals(StringUtil.toString(b));
    }

    public static boolean equalsIgnoreCase(Object a, Object b) {
        return StringUtil.toString(a).equalsIgnoreCase(StringUtil.toString(b));
    }

    public static String[] chunk(String str, int chunkSize) {
        if (isEmpty(str) || chunkSize == 0)
            return new String[0];

        final int len = str.length();
        final int arrayLen = ((len - 1) / chunkSize) + 1;
        final String[] array = new String[arrayLen];
        for (int i = 0; i < arrayLen; ++i)
            array[i] = str.substring(i * chunkSize, (i * chunkSize) + chunkSize < len ? (i * chunkSize) + chunkSize : len);

        return array;
    }

    public static String namedFormat(String str, Map<String, String> substitutions) {
        for (String key : substitutions.keySet())
            str = str.replace('$' + key, substitutions.get(key));

        return str;
    }

    public static String namedFormat(String str, Object... nameValuePairs) {
        if (nameValuePairs.length % 2 != 0)
            throw new InvalidParameterException("You must include one value for each parameter");

        final HashMap<String, String> map = new HashMap<String, String>(nameValuePairs.length / 2);
        for (int i = 0; i < nameValuePairs.length; i += 2)
            map.put(StringUtil.toString(nameValuePairs[i]), StringUtil.toString(nameValuePairs[i + 1]));

        return namedFormat(str, map);
    }

    public static String getUserDataUrl(String url) {

        return null;
    }

    /**
     * @param s
     * @return
     * @Description:截取之间的字符串
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2014-10-28
     */
    public static List<String> match(String leftStr, String s, String rightStr) {
        List<String> results = new ArrayList<String>();
        Pattern p = Pattern.compile(leftStr + "([\\w/\\.]*)" + rightStr);
        Matcher m = p.matcher(s);
        while (!m.hitEnd() && m.find()) {
            results.add(m.group(1));
        }
        return results;
    }

    public static void noticeInternationalization(Context context, InboxMessageNewNotification notification) {

    }

    /**
     * @param text
     * @param gogaltext
     * @return SpannableString 返回类型
     * @Title: foregroundHight
     * @Description: 关键字高亮
     */
    public static SpannableString foregroundHight(String text, String gogaltext) {
        SpannableString sp = new SpannableString(text);
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(gogaltext)) {
            return sp;
        }

        text = text.toLowerCase(Locale.getDefault());
        gogaltext = gogaltext.toLowerCase(Locale.getDefault());
        int first = 0;
        int end = text.length();
        if (!text.equals(gogaltext)) {
            first = text.indexOf(gogaltext.substring(0, 1));
            end = first + gogaltext.length();
        }
        if (first > -1 && end > -1) {
            // 设置背景颜色
            sp.setSpan(new ForegroundColorSpan(Color.parseColor("#fc5255")), first, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sp;
    }

    /**
     * 分享自己的主页到微信连接
     */
    public static final int ME_PROFILE_TO_SHARE_WX = 0x00000001;
    /**
     * 分享自己的主页到朋友圈
     */
    public static final int ME_PROFILE_TO_SHARE_WXF = 0x00000002;
    /**
     * 复制自己的主页连接
     */
    public static final int ME_PROFILE_COPY_LINK = 0x00000003;
    /**
     * 复制别人的主页连接
     */
    public static final int OTHER_PROFILE_COPY_LINK = 0x00000004;
    /**
     * 分享别人的主页到微信连接
     */
    public static final int OTHER_PROFILE_TO_SHARE_WX = 0x00000005;
    /**
     * 分享自己的主页到朋友圈
     */
    public static final int OTHER_PROFILE_TO_SHARE_WXF = 0x00000006;
    public static final int CREATE_ERWEIMA = 0x00000007;
    public static final int SHARE_USER = 0x00000008;
    public static final int SHARE_POST = 0x00000009;
    public static final int SHARE_POST_IMAGE = 0x00000010;
    public static final int SHARE_POST_VIDEO = 0x00000011;

    public static String shareUserWebUrl(String userid, String toUserid, int tag) {
        StringBuilder stringBuilder = new StringBuilder("http://www.wealoha.com/");
        stringBuilder.append("user/");
        return shareUrl(tag, stringBuilder.toString(), userid, toUserid);
    }

    // public static String sharePostWebUrl(String userid, String toUserid, Post
    // post, int appendType, int tag) {
    // StringBuilder stringBuilder = new
    // StringBuilder("http://www.wealoha.com/");
    // stringBuilder.append("post/");
    // if (appendType == SHARE_POST_IMAGE) {
    // stringBuilder.append(post.getPostId());
    // } else if (appendType == SHARE_POST_VIDEO) {
    // stringBuilder.append(post.getVideo());
    // }
    // return shareUrl(tag, stringBuilder.toString(), userid, toUserid);
    // }

    public static String sharePostWebUrl(String userid, String toUserid, Post post, int tag) {
        StringBuilder stringBuilder = new StringBuilder("http://www.wealoha.com/");
        stringBuilder.append("post/");
        stringBuilder.append(post.getPostId());
        return shareUrl(tag, stringBuilder.toString(), userid, toUserid);
    }

    public static String shareUrl(int tag, String url, String userid, String toUserid) {
        StringBuilder stringBuilder = new StringBuilder(url);
        switch (tag) {
            case ME_PROFILE_TO_SHARE_WX:
                stringBuilder.append("?shareby=" + userid);
                stringBuilder.append("&from=singlemessage&isappinstalled=1");
                return stringBuilder.toString();
            case ME_PROFILE_TO_SHARE_WXF:

                return stringBuilder.toString();
            case OTHER_PROFILE_TO_SHARE_WX:
                stringBuilder.append("?shareby=" + userid);
                stringBuilder.append("&from=singlemessage&isappinstalled=1");
                return stringBuilder.toString();
            case OTHER_PROFILE_TO_SHARE_WXF:
                stringBuilder.append("?shareby=" + userid);
                stringBuilder.append("&from=timeline&isappinstalled=1");
                return stringBuilder.toString();
            case ME_PROFILE_COPY_LINK:
                stringBuilder.append("?shareby=" + userid);
                return stringBuilder.toString();
            case OTHER_PROFILE_COPY_LINK:
                stringBuilder.append("?shareby=" + userid);
                return stringBuilder.toString();
            case CREATE_ERWEIMA:
                return stringBuilder.toString();
            default:
                return null;
        }
    }

    /***
     * { #shareUserWebUrl(User, User, int)} or
     * { #sharePostWebUrl(User, User, Post, int)}
     *
     * @param user
     * @param toUser
     * @param tag
     * @return String
     */
    public static String shareWebPagerUrl(User user, User toUser, int tag) {
        StringBuilder stringBuilder = new StringBuilder("http://www.wealoha.com/user/");
        switch (tag) {
            case ME_PROFILE_TO_SHARE_WX:
                stringBuilder.append(user.getId());
                stringBuilder.append("?shareby=" + user.getId());
                stringBuilder.append("&from=singlemessage&isappinstalled=1");
                return stringBuilder.toString();
            case ME_PROFILE_TO_SHARE_WXF:

                return stringBuilder.toString();
            case OTHER_PROFILE_TO_SHARE_WX:
                stringBuilder.append(toUser.getId());
                stringBuilder.append("?shareby=" + user.getId());
                stringBuilder.append("&from=singlemessage&isappinstalled=1");
                return stringBuilder.toString();
            case OTHER_PROFILE_TO_SHARE_WXF:
                stringBuilder.append(toUser.getId());
                stringBuilder.append("?shareby=" + user.getId());
                stringBuilder.append("&from=timeline&isappinstalled=1");
                return stringBuilder.toString();
            case ME_PROFILE_COPY_LINK:
                stringBuilder.append(user.getId());
                stringBuilder.append("?shareby=" + user.getId());
                return stringBuilder.toString();
            case OTHER_PROFILE_COPY_LINK:
                stringBuilder.append(toUser.getId());
                stringBuilder.append("?shareby=" + user.getId());
                return stringBuilder.toString();
            case CREATE_ERWEIMA:
                stringBuilder.append(user.getId());
                return stringBuilder.toString();
            default:
                return null;
        }
    }

    public static void copyStringToClipboard(Context context, String text) {
        if (!TextUtils.isEmpty(text)) {
            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cmb.setPrimaryClip(ClipData.newPlainText(null, text));
        } else {
            ToastUtil.shortToast(context, R.string.is_not_work);
        }
    }

    public static String shareDescription(User user, Context context, RegionNodeUtil mRegionNodeUtil) {
        if (user != null) {
            StringBuilder sBuilder = new StringBuilder();
            List<String> regionNames = mRegionNodeUtil.getRegionNames(user.getRegionCode(), 10);
            if (regionNames == null) {
                return "";
            }
            Collections.reverse(regionNames);
            try {
                for (int i = 1; i < regionNames.size(); i++) {
                    sBuilder.append(regionNames.get(i));
                    if (i != regionNames.size() - 1) {
                        sBuilder.append("，");
                    }
                }
            } catch (Throwable e) {
                // 提示用户无法获取地区信息
                ToastUtil.shortToast(context, R.string.Unkown_Error);
            }
            sBuilder.append("\n");
            sBuilder.append(user.getAge());
            sBuilder.append("·");
            sBuilder.append(user.getHeight());
            sBuilder.append("·");
            sBuilder.append(user.getWeight());
            if (TextUtils.isEmpty(user.getZodiac())) {
                return sBuilder.toString();
            } else {
                sBuilder.append("·");
                sBuilder.append(getUserZodiac(user.getZodiac(), context));
                return sBuilder.toString();
            }
        } else {
            return "";
        }
    }

    public static String getUserZodiac(String zodiac, Context context) {
        if ("Aries".equals(zodiac)) {
            return context.getString(R.string.aries);
        } else if ("Taurus".equals(zodiac)) {
            return context.getString(R.string.taurus);
        } else if ("Gemini".equals(zodiac)) {
            return context.getString(R.string.gemini);
        } else if ("Cancer".equals(zodiac)) {
            return context.getString(R.string.cancer);
        } else if ("Leo".equals(zodiac)) {
            return context.getString(R.string.leo);
        } else if ("Virgo".equals(zodiac)) {
            return context.getString(R.string.virgo);
        } else if ("Libra".equals(zodiac)) {
            return context.getString(R.string.libra);
        } else if ("Scorpio".equals(zodiac)) {
            return context.getString(R.string.scorpio);
        } else if ("Sagittarius".equals(zodiac)) {
            return context.getString(R.string.sagittarius);
        } else if ("Capricorn".equals(zodiac)) {
            return context.getString(R.string.capricorn);
        } else if ("Aquarius".equals(zodiac)) {
            return context.getString(R.string.aquarius);
        } else if ("Pisces".equals(zodiac)) {
            return context.getString(R.string.pisces);
        } else {
            return null;
        }
    }

    /**
     * @param first
     * @param number
     * @return
     * @Description:
     * @see:
     * @since:
     * @description
     * @author: sunkist
     * @date:2015-1-28
     */
    public static String appendPhoneNum(String first, String number) {
        return first + " " + number;
    }

    /**
     * 兼容英文单词复数形式的文本资源
     *
     * @param count
     * @param strId
     * @param pluralFormStrId
     * @return
     */
    public static String getPluralformString(int count, int strId, int pluralFormStrId, Context context) {
        return context.getString(count > 1 ? pluralFormStrId : strId, count);
    }

    public static String encryptNum(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return phoneNum;
        }

        int index = phoneNum.indexOf(" ");

        if (index == -1) {
            return phoneNum;
        }

        String num = "";
        String headerCode = "";// ep：“+86” + 空格
        num = phoneNum.substring(index, phoneNum.length());
        headerCode = phoneNum.substring(0, index);
        int start = (num.length() - 4) / 2;

        return headerCode + num.replace(num.substring(start, start + 4), "****");

    }

}
