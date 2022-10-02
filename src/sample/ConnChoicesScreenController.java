package sample;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 * Created by Armin on 7/18/2016.
 */
public class ConnChoicesScreenController implements ControlledScreen {

    public ToggleGroup connChoice;
    ScreensController myScreenController;
    Main myLogicalParent;

    @Override
    public void setParents(ScreensController screenPage,Main logical) {
        myLogicalParent = logical;
        myScreenController = screenPage;
    }

    @FXML
    public void handleConnChoice() {
        RadioButton connCh = (RadioButton) connChoice.getSelectedToggle();
        switch (connCh.getId()) {
            case "rd0":
                System.out.println("Radio 0 pushed");
                myLogicalParent.initWifiClient();
                break;
            case "rd1":
                System.out.println("Radio 1 pushed");
                //myLogicalParent.initBtClient();
                myLogicalParent.startBtServer();
                break;
            default:
                break;
        }
        //myLogicalParent.
    }
}


