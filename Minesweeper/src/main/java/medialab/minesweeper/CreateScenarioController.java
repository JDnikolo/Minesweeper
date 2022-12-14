package medialab.minesweeper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import medialab.minesweeper.gameLogic.scenario;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class CreateScenarioController {
    @FXML
    TextField timeField, nameField,mineField;
    @FXML
    RadioButton diffButton1,diffButton2;
    @FXML
    CheckBox uberBox;

    @FXML
    public void setPrompts(ActionEvent actionEvent) {
        if (actionEvent.getSource()==diffButton1){
            timeField.setPromptText("120-180");
            mineField.setPromptText("9-11");
            uberBox.setSelected(false);
        }
        if (actionEvent.getSource()==diffButton2){
            timeField.setPromptText("240-360");
            mineField.setPromptText("35-45");
            uberBox.setSelected(true);
        }
    }
    @FXML
    protected void resetFields(){
        nameField.setText("Scenario-"+(new Random().nextInt(0,999999)));
        timeField.setText("");
        mineField.setText("");
    }
    @FXML
    protected void createScenario(){
        boolean failed=false;
        String defaultStyle=nameField.getStyle();
        String errorStyle="-fx-text-box-border: #B22222; -fx-focus-color: #B22222;";
        timeField.setStyle(defaultStyle);
        mineField.setStyle(defaultStyle);
        nameField.setStyle(defaultStyle);
        int mines, seconds,difficulty;
        try {
            mines = Integer.parseInt(mineField.getText());
            seconds = Integer.parseInt(timeField.getText());
        }catch (NumberFormatException e){
            mineField.setStyle(errorStyle);
            timeField.setStyle(errorStyle);
            failed=true;
            mines=seconds=0;
        }
        difficulty = diffButton1.isSelected() ? 1:2;
        String scenarioName = new String(nameField.getText());
        if (!scenarioName.endsWith(".txt")) scenarioName+=".txt";
        boolean uber=false;
        switch (difficulty){
            case 1: {
                if (mines > 11 || mines < 9) {
                    mineField.setStyle(errorStyle);
                    failed = true;
                }
                if (seconds > 180 || seconds < 120) {
                    timeField.setStyle(errorStyle);
                    failed = true;
                }
                break;
            }
            case 2:{
                if (mines < 35 || mines > 45) {
                    mineField.setStyle(errorStyle);
                    failed = true;
                }
                if (seconds < 240 || seconds > 360) {
                    timeField.setStyle(errorStyle);
                    failed = true;
                }
                uber=uberBox.isSelected();
                break;
            }
        }
        if (scenarioName==""){
            nameField.setStyle(errorStyle);
            failed=true;
        }
        Alert result;
        if (failed){
            result = new Alert(Alert.AlertType.ERROR, "~", ButtonType.OK);
            result.setTitle("Invalid Scenario!");
            result.setContentText("Your scenario parameters are invalid!");
            result.showAndWait();
        }else{
            try{

                //this WILL overwrite a file if it already exists, maybe inform user?
                FileWriter outputFile =new FileWriter("medialab\\"+scenarioName);
                System.out.println(""+difficulty+" "+mines+" "+seconds+" "+uber);
                scenario outputScenario = new scenario((byte) difficulty,mines,seconds,uber);
                System.out.println(outputScenario.toFileFormat());
                outputFile.write(outputScenario.toFileFormat());
                outputFile.close();
                result = new Alert(Alert.AlertType.NONE, "~", ButtonType.OK);
                result.setTitle("Scenario created!");
                result.setContentText("Your scenario was created successfully!");
                result.showAndWait();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
