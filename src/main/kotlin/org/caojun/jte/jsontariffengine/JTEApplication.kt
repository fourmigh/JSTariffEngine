package org.caojun.jte.jsontariffengine

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

class JTEApplication : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(JTEApplication::class.java.getResource("jte-home.fxml"))
        val scene = Scene(fxmlLoader.load(), 1200.0, 600.0)
        stage.title = "JsonTariffEngine"
        stage.scene = scene
//        stage.isFullScreen = true
        stage.show()
    }
}

fun main() {
    Application.launch(JTEApplication::class.java)
}