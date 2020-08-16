package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

public class MyModel extends Observable implements IModel{

    private static Maze maze;
    private int x;
    private int y;
    private static Solution solution;
    private static int row;
    private static int col;

    public MyModel() {
        maze = null;
        solution = null;
        x = 0;
        y = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Maze getMaze() {
        return maze;
    }

    public void assignObserver(Observer o) {
        this.addObserver(o);
    }

    public void setMaze(Maze newMaze) {
        maze = newMaze;
        this.x = newMaze.getStartPosition().getRowIndex();
        this.y = newMaze.getStartPosition().getColumnIndex();
    }

    public Solution getSolution() {
        return solution;
    }

    public void setAlgorithmSolution(String algorithmSolution)
    {
        String path = "resources/config.properties";
        try (InputStream input = new FileInputStream(path))
        {
            Properties prop = new Properties();
            // load a properties file
            prop.load(input);

            FileOutputStream out = new FileOutputStream(path);
            prop.setProperty("AlgorithmsSearch", algorithmSolution);
            prop.store(out, null);
            out.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void solve()
    {
        maze.setStartPosition(new Position(x, y));
        CommunicateWithServer_SolveSearchProblem();

        setChanged();
        notifyObservers();
    }

    public void generateMaze(int x, int y)
    {
        row = x;
        col = y;
        CommunicateWithServer_MazeGenerating();
        this.x = maze.getStartPosition().getRowIndex();
        this.y = maze.getStartPosition().getColumnIndex();

        setChanged();
        notifyObservers();
    }

    public void updateLocation(int direction)
    {
        int player_row_position = x;
        int player_col_position = y;
        Position pos = new Position(player_row_position, player_col_position);
        Position pos1;
        Position pos2;
        switch (direction){
            case 8:
                if (pos.getRowIndex() - 1 >= 0) {
                    pos = new Position(pos.getRowIndex() - 1, pos.getColumnIndex());
                    if (maze.getValue(pos) == 0) {
                        x--;
                        setChanged();
                        notifyObservers();
                    }
                }
                break;

            case 2:
                if (pos.getRowIndex() + 1 <= maze.getX() - 1) {
                    pos = new Position(pos.getRowIndex() + 1, pos.getColumnIndex());
                    if (maze.getValue(pos) == 0) {
                        x++;
                        setChanged();
                        notifyObservers();
                    }
                }
                break;

            case 6:
                if (pos.getColumnIndex() + 1 <= maze.getY() - 1) {
                    pos = new Position(pos.getRowIndex(), pos.getColumnIndex() + 1);
                    if (maze.getValue(pos) == 0) {
                       y++;
                        setChanged();
                        notifyObservers();
                    }
                }
                break;

            case 4:
                if (pos.getColumnIndex() - 1 >= 0) {
                    pos = new Position(pos.getRowIndex(), pos.getColumnIndex() - 1);
                    if (maze.getValue(pos) == 0) {
                        y--;
                        setChanged();
                        notifyObservers();
                    }
                }
                break;

            case 1:
                if ((pos.getRowIndex() + 1 <= maze.getX() - 1) && (pos.getColumnIndex() - 1 >= 0)) {
                    pos1 = new Position(player_row_position+1, player_col_position);
                    pos2 = new Position(player_row_position, player_col_position-1);
                    pos = new Position(pos.getRowIndex() + 1, pos.getColumnIndex() - 1);
                    if (((maze.getValue(pos1) == 0)|| (maze.getValue(pos2) == 0)) && (maze.getValue(pos) == 0)) {
                        x++;
                        y--;
                        setChanged();
                        notifyObservers();
                    }
                }
                break;

            case 3:
                if ((pos.getRowIndex()+1 <= maze.getX()-1)&&(pos.getColumnIndex()+1 <= maze.getY()-1)) {
                    pos1 = new Position(player_row_position+1, player_col_position);
                    pos2 = new Position(player_row_position, player_col_position+1);
                    pos = new Position(pos.getRowIndex() + 1, pos.getColumnIndex() + 1);
                    if (((maze.getValue(pos1) == 0)|| (maze.getValue(pos2) == 0)) && (maze.getValue(pos) == 0)) {
                        x++;
                        y++;
                        setChanged();
                        notifyObservers();
                    }
                }
                break;

            case 7:
                if ((pos.getRowIndex() - 1 >= 0) && (pos.getColumnIndex() - 1 >= 0)) {
                    pos1 = new Position(player_row_position-1, player_col_position);
                    pos2 = new Position(player_row_position, player_col_position-1);
                    pos = new Position(pos.getRowIndex() - 1, pos.getColumnIndex() - 1);
                    if (((maze.getValue(pos1) == 0)|| (maze.getValue(pos2) == 0)) && (maze.getValue(pos) == 0)) {
                        x--;
                        y--;
                        setChanged();
                        notifyObservers();
                    }
                }
                break;

            case 9:
                if ((pos.getRowIndex()-1 >= 0)&&(pos.getColumnIndex()+1 <= maze.getY()-1)) {
                    pos1 = new Position(player_row_position-1, player_col_position);
                    pos2 = new Position(player_row_position, player_col_position+1);
                    pos = new Position(pos.getRowIndex() - 1, pos.getColumnIndex() + 1);
                    if (((maze.getValue(pos1) == 0)|| (maze.getValue(pos2) == 0)) && (maze.getValue(pos) == 0)) {
                        x--;
                        y++;
                        setChanged();
                        notifyObservers();
                    }
                }
                break;
        }
    }

    private static void CommunicateWithServer_MazeGenerating() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer)
                {
                    try
                    {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row, col};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[1000040 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer)
                {
                    try
                    {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        //maze.print();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        solution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
