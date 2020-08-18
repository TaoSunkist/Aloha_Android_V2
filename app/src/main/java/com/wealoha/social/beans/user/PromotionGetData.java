package com.wealoha.social.beans.user;

import java.io.Serializable;

import com.wealoha.social.beans.ResultData;

/**
 * @author javamonk
 * @createTime 14-10-16 PM12:02
 */
public class PromotionGetData extends ResultData implements Serializable {

	private static final long serialVersionUID = -6138532041290176835L;
	public static final String TAG = PromotionGetData.class.getSimpleName();
	public String promotionCode;
	public int quotaReset;
	public int inviteNewUserCount;
	public boolean alohaGetLocked;
	public int alohaGetUnlockInviteNeed;
	public int alohaGetUnlockMoreInviteNeed;
	public int alohaGetUnlockMax;
	public int quotaPerPerson;

}
