package View;

import Model.ToSave;
import Server.*;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.*;
import algorithms.search.Solution;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MyViewController extends Canvas implements Initializable, Observer, IView {
    public static Stage window;
    public MenuItem saveItem;
    public MenuItem loadItem;
    public MenuItem solveItem;
    public VBox vBox;
    private MazeDisplayer mazeDisplayer;
    private Maze currentMaze;
    private Solution solution;
    private int numOfSavedMaze;
    private ZoomPane zoomPane;
    private static MediaPlayer mediaPlayer;
    private String soundPath;
    private String previousPath;

    private StringProperty update_player_position_row = new SimpleStringProperty();
    private StringProperty update_player_position_col = new SimpleStringProperty();
    private MyViewModel viewModel;
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mazeDisplayer = new MazeDisplayer();
        mazeDisplayer.setOnKeyPressed(event -> keyPressed(event));
        mazeDisplayer.setOnKeyReleased(event -> keyReleased(event));
        zoomPane = new ZoomPane(mazeDisplayer);
        mazeDisplayer.setImagestart("resources/wellcome.jpg");

        mazeDisplayer.widthProperty().bind(zoomPane.widthProperty());
        mazeDisplayer.heightProperty().bind(zoomPane.heightProperty());
        zoomPane.setStyle("-fx-border-color: black;");
        zoomPane.setPrefHeight(1600);


        vBox.getChildren().addAll(zoomPane);

        //set close properly
        window.setOnCloseRequest(e -> {
            e.consume();
            exit();
        });



        window.setResizable(true);

        numOfSavedMaze = ToSave.allFile().size();
        if (numOfSavedMaze == 0)
            loadItem.setDisable(true);
        saveItem.setDisable(true);
        solveItem.setDisable(true);

        pokemon();
        sound();
        mediaPlayer.stop();

        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        solveSearchProblemServer.start();
        mazeGeneratingServer.start();
    }

    public void exit() {
        if (Boxes.confirmation(null, "Are you sure you want to exit?"))
        {
            window.close();
            mazeGeneratingServer.stop();
            solveSearchProblemServer.stop();
        }
    }

    public void New() {
        String row, col, title, default_a, default_b;
        row = "Enter number of rows";
        col = "Enter number if columns";
        title = "make a new game!";
        default_a = "10";
        default_b = "10";

        while (true) {
            Pair<String, String> pair = Boxes.getSize(row, col, title, default_a, default_b);
            if (pair.getKey().equals("-1948") && pair.getValue().equals("-1948"))
                break;

            int x = tryParse(pair.getKey());
            int y = tryParse(pair.getValue());
            if (x > 1 && y > 1 && x <= 1000 && y <= 1000) {

                genrateNewMaze();
                viewModel.generateMaze(x, y);

                mazeDisplayer.drawMaze(currentMaze);
                window.setTitle("Visca Barcelona");
                break;
            } else
                Boxes.error(null, "one or more argument are not correct");
        }
    }

    public void save() {
        if (currentMaze == null) {
            Boxes.error(null, "There are no maze to save");
            return;
        }
        while (true) {
            String name = Boxes.text(null, "name of your maze", "maze");
            if (name.equals("my name is inigo montoya"))
                break;
            if (ToSave.allFile().contains(name))
                Boxes.error(null, "this name already exist");
            else {
                currentMaze.setStartPosition(new Position(mazeDisplayer.getRow_player(), mazeDisplayer.getCol_player()));
                ToSave.save(name, currentMaze);
                numOfSavedMaze++;
                window.setTitle(name);
                loadItem.setDisable(false);
                break;
            }
        }
    }

    public void load() {
        List<String> lst = ToSave.allFile();
        if (lst.size() == 0) {
            saveItem.setDisable(true);
            solveItem.setDisable(true);
            Boxes.error(null, "There are no mazes");
        }
        else {
            String name = Boxes.choice(null, "name of your maze", lst);
            if (!name.equals("my name is inigo montoya")) {
                lst = ToSave.allFile();
                if (lst.contains(name)) {
                    genrateNewMaze();
                    currentMaze = ToSave.load(name);

                    if (currentMaze == null)
                        return;


                    mazeDisplayer.drawMaze(currentMaze);
                    window.setTitle(name);
                    viewModel.setMaze(currentMaze);
                } else {
                    saveItem.setDisable(true);
                    solveItem.setDisable(true);
                    Boxes.error(null, "Error");
                }
            }
        }
    }
    public void deleteAllSavedMazes()
    {
        deleteAllfrom("resources/saveMaze", ".Maze");
    }

    private void deleteAllfrom(String path, String end) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.getName().endsWith(end)) {
                    f.delete();
                }
            }
        }
        loadItem.setDisable(true);
        Boxes.information(null, "all mazes deleted","Information window");
    }

    public void argentina() {
        mazeDisplayer.setImageFileNamePlayer("resources/argentina blue.jpg");
        mazeDisplayer.setImageFileNameGoal("resources/cup blue.jpg");
        mazeDisplayer.setImagePlayerYellow("resources/argentina yellow.jpg");
        mazeDisplayer.setImageFileNwin("resources/maradona.jpg");
        mazeDisplayer.setGoalYellow("resources/cup yellow.jpg");
        soundPath = "resources/Vamos Vamos Argentina.mp3";
        previousPath = "resources/Vamos Vamos Argentina.mp3";
        mazeDisplayer.draw();

        if(currentMaze != null)
            sound();
    }

    public void pacman()
    {
        mazeDisplayer.setImageFileNamePlayer("resources/pacman blue.jpg");
        mazeDisplayer.setImageFileNameGoal("resources/ghost blue.jpg");
        mazeDisplayer.setImagePlayerYellow("resources/pacman yellow.jpg");
        mazeDisplayer.setImageFileNwin("resources/pacman win.jpg");
        mazeDisplayer.setGoalYellow("resources/ghost yellow.jpg");
        soundPath = "resources/PacMan Original Theme.mp3";
        previousPath = "resources/PacMan Original Theme.mp3";
        mazeDisplayer.draw();

        if(currentMaze != null)
            sound();
    }

    public void pokemon()
    {
        mazeDisplayer.setImageFileNamePlayer("resources/ash Blue.jpg");
        mazeDisplayer.setImageFileNameGoal("resources/charmander blue.jpg");
        mazeDisplayer.setImagePlayerYellow("resources/ash yellow.jpg");
        mazeDisplayer.setImageFileNwin("resources/ash win.jpg");
        mazeDisplayer.setGoalYellow("resources/charmander yellow.jpg");
        soundPath = "resources/Pokemon Theme Song.mp3";
        previousPath = "resources/Pokemon Theme Song.mp3";
        mazeDisplayer.draw();

        if(currentMaze != null)
            sound();
    }

    public void solve() {
        solveCode();
    }

    private void solveCode()
    {
        if (currentMaze != null && !mazeDisplayer.isShowSolution())
            viewModel.solve();
        else
        {
            mazeDisplayer.setShowSolutiontoFalse();
            mazeDisplayer.draw();
        }
    }

    public void bfs() {
        viewModel.ChooseAlgorithm("BFS");
    }

    public void bestND() {
        viewModel.ChooseAlgorithm("BestNoDiagonals");
    }

    public void best() {
        viewModel.ChooseAlgorithm("BestFirstSearch");
    }

    public void dfs() {
        viewModel.ChooseAlgorithm("DFS");
    }

    public void helpMe() {
        String message = "You can move your player with the numpad numbers\n";
        message += "You can't get out of boundaries and move on walls\n";
        message += "You can move in slant with the numbers 1,3,7,9\n";
        message += "The goal is to get to the target that you can choose in settings on options \n";
        message += "You can creat a new maze all the time by click on the new button\n";
        message += "You can also save and load all your mazes to and from the disk\n";
        message += "Good luck and have fun!\n";
        Boxes.information(null, message,"How to play");
    }

    private int tryParse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.NUMPAD5)
            solveCode();
        else if (keyEvent.getCode() == KeyCode.F)
            mazeDisplayer.setShowImegeYellow();
        else if (keyEvent.getCode() == KeyCode.ESCAPE)
            exit();
        else if (keyEvent.getCode() == KeyCode.CONTROL)
            zoomPane.setCtrl(true);
        else if (keyEvent.getCode() == KeyCode.N)
            New();
        else if (currentMaze != null)
            viewModel.move(keyEvent);
    }

    private void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.CONTROL)
            zoomPane.setCtrl(false);
        mazeDisplayer.requestFocus();
    }

    public String get_update_player_position_row() {
        return update_player_position_row.get();
    }

    private void set_update_player_position_row(String x) {
        update_player_position_row.set(x);
    }

    public String get_update_player_position_col() {
        return update_player_position_col.get();
    }

    private void set_update_player_position_col(String y) {
        update_player_position_col.set(y);
    }

    private void sound() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
        Media sound = new Media(new File(soundPath).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private void genrateNewMaze()
    {
        if (previousPath != null) {
            soundPath = previousPath;
            previousPath = null;
        }
        if (currentMaze == null)
            sound();
        mazeDisplayer.setWin(false);

        mazeDisplayer.requestFocus();
        mazeDisplayer.setShowSolutiontoFalse();
        saveItem.setDisable(false);
        solveItem.setDisable(false);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
        //System.out.println(mouseEvent.getClickCount());
    }

    private void finish() {
        previousPath = soundPath;
        soundPath = "resources/Queen + Adam Lambert - You Are The Champions.mp3";
        sound();

        mazeDisplayer.setWin(true);
        mazeDisplayer.draw();
        currentMaze = null;
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void creators() {
        String message = "The creators of this project are Tomer Aizicovich and Shahar Gefen";
        Boxes.information(null, message, "Creators");

    }
    public void aboutsolve(){
        String message = "The solve algorithm is Best Search First without slants";
        Boxes.information(null, message, "About solve");
    }

    public void Properties()
    {
        try (InputStream input = new FileInputStream("resources/config.properties"))
        {
            Properties prop = new Properties();
            prop.load(input);
            String message = "The search algorithm is " + prop.getProperty("AlgorithmsSearch") + "\n";
            message += "The generate algorithm is " + prop.getProperty("AlgorithmsGenerators") + "\n";
            message += "The number of  threads is " + prop.getProperty("NumOfThreads") + "\n";
            Boxes.information(null, message, "Properties");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MyViewModel) {
            if (currentMaze == null)//generateMaze
            {
                currentMaze = viewModel.getMaze();
                mazeDisplayer.drawMaze(currentMaze);
            } else {
                Maze maze = viewModel.getMaze();

                if (maze == currentMaze)//Not generateMaze
                {
                    int rowChar = mazeDisplayer.getRow_player();
                    int colChar = mazeDisplayer.getCol_player();
                    int rowFromViewModel = viewModel.getX();
                    int colFromViewModel = viewModel.getY();

                    if (rowFromViewModel == rowChar && colFromViewModel == colChar)//Solve Maze
                    {
                        solution = viewModel.getSolution();
                        if (solution != null)
                            this.mazeDisplayer.solve(solution);
                    } else//Update location
                    {
                        set_update_player_position_row(rowFromViewModel + "");
                        set_update_player_position_col(colFromViewModel + "");
                        this.mazeDisplayer.set_player_position(rowFromViewModel, colFromViewModel);

                        if ((colFromViewModel == currentMaze.getGoalPosition().getColumnIndex()) && (rowFromViewModel == currentMaze.getGoalPosition().getRowIndex()))
                            finish();
                    }
                } else//GenerateMaze
                {
                    this.currentMaze = maze;
                    mazeDisplayer.drawMaze(currentMaze);
                }
            }
        }
    }
}