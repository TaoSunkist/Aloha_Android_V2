package com.wealoha.social.beans

/**
 * 取数据的方向
 *
 * @author javamonk
 * @createTime 2015年3月12日 下午2:13:31
 */
enum class Direct(//
    val value: String
) {
    /** 更新的数据  */
    Late("late"),  //

    /** 更旧的数据，默认值，和以前的接口一致  */
    Early("early");

}