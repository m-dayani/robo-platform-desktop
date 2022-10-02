package sample;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class WifiServer {
    private static final int DEFAULT_SERVER_PORT = 27015;

    private String serverIp;
    private int serverPort;
    private ServerSocket serverSocket;
    private Socket mSocket;

    private ActionListener onConnectionSuccessful;
    private ActionListener onConnectionFailed;

    String partnerName;

    BufferedReader in;
    PrintWriter out;


    public WifiServer() {
        serverPort = DEFAULT_SERVER_PORT;
    }

    public void setServerPort(int port) {
        this.serverPort = port;
    }

    public void startServer(int sPort) {

        setServerPort(sPort);

        Thread serverThread = new Thread(new ServerTask());
        serverThread.start();
    }

    class ServerTask implements Runnable {

        public void run() {

            try {
                serverSocket = new ServerSocket(serverPort);

                System.out.println("Waiting for client to connect...");

                mSocket = serverSocket.accept();

                out = new PrintWriter(mSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

                serverIp = mSocket.getInetAddress().getHostAddress();
                serverPort = mSocket.getLocalPort();

                partnerName = mSocket.getRemoteSocketAddress().toString();

                System.out.println(serverIp+":"+serverPort+" connected successfully.");

                WifiServer.this.connected();
            }
            catch (IOException e) {

                e.printStackTrace();
                WifiServer.this.connFailed();
            }
        }
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getSuggestedInterfaces(boolean useIPv4) {

        StringBuilder sb = new StringBuilder();
        sb.append("Suggested Interfaces:\n");
        String sp = "\t";

        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4) {
                                sb.append(sp).append(intf.getDisplayName()).append(":\t").append(sAddr).append("\n");
                            }
                        }
                        else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                String res = delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                                sb.append(sp).append(intf.getDisplayName()).append(":\t").append(res).append("\n");
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ignored) {
            sb.append("Error while trying to read interfaces\n");
        } // for now eat exceptions
        return sb.toString();
    }

    public void setOnConnectionFailed(ActionListener onConnectionFailed) {
        this.onConnectionFailed = onConnectionFailed;
    }

    public void setOnConnectionSuccessful(ActionListener onConnectionSuccessful) {
        this.onConnectionSuccessful = onConnectionSuccessful;
    }

    public void connected() {
        if (onConnectionSuccessful != null)
            onConnectionSuccessful.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX+1,""));
    }

    public void connFailed() {
        if (onConnectionFailed != null)
            onConnectionFailed.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX+1,""));
    }

    public void clean() {

        if (serverSocket != null) {
            try {
                serverSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = null;
        }
        if (mSocket != null) {
            try {
                mSocket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            mSocket = null;
        }
    }

    public String getLocalDeviceName() {

        return serverIp+':'+serverPort;
    }
}
