package com.wealoha.social.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wealoha.social.beans.RegionNode;
import com.wealoha.social.inject.Injector;

/**
 * 用法
 *
 * <code>
 *
 * @author javamonk
 * @Inject RegionNodeUtil regionNodeUtil; <br/> regionNodeUtil.getByCode(null,
 * "CN_Beijing").name); </pre>
 * @date 2014-10-28 下午2:48:07
 * @see
 * @since
 */
public class RegionNodeUtil {

    private String TAG = RegionNodeUtil.class.getSimpleName();

    @Inject
    Context context;

    private Map<String, RegionNode> regionNodeMap;

    public RegionNodeUtil() {
        super();
        Injector.inject(this);
        init();
    }

    private void init() {
        try {
            XL.d(TAG, "初始化地区字典数据");

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, RegionNode>>() {
            }.getType();

            Locale currentLocale = context.getResources().getConfiguration().locale;

            String jsonFile = null;
            if (Locale.TRADITIONAL_CHINESE.equals(currentLocale)) {
                jsonFile = "region/region_zh_TW.json";
            } else if (Locale.SIMPLIFIED_CHINESE.equals(currentLocale)) {
                jsonFile = "region/region_zh_CN.json";
            } else {
                jsonFile = "region/region_en.json";
            }

            // 默认都是繁体
            // jsonFile = "region/region_zh_TW.json";

            XL.d(TAG, "使用Json文件: " + jsonFile);
            String json = StringUtil.readStream(context.getAssets().open(jsonFile));
            regionNodeMap = gson.fromJson(json, type);

        } catch (IOException e) {
            XL.w(TAG, "读取地区文件失败", e);
            throw new RuntimeException(e);
        }
    }

    public Map<String, RegionNode> getRegionNodeMap() {
        return regionNodeMap;
    }

    /**
     * 获取地区
     *
     * @param code
     * @param maxLevel
     * @return 范围从小到大
     */
    public List<String> getRegionNames(String code, int maxLevel) {
        List<String> names = new ArrayList<String>();
        RegionNode node = getByCode(code);
        if (node != null) {
            if (StringUtil.isNotEmpty(node.getAbbr())) {
                names.add(node.getAbbr());
            } else {
                names.add(node.getName());
            }

            String[] codes = StringUtil.split(code, "_");
            int max = Math.min(maxLevel, codes.length - 1);
            // int max = 0;
            // if (codes.length < 3) {
            // max = codes.length - 1;
            // } else {
            // max = codes.length - 2;
            // }
            for (int i = 0; i < max; i++) {
                code = StringUtil.join("_", subArray(codes, max - i));
                node = getByCode(code);

                XL.i("CODE_RE", "code:" + code);
                if (node == null) {
                    break;
                }
                if (StringUtil.isNotEmpty(node.getAbbr())) {
                    names.add(node.getAbbr());
                } else {
                    names.add(node.getName());
                }
            }
        }

        return names;
    }

    /**
     * 取某个地区码对应的地区
     *
     * @param code
     * @return 可能为null
     */
    public RegionNode getByCode(String code) {
        if (StringUtil.isEmpty(code)) {
            return null;
        }
        String[] codes = StringUtil.split(code, "_");

        Map<String, RegionNode> regionNodeMap = getRegionNodeMap();
        RegionNode node = regionNodeMap.get(codes[0]);
        if (codes.length == 1) {
            return node;
        }
        if (node == null) {
            return null;
        }
        for (int i = 1; i < codes.length; i++) {
            String nextCode = StringUtil.join("_", subArray(codes, i + 1));
            XL.d(TAG, "nextCode: " + nextCode);
            node = node.getRegions().get(nextCode);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private String[] subArray(String[] array, int to) {
        String[] result = new String[to + 1];
        for (int i = 0; i < to; i++) {
            result[i] = array[i];
        }
        return result;
    }

}
