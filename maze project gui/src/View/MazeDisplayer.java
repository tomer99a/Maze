package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MazeDisplayer extends Canvas {


    public MazeDisplayer() {
        // Redraw canvas when size changes.
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }



    private boolean showSolution = false;
    private boolean win = false;
    private boolean showImegeYellow = false;
    private Maze maze;
    private List<Position> poslst;

    private int row_player =0;
    private int col_player =0;

    private StringProperty imageFilewin = new SimpleStringProperty();
    private StringProperty imageFileNameGoal = new SimpleStringProperty();
    private StringProperty imageFileNamePlayer = new SimpleStringProperty();
    private StringProperty imagePlayerYellow = new SimpleStringProperty();
    private StringProperty imageGoalYellow = new SimpleStringProperty();
    private StringProperty imageStart = new SimpleStringProperty();


    private String getImageFilewin() {
        return imageFilewin.get();
    }
    private String getImagestart() {
        return imageStart.get();
    }

    private String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }


    private String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    private String getImagePlayerYellow() {
        return imagePlayerYellow.get();
    }

    private String getGoalYellow() {
        return imageGoalYellow.get();
    }

    void setGoalYellow(String imageGoalYellow) {
        this.imageGoalYellow.set(imageGoalYellow);
    }

    void setImagePlayerYellow(String imageFileNameWall) {
        this.imagePlayerYellow.set(imageFileNameWall);
    }

    void setImageFileNwin(String imageFileNameWall) {
        this.imageFilewin.set(imageFileNameWall);
    }

    void setImagestart(String imageFileNameWall) {
        this.imageStart.set(imageFileNameWall);
    }

    boolean isShowSolution() {
        return showSolution;
    }

    void setWin(boolean win) {
        this.win = win;
    }

    void setShowImegeYellow() {
        showImegeYellow = !showImegeYellow;
        draw();
    }

    void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    int getRow_player() {
        return row_player;
    }

    int getCol_player() {
        return col_player;
    }

    void set_player_position(int row, int col){
        this.row_player = row;
        this.col_player = col;
        draw();
    }

    void setShowSolutiontoFalse()
    {
        showSolution = false;
    }

    void solve(Solution solution)
    {
        if (!showSolution)
        {
            poslst = new ArrayList<>();
            for (AState state : solution.getSolutionPath())
                poslst.add(state.getState());
        }
        showSolution = !showSolution;
        draw();
    }

    void win()
    {
        GraphicsContext graphicsContext = getGraphicsContext2D();
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        Image winImage = null;
        try {
            winImage = new Image(new FileInputStream(getImageFilewin()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no Win Image....");
        }
        graphicsContext.drawImage(winImage,0,0,canvasWidth,canvasHeight);
        showSolution = false;
    }

    void startStage()
    {
        GraphicsContext graphicsContext = getGraphicsContext2D();
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        Image startImage = null;
        try {
            startImage = new Image(new FileInputStream(getImagestart()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no Goal Image....");
        }
        graphicsContext.drawImage(startImage,0,0,canvasWidth,canvasHeight);
    }

    void drawMaze(Maze maze)
    {
        this.maze = maze;
        set_player_position(maze.getStartPosition().getRowIndex(), maze.getStartPosition().getColumnIndex());
    }

    void draw()
    {
        if (win)
            win();
        else if (maze == null)
            startStage();
        else
        {
            double canvasHeight = getHeight() ;
            double canvasWidth = getWidth();
            int row = maze.getX();
            int col = maze.getY();
            double cellHeight = canvasHeight/row;
            double cellWidth = canvasWidth/col;
            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
            graphicsContext.setFill(Color.RED);
            double w,h;
            //Draw Maze
            for(int i=0;i<row;i++)
            {
                for(int j=0;j<col;j++)
                {
                    h = i * cellHeight;
                    w = j * cellWidth;

                    if(maze.getValue(new Position(i, j)) == 1) // Wall
                    {
                        graphicsContext.setFill(Color.RED);
                        graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                    }
                    else
                    {
                        graphicsContext.setFill(Color.rgb(0, 255, 245));
                        graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                    }
                    if (showSolution && poslst.contains(new Position(i, j)))
                    {
                        graphicsContext.setFill(Color.YELLOW);
                        graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                    }
                }
            }

            double h_player = getRow_player() * cellHeight;
            double w_player = getCol_player() * cellWidth;
            Image playerImage = null;
            try {
                if ((showSolution && poslst.contains(new Position(getRow_player(), getCol_player()))) || showImegeYellow)
                    playerImage = new Image(new FileInputStream(getImagePlayerYellow()));
                else
                    playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image player....");
            }

            double h_goal = maze.getGoalPosition().getRowIndex() * cellHeight;
            double w_goal = maze.getGoalPosition().getColumnIndex() * cellWidth;
            Image goalImage = null;
            try {
                if (showSolution || showImegeYellow)
                    goalImage = new Image(new FileInputStream(getGoalYellow()));
                else
                    goalImage = new Image(new FileInputStream(getImageFileNameGoal()));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Goal Image....");
            }
            graphicsContext.drawImage(goalImage,w_goal,h_goal,cellWidth,cellHeight);
            graphicsContext.drawImage(playerImage,w_player,h_player,cellWidth,cellHeight);
        }
    }
}
