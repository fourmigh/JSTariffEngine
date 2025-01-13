package org.caojun.jte.jsontariffengine

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.MenuItem
import javafx.stage.FileChooser
import org.caojun.jte.jsontariffengine.utils.FileUtils
import org.caojun.library.jte.JsonTariffEngine
import org.slf4j.LoggerFactory

class HomeController {

//    private val logger = LoggerFactory.getLogger(HomeController::class.java)
    private var jte: JsonTariffEngine? = null

    @FXML
    private lateinit var miOpenFile: MenuItem
    @FXML
    private lateinit var miQuit: MenuItem

    @FXML
    fun initialize() {
        miOpenFile.setOnAction {
            val fileChooser = FileChooser()
            fileChooser.title = "Open File"


            // 显示打开文件对话框
            val file = fileChooser.showOpenDialog(miOpenFile.parentPopup.ownerWindow)
            val json = FileUtils.readFileContent(file)
            println("[JsonTariffEngine] json: $json")
            jte = JsonTariffEngine(json, object : JsonTariffEngine.Listener {
                override fun onLog(log: String) {
                    println("[JsonTariffEngine] onLog: $log")
                }

            })
        }

        miQuit.setOnAction {
            Platform.exit()
        }
    }
}