package medialab.minesweeper;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import medialab.minesweeper.gameLogic.Game;
import medialab.minesweeper.gameLogic.scenario;
import java.io.IOException;

public class MainPageController {
    private StackPane[][] panes;
    @FXML
    private Pane minePane;
    @FXML
    private Text timeDisplay,flagDisplay,mineDisplay;
    private scenario loadedScenario;
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
        panes = new StackPane[size][size];
        GridPane field = new GridPane();
        field.setAlignment(Pos.CENTER);
        for (int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                //todo configure rectangles to reveal/flag
                int finalI = i;
                int finalJ = j;
                StackPane R = new StackPane();
                R.setAlignment(Pos.CENTER);
                R.setBackground(new Background(new BackgroundFill(Color.valueOf("grey"), CornerRadii.EMPTY, Insets.EMPTY)));
                R.setMinSize(500.0/size,500.0/size);
                R.setMaxSize(500.0/size,500.0/size);
                R.setOnMouseClicked(event -> {
                    squareClick(event, finalI, finalJ,size);
                });
                field.add(R,j,i);
                Text content = new Text(" ");
                R.getChildren().add(content);
                panes[i][j]=R;
            }
        }
        field.setGridLinesVisible(true);
        minePane.getChildren().add(field);
        mineDisplay.setText(Integer.toString(game.flagsLeft));
        flagDisplay.setText(Integer.toString(game.flagsLeft));
        timeDisplay.setText((Integer.toString(game.timeLeft)));
        game.start();
    }
    EventHandler<MouseEvent> squareClick(MouseEvent click,int x, int y,int size) {
        String status = game.getStatus();
        if (status == "running") {
            if (click.getButton() == MouseButton.PRIMARY) {
                System.out.println("Revealing " + x + " " + y);
                game.reveal(x, y);
            } else {
                //todo flag
                System.out.println("Flagging " + x + " " + y);
                game.flag(x, y);
            }
            refreshField(size);
            status = game.getStatus();
            if (status != "running"){
                //TODO handle game end
            }
        }
        return null;
    }
    void refreshField(int size){
        for (int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                Text content = (Text)panes[i][j].getChildren().get(0);
                char toPut = game.getBoardChar(i,j);
                content.setText(""+toPut);
                if (toPut!='\u0000'&& toPut!='F'){
                    panes[i][j].setBackground(new Background(new BackgroundFill(Color.valueOf("white"), CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
        }
    }
}