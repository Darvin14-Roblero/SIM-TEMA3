module com.darvinsistems.app.variablesaleatorias {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.darvinsistems.app.variablesaleatorias to javafx.graphics, javafx.fxml;
    exports com.darvinsistems.app.variablesaleatorias;
}