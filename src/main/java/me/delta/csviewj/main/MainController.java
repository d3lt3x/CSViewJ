package me.delta.csviewj.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import me.delta.csviewj.table.CSViewTable;

import java.io.IOException;

public class MainController {


    //Initializes variables from fxml-resource file.
    private final Stage stage;
    private CSViewTable csViewTable;
    @FXML
    private TableView<String[]> table;
    @FXML
    private PieChart pieChart;
    @FXML
    private MenuBar menuBar;

    public MainController(Stage stage) throws IOException {

        this.stage = stage;


        //Disables unusable menu-bar items. Delay required because variables may not have been initialized yet by the fxml-loader.
        Platform.runLater(() -> {
            try {
                this.csViewTable = new CSViewTable(this.table, this.stage, this);
                this.menuBar.getMenus().forEach(menu -> menu.setOnShown(event -> {

                    menu.getItems().stream().filter(item -> item.getId() == null).forEach(item -> {
                        item.setDisable(!this.csViewTable.isOpen());
                    });

                }));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


    }

    //Initializes methods from fxml-resource file and defines their functionality.
    @FXML
    public void openFile() throws IOException {
        this.csViewTable.openFile();
    }

    @FXML
    public void saveFile() throws IOException {
        this.csViewTable.saveToFile();
    }

    @FXML
    public void saveFileAs() throws IOException {
        this.csViewTable.saveFileAs();
    }

    @FXML
    public void quit() {
        this.stage.close();
    }

    @FXML
    public void close() throws IOException {
        this.csViewTable.close();
    }

    @FXML
    public void revert() throws IOException {
        this.csViewTable.revert();
    }

    @FXML
    public void addColumn() {
        this.csViewTable.addColumn();
    }

    @FXML
    public void addRow() {
        this.csViewTable.addRow();
    }

    @FXML
    public void deleteColumn() {
        this.csViewTable.deleteColumn();
    }

    @FXML
    public void deleteRow() {
        this.csViewTable.deleteRow();
    }

    @FXML
    public void editCell() {
        this.csViewTable.editCell();
    }

    @FXML
    public void clearCell() {
        this.csViewTable.clearCell();
    }

    @FXML
    public void showPieChart() {
        this.csViewTable.showPieChart();
    }

    public PieChart getPieChart() {
        return this.pieChart;
    }
}