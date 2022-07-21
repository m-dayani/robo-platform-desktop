package sample;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WifiClient {
    //object used for waiting
    //private static Object lock = new Object();

    //vector containing the devices discovered
    //private static Vector<RemoteDevice> vecDevices=new Vector();
    private static final String DEFAULT_SERVER_IP = "192.168.1.100";
//    private static final String DEFAULT_HOTSPOT_IP = "192.168.1.43";
    private static final int DEFAULT_SERVER_PORT = 27015;


    private String serverIp;
    private int serverPort;
    private Socket clientSocket;
//    private final PrintWriter output = null; //write port
//    private final BufferedReader input = null; //read (from server) port

    public static String getDefaultServerIp() {
        return DEFAULT_SERVER_IP;
    }
//    public static String getDefaultHotspotIp() {
//        return DEFAULT_HOTSPOT_IP;
//    }
    public static int getDefaultServerPort() {
        return DEFAULT_SERVER_PORT;
    }

    public void setServerIp(String ip) {
        this.serverIp = ip;
    }
    public void setServerPort(int port) {
        this.serverPort = port;
    }
//    public void setTargetController(roboController controller) {
//        this.controller = controller;
//    }

    public void connectClient(String sIp, int sPort) {
        //setTargetController(controller);
        setServerIp(sIp);
        setServerPort(sPort);

        Thread clientThread = new Thread(new ClientTask());
        clientThread.start();
    }

//    public void disconnectClient() throws IOException {
//        //        if (inputTask !=  null && input != null) {
////            inputTask.stop();
////            //input.close();
////        }
//        if (clientSocket != null) {
//            clientSocket.close();
//        }
//        if (clientThread != null) {
//            //thread1.join();
//            clientThread = null;
//        }
//    }

//    public void sendMessage(String msg) throws IOException {
//    }

//    class UpdateControllerTask implements Runnable {
//
//        boolean state;
//        UpdateControllerTask(boolean state) {
//            this.state = state;
//        }
//        @Override
//        public void run() {
//            controller.setWifiAvailable(state);
//            controller.updateConnection(state);
//            if (state) {
//                controller.setWifiManager(MyWifiManager.this);
//                controller.report("connected successfully to: " +
//                        serverIp + ':' + Integer.toString(serverPort));
//            }
//            else {
//                controller.report("Something went wrong, unable to connect to remote server...");
//            }
//        }
//    }

    class ClientTask implements Runnable {

        public void run() {
            try {
                clientSocket = new Socket(serverIp,serverPort);
                partnerName = serverIp+':'+serverPort;
                out = new PrintWriter(clientSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                WifiClient.this.connected();

//                inputTask = new InputTask();
//                new Thread(inputTask).start();
//                //TODO: Careless use of this could cause serious threading problems
//                Platform.runLater(new UpdateControllerTask(true));
            }
            catch (IOException e) {
                e.printStackTrace();
                //Platform.runLater(new UpdateControllerTask(false));
                WifiClient.this.connFailed();
            }
        }
    }

    /*class InputTask implements Runnable {
        private boolean exit = false;
        @Override
        public void run() {

            while (!exit) {
                try {
                    final String message = input.readLine();
                    if (message != null) {

                    } else {
                        clientThread = new Thread(new ClientTask());
                        clientThread.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void stop() {
            exit = true;
        }
    }

    class OutputTask implements Runnable {
        private String message;
        OutputTask(String message) {
            this.message = message+'\n';
        }
        @Override
        public void run() {
            output.write(message);
            output.flush();

        }
    }*/

//    private static final String connectionURL=null;

    //private ActionListener onDeviceDiscovery;
    private ActionListener onConnectionSuccessful;
    private ActionListener onConnectionFailed;

    //DiscoveryAgent agent;

    String partnerName;

    BufferedReader in;
    PrintWriter out;

    public void setOnConnectionFailed(ActionListener onConnectionFailed) {
        this.onConnectionFailed = onConnectionFailed;
    }

    public void setOnConnectionSuccessful(ActionListener onConnectionSuccessful) {
        this.onConnectionSuccessful = onConnectionSuccessful;
    }

//    boolean isOK = false;
//
//    //implement this method since services are not being discovered
//    public void serviceSearchCompleted(int transID, int respCode) {
//        if(!isOK) {
//            if(onConnectionFailed != null) onConnectionFailed.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX+1,""));
//        }
//    }
//
//    public void inquiryCompleted(int discType) {
//        if(onConnectionSuccessful != null) onConnectionSuccessful.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX + 1,""));
//        System.out.println("Device Inquiry Completed. ");
//    }

    public void connected() {
        if (onConnectionSuccessful != null)
            onConnectionSuccessful.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX+1,""));
    }

    public void connFailed() {
        if (onConnectionFailed != null)
            onConnectionFailed.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX+1,""));
    }

    public void clean() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getLocalDeviceName() {
        return clientSocket.getLocalAddress().toString()+':'+clientSocket.getLocalPort();
    }
}
