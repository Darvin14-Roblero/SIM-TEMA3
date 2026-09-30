module com.darvinsistems.app.variablesdiscretas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.jfree.jfreechart;

    opens com.darvinsistems.app.variablesdiscretas to javafx.fxml;
    exports com.darvinsistems.app.variablesdiscretas;
}