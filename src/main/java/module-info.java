module me.delta.csviewj.main {

    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.commons.csv;
    requires org.apache.commons.lang3;

    exports me.delta.csviewj.main;
    opens me.delta.csviewj.main to javafx.fxml;
    exports me.delta.csviewj.table;
    opens me.delta.csviewj.table to javafx.fxml;

}