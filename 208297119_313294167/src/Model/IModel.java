package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

import java.util.Observer;

public interface IModel {
    int getX();
    int getY();
    void generateMaze(int x, int y);
    Maze getMaze();
    void updateLocation(int direction);
    void assignObserver(Observer o);
    Solution getSolution();
    void solve();
    void setAlgorithmSolution(String algorithmSolution);
    void setMaze(Maze maze);
}


/*
<Menu mnemonicParsing="false" text="Choose a solve algorithm">
<items>
<MenuItem mnemonicParsing="false" onAction="#bfs" text="Breadth first search" />
<MenuItem mnemonicParsing="false" onAction="#dfs" text="Depth first search" />
<MenuItem mnemonicParsing="false" onAction="#best" text="BEST first search" />
<MenuItem mnemonicParsing="false" onAction="#bestND" text="BEST No Diagonals" />
</items>
</Menu>
*/
