package medialab.minesweeper;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import medialab.minesweeper.customExceptions.InvalidDescriptionException;
import medialab.minesweeper.customExceptions.InvalidValueException;
import medialab.minesweeper.gameLogic.scenario;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LoadScenarioController {
    scenario loadedScenario;
    @FXML
    TextField scenarioField;
    @FXML
    CheckBox onSuccessClose, onSuccessStart;
    @FXML
    Text messageText;

    @FXML
    protected void findScenario() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("base.fxml"));
        Parent root = (Parent)loader.load();
        MainPageController controller = (MainPageController) loader.getController();
        String filename=scenarioField.getText()+".txt";
        scenario toLoad;
        if (filename.length()==0){
            messageText.setText("The filename cannot be empty!");
            return;
        }
        try{
           toLoad = scenario.readFromFile(filename);
        } catch (FileNotFoundException e) {
            messageText.setText("The file was not found,\nis the name correct?");
            return;
        } catch (InvalidDescriptionException ide){
            messageText.setText("The file's format is incorrect.");
            return;
        } catch (InvalidValueException ive){
            messageText.setText("The file's values are incorrect.");
            return;
        }
        loadedScenario=toLoad;
        messageText.setText("Loaded "+filename+" successfully.");
        controller.setScenario(loadedScenario);
        if (onSuccessStart.isSelected()){
            Stage stage = (Stage) onSuccessStart.getScene().getWindow();
            stage.setOnCloseRequest(event ->{
                stage.close();
            });
        }
        if (onSuccessClose.isSelected()){
            Stage stage = (Stage) onSuccessClose.getScene().getWindow();
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished( event -> stage.close() );
            delay.play();
        }
    }
}
