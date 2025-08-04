package top.sacz.bili.biz.user.util

import top.sacz.bili.api.BiliResponse

object ModifyUtils {


    /**
     * 根据响应结果获取对应的提示语
     */
    fun getToastByModifyResult(result: BiliResponse.SuccessOrNull<Nothing>): String {
        return when (result.code) {
            0 -> "成功"
            -101 -> "请先登录"
            -102 -> "账号被封停"
            -111 -> "csrf校验失败"
            -400 -> "请求错误"
            22001 -> "不能对自己进行此操作"
            22002 -> "因对方隐私设置，你还不能关注"
            22003 -> "关注失败，请将该用户移除黑名单之后再试"
            22008 -> "黑名单达到上限"
            22009 -> "关注失败，已达关注上限"
            22013 -> "账号已注销，无法完成操作"
            22014 -> "已经关注用户，无法重复关注"
            22120 -> "重复加入黑名单"
            40061 -> "用户不存在"
            else -> "未知错误"
        }
    }
}