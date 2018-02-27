package ClientGUI;

import com.jfoenix.controls.JFXButton;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class BtnsController {
	
	ChatController c = new ChatController();
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXButton settings;

    @FXML
    private JFXButton dissconnect;

    @FXML
    private JFXButton export;

    @FXML
    private JFXButton close;

    @FXML
    void DisconnectAct(ActionEvent event) {
    		c.DisconnectPress(event);
    }

    @FXML
    void closeAct(ActionEvent event) {

    }

    @FXML
    void exportAct(ActionEvent event) {

    }

    @FXML
    void settingsAct(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert settings != null : "fx:id=\"settings\" was not injected: check your FXML file 'Btns.fxml'.";
        assert dissconnect != null : "fx:id=\"dissconnect\" was not injected: check your FXML file 'Btns.fxml'.";
        assert export != null : "fx:id=\"export\" was not injected: check your FXML file 'Btns.fxml'.";
        assert close != null : "fx:id=\"close\" was not injected: check your FXML file 'Btns.fxml'.";

    }
}
