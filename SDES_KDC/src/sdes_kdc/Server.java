package sdes_kdc;

import AssistantClasses.CharBitPairing;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The KDC Server.
 * @author Giannouloudis Stergios
 */
public class Server {
    
    public static KDC_ClientList kdcList;  //this server's clients list
    public static ActiveClientsList activeClientsList;
    static int serverPort;          //the port the server listens to
    static ServerSocket serverSocket;
    static Socket socket;

    public static BufferedWriter buffWriter;
    private static final String logFileName = "KDC_Logfile.txt";


    public static CharBitPairing cbp;

    /**
     * Loads necessary files.
     * Creates a new thread for every new client
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        System.out.println("KDC>> Server starts-up.");
        activeClientsList = new ActiveClientsList();        
        System.out.println("KDC>> Loading configuration file...");
        kdcList = new KDC_ClientList(); //this server's clients list
        System.out.println("KDC>> Creating log file...");
        createLogFile();

        //necesary for class:Connection
        cbp = new CharBitPairing();
        serverPort = kdcList.getServersPort();

        try {
            serverSocket = new ServerSocket(serverPort);
            while (true) {
                socket = serverSocket.accept();
                System.out.println("KDC>> Conection initialization request, by " + socket.getInetAddress().getHostName()
                                    + ", with IP: " + socket.getInetAddress()  + ", at port: "+ socket.getLocalPort());

                writeLogFile("Conection initialization request, by " + socket.getInetAddress().getHostName()
                                    + ", with IP: " + socket.getInetAddress()  + ", at port: "+ socket.getLocalPort());

                Connection connection = new Connection(socket);
            }
        } catch (IOException ex) {
            System.out.println("KDC>> IOException " + ex.getMessage() + " Maybe unable to create a socket.");
        } finally {
            try {
                buffWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    /**
     * Creates the server's logFile
     */
    private static boolean createLogFile() {
        try {
            buffWriter = new BufferedWriter(new FileWriter(logFileName));

            writeLogFile("<<START>>");
            System.out.println("KDC>> Log file created.");
            return true;
        } catch (IOException ex) {
            System.out.println("KDC>> Unable to create server's log file. MSG:" + ex.getMessage());
            return false;
        }
    }

    
    public static void writeLogFile(String message){
        try {
            buffWriter.write(message);
            buffWriter.newLine();
            buffWriter.flush();
        } catch (IOException ex) {
            System.out.println("KDC>> Error. Unable to write to KDC Log File.");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
