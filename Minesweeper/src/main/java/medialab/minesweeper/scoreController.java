package medialab.minesweeper;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.nio.Buffer;

public class scoreController {
    @FXML
    GridPane scoreGrid;

    @FXML
    protected void initialize(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("medialab\\scores.txt"));
            String line;
            Text toAdd;
            int start = 1;
            String color;
            while ((line=reader.readLine())!=null){
                String[] stuff=line.split(", ");
                if (stuff[3].equals("CPU")){
                    color="Red";
                }else color = "blue";
                toAdd = new Text(""+start);
                toAdd.setFill(Paint.valueOf(color));
                int item=1;
                for (String s:stuff){
                    toAdd=new Text(s);
                    toAdd.setFill(Paint.valueOf(color));
                    scoreGrid.add(toAdd,item++,start);
                }
                start++;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
