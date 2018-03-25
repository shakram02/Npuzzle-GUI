package sample;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class Controller {
    @FXML
    private GridPane mainGrid;
    @FXML
    private TextField gridInput;

    private ArrayList<Label> labels = new ArrayList<>(9);

    public void initialize() {
        mainGrid.setGridLinesVisible(true);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Label l = new Label("-");

                GridPane.setFillWidth(l, true);
                GridPane.setFillHeight(l, true);
                GridPane.setHalignment(l, HPos.CENTER);
                GridPane.setValignment(l, VPos.CENTER);

                labels.add(l);
                mainGrid.add(l, i, j);
            }
        }


    }

    @FXML
    public void loadElements() {
        String[] splits = gridInput.getText().split(",");

        if (splits.length != labels.size()) {
            throw new IllegalArgumentException("Cell count isn't correct");
        }

        for (int i = 0; i < labels.size(); i++) {
            labels.get(i).setText(splits[i]);
        }
    }
}
