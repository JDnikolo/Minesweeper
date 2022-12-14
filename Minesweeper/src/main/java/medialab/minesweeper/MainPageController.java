package medialab.minesweeper;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import medialab.minesweeper.gameLogic.Game;
import medialab.minesweeper.gameLogic.scenario;

import java.io.*;

public class MainPageController {
    private StackPane[][] panes;
    @FXML
    private Pane minePane;
    @FXML
    private Text timeDisplay,flagDisplay,mineDisplay;
    private scenario loadedScenario;
    private Timeline time=null;
    @FXML
    private Menu scenarioThingy;
    private Game game = null;

    @FXML
    public void setScenario(String name){
       scenarioThingy.setText(name);
    }
    @FXML
    protected void onClickSolve(){
        if (game==null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "No active game to solve!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        if (!game.getStatus().equals("running")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "The game is already over!", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        game.endGame();
        refreshField(loadedScenario.diff==1?9:16);
        handleEnd(false);
    }
    @FXML
    protected void onClickResults() throws IOException{
        Stage scenarioStage = new Stage();
        Parent root = FXMLLoader.load(MinesweeperApp.class.getResource("results.fxml"));
        Scene scene = new Scene(root);
        scenarioStage.setTitle("Last 5 games:");
        scenarioStage.setScene(scene);
        scenarioStage.showAndWait();
    }
    @FXML
    protected void onClickExit(){
        Stage stage = (Stage) timeDisplay.getScene().getWindow();
        stage.close();
    }
    @FXML
    protected void onClickCreate() throws IOException {
        Stage scenarioStage = new Stage();
        FXMLLoader loader = new FXMLLoader(MinesweeperApp.class.getResource("scenarioCreation.fxml"));
        Scene scene = new Scene(loader.load(),320,320);
        scenarioStage.setTitle("Create a scenario");
        scenarioStage.setScene(scene);
        scenarioStage.showAndWait();
    }
    @FXML
    protected void onClickLoad() throws IOException {
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
        timeDisplay.setFill(Paint.valueOf("black"));
        game.start();
        configTimeline();
    }

    private void configTimeline() {
        if (time==null){
            time = new Timeline();
            time.setCycleCount(Timeline.INDEFINITE);
            time.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1),
                            actionEvent -> {
                                int current = Integer.parseInt(timeDisplay.getText())-1;
                                if (current<0){
                                    time.stop();
                                    game.endGame();
                                    handleEnd(true);
                                    timeDisplay.setFill(Paint.valueOf("red"));
                                }else{
                                    timeDisplay.setText(Integer.toString(current));
                                }
                            }));
        }
        time.playFromStart();
    }
    private void handleEnd(boolean defeat){
        Alert end;
        time.stop();
        StringBuilder newEntry = new StringBuilder();
        newEntry.append(loadedScenario.mines).append(", ");
        newEntry.append(game.getTries()).append(", ");
        newEntry.append(game.getTotalTime()).append(", ");
        String status = game.getStatus();
        if (status.equals("loss") || status.equals("running")){
            if (defeat){
                end = new Alert(Alert.AlertType.NONE, "Oh no!", ButtonType.OK);
                end.setTitle("Oh no!");
                end.setContentText("You lost! Better luck next time...");
            } else {
                end = new Alert(Alert.AlertType.NONE, "Oh no!", ButtonType.OK);
                end.setTitle("Here's the solution!");
                end.setContentText("You can figure it out next time.");
            }
            newEntry.append("CPU");
        }else{
            end = new Alert(Alert.AlertType.NONE, "Congratulations!", ButtonType.OK);
            end.setTitle("Congratulations!");
            end.setContentText("You won!");
            newEntry.append("Player");
        }
        end.show();
        writeResults(newEntry.toString());
    }
    protected void writeResults(String entry){
        FileWriter fw = null;
        FileReader fr = null;
        BufferedReader buffer = null;
        File temp = new File("medialab\\tempscores.txt");
        File scores = new File("medialab\\scores.txt");
        try {
            if (temp.exists()){
                temp.delete();
            }
            temp.createNewFile();
            if (!scores.exists()){
                scores.createNewFile();
            }
            fw = new FileWriter(temp, false);
            fr = new FileReader(scores);
            buffer = new BufferedReader(fr);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            fw.write(entry+'\n');
            String line;
            int lines=0;
            while ((line = buffer.readLine()) != null) {
                if (lines<4){
                    fw.write(line+'\n');
                }
                lines+=1;
            }
            buffer.close();
            fw.close();
            fr.close();
            scores.delete();
            temp.renameTo(scores);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            }
            flagDisplay.setText(Integer.toString(game.flagsLeft));
            refreshField(size);
            status = game.getStatus();
            if (!status.equals("running") && time.getStatus()==Animation.Status.RUNNING){
                handleEnd(true);
            }
        }
    }
    void refreshField(int size){
        for (int i=0;i<size;i++){
            for (int j=0;j<size;j++){
                Text content = (Text)panes[i][j].getChildren().get(0);
                char toPut = game.getBoardChar(i,j);
                switch (toPut){
                    case 'M':
                    case 'T': {
                        content.setFill(Paint.valueOf("red"));
                        content.setFont(Font.font("Verdana",20));
                        break;
                    }
                    case 'F':{
                        content.setFill(Paint.valueOf("blue"));
                        content.setFont(Font.font("Verdana",20));
                        break;
                    }
                    default:{
                        content.setFill(Paint.valueOf("black"));
                        content.setFont(Font.font("Verdana",15));
                        break;
                    }
                }
                if (toPut!='E'){
                    content.setText(""+toPut);
                } else{
                    content.setText("");
                }
                if (toPut!='\u0000'&& toPut!='F'&& toPut!='M'){
                    panes[i][j].setBackground(new Background(new BackgroundFill(Color.valueOf("white"), CornerRadii.EMPTY, Insets.EMPTY)));
                }
            }
        }
    }
}