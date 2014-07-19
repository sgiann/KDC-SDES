package sdes_kdc;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Represents a Client to the Server.
 * Keeps all the clients data.
 * @author Giannouloudis Stergios
 */
public class Client {
    private String cName;
    private String cKey;
    private String cIpAddress;
    private int cSocketPort;
    private int clientsListenPort;
    private String clientsHostName;
    private DataInputStream kdcInSrteam;
    private DataOutputStream kdcOutStream;

    /**
     * Constructor. 
     * Initializes a client's data.
     */
    public Client() {
        cName = "";
        cKey = "0000000000";
        cIpAddress = "";
        cSocketPort = 0;
        clientsListenPort = 0;
        clientsHostName = "";
    }

    public String getcIpAddress() {
        return cIpAddress;
    }

    public void setcIpAddress(String cIpAddress) {
        this.cIpAddress = cIpAddress;
    }

    public String getcKey() {
        return cKey;
    }

    public void setcKey(String cKey) {
        this.cKey = cKey;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public int getcSocketPort() {
        return cSocketPort;
    }

    public void setcSocketPort(int cSocketPort) {
        this.cSocketPort = cSocketPort;
    }

    public int getClientsListenPort() {
        return clientsListenPort;
    }

    public void setClientsListenPort(int clientsListenPort) {
        this.clientsListenPort = clientsListenPort;
    }

    public String getClientsHostName() {
        return clientsHostName;
    }

    public void setClientsHostName(String clientsHostName) {
        this.clientsHostName = clientsHostName;
    }

    public DataInputStream getKdcInSrteam() {
        return kdcInSrteam;
    }

    public void setKdcInSrteam(DataInputStream kdcInSrteam) {
        this.kdcInSrteam = kdcInSrteam;
    }

    public DataOutputStream getKdcOutStream() {
        return kdcOutStream;
    }

    public void setKdcOutStream(DataOutputStream kdcOutStream) {
        this.kdcOutStream = kdcOutStream;
    }

    @Override
    public String toString() {
        String clientAsString = "";
        clientAsString = "Name= " + cName + ", Key= " + cKey + "\n"
                + "Port= " + cSocketPort + ", IPAddr= " + cIpAddress + "clientsListenPort= " + clientsListenPort + "\n";
        return clientAsString;
    }

    /**
     * Compares the two clients <br />
     * VITAL FOR SYSTEMS AUTHORIZATION PROCCESS!!
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {

        if(this == obj)
            return true;

        if(!(obj instanceof Client))
            return false;

        Client clientObj = (Client) obj;

        return ( this.cName.equals(clientObj.getcName()) &&
                 this.cKey.equals(clientObj.getcKey())      );
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.cName != null ? this.cName.hashCode() : 0);
        hash = 41 * hash + (this.cKey != null ? this.cKey.hashCode() : 0);
        return hash;
    }
    
}
