package top.suzhelan.bili.biz.biliplayer.ui.controller

import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.platform.BiliContext

/**
 * 播放器管理池 被viewModel调用
 */
object PlayerPool {
    private val pool = mutableMapOf<String, PlayerSyncController>()
    fun create(id: String, context: BiliContext): PlayerSyncController {
        //检查播放器是否存在
        if (pool.containsKey(id)) {
            return pool[id]!!
        }
        val player = PlayerSyncController(context)
        pool[id] = player
        return player
    }

    fun get(id: String): PlayerSyncController {
        check(pool.containsKey(id))
        return pool[id]!!
    }

    fun remove(id: String) {
        val player = pool.remove(id)
        player?.playOrPause()
        player?.close()
    }

    fun has(id: String): Boolean {
        return pool.containsKey(id)
    }
}