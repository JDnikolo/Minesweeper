package medialab.minesweeper.gameLogic;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import medialab.minesweeper.customExceptions.*;

public class scenario {
    public byte diff;
    public int mines;
    public int time;
    public boolean uber;

    /**
     * Copy class constructor.
     * Creates a new scenario instance using the parameters found in the existing scenario @sc.
     * @param sc the scenario instance to be copied
     */
    public scenario(scenario sc){
        diff=sc.diff;
        mines=sc.mines;
        time=sc.time;
        uber=sc.uber;
    }

    /**
     * Default class constructor.
     * Creates a level 1 scenario using the minimum mine amount and the maximum completion time.
     */
    public scenario(){
        this.diff=1;
        this.mines=9;
        this.time = 180;
        this.uber=false;
    }

    /**
     * Class constructor with specific scenario parameters.
     * This creates a new scenario instance using the passed parameters.
     * This does not check whether the parameters are within the appropriate
     * bounds for each difficulty.
     * @param d int: the difficulty of the created scenario (1 or 2)
     * @param m int: number of mines the scenario will include
     * @param t int: the available time to beat the scenario
     * @param u boolean: whether the scenario includes an uber-mine
     */
    public scenario(byte d, int m, int t,boolean u){
        diff=d;
        mines=m;
        time=t;
        uber=u;
    }

    /**
     * Static method for scenario instance creation from a file in the medialab folder.
     * Reads the scenario parameters from filename in the medialab folder of the application,
     * validates that they are within the bounds of each difficulty and returns the
     * scenario instance. The file should contain the parameters,each on a new line, in this order:
     * <p>difficulty(int)</p>
     * <p>mines(int)</p>
     * <p>time(int)</p>
     * <p>uber-mine(0 or 1)</p>
     * @param filename a string containing the full name of the file to read from
     * @return the scenario created from filename
     * @throws FileNotFoundException
     * @throws InvalidDescriptionException
     * @throws InvalidValueException
     */
    static public scenario readFromFile(String filename) throws FileNotFoundException, InvalidDescriptionException, InvalidValueException {
        byte d;
        int m;
        int t;
        int u;
        File file = new File("medialab\\"+filename);
        Scanner sc = new Scanner(file);
        try {
            d = sc.nextByte();
            m = sc.nextInt();
            t = sc.nextInt();
            u = sc.nextInt();
        } catch (InputMismatchException e){
            throw new InvalidDescriptionException("Invalid gameLogic.scenario description in file \""+filename+"\"",e);
        } catch (NoSuchElementException e){
            throw new InvalidDescriptionException("Invalid gameLogic.scenario description in file \""+filename+"\"",e);
        }
        if (d!=1 && d!=2){
            throw new InvalidValueException(filename+": invalid difficulty.");
        } else if (d==1) {
            if (m>11 || m<9) throw new InvalidValueException(filename+": invalid mine amount.");

            if (t<120 || t>180) throw new InvalidValueException(filename+": invalid time.");

            if (u!=0) throw new InvalidValueException(filename+": invalid uber-mine flag.");
        } else { //d==2
            if (m>45 || m<35) throw new InvalidValueException(filename+": invalid mine amount.");

            if (t<240 || t>360) throw new InvalidValueException(filename+": invalid time.");

            if (u!=1) throw new InvalidValueException(filename+": invalid mine amount.");
    }
        return new scenario(d,m,t,u==1);
    }

    /**
     * String output of the scenario instance parameters.
     * This builds a string composed of four lines, each containing a parameter of the scenario
     * instance, in the order: difficulty,mines,available time,uber-mine.
     * @return the string that describes the scenario, in the same format used by readFromFile
     */
    public String toFileFormat(){
        StringBuilder output = new StringBuilder();
        output.append(diff).append('\n');
        output.append(mines).append('\n');
        output.append(time).append('\n');
        output.append((uber)?1:0);
        return output.toString();
    }
}
