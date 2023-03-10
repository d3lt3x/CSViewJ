package me.delta.csviewj.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import me.delta.csviewj.main.MainController;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CSViewTable {

    private final Stage stage;
    private final CSVFormat format;
    private final TableView<String[]> table;
    private final MainController controller;
    private final PieChart pieChart;
    private File file;
    private List<String[]> records;


    //Defines the csv-format and initializes variables.
    public CSViewTable(TableView<String[]> table, Stage stage, MainController controller) throws IOException {

        this.stage = stage;
        this.table = table;
        this.format = CSVFormat.DEFAULT.builder().setDelimiter(';').build();
        this.controller = controller;
        this.pieChart = this.controller.getPieChart();

    }

    //Loads file, initializes rest of class.
    private void initialize(File file) throws IOException {

        if (file == null) return;

        this.file = file;

        this.stage.setTitle("CSViewJ - Editing " + this.file.getName());


        //Reads translated file, saves to local records variable.
        try (FileReader reader = new FileReader(file)) {

            List<CSVRecord> csvRecords = CSVParser.parse(reader, this.format).getRecords();

            this.records = csvRecords.stream().map(record -> {
                String[] array = new String[csvRecords.get(0).size()];
                for (int i = 0; i < array.length; i++) {
                    if (record.size() > i) array[i] = record.get(i);
                    else array[i] = "";

                }

                return array;
            }).toList();
        }
        loadTable();
    }


    //Creates a new column.
    private void createColumn(String name) {

        //Sets TableCell logic (CellEditor)
        Callback<TableColumn<String[], String>, TableCell<String[], String>> cellFactory = p -> new CellEditor();

        //Adds empty column.
        TableColumn<String[], String> column = new TableColumn<>(name);
        this.table.getColumns().add(column);


        //Sets cell-value-factory (Defines the handling logic of given items), makes sure content isn't null.
        column.setCellValueFactory(cellDataFeatures -> {
            String[] record = cellDataFeatures.getValue();
            final int columnIndex = cellDataFeatures.getTableView().getColumns().indexOf(cellDataFeatures.getTableColumn());
            String content = "";

            if (columnIndex < record.length) content = record[columnIndex];

            return new SimpleStringProperty(content);
        });

        column.setCellFactory(cellFactory);
        column.setSortable(false);


        //Sets new value of edited cell.
        column.setOnEditCommit(event -> {
            String[] record = this.table.getItems().get(event.getTablePosition().getRow());

            final int columnIndex = event.getTableView().getColumns().indexOf(event.getTableColumn());
            String newString = event.getNewValue();
            record[columnIndex] = newString;
        });


    }

    //Sets all items from records.
    private void loadTable() {

        if (this.records.get(0) == null) return;

        this.table.getItems().clear();
        this.table.getColumns().clear();

        for (int i = 1; this.records.get(0).length >= i; i++)
            createColumn(String.valueOf(i));

        this.table.setItems(FXCollections.observableArrayList(this.records));

        this.table.getSelectionModel().setCellSelectionEnabled(true);

        //Sets event for editing a cell.
        this.table.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {

            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && table.getEditingCell() == null) {
                TablePosition focusedCellPosition = this.table.getFocusModel().getFocusedCell();
                this.table.edit(focusedCellPosition.getRow(), focusedCellPosition.getTableColumn());
            }
        });

        //disables/enables menu-items for editing, disabled when nothing selected.
        this.table.setOnContextMenuRequested(event -> this.table.getContextMenu().getItems().forEach(item -> item.setDisable(this.table.getFocusModel().getFocusedCell() == null)));

    }

    //Saves the file.
    public void saveToFile() throws IOException {
        this.writeToFile(this.file);
    }


    //Writes csv-data to file.
    private void writeToFile(File file) throws IOException {

        if (file == null) return;

        try (FileWriter writer = new FileWriter(file)) {
            CSVPrinter printer = new CSVPrinter(writer, this.format);

            for (String[] row : this.records)
                printer.printRecord(row);


            if (file != this.file) this.initialize(file);

        }
    }


    //Opens save-file dialog, writes csv-data to selected file.
    public void saveFileAs() throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(this.stage);

        this.writeToFile(file);
    }

    //Clears everything and sets its resource variables to null.
    public void close() {
        this.table.getItems().clear();
        this.table.getColumns().clear();
        this.file = null;
        this.records = null;
        this.stage.setTitle("CSViewJ - Idle");
        this.pieChart.setTitle("");
        this.pieChart.setData(FXCollections.observableArrayList());
    }


    //Reverts by reloading unsaved file.
    public void revert() throws IOException {
        this.initialize(this.file);
    }


    //Adds column to last index
    public void addColumn() {

        createColumn(String.valueOf(this.records.get(0).length + 1));

        this.records = this.records.stream().map(array -> Arrays.copyOf(array, array.length + 1)).toList();

        this.table.setItems(FXCollections.observableArrayList(this.records));
        this.table.refresh();

    }

    //Adds row at bottom.
    public void addRow() {

        List<String[]> updatedRecords = new ArrayList<>(this.records);
        updatedRecords.add(new String[this.records.get(0).length]);
        this.records = updatedRecords;
        this.table.setItems(FXCollections.observableArrayList(this.records));
        this.table.refresh();
    }

    //Opens open-file dialog, initializes class from file.
    public void openFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        this.initialize(fileChooser.showOpenDialog(this.stage));
    }

    //Deletes selected row.
    public void deleteRow() {
        int rowIndex = this.table.getFocusModel().getFocusedCell().getRow();
        if (rowIndex == -1 || this.records.size() <= rowIndex) return;

        List<String[]> updatedRecords = new ArrayList<>(this.records);
        updatedRecords.remove(rowIndex);
        this.records = updatedRecords;
        this.table.setItems(FXCollections.observableArrayList(this.records));
        this.table.refresh();
    }

    //Deletes selected column.
    public void deleteColumn() {

        int columnIndex = this.table.getFocusModel().getFocusedCell().getColumn();

        if (columnIndex == -1 || this.records.get(0).length <= columnIndex) return;

        List<String[]> updatedRecords = new ArrayList<>();

        for (String[] record : this.records) {
            updatedRecords.add(ArrayUtils.remove(record, columnIndex));
        }

        for (int i = columnIndex; i < this.table.getColumns().size(); i++) {
            this.table.getColumns().get(i).setText(String.valueOf(i));
        }
        this.table.getColumns().remove(columnIndex);
        this.records = updatedRecords;
        this.table.setItems(FXCollections.observableArrayList(this.records));
        this.table.refresh();

    }


    //Calculates data and shows pieChart for selected column.
    public void showPieChart() {

        int columnIndex = this.table.getFocusModel().getFocusedCell().getColumn();
        if (columnIndex == -1) return;
        ObservableList<PieChart.Data> dataList = FXCollections.observableArrayList();

        List<String> column = new ArrayList<>(this.records.subList(1, this.records.size() - 1).stream().map(array -> array[columnIndex]).toList());
        column.removeIf(String::isEmpty);
        column.removeIf(String::isBlank);

        List<String> distinctElements = column.stream().distinct().toList();
        float totalPercentage = 0;

        for (String value : distinctElements) {
            float percentage = ((float) Collections.frequency(column, value) / column.size() * 100);
            if (percentage < 0.5) continue;
            totalPercentage += percentage;
            PieChart.Data data = new PieChart.Data(value + " - " + String.format("%.2f%%", percentage), Collections.frequency(column, value));
            dataList.add(data);
        }

        PieChart.Data otherData = new PieChart.Data("Sonstige" + " - " + String.format("%.2f%%", 100 - totalPercentage), (100 - totalPercentage) / 100 * column.size());
        dataList.add(otherData);

        this.pieChart.setData(dataList);
        this.pieChart.setTitle(this.records.get(0)[columnIndex]);

    }

    //Checks if anything is loaded.
    public boolean isOpen() {
        return this.records != null;
    }


    //Enters edit-mode for cell.
    public void editCell() {
        if (this.table.getFocusModel().getFocusedCell() == null) return;
        this.table.edit(this.table.getFocusModel().getFocusedCell().getRow(), this.table.getFocusModel().getFocusedCell().getTableColumn());
    }


    //Clears a cell.
    public void clearCell() {
        if (this.table.getFocusModel().getFocusedCell() == null) return;
        List<String[]> updatedRecords = new ArrayList<>(this.records);
        updatedRecords.get(this.table.getFocusModel().getFocusedCell().getRow())[this.table.getFocusModel().getFocusedCell().getColumn()] = "";
        this.records = updatedRecords;
        this.table.setItems(FXCollections.observableArrayList(this.records));
        this.table.refresh();
    }

}
