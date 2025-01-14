package org.caojun.jte.jsontariffengine

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import org.caojun.jte.jsontariffengine.utils.FileUtils
import org.caojun.jte.jsontariffengine.utils.MenuUtils
import org.caojun.jte.jsontariffengine.utils.RecentMenuUtils
import org.caojun.library.jte.JsonTariffEngine
import org.caojun.library.jte.RuleSet
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
    private lateinit var lvTariff: ListView<String>
    @FXML
    private lateinit var lvRule: ListView<String>
    @FXML
    private lateinit var taRule: TextArea
    @FXML
    private lateinit var btnTariffAdd: Button
    @FXML
    private lateinit var btnTariffEdit: Button
    @FXML
    private lateinit var btnRuleAdd: Button
    @FXML
    private lateinit var btnRuleEdit: Button

    private var currentTariffName = ""

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

        btnTariffAdd.setOnAction {
            openController<TariffNameController>("tariff-name", btnTariffAdd.text, object : ControllerListener<TariffNameController> {
                override fun onInit(controller: TariffNameController) {

                }

                override fun onReturn(controller: TariffNameController) {
                    val tariffName = controller.getReturnData()
                    updateTariffName(tariffName.first, tariffName.second)
                }
            })
        }
        btnTariffEdit.setOnAction {
            if (currentTariffName.isEmpty()) {
                return@setOnAction
            }
            openController<TariffNameController>("tariff-name", "${btnTariffEdit.text}: $currentTariffName", object : ControllerListener<TariffNameController> {
                override fun onInit(controller: TariffNameController) {
                    controller.setInitialData(currentTariffName)
                }

                override fun onReturn(controller: TariffNameController) {
                    val tariffName = controller.getReturnData()
                    updateTariffName(tariffName.first, tariffName.second)
                }

            })
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
            if (updateJTE(json)) {
                val listRecentFile = RecentMenuUtils.add(file)
                loadRecentFileMenu(listRecentFile)
            }
        } else {
            val listRecentFile = RecentMenuUtils.clear()
            loadRecentFileMenu(listRecentFile)
        }
    }

    private interface ControllerListener<T> {
        fun onInit(controller: T)
        fun onReturn(controller: T)
    }
    private fun <T> openController(fxml: String, title: String, listener: ControllerListener<T>, width: Double = 320.0, height: Double = 240.0) {
        val fxmlLoader = FXMLLoader(JTEApplication::class.java.getResource("$fxml.fxml"))
        val scene = Scene(fxmlLoader.load(), width, height)
        val stage = Stage()
        stage.initModality(Modality.APPLICATION_MODAL)

        val controller = fxmlLoader.getController<T>()
        stage.title = title
        stage.scene = scene
        listener.onInit(controller)
        stage.showAndWait()
        listener.onReturn(controller)
    }

    private fun updateJTE(json: String): Boolean {
        println("[JsonTariffEngine] json: $json")
        jte = JsonTariffEngine(json, object : JsonTariffEngine.Listener {
            override fun onLog(log: String) {
                println("[JsonTariffEngine] onLog: $log")
            }
        })
        lvTariff.items.clear()
        lvRule.items.clear()
        val result = jte?.isEnabled() == true
        if (result) {
            taFileContent.text = JsonUtils.format(json)

            val tariffs = jte?.getAllRuleNames() ?: emptyArray()
            lvTariff.items.addAll(tariffs)
            lvTariff.selectionModel.selectedIndexProperty().addListener { observable, oldValue, newValue ->
                println("lvTariff.Selected Index Changed from $oldValue to $newValue")
                currentTariffName = ""
                lvRule.items.clear()
                val tariffIndex = newValue.toInt()
                if (tariffIndex < 0 || tariffIndex >= tariffs.size) {
                    return@addListener
                }
                currentTariffName = tariffs[tariffIndex]
                println("Selected tariffName: $currentTariffName")
                val ruleSet = jte?.getRuleSet(currentTariffName)
                val rules = ruleSet?.rules ?: emptyList()
                val ruleNames = ArrayList<String>()
                for (rule in rules) {
                    ruleNames.add(rule.name)
                }
                lvRule.items.addAll(ruleNames)
                lvRule.selectionModel.selectedIndexProperty().addListener { observable, oldValue, newValue ->
                    println("lvRule.Selected Index Changed from $oldValue to $newValue")
                    val ruleIndex = newValue.toInt()
                    if (ruleIndex >= 0 && ruleIndex < rules.size) {
                        val rule = rules[ruleIndex]
                        taRule.text = JsonUtils.format(JsonUtils.toJson(rule))
                    } else {
                        taRule.text = null
                    }
                }
            }
        } else {
            jte = null
            taFileContent.text = null
        }
        return result
    }
    private fun updateTariffName(initTariffName: String, resultTariffName: String) {
        if (resultTariffName.isEmpty()) {
            return
        }
        val isAdd = initTariffName.isEmpty()
        val listRuleSet = (jte?.getListRuleSet() ?: emptyMap()).toMutableMap()
        if (isAdd) {
            listRuleSet[resultTariffName] = RuleSet()
        } else {
            listRuleSet[resultTariffName] = listRuleSet[initTariffName] as RuleSet
            listRuleSet.remove(initTariffName)
        }
        updateJTE(JsonUtils.toJson(listRuleSet))
    }
}