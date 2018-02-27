package ClientGUI;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class BtnsController {
	
	private ChatController ChatController;

    @FXML
    private JFXButton navSettingsBtn;

    @FXML
    private JFXButton navDisconnectBtn;

    @FXML
    private JFXButton navExportBtn;

    @FXML
    private JFXButton navCloseBtn;

    @FXML
    void closeNav(ActionEvent event) {

    }

    @FXML
    void disconnect(ActionEvent event) {
    		//ChatController.disconnect();
    		if(ChatController.connected) {
    			navDisconnectBtn.setText("Disconnect");
    		} else {
    			navDisconnectBtn.setText("Connect");
    		}
    }

    @FXML
    void exportChat(ActionEvent event) {

    }

    @FXML
    void showSettings(ActionEvent event) {

    }

}
