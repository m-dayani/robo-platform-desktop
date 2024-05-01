package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import sample.MyWirelessDriver.WirelessCommand;
import sample.MyWirelessDriver.WirelessMsg;

public class ControlPanelController implements ControlledScreen {

    @FXML
    private TextFlow chatLog;
    @FXML
    private TextField chatText;
    public Button connectBtn;
    public TextField keyCommands;
    public Button sendButton;

    ScreensController myScreenController;
    Main myLogicalParent;

    public void initialize() {
        this.setConnected();
    }

    @Override
    public void setParents(ScreensController screenPage,Main logical) {
        myLogicalParent = logical;
        myScreenController = screenPage;
    }

    public void setConnected() {
        this.connectBtn.setText("Disconnect");
    }

    public void disconnect() {

        myLogicalParent.backHome();
    }

    private void reportMessage(String tag, String msg, String style) {

        Text blue = new Text(tag + " : ");
        blue.setStyle(style);

        Text m = new Text(msg + "\n");

        chatLog.getChildren().add(blue);
        chatLog.getChildren().add(m);
    }

    @FXML
    protected void sendMassage() {
        String msg = chatText.getText();
        this.reportMessage(myLogicalParent.myName, msg,"-fx-fill: #0098d8");

        chatText.setText("");
        myLogicalParent.sendMsg(MyWirelessDriver.encodeMessage(new WirelessMsg(WirelessCommand.CMD_WORD, msg)));
    }

    public void receivedMassage(String msg){
        this.reportMessage(myLogicalParent.partnerName,msg,"-fx-fill: orangered");
    }

    public void handleCmdBtn(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();
        System.out.println(btn.getText());
        myLogicalParent.sendMsg(MyWirelessDriver.encodeMessage(new WirelessMsg(WirelessCommand.CMD_DIR, btn.getText().toLowerCase())));
    }

    // These two work perfectly (sticky key commands) when comments area is selected
    public void handleKeyPressed(KeyEvent keyEvent) {
        keyCommands.setText("");
//        System.out.println("ControlPanel: onKeyPressed");
        //this.reportMessage("Key Command: ", keyEvent.getCode().toString()+','+keyEvent.getText(),"");
        myLogicalParent.handleOnKeyPressed(keyEvent);
    }

    public void handleKeyReleased(KeyEvent keyEvent) {
        keyCommands.setText("");
//        System.out.println("ControlPanel: onKeyReleased");
        myLogicalParent.handleOnKeyReleased(keyEvent);
    }

    // handle keyRelease for implementing sticky key commands

    public void handleMousePressed(MouseEvent event) {

//        System.out.println("Mouse Pressed");
        Button btn = (Button) event.getSource();
//        System.out.println(btn.getText());
        myLogicalParent.sendMsg(MyWirelessDriver.encodeMessage(new WirelessMsg(WirelessCommand.CMD_DIR, btn.getText().toLowerCase())));
    }

    public void handleMouseReleased() {

//        System.out.println("Mouse Released");
//        Button btn = (Button) event.getSource();
//        System.out.println(btn.getText());
        myLogicalParent.sendMsg(MyWirelessDriver.encodeMessage(new WirelessMsg(WirelessCommand.CMD_DIR, "0")));
    }
}

