package org.caojun.jte.jsontariffengine.controller

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.TextField
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import org.caojun.jte.jsontariffengine.AddRuleTypeController

open class RuleTypeController {

    @FXML
    lateinit var tgRuleType: ToggleGroup
    @FXML
    lateinit var tfRuleName: TextField

    fun initialize(paneCommonPart: Pane) {
        val loader = FXMLLoader(AddRuleTypeController::class.java.getResource("rule-type.fxml"))
        val commonPartParent = loader.load<VBox>()
        val commonPartController = loader.getController<RuleTypeController>()
        paneCommonPart.children.add(commonPartParent)

        tgRuleType = commonPartController.tgRuleType
        tfRuleName = commonPartController.tfRuleName
    }
}