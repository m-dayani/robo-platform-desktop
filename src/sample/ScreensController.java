package sample;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.HashMap;

public class ScreensController extends StackPane {

    private final HashMap<String, Node> screens = new HashMap<>();
    private final HashMap<String, Object> controllers = new HashMap<>();

    Main logicalParent;

    public ScreensController(Main logical){
        logicalParent = logical;
    }

    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    public void loadScreen(String name, String resource) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = myLoader.load();
            ControlledScreen myScreenControler = myLoader.getController();
            controllers.put(name,myLoader.getController());
            myScreenControler.setParents(this,logicalParent);
            addScreen(name, loadScreen);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

//    public boolean unloadScreen(String name) {
//        if(screens.remove(name) == null) {
//            System.out.println("Screen doesn't exist");
//            return false;
//        } else {
//            return true;
//        }
//    }

    public void setScreen(final String name) {

        Node currNode = screens.get(name);
        if (currNode != null) { //screen loaded
            final DoubleProperty opacity = opacityProperty();
            //Is there is more than one screen
            if (!getChildren().isEmpty()) {
                Timeline fade = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)), new KeyFrame(new Duration(500),
                        (ActionEvent e) -> {
                            //remove displayed screen
                            getChildren().remove(0);
                            //add new screen
                            getChildren().add(0, currNode);
                            Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)), new KeyFrame(new Duration(500), new KeyValue(opacity, 1.0)));
                            fadeIn.play();
                        }
                        , new KeyValue(opacity, 0.0)));
                fade.play();
            }
            else {
                //no one else been displayed, then just show
                setOpacity(0.0);
                getChildren().add(currNode);
                Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)), new KeyFrame(new Duration(1000), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
        }
        else {
            System.out.println("screen: "+name+" hasn't been loaded!\n");
        }
    }

    public Object getScreenController(final String name){
        return controllers.get(name);
    }

    public void setLoadingScreenText(String pageName, String msg) {
        ((LoadingScreenController)this.getScreenController(pageName)).setText(msg);
    }
}
