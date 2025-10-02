package top.suzhelan.bili.shared.common.ui.icons

import bilicompose.shared.common.generated.resources.Res
import bilicompose.shared.common.generated.resources.ic_lv0
import bilicompose.shared.common.generated.resources.ic_lv1
import bilicompose.shared.common.generated.resources.ic_lv2
import bilicompose.shared.common.generated.resources.ic_lv3
import bilicompose.shared.common.generated.resources.ic_lv4
import bilicompose.shared.common.generated.resources.ic_lv5
import bilicompose.shared.common.generated.resources.ic_lv6


object CommIcons {

}

object LevelIcons {
    val lv0 = Res.drawable.ic_lv0
    val lv1 = Res.drawable.ic_lv1
    val lv2 = Res.drawable.ic_lv2
    val lv3 = Res.drawable.ic_lv3
    val lv4 = Res.drawable.ic_lv4
    val lv5 = Res.drawable.ic_lv5
    val lv6 = Res.drawable.ic_lv6
    val levelIconMap = mapOf(
        0 to lv0,
        1 to lv1,
        2 to lv2,
        3 to lv3,
        4 to lv4,
        5 to lv5,
        6 to lv6,
    ).withDefault {
        lv0
    }
}