package org.caojun.jte.jsontariffengine

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TextField

class TariffNameController {

    private var initTariffName = ""
    private var resultTariffName = ""
    fun setInitialData(data: String) {
        initTariffName = data
        tfTariffName.text = data
    }

    fun getReturnData(): Pair<String, String> {
        println("TariffNameController: $initTariffName, $resultTariffName")
        return Pair(initTariffName, resultTariffName)
    }

    @FXML
    private lateinit var tfTariffName: TextField
    @FXML
    private lateinit var btnOK: Button

    @FXML
    fun initialize() {
        btnOK.setOnAction {
            resultTariffName = tfTariffName.text
            btnOK.scene.window.hide()
        }
    }
}