package com.wealoha.social.beans

/**
 * @author:sunkist
 * @see:
 * @since:
 * @description
 * @copyright wealoha.com
 * @Date:2015年3月11日
 */
data class Comment2GetData(
    var nextCursorId: String = Direct.Early.value,
    var userMap: Map<String, UserDTO> = mapOf(),
    var imageMap: Map<String, ImageCommonDto> = mapOf(),
    var list: List<CommentDTO> = listOf(),
    var lateCursorId: String = Direct.Late.value
) : ResultData() {
    companion object {
        fun fake(): Comment2GetData {
            val imageMap: HashMap<String, ImageCommonDto> = hashMapOf()
            val userMap: HashMap<String, UserDTO> = hashMapOf()
            val list: ArrayList<CommentDTO> = arrayListOf()

            (0..10).forEach {
                val toUserDTO = UserDTO.fake()
                val fromUserDTO = UserDTO.fake()
                imageMap[toUserDTO.avatarImage.imageId] = ImageCommonDto.init(toUserDTO.avatarImage)
                userMap[toUserDTO.id] = toUserDTO
                val commonDto =
                    CommentDTO.fakeForList(toUserDTO = toUserDTO, fromUserDTO = fromUserDTO)
                list.add(commonDto)
            }
            return Comment2GetData(
                nextCursorId = Direct.Early.value,
                userMap = mapOf(),
                imageMap = imageMap,
                list = list,
                lateCursorId = Direct.Late.value
            )
        }
    }
}