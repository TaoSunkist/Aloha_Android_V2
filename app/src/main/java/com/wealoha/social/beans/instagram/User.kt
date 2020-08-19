package com.wealoha.social.beans.instagram

class User {
    /*
	 * "user": { "id": "1574083", "username": "snoopdogg", "full_name": "Snoop Dogg", "profile_picture":
	 * "http://distillery.s3.amazonaws.com/profiles/profile_1574083_75sq_1295469061.jpg" }
	 */
    @kotlin.jvm.JvmField
    var id: String? = null
    @kotlin.jvm.JvmField
    var username: String? = null
    var full_name: String? = null
    var profile_picture: String? = null
}