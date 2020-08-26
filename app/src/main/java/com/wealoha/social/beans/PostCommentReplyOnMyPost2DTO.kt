package com.wealoha.social.beans

data class PostCommentReplyOnMyPost2DTO(
    var replyUser: String,
    var fromUser: String,
    var commentId: String,
    var comment: String,
    var postId: String
) : AbsNotify2DTO() {
    //	eplyUser	String	lCbBDMRH4t8
    //	fromUser	String	dlfI58Dv3uU
    //	commentId	String	rRAbrZHkB1oUoqakK8Kmjg
    //	comment	String	就想念大家
    //	postId	String	dnd_CTsAPjVPrCYDWVbkEQ

}