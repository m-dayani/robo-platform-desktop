package sample;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Class that implements SPP Server which accepts single line of
 * message from an SPP client and sends a single line of response to the client.
 */
public class SPPServer extends Thread {

    //Create a UUID for SPP / RFComm
    private static final String DEFAULT_UUID_STRING = "0000110100001000800000805f9b34fb";
//    final static UUID SERVICE_UUID = new UUID(DEFAULT_UUID_STRING, false);
    final static String SERVICE_NAME = "Robo-Platform";
    //Create the Service URL
//    private static final String connectionString = "btspp://localhost:" + SERVICE_UUID + ";name="+SERVICE_NAME;

    String partnerName;

    StreamConnectionNotifier streamConnNotifier;
    StreamConnection mConnection;

    private ActionListener onConnectionSuccessful;

    private Main mLogicalParent;

    public void setOnConnectionSuccessful(ActionListener onConnectionSuccessful) {
        this.onConnectionSuccessful = onConnectionSuccessful;
    }

    public void setLogicalParent(Main logicalParent) { mLogicalParent = logicalParent; }

    //start server
    public void run(){
        try {
            UUID SERVICE_UUID = new UUID(DEFAULT_UUID_STRING, false);
            String connectionString = "btspp://localhost:" + SERVICE_UUID + ";name="+SERVICE_NAME;

            //open server url
            streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

            //Wait for client connection
            System.out.println("\nServer Started. Waiting for clients to connect…");
            mConnection = streamConnNotifier.acceptAndOpen();

            RemoteDevice dev = RemoteDevice.getRemoteDevice(mConnection);
            System.out.println("Remote device address: " + dev.getBluetoothAddress());
            System.out.println("Remote device name: " + dev.getFriendlyName(true));

            partnerName = dev.getFriendlyName(true);

            if(onConnectionSuccessful != null)
                onConnectionSuccessful.actionPerformed(new ActionEvent(this,ActionEvent.RESERVED_ID_MAX+1,""));
        }
        catch (IOException e){
            e.printStackTrace();
            if (mLogicalParent != null) {
                mLogicalParent.backHome();
            }
        }
        finally {
            try {
                streamConnNotifier.close();
                streamConnNotifier = null;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection(){
        try {
            if (streamConnNotifier != null) {
                streamConnNotifier.close();
                streamConnNotifier = null;
            }
            if (mConnection != null) {
                mConnection.close();
                mConnection = null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        //display local device address and name
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: "+localDevice.getBluetoothAddress());
        System.out.println("Name: "+localDevice.getFriendlyName());
    }

    public String getLocalDeviceName() {

        try {
            return LocalDevice.getLocalDevice().getFriendlyName();
        }
        catch (BluetoothStateException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedReader getBufferedReader() {

        if (mConnection != null) {
            try {
                return new BufferedReader(new InputStreamReader(mConnection.openInputStream()));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public PrintWriter getPrintWriter() {

        if (mConnection != null) {
            try {
                return new PrintWriter(mConnection.openOutputStream());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
