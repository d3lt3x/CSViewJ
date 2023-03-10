package me.delta.csviewj.table;

import javafx.application.Platform;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class CellEditor extends TableCell<String[], String> {

    //This Class defines the editing logic for a table-cell.
    private TextField textField;

    //Enters edit-state for selected cell.
    @Override
    public void startEdit() {

        super.startEdit();

        if (this.textField == null) {
            this.createTextField();
        }

        super.setGraphic(this.textField);
        super.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.textField.selectAll();
    }

    //Leaves edit-state and cancels it.
    @Override
    public void cancelEdit() {

        super.cancelEdit();

        super.setText(String.valueOf(getItem()));
        super.setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    //Updates content of a cell.
    @Override
    public void updateItem(String item, boolean empty) {

        super.updateItem(item, empty);

        if (empty) {
            super.setText(null);
            super.setGraphic(this.textField);
        } else {
            if (super.isEditing()) {
                if (this.textField != null) {
                    this.textField.setText(this.getString());
                }
                super.setGraphic(this.textField);
                super.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                super.setText(this.getString());
                super.setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }


    //Creates editable text-field for selected cell and defines key-events.
    private void createTextField() {

        this.textField = new TextField(this.getString());
        this.textField.setMinWidth(getWidth() - getGraphicTextGap() * 2);

        this.textField.setOnKeyPressed(event -> {

            if (event.getCode() == KeyCode.ENTER) {
                super.commitEdit(this.textField.getText());
                Platform.runLater(() -> this.cancelEdit());

            } else if (event.getCode() == KeyCode.ESCAPE) {
                this.cancelEdit();

            } else if (event.getCode() == KeyCode.DELETE) {
                super.commitEdit("");
                Platform.runLater(() -> this.cancelEdit());
            }
        });

    }

    //Returns empty string when item is null
    private String getString() {
        return getItem() == null ? "" : getItem();
    }
}

