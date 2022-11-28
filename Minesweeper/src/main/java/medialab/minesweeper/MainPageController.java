package medialab.minesweeper;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    private Timeline time;
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
                R.setOnMouseClicked(event -> squareClick(event, finalI, finalJ,size));
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
        configTimeline();
    }

    private void configTimeline() {
        time = new Timeline();
        time.setCycleCount(Timeline.INDEFINITE);
        time.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        actionEvent -> {
                            int current = Integer.parseInt(timeDisplay.getText())-1;
                            if (current<0){
                                handleEnd();
                                time.stop();
                            }else{
                                timeDisplay.setText(Integer.toString(current));
                            }
                        }));
        time.playFromStart();
    }
    private void handleEnd(){
        Alert end;
        time.stop();
        String status = game.getStatus();
        if (status.equals("loss") || status.equals("running")){
            //TODO handle loss
            end = new Alert(Alert.AlertType.NONE, "Oh no!", ButtonType.OK);
            end.setTitle("Oh no!");
            end.setContentText("You lost! Better luck next time...");
        }else{
            //TODO handle win
            end = new Alert(Alert.AlertType.NONE, "Congratulations!", ButtonType.OK);
            end.setTitle("Congratulations!");
            end.setContentText("You won!");
        }
        end.show();
    }

    void squareClick(MouseEvent click, int x, int y, int size) {
        String status = game.getStatus();
        if (status.equals("running")) {
            if (click.getButton() == MouseButton.PRIMARY) {
                //System.out.println("Revealing " + x + " " + y);
                game.reveal(x, y);
            } else {
                //System.out.println("Flagging " + x + " " + y);
                game.flag(x, y);
                flagDisplay.setText(Integer.toString(game.flagsLeft));
            }
            refreshField(size);
            status = game.getStatus();
            if (!status.equals("running") && time.getStatus()==Animation.Status.RUNNING){
                handleEnd();
            }
        }
    }
    void refreshField(int size){
        for (int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                Text content = (Text)panes[i][j].getChildren().get(0);
                char toPut = game.getBoardChar(i,j);
                if (toPut=='M' || toPut=='T'){
                    content.setFill(Paint.valueOf("red"));
                }
                if (toPut=='F' ){
                    content.setFill(Paint.valueOf("blue"));
                }
                if (toPut!='E'){
                    content.setText(""+toPut);
                }
                if (toPut!='\u0000'&& toPut!='F'&& toPut!='M'){
                    panes[i][j].setBackground(new Background(new BackgroundFill(Color.valueOf("white"), CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
        }
    }
}