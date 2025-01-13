package org.caojun.jte.jsontariffengine

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.FileChooser
import org.caojun.jte.jsontariffengine.utils.FileUtils
import org.caojun.jte.jsontariffengine.utils.MenuUtils
import org.caojun.jte.jsontariffengine.utils.RecentMenuUtils
import org.caojun.library.jte.JsonTariffEngine
import org.caojun.library.jte.utils.JsonUtils
import java.io.File


class HomeController {

    //    private val logger = LoggerFactory.getLogger(HomeController::class.java)
    private var jte: JsonTariffEngine? = null

    @FXML
    private lateinit var taFileContent: TextArea
    @FXML
    private lateinit var miOpenFile: MenuItem
    @FXML
    private lateinit var miQuit: MenuItem
    @FXML
    private lateinit var mOpenRecent: Menu
    @FXML
    lateinit var lvTariff: ListView<String>

    @FXML
    fun initialize() {
        miOpenFile.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.title = "Open File"
            // 显示打开文件对话框
            val file = fileChooser.showOpenDialog(miOpenFile.parentPopup.ownerWindow)
            openJTEFile(file)
        }

        miQuit.setOnAction {
            Platform.exit()
        }

        val listRecentFile = RecentMenuUtils.load()
        loadRecentFileMenu(listRecentFile)
    }

    private fun loadRecentFileMenu(list: Array<Pair<MenuItem, File>>) {
        MenuUtils.clear(mOpenRecent)
        for (pair in list) {
            val menuItem = pair.first
            MenuUtils.addRecentFile(mOpenRecent, menuItem)
            val file = pair.second
            menuItem.setOnAction {
                openJTEFile(file)
            }
        }
    }

    private fun openJTEFile(file: File) {
        if (file.exists()) {
            val json = FileUtils.readFileContent(file)
            println("[JsonTariffEngine] json: $json")
            jte = JsonTariffEngine(json, object : JsonTariffEngine.Listener {
                override fun onLog(log: String) {
                    println("[JsonTariffEngine] onLog: $log")
                }
            })
            if (true == jte?.isEnabled()) {
                val listRecentFile = RecentMenuUtils.add(file)
                loadRecentFileMenu(listRecentFile)
                taFileContent.text = JsonUtils.format(json)

                val tariffs = jte?.getAllRuleNames() ?: emptyArray()
                lvTariff.items.clear()
                lvTariff.items.addAll(tariffs)
                lvTariff.selectionModel.selectedIndexProperty().addListener { observable, oldValue, newValue ->
                    println("Selected Index Changed from $oldValue to $newValue")
                }
            } else {
                jte = null
                taFileContent.text = null
            }
        } else {
            val listRecentFile = RecentMenuUtils.clear()
            loadRecentFileMenu(listRecentFile)
        }
    }
}