package Model;

import IO.*;
import algorithms.mazeGenerators.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ToSave {

    public static boolean save(String name, Maze maze)
    {
        //String mazePath = tempDirectoryPath + "Maze" + counter + ".Maze";
        OutputStream out = null;
        try
        {
            name = "resources/saveMaze/" + name + ".Maze";
            out = new MyCompressorOutputStream(new FileOutputStream(name));
            out.write(maze.toByteArray());
            out.flush();
            out.close();
        }
        catch (FileNotFoundException e)
        {
            return false;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Maze load(String name)
    {
        name = "resources/saveMaze/" + name + ".Maze";
        try
        {
            InputStream in = new MyDecompressorInputStream(new FileInputStream(name));
            byte savedMazeBytes[] = new byte[1000040];
            in.read(savedMazeBytes);
            in.close();
            return new Maze(savedMazeBytes);
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> allFile()
    {
        List<String> lst = new ArrayList<>();
        List<String> result = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get("resources/saveMaze/")))
        {
            lst = walk.map(x -> x.toString()).filter(f -> f.endsWith(".Maze")).collect(Collectors.toList());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        File file;
        for(int i=0; i<lst.size(); i++)
        {
            file = new File(lst.get(i));
            result.add(file.getName().substring(0, file.getName().length() - 5));
        }

        return result;
    }
}
