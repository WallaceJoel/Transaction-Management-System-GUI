package proj3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class which runs the project.
 * 
 * @author Nicholas McConnell, Joel Wallace ncm78 jtw91
 */
public class RunProject extends Application {
   Stage window;

   @Override
   public void start(Stage stage) throws Exception {
      Parent root = FXMLLoader.load(getClass().getResource("proj3FX.fxml"));
      Scene scene = new Scene(root, 1000, 1000);
      stage.setTitle("Welcome to the Bank");
      stage.setScene(scene);
      window = stage;
      window.show();
   }

   public static void main(String[] args) {
      launch(args);
   }

}
