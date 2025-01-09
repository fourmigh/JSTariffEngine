module org.caojun.library.jte.jsontariffengine {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;

    opens org.caojun.library.jte.jsontariffengine to javafx.fxml;
    exports org.caojun.library.jte.jsontariffengine;
}