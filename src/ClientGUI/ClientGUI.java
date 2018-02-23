package ClientGUI;

import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ClientGUI extends Application {
	
	private Stage serverStage;
		
    @Override
    public void start(Stage serverStage) throws Exception {
    	
        Parent root = new Parent() {};
        Scene scene = new Scene(root);
        
    	ScreenController screenController = new ScreenController(scene);
    	screenController.addScreen("ServerSelect", FXMLLoader.load(getClass().getResource( "gui/ServerSelect.fxml" )));
    	screenController.activate("ServerSelect");
        
        serverStage.setScene(scene);
        serverStage.setTitle("Chat");
        serverStage.show();
    	serverStage.getIcons().add(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
        
        serverStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                stop();
                System.exit(0);
            }
        });
    }
    
    public void SetWindowTitle(String title) {
        serverStage.setTitle(title);
	}
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop(){
		ScreenController.removeScreen("Chat");
        System.out.println("Stage is closing");
        System.exit(0);
        // Save file
    }
    
}
