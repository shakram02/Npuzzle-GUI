package sample;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import kotlin.Pair;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import npuzzle.UtilsKt;
import npuzzle.abstractions.Direction;
import npuzzle.abstractions.Move;
import npuzzle.abstractions.State;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Controller {
    @FXML
    private GridPane mainGrid;
    @FXML
    private TextField gridInput;

    @FXML
    private Label lblCount;

    @FXML
    private Button btnNext;

    @FXML
    private Button btnPrev;

    @FXML
    private RadioButton bfs;
    @FXML
    private RadioButton aStar;
    @FXML
    private RadioButton dfs;

    @FXML
    private Label stepIndex;
    private Collection<State<Integer>> solution;
    private ArrayList<Label> labels = new ArrayList<>(9);
    private ArrayList<ArrayList<String>> result;
    private int solutionIndex = -1;

    public void initialize() {
        mainGrid.setGridLinesVisible(true);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Label l = new Label("-");
                l.setFont(new Font(45));
                GridPane.setFillWidth(l, true);
                GridPane.setFillHeight(l, true);
                GridPane.setHalignment(l, HPos.CENTER);
                GridPane.setValignment(l, VPos.CENTER);

                l.textProperty().addListener((observableValue, oldVal, newVal) -> {
                    // Update colors to match the 0 item
                    if (newVal.equals("0")) {
                        l.setTextFill(new Color(1, 0, 0, 1));
                    } else {
                        l.setTextFill(new Color(0, 0, 0, 1));
                    }
                });

                labels.add(l);
                mainGrid.add(l, j, i);
            }
        }

        btnNext.setDisable(true);
        btnPrev.setDisable(true);
        lblCount.setVisible(false);
    }

    @FXML
    public void loadElements() {
        String[] splits = gridInput.getText().split(",");

        if (splits.length != labels.size()) {
            throw new IllegalArgumentException("Cell count isn't correct");
        }

        loadLabelData(splits);
        try {
            long start = System.nanoTime();

            solution = solveProblem(splits, UtilsKt::manhattanDistance);
//            solution = solveProblem(splits, UtilsKt::euclideanDistance);
            System.out.println("Solution is composed of:" + solution.size() + " steps");
            long duration = System.nanoTime() - start;

            result = new ArrayList<>();
            for (State<Integer> state : solution) {
                result.add(stateToArray(state));
            }

            solutionIndex = 1;
            btnNext.setDisable(false);

            lblCount.setText("Count: " + solution.size() + "\nDuration:" + (duration / 1000000) + " ms");
            lblCount.setVisible(true);
        } catch (IllegalStateException exc) {
            btnNext.setDisable(true);
            btnPrev.setDisable(true);
            lblCount.setText("Failed to find a solution");
            loadLabelData(new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"});
        }

    }

    @FXML
    public void onNext() {
        loadLabelData(result.get(solutionIndex));

        solutionIndex++;
        if (solutionIndex >= solution.size()) {
            btnNext.setDisable(true);
            solutionIndex--;
        } else {
            btnPrev.setDisable(false);
            btnNext.setDisable(false);
        }

        stepIndex.setText(String.format("%s/%s", solutionIndex, solution.size()));
    }

    @FXML
    public void onPrev() {
        loadLabelData(result.get(solutionIndex));

        solutionIndex--;
        if (solutionIndex < 0) {
            btnPrev.setDisable(true);
            solutionIndex++;
        } else {
            btnNext.setDisable(false);
            btnPrev.setDisable(false);
        }

        stepIndex.setText(String.format("%s/%s", solutionIndex, solution.size()));
    }

    private <T extends Number> ArrayList<String> stateToArray(State<T> state) {
        String[] intermediate = state.toString().split("\n");
        ArrayList<String> result = new ArrayList<>();

        for (String s : intermediate) {
            if (s.isEmpty()) {
                continue;
            }
            for (String number : s.split("\t")) {
                result.add(number.trim());
            }
        }

        return result;
    }

    private Collection<State<Integer>> solveProblem(String[] splits,
                                                    Function2<Pair<Integer, Integer>, Pair<Integer, Integer>, Integer> costFunction) {

        Integer[] graph = new Integer[labels.size()];
        Integer[] finalState = new Integer[labels.size()];
        for (int i = 0; i < labels.size(); i++) {
            finalState[i] = i;
        }
        for (int i = 0; i < labels.size(); i++) {
            String item = splits[i].trim();
            graph[i] = Integer.parseInt(item);
        }

        Move<Integer> move = new Move<>(graph, 0, Direction.None);
        State<Integer> initialState = new State<>(0, graph, move, null);
        Function1<State<Integer>, Boolean> isAtGoal = s -> Arrays.equals(finalState, s.getMove().getResult());
        if (bfs.isSelected()) {
            System.out.println("BFS");
            return npuzzle.SolversKt.bfs(initialState, isAtGoal, 1000);
        } else if (dfs.isSelected()) {
            System.out.println("DFS");
            return npuzzle.SolversKt.dfs(initialState, isAtGoal, 1000);
        } else if (aStar.isSelected()) {
            System.out.println("A*");
            return npuzzle.SolversKt.aStar(initialState, isAtGoal, costFunction, 1000);
        } else {
            throw new IllegalStateException();
        }
    }

    private void loadLabelData(String[] items) {
        for (int i = 0; i < labels.size(); i++) {
            String item = items[i].trim();
            labels.get(i).setText(item);
        }
    }

    private void loadLabelData(ArrayList<String> items) {
        for (int i = 0; i < labels.size(); i++) {
            String item = items.get(i).trim();
            labels.get(i).setText(item);
        }
    }
}
