package medialab.minesweeper;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import medialab.minesweeper.gameLogic.Game;
import medialab.minesweeper.gameLogic.scenario;

import java.io.IOException;
import java.util.EventListener;

public class MainPageController {
    @FXML
    private Pane minePane;
    @FXML
    private Text welcomeText,timeDisplay,flagDisplay,mineDisplay;
    private scenario loadedScenario;
    @FXML
    private VBox container;
    @FXML
    private Menu scenarioThingy;
    private Game game;

    @FXML
    public void setScenario(String name){
       scenarioThingy.setText(name);
    }
    @FXML
    protected void onCreateClick() throws IOException {
        Stage scenarioStage = new Stage();
        FXMLLoader loader = new FXMLLoader(MinesweeperApp.class.getResource("scenarioCreation.fxml"));
        Scene scene = new Scene(loader.load(),320,320);
        scenarioStage.setTitle("Create a scenario");
        scenarioStage.setScene(scene);
        scenarioStage.showAndWait();
    }
    @FXML
    protected void onLoadClick() throws IOException {
        Stage scenarioStage = new Stage();
        FXMLLoader loader = new FXMLLoader(MinesweeperApp.class.getResource("scenarioLoad.fxml"));
        Scene scene = new Scene(loader.load(),320,320);
        LoadScenarioController LDC = loader.getController();
        LDC.setLoadedScenario(loadedScenario);
        scenarioStage.setTitle("Load a scenario");
        scenarioStage.setScene(scene);
        scenarioStage.showAndWait();
        this.loadedScenario=LDC.loadedScenario;
        this.scenarioThingy.setText(LDC.scenarioName);
    }
    @FXML
    protected void onClickStart(){
        if (loadedScenario==null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "No scenario selected!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        game= new Game(loadedScenario);
        int size = loadedScenario.diff==1 ? 9:16;
        GridPane field = new GridPane();
        field.setAlignment(Pos.CENTER);
        for (int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                //todo configure rectangles to reveal/flag
                Rectangle R = new Rectangle(500/size,500/size, Color.valueOf("grey"));
                int finalI = i;
                int finalJ = j;
                R.setOnMouseClicked(event -> {
                    squareClick(event, finalI, finalJ);
                });
                field.add(R,j,i);
            }
        }
        field.setGridLinesVisible(true);
        minePane.getChildren().add(field);
        mineDisplay.setText(Integer.toString(game.flagsLeft));
        flagDisplay.setText(Integer.toString(game.flagsLeft));
        timeDisplay.setText((Integer.toString(game.timeLeft)));
        game.start();
    }
    EventHandler<MouseEvent> squareClick(MouseEvent click,int x, int y) {
        if (click.getButton()==MouseButton.PRIMARY) {
            System.out.println("Revealing "+x+" "+y);
            //todo reveal
        } else {
            //todo flag
            System.out.println("Flagging "+x+" "+y);
        }
        return null;
    }
}