package org.caojun.jte.jsontariffengine.utils

import javafx.scene.control.Menu
import javafx.scene.control.MenuItem

object MenuUtils {

    fun addRecentFile(openRecentMenu: Menu, newItem: MenuItem?) {
        openRecentMenu.items.add(newItem)
    }

    fun clear(openRecentMenu: Menu) {
        openRecentMenu.items.clear()
    }

    fun removeRecentFile(openRecentMenu: Menu, itemToRemove: MenuItem) {
        openRecentMenu.items.remove(itemToRemove)
    }
}