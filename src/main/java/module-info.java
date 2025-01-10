module org.caojun.jte.jsontariffengine {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires JTELibrary;

    opens org.caojun.jte.jsontariffengine to javafx.fxml;
    exports org.caojun.jte.jsontariffengine;
}