package sample;

import Model.IModel;
import Model.MyModel;
import View.MyViewController;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //deleteFolder(new File("resources/saveMaze"));

        MyViewController.window = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Visca Barcelona");
        Scene scene = new Scene(root, 500, 500);
        //primaryStage.setFullScreen(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        IModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.getName().endsWith(".Maze")) {
                    f.delete();
                }
            }
        }
    }
}

