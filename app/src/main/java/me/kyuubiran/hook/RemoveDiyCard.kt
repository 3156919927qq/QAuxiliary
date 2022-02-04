/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 qwq233@qwq2333.top
 * https://github.com/cinit/QAuxiliary
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */
package me.kyuubiran.hook

import android.app.Activity
import io.github.qauxv.base.annotation.FunctionHookEntry
import io.github.qauxv.base.annotation.UiItemAgentEntry
import io.github.qauxv.dsl.FunctionEntryRouter
import io.github.qauxv.hook.CommonSwitchFunctionHook
import io.github.qauxv.util.DexKit
import io.github.qauxv.util.QQVersion
import io.github.qauxv.util.isTim
import io.github.qauxv.util.requireMinQQVersion
import xyz.nextalone.util.get
import xyz.nextalone.util.hookBefore
import xyz.nextalone.util.set
import xyz.nextalone.util.throwOrTrue

@FunctionHookEntry
@UiItemAgentEntry
object RemoveDiyCard : CommonSwitchFunctionHook(
    "kr_remove_diy_card",
    intArrayOf(DexKit.N_VasProfileTemplateController_onCardUpdate)) {

    override val name = "屏蔽 DIY 名片"

    override val uiItemLocation = FunctionEntryRouter.Locations.Simplify.UI_PROFILE

    override fun initOnce() = throwOrTrue {
        DexKit.doFindMethod(DexKit.N_VasProfileTemplateController_onCardUpdate)!!
            .hookBefore(this) {
                when (it.thisObject) {
                    is Activity -> {
                        val card = it.args[0]
                        copeCard(card)
                    }
                    else -> {
                        if (requireMinQQVersion(QQVersion.QQ_8_6_0)) {
                            val card = it.args[1].get("card")
                            copeCard(card!!)
                            return@hookBefore
                        }
                        it.result = null
                    }
                }
            }
    }

    private fun copeCard(card: Any) {
        val id = card.get("lCurrentStyleId", Long::class.java)
        if ((21L == id) or (22L == id))
            card.set("lCurrentStyleId", 0)
    }

    override val isAvailable: Boolean get() = !isTim()
}