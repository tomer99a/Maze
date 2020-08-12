package ViewModel;

import Model.IModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.util.Observable;
import java.util.Observer;

import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;
import static javafx.scene.input.KeyCode.*;

public class MyViewModel extends Observable implements Observer {

    private IModel iModel;
    private Maze maze;
    private int x;
    private int y;

    public void setMaze(Maze maze) {
        this.maze = maze;
        this.x = maze.getStartPosition().getRowIndex();
        this.y = maze.getStartPosition().getColumnIndex();
        iModel.setMaze(maze);
    }

    private Solution solution;

    public MyViewModel(IModel iModel) {
        this.iModel = iModel;
        this.iModel.assignObserver(this);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Solution getSolution() {
        return solution;
    }

    public Maze getMaze() {
        return maze;
    }

    public void generateMaze(int x, int y)
    {
        iModel.generateMaze(x, y);
    }

    public void solve()
    {
        iModel.solve();
    }

    public void ChooseAlgorithm(String algorithmSolution){
        iModel.setAlgorithmSolution(algorithmSolution);
    }

    public void move(KeyEvent keyEvent) {
        int direction = -1;
        switch (keyEvent.getCode()) {
            case NUMPAD8:
                direction = 8;
                break;

            case NUMPAD2:
                direction = 2;
                break;

            case NUMPAD6:
                direction = 6;
                break;

            case NUMPAD4:
                direction = 4;
                break;

            case NUMPAD1:
                direction = 1;
                break;

            case NUMPAD3:
                direction = 3;
                break;

            case NUMPAD7:
                direction = 7;
                break;

            case NUMPAD9:
                direction = 9;
                break;
        }

        iModel.updateLocation(direction);
        keyEvent.consume();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof IModel)
        {
            if(maze == null)//generateMaze
            {
                this.maze = iModel.getMaze();
                x = iModel.getX();
                y = iModel.getY();
            }
            else
            {
                Maze maze = iModel.getMaze();

                if (maze.equals(this.maze))//Not generateMaze
                {
                    int rowChar = iModel.getX();
                    int colChar = iModel.getY();
                    if(x == rowChar && y == colChar)//Solve Maze
                        solution = iModel.getSolution();

                    else//Update location
                    {
                        x = rowChar;
                        y = colChar;
                    }
                }
                else//GenerateMaze
                {
                    this.maze = maze;
                    x = iModel.getX();
                    y = iModel.getY();
                }
            }

            setChanged();
            notifyObservers();
        }
    }
}
