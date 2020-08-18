package com.wealoha.social.api.user.bean;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import android.text.TextUtils;

import com.wealoha.social.api.common.bean.Image;
import com.wealoha.social.api.user.dto.UserDTO;

/**
 * @author javamonk
 * @createTime 2015年2月25日 上午10:57:45
 */
public class User implements Serializable {

    public static final String TAG = User.class.getName();
    /**
     *
     */
    private static final long serialVersionUID = -4646233984981667550L;
    private final String id;
    private final String name;
    private final String birthday;
    private final int age;
    private final int height;
    private final int weight;
    private final boolean me;
    private final String regionCode;
    private final List<String> region;
    private final String zodiac;
    private final String summary;
    private final List<String> selfPurposes;
    private final String selfTag;
    private final Image avatarImage;
    private final boolean profileIncomplete;
    private final int alohaCount;
    private int alohaGetCount;
    private boolean aloha;
    private boolean match;
    private final int postCount;
    private boolean block;
    private final boolean hasPrivacy;

    public User(String id, String name, String birthday, int age, int height, int weight, boolean me,//
                String regionCode, List<String> region, String zodiac, String summary, List<String> selfPurposes, String selfTag,//
                Image avatarImage, boolean profileIncomplete, int alohaCount, int alohaGetCount, boolean aloha,//
                boolean match, int postCount, boolean block, boolean hasPrivacy) {
        super();
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.me = me;
        this.regionCode = regionCode;
        this.region = region;
        this.zodiac = zodiac;
        this.summary = summary;
        this.selfPurposes = selfPurposes;
        this.selfTag = selfTag;
        this.avatarImage = avatarImage;
        this.profileIncomplete = profileIncomplete;
        this.alohaCount = alohaCount;
        this.alohaGetCount = alohaGetCount;
        this.aloha = aloha;
        this.match = match;
        this.postCount = postCount;
        this.block = block;
        this.hasPrivacy = hasPrivacy;
    }

    public static User fromDTO(UserDTO dto, Image avatarImage) {
        User user = null;
        if (dto != null) {
            user = new User(dto.id, dto.name, dto.birthday, dto.age, dto.height, //
                    dto.weight, dto.me, dto.regionCode, dto.region, dto.zodiac, //
                    dto.summary, dto.selfPurposes, dto.selfTag, avatarImage, //
                    dto.profileIncomplete, dto.alohaCount, dto.alohaGetCount, //
                    dto.aloha, dto.match, dto.postCount, dto.block, dto.hasPrivacy);
        }
        return user;
    }

    public static User fromDTO(UserDTO dto) {
        User user = null;
        if (dto != null) {
            user = new User(dto.id, dto.name, dto.birthday, dto.age, dto.height, //
                    dto.weight, dto.me, dto.regionCode, dto.region, dto.zodiac, //
                    dto.summary, dto.selfPurposes, dto.selfTag, Image.fromDTO(dto.avatarImage), //
                    dto.profileIncomplete, dto.alohaCount, dto.alohaGetCount, //
                    dto.aloha, dto.match, dto.postCount, dto.block, dto.hasPrivacy);
        }
        return user;
    }

    public static User formOldUser(com.wealoha.social.beans.User oldUser) {

        return new User(oldUser.id, oldUser.name, oldUser.birthday, parseInt(oldUser.age), parseInt(oldUser.height),//
                parseInt(oldUser.weight), oldUser.me, oldUser.regionCode, oldUser.region, oldUser.zodiac,//
                oldUser.summary, oldUser.selfPurposes, oldUser.selfTag, Image.fromOld(oldUser.avatarImage),//
                oldUser.profileIncomplete, oldUser.alohaCount, oldUser.alohaGetCount, //
                oldUser.aloha, oldUser.match, oldUser.postCount, oldUser.block, oldUser.hasPrivacy);
    }

    public static int parseInt(String str) {
        int temp = 0;
        if (!TextUtils.isEmpty(str)) {
            temp = Integer.parseInt(str);
        }
        return temp;
    }

    // public static User toOldUser(User user) {
    // com.wealoha.social.beans.User oldUse = new com.wealoha.social.beans.User();
    // oldUse.id = user.getId();
    // oldUse.age = String.valueOf(user.getAge());
    // oldUse.aloha = user.isAloha();
    // oldUse.alohaCount = user.getAlohaCount();
    // oldUse.avatarImage
    // return new com.wealoha.social.beans.User();
    // }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isMe() {
        return me;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getZodiac() {
        return zodiac;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getSelfPurposes() {
        return selfPurposes;
    }

    public String getSelfTag() {
        return selfTag;
    }

    public Image getAvatarImage() {
        return avatarImage;
    }

    public boolean isProfileIncomplete() {
        return profileIncomplete;
    }

    public int getAlohaCount() {
        return alohaCount;
    }

    public int getAlohaGetCount() {
        return alohaGetCount;
    }

    public boolean isAloha() {
        return aloha;
    }

    public boolean isMatch() {
        return match;
    }

    public int getPostCount() {
        return postCount;
    }

    public boolean isBlock() {
        return block;
    }

    public boolean hasPrivacy() {
        return hasPrivacy;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    /***
     * 加入黑名单
     *
     * @return void
     */
    public void block() {
        this.block = true;
    }

    /***
     * 移出黑名单
     *
     * @return void
     */
    public void removeBlock() {
        this.block = false;
    }

    /***
     * 被aloha,被aloha 数加1
     *
     * @return void
     */
    public void alohaed() {
        if (!aloha) {
            aloha = true;
            alohaGetCount++;
        }
    }

    public void dislike() {
        if (aloha) {
            aloha = false;
            alohaGetCount--;
        }
    }

    public List<String> getRegion() {
        return region;
    }

}
