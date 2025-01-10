package org.caojun.jte.jsontariffengine

import javafx.fxml.FXML
import javafx.scene.control.Label
import org.caojun.library.jte.JsonTariffEngine

class HelloController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private fun onHelloButtonClick() {
        val jte = JsonTariffEngine("", object : JsonTariffEngine.Listener {
            override fun onLog(log: String) {
                TODO("Not yet implemented")
            }

        })
        welcomeText.text = "Welcome to JavaFX Application! jte: ${jte.isEnabled()}"
    }
}