package View;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.List;
import java.util.Optional;

public class Boxes {

    private static Pair<String, String > pair = new Pair<>("-1", "-1");
    private static Boolean bool = false;
    private static String str = null;

    public static void information(String headerMessage, String message, String title)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerMessage);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void error(String headerMessage, String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("error window");
        alert.setHeaderText(headerMessage);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Boolean confirmation(String headerMessage, String message)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation window");
        alert.setHeaderText(headerMessage);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent(button -> bool = button == ButtonType.OK);
        return bool;
    }

    public static String text(String headerMessage, String message, String defaultValue)
    {
        TextInputDialog dialog = new TextInputDialog(defaultValue);
        dialog.setTitle("Text window");
        dialog.setHeaderText(headerMessage);
        dialog.setContentText(message);
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent())
            result.ifPresent(txt -> str = txt);
        else
            str = "my name is inigo montoya";

        return str;
    }

    public static String choice(String headerMessage, String message, List<String> choices)
    {
        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Choice window");
        dialog.setHeaderText(headerMessage);
        dialog.setContentText(message);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent())
            result.ifPresent(txt -> str = txt);
        else
            str = "my name is inigo montoya";

        return str;
    }

    public static Pair<String, String> getSize(String a, String b, String title, String default_a, String default_b) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        ButtonType ok = ButtonType.OK;
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField textA = new TextField(default_a);
        TextField textB = new TextField(default_b);

        grid.add(new Label(a), 0, 0);
        grid.add(textA, 1, 0);
        grid.add(new Label(b), 0, 1);
        grid.add(textB, 1, 1);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ok)
                return new Pair<>(textA.getText(), textB.getText());
            return new Pair<>("-1948", "-1948");
        });

        dialog.getDialogPane().setContent(grid);
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(resultPair -> pair = resultPair);
        return pair;
    }
}