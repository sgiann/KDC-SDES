package sdes_kdc;

import java.util.ArrayList;

/**
 * A list of the active clients connected to the server.
 * @author Gianouloudis Stergios
 */
public class ActiveClientsList extends ArrayList<Client> {

    /**
     * Constructor
     */
    public ActiveClientsList() {
        super();
    }

    /**
     * Removes a client from the active clients list.
     * @param client The client to be removed
     * @return True if client was succesfully removed. False otherwise.
     */
    public boolean remove(Client client) {
        for (Client c : this) {
            if (client.equals(c)) {
                if (this.remove(c)) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    /**
     * Checks if the list contains a client with that name.
     * @param clientName The name of the client we are looking for.
     * @return True if the list contains the client with the given name.
     */
    public boolean containsByName(String clientsName) {
        for (Client c : this) {
            if (c.getcName().equals(clientsName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @return The active clients list
     */
    public ArrayList getList() {
        return this;
    }

    /**
     * Searches by name and returns a "Client" object.
     * @param name The clients name we want to retrieve
     * @return The client we are looking for as "Client" object.
     */
    public Client getClient(String name) {
        for (Client c : this) {
            if (c.getcName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Finds the key of a client given his hostName
     * @param hostName
     * @return Client's key
     */
    public String getKeyByHostName(String hostName) {
        for (Client c : this) {
            String slashPlusHostName = "/" + hostName;
            String c_getIpAddress = c.getcIpAddress();
            String linuxRecognizeIPLikeThis = hostName + "/" + hostName;
            if (c.getcIpAddress().equals(slashPlusHostName) || c.getClientsHostName().equals(hostName) || c.getcIpAddress().equals(linuxRecognizeIPLikeThis)) {
                return c.getcKey();
            }
        }
        return "";
    }

    /**
     * Finds the client by his Hostname
     * @param hostName
     * @return A client with the given Hostname
     */
    public Client getClientByHostName(String hostName) {
        for (Client c : this) {
            String slashPlusHostName = "/" + hostName;
            String linuxRecognizeIPLikeThis = hostName + "/" + hostName;
            if (c.getcIpAddress().equals(slashPlusHostName) || c.getClientsHostName().equals(hostName) || c.getcIpAddress().equals(linuxRecognizeIPLikeThis)) {
                return c;
            }
        }
        return null;
    }
}
