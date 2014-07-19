package sdes_kdc;

import AssistantClasses.Decrypt;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A connection with a client.
 * Handles the communiction between KDC and a client.
 * @author Giannouloudis Stergios
 */
public class Connection extends Thread {

    private static DataInputStream input;
    private static DataOutputStream output;
    private Socket socket;
    private static Client newActiveClient;
    private String clientsRequest;

    public Connection(Socket socket) {
        this.socket = socket;
        this.start();
    }

    /**
     * Handles client's requests.
     */
    @Override
    public void run() {

        //bind io streams to socket
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            System.out.println("KDC>> Could not bind IO streams to socket" + ex.getMessage());
        }

        //read from client
        try {
            //initialization request comes without encryption
            clientsRequest = input.readUTF();//unencrypted initialization message.
            while (handleClientsRequest(clientsRequest, socket)) {
                clientsRequest = Server.cbp.decryptBinaryMessage(input.readUTF(), Server.activeClientsList.getKeyByHostName( socket.getInetAddress().getHostName() ) );//input1, key1 );
            }
        } catch (IOException ex) {
            System.out.println("KDC>> Failed to listen to clients (first) request.");
            Server.writeLogFile("Error. Failed to listen to clients request.");
        }
    }

    /**
     * Handles clients requests. These might be 1.Initialization,
     * 2.Connect to a peer
     * @param message The request.
     * @param cConnection
     * @return True if request was successfully executed.
     */
    private boolean handleClientsRequest(String message, Socket cConnection) {//, DataInputStream tempInStream) {
        if (message.equals("initialize")) {
            if (acceptNewClient(cConnection)) {
                return true;
            } else {
                System.out.println("KDC>> Could not initialize the connection to the new client. " +
                        "Its credentials may be invalid");
                Server.writeLogFile("Error. Unable to initialize connection with the client." + "Its credentials may be invalid");
                return false;
            }
        } else if (message.equals("start connection")) {
            try {
                String requestersHostName = this.socket.getInetAddress().getHostName();
                //String gettingPeersName = Server.activeClientsList.getClientByHostName( this.socket.getInetAddress().getHostName() ).getKdcInSrteam().readUTF();
                String gettingPeersName = Server.activeClientsList.getClientByHostName( requestersHostName ).getKdcInSrteam().readUTF();
                String peersName = Server.cbp.decryptBinaryMessage(gettingPeersName, Server.activeClientsList.getKeyByHostName( this.socket.getInetAddress().getHostName() ) );//read peers name

                System.out.println("KDC>> Connection request from "
                        + Server.activeClientsList.getClientByHostName(cConnection.getInetAddress().getHostName()).getcName()
                        + " with " + peersName);

                Server.writeLogFile(">>"
                        + Server.activeClientsList.getClientByHostName(cConnection.getInetAddress().getHostName()).getcName()
                        + " wants to connect to " + peersName);

                if (startConnection(peersName, cConnection.getInetAddress(), cConnection.getPort(), Server.activeClientsList.getKeyByHostName( socket.getInetAddress().getHostName() ))) {
                    return true;
                } else {
                    System.out.println("KDC>> Could not start connection with " + peersName + ".");
                    Server.writeLogFile("Error. Failed to connect the two clients.");
                    return true;
                }
            } catch (IOException ex) {
                System.out.println("KDC>> Could not read the peer to connect to.");
            }
            return true;
        } else {
            System.out.println("KDC>> Clients message could not be handled");
            return true;
        }
    }

    /**
     * Creates a new Client.<br/>
     * If his credentials are valid, he is added to the list of active clients.
     * @param cConnection The new client's connection.
     * @return True if new client's credentials are valid and if he is succesfully added to the active clients' list. False otherwise.
     */
    private static boolean acceptNewClient(Socket cConnection) {
        try {
            String cName = input.readUTF();//UNencrypted clients name
            String encryptedKey = input.readUTF();//Encrypted clients key
            String cKey = getClientsKeyFromList(cName); //get clients key from native clients'-list

            //VITAL POINT OF AUTHORIZATION PROCESS
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            Decrypt decrypt = new Decrypt(cKey);
            String decryptedKey = decrypt.decryptKey(encryptedKey);
            if (cKey.equals(decryptedKey)) {

                //create the new client
                newActiveClient = new Client();
                newActiveClient.setcName(cName);
                newActiveClient.setcKey(cKey);
                //get the clients address from the connection, not "from him"
                newActiveClient.setcIpAddress(cConnection.getInetAddress().toString());
                //read client's socket port
                newActiveClient.setcSocketPort( Integer.parseInt( Server.cbp.decryptBinaryMessage(input.readUTF(),cKey) ) );
                newActiveClient.setClientsListenPort( Integer.parseInt( Server.cbp.decryptBinaryMessage(input.readUTF(), cKey) ) );
                newActiveClient.setClientsHostName( Server.cbp.decryptBinaryMessage(input.readUTF(),cKey) );
                //keep this specific input stream.
                newActiveClient.setKdcInSrteam(input);
                newActiveClient.setKdcOutStream(output);

                if (Server.activeClientsList.add(newActiveClient)) {
                    System.out.println("KDC>> A new client was activated!");
                    Server.writeLogFile(">>" + newActiveClient.getcName() + " is now active on KDC.");

                    //tell the client he was accepted
                    String encResponse = Server.cbp.encryptTextMessage("Accepted by KDC.", newActiveClient.getcKey());
                    output.writeUTF(encResponse);
                    //connections should NOT close here!
                    return true;
                } else {
                    System.out.println("KDC>> Coud not add new client to the Actives' list.");
                    System.out.println("KDC>> Client could not be activated.");
                    Server.writeLogFile("Error. Unable to activate new client.");
                    output.writeUTF(Server.cbp.encryptTextMessage("KDC says: Your credentials may be invalid.", newActiveClient.getcKey()));
                    return false;
                }
            } else {
                System.out.println("KDC>> Clients credentials may be invalid");
                output.writeUTF(Server.cbp.encryptTextMessage("KDC says: Your credentials may be invalid.", newActiveClient.getcKey()));
                return false;
            }
        } catch (IOException ex) {
            System.out.println("KDC>> Error at client's initialization proccess. MSG: " + ex.getMessage());
            //Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Notifies the one client that another wants to talk to him
     * and sends both clients the neccessary data to connecto to the other client.
     * @param peersName The peer to be notified
     * @return True, if both peers received the neccessary information.
     *          False, if something went wrong.
     */
    private static boolean startConnection(String peersName, InetAddress requestersAddr, int requestersPort, String requestersKey) {
        //search for peers name on active clients list
        if (Server.activeClientsList.containsByName(peersName)) {
            try {
                DataOutputStream kdcToRequester = Server.activeClientsList.getClientByHostName( requestersAddr.getHostName() ).getKdcOutStream();
                //tell the requester that peer was found
                kdcToRequester.writeUTF(Server.cbp.encryptTextMessage("Requested peer was found.", requestersKey));

                //create a new and common key
                String commonKey = createCommonKey();

                //connect and send key to receiver/listener
                //1 find the peer
                Client peer = Server.activeClientsList.getClient(peersName);
                //2 tell him that one wants to connect to him and 3 send him the key
                notifyPeer(peer, commonKey, requestersAddr, requestersPort);

                //send common key to requester. 
                kdcToRequester.writeUTF(Server.cbp.encryptTextMessage( commonKey, requestersKey ));
                //send peers info to requester
                kdcToRequester.writeUTF(Server.cbp.encryptTextMessage( peer.getClientsHostName(), requestersKey ));
                kdcToRequester.writeUTF(Server.cbp.encryptTextMessage( String.valueOf(peer.getClientsListenPort()), requestersKey ));
                //here is the end of KDC's job.
            } catch (IOException ex) {
                System.out.println("KDC>> Unable to send \"peer was found\" answer to client.");
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("KDC>> Connection request to inactive client: " + "(" + peersName + ")");

            Server.writeLogFile("Error. Conection request to an inactive client.");
            
            try {
                //tell the requester that peer was not found
                output.writeUTF(Server.cbp.encryptTextMessage( "Could not find requested peer.", requestersKey ));
                return false;
            } catch (IOException ex) {
                System.out.println("KDC>> Unable to send \"could not find peer\" answer to client.");
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    /**
     * Scans the KDC_ClientsList in order to spot clients name
     * Gets clients key.
     * @param encKey
     * @return The clients key (if found) or empty string elsewise
     */
    public static String getClientsKeyFromList(String cName) {
        for (Client c : KDC_ClientList.getClientsList()) {
            if (c.getcName().equals(cName)) {
                return c.getcKey();
            }
        }
        return "";
    }

    /**
     * Creates a new 10-bit binary number.
     * @return A 10-bit binary number as string
     */
    private static String createCommonKey() {
        String key = "";
        Random rand = new Random();

        for (int i = 0; i < 10; i++) {
            int n = 0;
            if (rand.nextBoolean()) {
                n = 1;
            }
            key += n;
        }
        return key;
    }

    /**
     * Informs the peer that another client wants to talk to him.
     * @param peer The peer to be notified
     * @param commonKey The new key
     * @param requestersAddr The <code>InetAdress</code> of the client who started the conversation
     * @param requestersPort The port of the client who started the conversation.
     */    
    private static void notifyPeer(Client peer, String commonKey, InetAddress requestersAddr, int requestersPort) {
        try {
            //create a new socket only to notify the other peer.
            String peersHostName = peer.getClientsHostName();
            InetAddress i = InetAddress.getByName(peersHostName);
            Socket notifySocket = new Socket(i, peer.getClientsListenPort());

            //bind IO to socket
            try {
                DataOutputStream output2 = new DataOutputStream(notifySocket.getOutputStream());
                output2.writeUTF( Server.cbp.encryptTextMessage("incomming request", peer.getcKey()) );
                output2.writeUTF( Server.cbp.encryptTextMessage(commonKey, peer.getcKey()) );
                output2.writeUTF( Server.cbp.encryptTextMessage(String.valueOf(requestersPort), peer.getcKey()) );
                output2.writeUTF( Server.cbp.encryptTextMessage(requestersAddr.toString(), peer.getcKey()) );
                output2.close();
                notifySocket.close();

                Server.writeLogFile(">> Sent notification to " + peer.getcName() + ".");

            } catch (IOException ex) {
                System.out.println("KDC>> Could not bind IO streams to notify peer client. MSG:" + ex.getMessage());
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnknownHostException ex) {
            Server.writeLogFile("Error. Could not resolve listeners address. MSG:" + ex.getLocalizedMessage());
            System.out.println("UnknownHost. MSG: " + ex.getMessage());
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("IO exception. MSG:" + ex.getMessage());
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}