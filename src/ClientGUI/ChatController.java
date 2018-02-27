package ClientGUI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.prefs.Preferences;

import org.apache.commons.io.FileUtils;
import org.controlsfx.control.Notifications;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class ChatController {
	
	private String server, username;
	private int port;
	public boolean connected;
	
	private Client client;
	
	//  app prefs - https://stackoverflow.com/questions/4017137/how-do-i-save-preference-user-settings-in-java
	public Preferences prefs = Preferences.userNodeForPackage(ClientGUI.class);
	
	// detech OS
	private static String OS = System.getProperty("os.name").toLowerCase();

    @FXML
    private JFXTextArea MsgArea;

    @FXML
    private JFXTextField msgFld;

    @FXML
    private JFXButton sendBtn;

    @FXML
    private JFXListView<String> UserList = new JFXListView<String>();


    @FXML
    private JFXButton fileBtn;

    @FXML
    private JFXButton exportBtn;

    @FXML
    private JFXButton disconnectBtn;

    @FXML
    private JFXButton open;

    @FXML
    private Text connectedLabel;


    @FXML
    private JFXDrawer drawer;
    
    @FXML
    private JFXHamburger hamburger;
    
    public void setSideBar() {
    
    	HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED,(e)->{
            transition.setRate(transition.getRate()*-1);
            transition.play();

        		drawer.toFront();
            if(drawer.isShown())
            {
            	drawer.close();
            }else {
            	drawer.open();
            }
                
        });
    
    	try {
    		AnchorPane box = FXMLLoader.load(getClass().getResource("gui/Btns.fxml"));
			drawer.setSidePane(box);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
	@FXML
	public void initialize() {
		setSideBar();
		server = prefs.get("server_address", "");
		port = Integer.parseInt(prefs.get("server_port", ""));
		username = prefs.get("username", "");
		
		
		try {
			client = new Client(server, port, username, this);
			if(client.start()) {
				connected = true;
				connectedLabel.setText("Connected");
				msgFld.setDisable(false);
				sendBtn.setDisable(false);
				fileBtn.setDisable(false);
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
				getMsgWhileOffline();
			} else {
				connected = false;
				connectedLabel.setText("Disconnected");
				msgFld.setDisable(true);
				sendBtn.setDisable(true);
				fileBtn.setDisable(true);
				disconnectBtn.setText("Connect");
			}
		}catch (Exception e) {
			append("Error connecting");
			connectedLabel.setText("Disconnected");
			msgFld.setDisable(true);
			sendBtn.setDisable(true);
			fileBtn.setDisable(true);
			disconnectBtn.setText("Connect");
		}
	}
	
	public void disconnect() {
		if(connected) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			disconnectBtn.setText("Connect");
			msgFld.setDisable(true);
			sendBtn.setDisable(true);
			fileBtn.setDisable(true);
			connectedLabel.setText("Disconnected");
			UserList.getItems().clear();
			connected = false;
		} else if (!connected) {
			reconnectToServer();
		}
	}

    @FXML
    void DisconnectPress(ActionEvent event) {
		if(connected) {
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			disconnectBtn.setText("Connect");
			msgFld.setDisable(true);
			sendBtn.setDisable(true);
			fileBtn.setDisable(true);
			connectedLabel.setText("Disconnected");
			UserList.getItems().clear();
			connected = false;
		} else if (!connected) {
			reconnectToServer();
		}
    }

    @FXML
    void FilePress(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select File < 5mb");
    	File selectedFile = fileChooser.showOpenDialog(null);
    	
    	if (selectedFile != null) {
    		if(selectedFile.length()>5242880) { // limit file to 5mb
    			ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
		        Notifications.create().title("Chat: "+server+":"+port).text("File selected is to large. Select a file less than 5mb").graphic(icon).show();
    		} else {
			    ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
		        Notifications.create().title("Chat: "+server+":"+port).text("File selected: " + selectedFile.getName()).graphic(icon).show();
		        try {
					SendFile(selectedFile);
				} catch (IOException e1) {
					Notifications.create().title("Chat: "+server+":"+port).text("Error Sending File: " + selectedFile.getName()).showError();
				}
    		}
    	}
    }

    @FXML
    void SendMsgPress(ActionEvent event) {
		if(!msgFld.getText().equals("")) {
			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msgFld.getText()));
			msgFld.setText("");
		}
    }

    @FXML
    void SendMsgPressedEnter(ActionEvent event) {
    		if(!msgFld.getText().equals("")) {
    			client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msgFld.getText()));
    			msgFld.setText("");
    		}
    }
	
    @FXML
    void exportBtnPress(ActionEvent event) throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		if(isWindows()) {
			File userDirectory = new File(System.getProperty("user.home")+"\\Downloads");
			if(!userDirectory.canRead())
			    userDirectory = new File("c:/");
			fileChooser.setInitialDirectory(userDirectory);
		}
		
		fileChooser.setInitialFileName("ChatExport"+new Timestamp(System.currentTimeMillis())+".txt");
		File savedFile = fileChooser.showSaveDialog(null);

		if (savedFile != null) {
			
	    		BufferedWriter out = new BufferedWriter(new FileWriter(savedFile));
		    	try {
		    	    out.write(MsgArea.getText());
		    	}
		    	catch (IOException e)
		    	{
		    	    System.out.println("Exception ");
		    	    Notifications.create().title("Chat: "+server+":"+port).text("An ERROR occurred while saving the file").showError();
		    	}
		    	finally
		    	{
		    	    out.close();
				    ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
			        Notifications.create().title("Chat: "+server+":"+port).text("File saved: " + savedFile.toString()).graphic(icon).show();
		    	}
		}

    }
    
    private void reconnectToServer() {
		try {
			client = new Client(server, port, username, this);
			if(client.start()) {
				connected = true;
				disconnectBtn.setText("Disconnect");
				connectedLabel.setText("Connected");
				msgFld.setDisable(false);
				sendBtn.setDisable(false);
				fileBtn.setDisable(false);
				client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
			}
			
			client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
		} catch (Exception e) {
			connectedLabel.setText("Disconnected");
			msgFld.setDisable(true);
			sendBtn.setDisable(true);
			fileBtn.setDisable(true);
			connected = false;
		}
	}

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
    
    public void append(String msg) {
		try {
			if(msg.contains("ServerResetUserList:")) {
				Platform.runLater(new Runnable() {
    			    @Override
    			    public void run() {
    			    	UserList.getItems().clear();
    			    }
    			});
			} else {
				if(msg.contains("ServerAddToUserList:")) {
					Platform.runLater(new Runnable() {
	    			    @Override
	    			    public void run() {
	    					UserList.getItems().add(msg.replace("ServerAddToUserList:", ""));
	    			    }
	    			});
				} else if(msg.contains("|ClintSendFile|")) {
					//System.out.println(msg);
					
					String s = new String(msg);
					
					String[] separatedMsg = s.split("\\|");

					String userAndTime = separatedMsg[0];
					String fileName = separatedMsg[2];
					String fileEncoded = msg.replace(userAndTime + "|ClintSendFile|" + fileName + "|", "");
					 
					System.out.println("Saving: "+ fileName);
					
					byte[] encoded = fileEncoded.getBytes("ISO-8859-1");

					if(!msg.contains(username)) {
						//  save file as
						Platform.runLater(new Runnable() {
			    			    @Override
			    			    public void run() {
			    					FileChooser fileChooser = new FileChooser();
			    					fileChooser.setTitle("Save File");
			    					if(isWindows()) {
				    					File userDirectory = new File(System.getProperty("user.home")+"\\Downloads");
				    					if(!userDirectory.canRead())
				    					    userDirectory = new File("c:/");
				    					fileChooser.setInitialDirectory(userDirectory);
			    					}
			    					
			    					fileChooser.setInitialFileName(separatedMsg[2]);
			    					File savedFile = fileChooser.showSaveDialog(null);
		
			    					if (savedFile != null) {
		
			    					    try {
			    					    	FileUtils.writeByteArrayToFile(savedFile, encoded);
			    					    }
			    					    catch(IOException e) {
			    						
			    					        e.printStackTrace();
			    					        Notifications.create().title("Chat: "+server+":"+port).text("An ERROR occurred while saving the file").showError();
			    					        return;
			    					    }
			    					    ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
			    				        Notifications.create().title("Chat: "+server+":"+port).text("File saved: " + savedFile.toString()).graphic(icon).show();
			    					}
		    					}
			    			});
					}
					
					userAndTime=null;
					fileName=null;
					fileEncoded=null;
				} else {
					MsgArea.appendText(msg);
					if(!msg.contains(username)) {
	        			Platform.runLater(new Runnable() {
	        			    @Override
	        			    public void run() {
	        			    	ImageView icon = new ImageView(new Image(this.getClass().getResourceAsStream("res/icon64.png")));
	        			    
	        			    	Notifications.create().title("Chat: "+server+":"+port).text(msg).graphic(icon).show();
	        			    }
	        			});
	        		}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
    
    private void SendFile(File file) throws IOException {
    	// convert file to byte array then to string to send to server
		byte[] str = FileUtils.readFileToByteArray(file);
		String decoded = new String(str, "ISO-8859-1");
		client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, "|ClintSendFile|" + file.getName() + "|" + decoded));
	}

	public void connectionFailed() {
		append("Connection to chat server failed.");
		connected = false;
        Platform.runLater(new Runnable() {
            @Override public void run() {
        		disconnectBtn.setText("Connect");
				msgFld.setDisable(true);
				sendBtn.setDisable(true);
				fileBtn.setDisable(true);
        		connectedLabel.setText("Disconnected");
        		UserList.getItems().clear();
            }
        });
	}
	
	public void serverClosedConnection() {
		append("Connection closed.\n");
		connected = false;
        Platform.runLater(new Runnable() {
            @Override public void run() {
        		disconnectBtn.setText("Connect");
				msgFld.setDisable(true);
				sendBtn.setDisable(true);
				fileBtn.setDisable(true);
        		connectedLabel.setText("Disconnected");
        		UserList.getItems().clear();
            }
        });
	}
	
	private void getMsgWhileOffline() throws ClassNotFoundException {
	   try{
	      //STEP 2: Register JDBC driver
	      Class.forName("com.mysql.jdbc.Driver");

	      //STEP 3: Open a connection
	      System.out.println("Connecting to a selected database...");
	      Connection con=DriverManager.getConnection("jdbc:mysql://trowlink.com:3306/JavaChat?autoReconnect=true&useSSL=false","chat","7xsVuPeF1rCQOeo2");
	      System.out.println("Connected database successfully...");
	      
	      //STEP 4: Execute a query
	      System.out.println("Creating statement...");
	      Statement stmt = con.createStatement();

	      //String sql = "SELECT * FROM "+getChatRmName(port);
	      String sql = "SELECT * FROM ( SELECT * FROM "+getChatRmName(port)+" ORDER BY id DESC LIMIT 15 ) sub ORDER BY id ASC";
	      ResultSet rs = stmt.executeQuery(sql);
	      //STEP 5: Extract data from result set
	      while(rs.next()){
	         //Retrieve by column name
	         String timedate = rs.getString("timestamp");
	         String user = rs.getString("user");
	         String msg = rs.getString("msg");

	         //Display values
	         MsgArea.appendText(timedate+" "+user+": "+msg+"\n");
	      }
	      rs.close();
	   }catch(SQLException se){
	      //Handle errors for JDBC
	      se.printStackTrace();
	   }
	}
	private String getChatRmName(int p) {
		p = port-7999;
		String formattedNumber = String.format("%03d", p);
		return "C"+formattedNumber;
	}
	
	@FXML
	public void exitApplication(ActionEvent event) {
		System.out.println("chatcontroller exit");
		client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
		Platform.exit();
	}
}
