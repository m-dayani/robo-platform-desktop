package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Main extends Application implements EventHandler<WindowEvent> {

    private static final String DEFAULT_TEST_COMMAND = "test";

    ScreensController mainContainer;

    SPPClient blthClient;
    WifiClient wifiClient;

    private StreamPoller inputHandler;

    String myName;
    String partnerName;

    BufferedReader in;
    PrintWriter out;

    public Main(){
        mainContainer = new ScreensController(this);
        mainContainer.loadScreen("ControlPanel","ControlPanel.fxml");
        mainContainer.loadScreen("ConnChoicesScreen","ConnChoicesScreen.fxml");
        mainContainer.loadScreen("LoadingScreen","LoadingScreen.fxml");
        mainContainer.loadScreen("ServerListScreen","ServerListScreen.fxml");
        mainContainer.loadScreen("WifiConnScreen", "WifiConnScreen.fxml");
        mainContainer.setScreen("ConnChoicesScreen");
    }

    /*================================= Wifi Connection ===============================*/

    public void initWifiClient() {
        mainContainer.setScreen("WifiConnScreen");
    }

    public void connectWifiClient(String sIp, int sPort) {
        wifiClient = new WifiClient();
        wifiClient.setOnConnectionFailed(new ConnFailureListener("WifiConnScreen"));
        wifiClient.setOnConnectionSuccessful(new WifiConnSuccessListener("ControlPanel"));
        mainContainer.setLoadingScreenText("LoadingScreen","Waiting for device connection ...");
        mainContainer.setScreen("LoadingScreen");
        wifiClient.connectClient(sIp, sPort);
    }

    /*=============================== Bluetooth Connection ============================*/

    public void initBlthClient() {
        blthClient = new SPPClient();
        blthClient.setOnDeviceDiscovery(new DeviceDiscoveryListener("ServerListScreen"));
        mainContainer.setLoadingScreenText("LoadingScreen","Searching for Bluetooth Devices ...");
        mainContainer.setScreen("LoadingScreen");
        blthClient.startDiscovery();

        blthClient.setOnConnectionFailed(new ConnFailureListener("ServerListScreen"));
        blthClient.setOnConnectionSuccessful(new BlthConnSuccessListener("ControlPanel"));
    }

    public void refresh(){
        mainContainer.setLoadingScreenText("LoadingScreen","Searching for Bluetooth Devices ...");
        mainContainer.setScreen("LoadingScreen");
        blthClient.startDiscovery();
    }

    public void connectToDevice(int index){
        mainContainer.setLoadingScreenText("LoadingScreen","Connecting ...");
        mainContainer.setScreen("LoadingScreen");
        blthClient.connect(index);
    }

    /*================================== Data Transfer ================================*/

    public void sendMsg(String s){
        System.out.println("must send " + s);
        out.write(s + "\r\n");
        out.flush();
        if (s.equals(DEFAULT_TEST_COMMAND)) {
            this.backHome();
        }
    }

    public void receiveMsg(String s){
        ControlPanelController ch = ((ControlPanelController)mainContainer.getScreenController("ControlPanel"));
        ch.recivedMassage(s);
    }

    /*================================= Scene Management ==============================*/

    //TODO: Clean open resources
    public void disconnect() {
        if (wifiClient !=  null) {
            wifiClient.clean();
            wifiClient = null;
            if (inputHandler != null) {
                inputHandler.stopInput();
                inputHandler = null;
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.close();
                out = null;
            }
        }
    }

    public void backHome() {
        this.disconnect();
        mainContainer.setScreen("ConnChoicesScreen");
    }

    public void handleOnKeyPressed(KeyEvent e) {
        String msg = "";
        if (e.getText().isEmpty()) {
            msg += e.getCode();
        }
        else {
            msg += e.getText();
        }
        System.out.println("Key event: "+msg);
        this.sendMsg(msg);
    }

    //TODO: handle keyRelease for implementing sticky key commands
//    public void handleOnKeyReleased(KeyEvent keyEvent) {
//        this.sendMsg("x");
//    }

    @Override
    public void handle(WindowEvent event) {
        this.disconnect();
    }

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.getChildren().addAll(mainContainer);
        AnchorPane.setTopAnchor(mainContainer,0.0);
        AnchorPane.setBottomAnchor(mainContainer,0.0);
        AnchorPane.setLeftAnchor(mainContainer,0.0);
        AnchorPane.setRightAnchor(mainContainer,0.0);

        Scene scene = new Scene(root,350,600);
        scene.setOnKeyPressed(this::handleOnKeyPressed);
        scene.setOnKeyReleased(e -> {
            //this.handleOnKeyReleased(e);
        });
        primaryStage.setTitle("Robo Controller v1.0");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(this);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /*============================= Util Objects ===========================*/

    class BlthConnSuccessListener implements ActionListener {
        private final String transferPage;
        BlthConnSuccessListener(String pageName) {
            this.transferPage = pageName;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            in = blthClient.in;
            out = blthClient.out;
            myName = blthClient.getLocalDeviceName();
            partnerName = blthClient.partnerName;
            mainContainer.setScreen(transferPage);
            inputHandler = new StreamPoller();
            inputHandler.start();
        }
    }

    class WifiConnSuccessListener implements ActionListener {
        private final String transferPage;
        WifiConnSuccessListener(String pageName) {
            this.transferPage = pageName;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            in = wifiClient.in;
            out = wifiClient.out;
            myName = wifiClient.getLocalDeviceName();
            partnerName = wifiClient.partnerName;
            mainContainer.setScreen(transferPage);
            inputHandler = new StreamPoller();
            inputHandler.start();
        }
    }

    class ConnFailureListener implements ActionListener {
        private final String transferPage;
        ConnFailureListener(String pageName) {
            this.transferPage = pageName;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            mainContainer.setScreen(this.transferPage);
        }
    }

    class DeviceDiscoveryListener implements ActionListener {
        private final String transferPage;
        DeviceDiscoveryListener(String pageName) {
            this.transferPage = pageName;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            ObservableList<RemoteDeviceInfo> devInfo = FXCollections.observableList(blthClient.getDeviceInfos());
            ((ServerListScreenController) mainContainer.getScreenController(transferPage)).setTableData(devInfo);
            mainContainer.setScreen(transferPage);
        }
    }

    class StreamPoller extends Thread{

        boolean isRunning = false;
        public void run(){
            isRunning = true;
            while(isRunning){
                try {
                    if(in != null) {
                        System.out.println("in not null");
                        String s = in.readLine();
                        if (s != null) Platform.runLater(() -> receiveMsg(s));
                        else this.stopInput();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void stopInput() {
            isRunning = false;
        }
    }
}
