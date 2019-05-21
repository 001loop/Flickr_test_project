package com.mudrichenko.evgeniy.flickrtestproject

import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import com.orhanobut.logger.Logger

class BottomNavigationViewHelper {

    companion object {
        fun disableShiftMode(view: BottomNavigationView) {
            val menuView = view.getChildAt(0) as BottomNavigationMenuView
            try {
                val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
                shiftingMode.isAccessible = true
                shiftingMode.setBoolean(menuView, false)
                shiftingMode.isAccessible = false
                for (i in 0 until menuView.childCount) {
                    val item = menuView.getChildAt(i) as BottomNavigationItemView
                    item.setShiftingMode(false)
                    item.setChecked(item.itemData.isChecked)
                }
            } catch (e: NoSuchFieldException) {
                Logger.e("NoSuchFieldException")
            } catch (e: IllegalAccessException) {
                Logger.e("IllegalAccessException")
            }

        }
    }

}