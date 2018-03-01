package ClientGUI;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

	
public class BtnsController {
	ChatController cont = new ChatController();
	
	private ChatController ClearController;

    @FXML
    private JFXButton navSettingsBtn;

    @FXML
    private JFXButton navDisconnectBtn;

    @FXML
    private JFXButton navExportBtn;

    @FXML
    private JFXButton navCloseBtn;

    @FXML
    void clearNav(ActionEvent event) {

    }

    @FXML
    void disconnect(ActionEvent event) {
    		//ChatController.disconnect();
    	System.out.println(cont.connected);
    		if(cont.connected) {
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
