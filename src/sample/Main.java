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

import sample.MyWirelessDriver.WirelessMsg;
import sample.MyWirelessDriver.WirelessCommand;


public class Main extends Application implements EventHandler<WindowEvent> {

    private static final String DEFAULT_TEST_COMMAND = "test";

    private String mLastCmdKeyCode = "0";

    ScreensController mainContainer;

    SPPClient btClient;
    SPPServer btServer;

    WifiClient wifiClient;
    WifiServer wifiServer;

    private StreamPoller inputHandler;

    String myName;
    String partnerName;

    BufferedReader in;
    PrintWriter out;

    public Main() {

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
        Object wifiConnController = mainContainer.getScreenController("WifiConnScreen");
        if (wifiConnController instanceof WifiConnScreenController) {
            ((WifiConnScreenController) wifiConnController).initialize();
        }
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

    public void startWifiServer(int sPort) {

        wifiServer = new WifiServer();
        wifiServer.setOnConnectionFailed(new ConnFailureListener("WifiConnScreen"));
        wifiServer.setOnConnectionSuccessful(new WifiConnSuccessListener("ControlPanel"));
        mainContainer.setLoadingScreenText("LoadingScreen", "Waiting for device connection ...");
        mainContainer.setScreen("LoadingScreen");
        wifiServer.startServer(sPort);
    }

    /*=============================== Bluetooth Connection ============================*/

    public void startBtServer() {

        btServer = new SPPServer();
        btServer.setLogicalParent(this);
        mainContainer.setLoadingScreenText("LoadingScreen","Waiting for Remote Device to Connect ...");
        mainContainer.setScreen("LoadingScreen");
        btServer.start();

        btServer.setOnConnectionSuccessful(new BtConnSuccessListener("ControlPanel"));
    }

    public void initBtClient() {

        btClient = new SPPClient();
        btClient.setOnDeviceDiscovery(new DeviceDiscoveryListener("ServerListScreen"));
        mainContainer.setLoadingScreenText("LoadingScreen","Searching for Bluetooth Devices ...");
        mainContainer.setScreen("LoadingScreen");
        btClient.startDiscovery();

        btClient.setOnConnectionFailed(new ConnFailureListener("ServerListScreen"));
        btClient.setOnConnectionSuccessful(new BtConnSuccessListener("ControlPanel"));
    }

    public void refresh() {

        mainContainer.setLoadingScreenText("LoadingScreen","Searching for Bluetooth Devices ...");
        mainContainer.setScreen("LoadingScreen");
        btClient.startDiscovery();
    }

    public void connectToDevice(int index){
        mainContainer.setLoadingScreenText("LoadingScreen","Connecting ...");
        mainContainer.setScreen("LoadingScreen");
        btClient.connect(index);
    }

    /*================================== Data Transfer ================================*/

    public void sendMsg(String s) {

        System.out.println("must send " + s);

        out.write(s + "\r\n");
        out.flush();

        if (s.equals(DEFAULT_TEST_COMMAND)) {
            this.backHome();
        }
    }

    public void receiveMsg(String s){
        ControlPanelController ch = ((ControlPanelController)mainContainer.getScreenController("ControlPanel"));
        ch.receivedMassage(s);
    }

    /*================================= Scene Management ==============================*/

    public void disconnect() {

        if (inputHandler != null) {
            inputHandler.stopInput();
            inputHandler = null;
        }
        if (wifiClient !=  null) {
            wifiClient.clean();
            wifiClient = null;
        }
        if (wifiServer != null) {
            wifiServer.clean();
            wifiServer = null;
        }
        if (btClient != null) {
            btClient.clean();
            btClient = null;
        }
        if (btServer != null) {
            btServer.closeConnection();
            btServer = null;
        }
        if (in != null) {
            //try {
                //in.close();
                in = null;
            //}
            //catch (IOException e) {
            //    e.printStackTrace();
            //}
        }
        if (out != null) {
            out.close();
            out = null;
        }
    }

    public void backHome() {
        this.disconnect();
        mainContainer.setScreen("ConnChoicesScreen");
    }

    // These two methods handle window key presses

    public void handleOnKeyPressed(KeyEvent e) {
        String msg = "";
        if (e.getText().isEmpty()) {
            msg += e.getCode();
        }
        else {
            msg += e.getText();
        }

        // make this more efficient (only send if different key is pressed)
        if (msg.equals(mLastCmdKeyCode)) {
            return;
        }
        mLastCmdKeyCode = msg;

        System.out.println("Key event: "+msg);
        this.sendMsg(MyWirelessDriver.encodeMessage(new WirelessMsg(WirelessCommand.CMD_CHAR, msg)));
    }

    // handle keyRelease for implementing sticky key commands
    // TODO: the release handler is called twice when entering cmmand keys -> why??
    public void handleOnKeyReleased(KeyEvent e) {
        String msg = "0";
        mLastCmdKeyCode = msg;
        System.out.println("Key event: "+msg);
        this.sendMsg(MyWirelessDriver.encodeMessage(new WirelessMsg(WirelessCommand.CMD_CHAR, msg)));
    }

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
        scene.setOnKeyReleased(this::handleOnKeyReleased);
        primaryStage.setTitle("Robo Controller v1.0");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(this);
        primaryStage.show();
    }

    /*@Override
    public void stop() throws Exception {

        this.disconnect();
        super.stop();
    }*/

    public static void main(String[] args) {
        launch(args);
    }

    /*============================= Util Objects ===========================*/

    class BtConnSuccessListener implements ActionListener {
        private final String transferPage;
        BtConnSuccessListener(String pageName) {
            this.transferPage = pageName;
        }
        @Override
        public void actionPerformed(ActionEvent e) {

            if (btServer != null) {
                in = btServer.getBufferedReader();
                out = btServer.getPrintWriter();
                myName = btServer.getLocalDeviceName();
                partnerName = btServer.partnerName;
            }
            else if (btClient != null) {
                in = btClient.in;
                out = btClient.out;
                myName = btClient.getLocalDeviceName();
                partnerName = btClient.partnerName;
            }
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

            if (wifiClient != null) {
                in = wifiClient.in;
                out = wifiClient.out;
                myName = wifiClient.getLocalDeviceName();
                partnerName = wifiClient.partnerName;
            }
            else if (wifiServer != null) {
                in = wifiServer.in;
                out = wifiServer.out;
                myName = wifiServer.getLocalDeviceName();
                partnerName = wifiServer.partnerName;
            }
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
            ObservableList<RemoteDeviceInfo> devInfo = FXCollections.observableList(btClient.getDeviceInfos());
            ((ServerListScreenController) mainContainer.getScreenController(transferPage)).setTableData(devInfo);
            mainContainer.setScreen(transferPage);
        }
    }

    class StreamPoller extends Thread {

        boolean isRunning = false;
        public void run() {

            isRunning = true;
            while (isRunning) {
                try {
                    if(in != null) {

                        String s = in.readLine();
                        if (s != null) {

                            WirelessMsg msg = MyWirelessDriver.decodeMessage(s);
                            System.out.println("got input: "+msg.mMessage);

                            if (MyWirelessDriver.matchesTest(msg)) {

                                System.out.println("matched expected test command");
                                Platform.runLater(() -> sendMsg(MyWirelessDriver.getTestResponse()));
                            }
                            else {
                                Platform.runLater(() -> receiveMsg(s));
                            }
                        }
                        else {
                            this.stopInput();
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void stopInput() {
            isRunning = false;
        }
    }
}
