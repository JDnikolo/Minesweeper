package medialab.minesweeper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import medialab.minesweeper.gameLogic.scenario;

import java.io.IOException;

public class MainPageController {
    @FXML
    private Label welcomeText;
    private scenario loadedScenario;
    @FXML
    private VBox container;

    @FXML
    public void setScenario(scenario sc){
        loadedScenario=sc;
        System.out.println(sc.toFileFormat());
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
    @FXML
    protected void onLoadClick() throws IOException {
        Stage scenarioStage = new Stage();
        FXMLLoader loader = new FXMLLoader(MinesweeperApp.class.getResource("scenarioLoad.fxml"));
        Scene scene = new Scene(loader.load(),320,320);
        scenarioStage.setTitle("Load a scenario");
        scenarioStage.setScene(scene);
        scenarioStage.show();
    }
}