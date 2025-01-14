package org.caojun.jte.jsontariffengine

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.RadioButton
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import org.caojun.jte.jsontariffengine.controller.RuleTypeController

class AddRuleTypeController: RuleTypeController() {

    private var ruleType = ""
    private var ruleName = ""
    fun getReturnData(): Pair<String, String> {
        println("AddRuleTypeController: $ruleType, $ruleName")
        return Pair(ruleType, ruleName)
    }

    @FXML
    private lateinit var root: VBox
    @FXML
    private lateinit var paneCommonPart: Pane
    @FXML
    private lateinit var btnOK: Button

    @FXML
    fun initialize() {

        initialize(paneCommonPart)
        root.padding = Insets(20.0, 20.0, 20.0, 20.0)

        tgRuleType.selectedToggleProperty().addListener { ov, old_toggle, new_toggle ->
            if (new_toggle != null) {
                // 获取当前选中的 RadioButton
                val selectedRadioButton = new_toggle as RadioButton
                // 获取选中的 RadioButton 的文本
                ruleType = selectedRadioButton.text
            } else {
                ruleType = ""
            }
        }

        btnOK.setOnAction {
            if (ruleType.isEmpty()) {
                return@setOnAction
            }
            ruleName = tfRuleName.text
            btnOK.scene.window.hide()
        }
    }
}