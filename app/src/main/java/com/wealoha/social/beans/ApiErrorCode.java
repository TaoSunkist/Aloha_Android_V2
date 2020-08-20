package com.wealoha.social.beans;

import android.util.SparseArray;

/**
 * Api错误<br/>
 * 
 * Api的错误有两级，status(全局)和error(错误)，都统一成一个枚举 转换脚本:
 * 
 * <pre>
 * #!/usr/bin/perl
 * # perl xxx.pl path/to/ResponseErrorConstants.java
 * while (<>) {
 * 	if (m{int\s(\w+)\s=\s(\d+)}xmsi) {
 *     my ($name, $value) = ($1, $2);
 *     my @names = split(/_/, $name);
 *     foreach my $n(@names) {
 *         next if $n =~ /error/i;
 *         print ucfirst(lc($n));
 *     }    
 *     print "($value), //\n";
 * 	}
 * }
 * </pre>
 * 
 * @author javamonk
 * @createTime 2015年3月6日 上午11:25:36
 */
public enum ApiErrorCode {
	Ok(200), //
	NullResult(-1), //
	AuthFail(401), //
	UserIllegal(451), //
	NotFound(404), //
	ServerError(500), //
	RequestParamValidationFail(501), //
	ThresholdHit(503), //

	// 以下是常规错误，从cu-api项目里移植过来(ResponseErrorConstants)，每次运行脚本即可
	UserNotFound(200502), //
	UsernameUsed(200503), //
	EmailUsed(200504), //
	ImageUploadFail(200505), //
	AudioUploadFail(200506), //
	EmotionNotFound(200507), //
	LicenseInvalid(200508), //
	InvalidPassword(200509), //
	InvalidEmail(200510), //
	InvalidPasswordResetCode(200511), //
	BlockByYourSelf(200513), //
	BlockByOther(200514), //
	InboxForFriendsOnly(200515), //
	MobileNumberRegistered(200516), //
	InvalidMobileVerifyCode(200517), //
	MatchNoMoreToday(200518), //
	MatchNoMoreData(200519), //
	InvalidMobileNumber(200520), //
	InvalidSummary(200522), //
	MatchNope(200523), //
	InvalidAccessToken(200524), //
	ConnectRemoteFail(200525), //
	InvalidDescription(200526), //
	InvalidComment(200527), //
	InvalidCode(200528), //
	GuidDuplicate(200529), //
	AlreadyConnect(200530), //
	;

	private final int value;

	private static final SparseArray<ApiErrorCode> valuesMap = new SparseArray<ApiErrorCode>();

	static {
		for (ApiErrorCode e : values()) {
			valuesMap.put(e.getValue(), e);
		}
	}

	private ApiErrorCode(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	/**
	 * 转换api的错误码
	 * 
	 * @param result
	 * @return
	 */
	public static ApiErrorCode fromResult(Result<? extends ResultData> result) {
		if (result == null) {
			return NullResult;
		}

		if (result.isOk()) {
			return Ok;
		}

		if (result.getStatus() != Ok.value) {
			return valuesMap.get(result.getStatus());
		}

		return valuesMap.get(result.getData().error);
	}
}
