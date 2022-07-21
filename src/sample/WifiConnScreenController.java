package sample;

import javafx.scene.control.TextField;

/**
 * Created by Armin on 7/18/2016.
 */
public class WifiConnScreenController implements ControlledScreen {

    public TextField sIpAddress;
    public TextField sPort;
    ScreensController myScreenController;
    Main myLogicalParent;

    public void initialize() {
        sIpAddress.setText(WifiClient.getDefaultServerIp());
        sPort.setText(Integer.toString(WifiClient.getDefaultServerPort()));
    }

    @Override
    public void setParents(ScreensController screenPage,Main logical) {
        myLogicalParent = logical;
        myScreenController = screenPage;
    }

    public void connectClient() {
        myLogicalParent.connectWifiClient(sIpAddress.getText(),Integer.parseInt(sPort.getText()));
    }

    public void backHome() {
        myLogicalParent.backHome();
    }
}


