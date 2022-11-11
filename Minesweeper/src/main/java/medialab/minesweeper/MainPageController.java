package medialab.minesweeper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import medialab.minesweeper.gameLogic.scenario;

import java.io.IOException;

public class MainPageController {
    @FXML
    private Label welcomeText;
    private scenario loadedScenario;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    protected void onCreateClick() throws IOException {
        Stage scenarioStage = new Stage();
        FXMLLoader loader = new FXMLLoader(MinesweeperApp.class.getResource("scenarioCreation.fxml"));
        Scene scene = new Scene(loader.load(),320,320);
        scenarioStage.setTitle("Create a scenario");
        scenarioStage.setScene(scene);
        scenarioStage.show();
    }
}