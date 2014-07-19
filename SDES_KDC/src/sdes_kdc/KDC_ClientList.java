package sdes_kdc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class parses the KDC's logfile and adds all its clients to a list.
 * @author Giannouloudis Stergios
 */
public class KDC_ClientList {

    private static final String configFileName = "kdc_config.xml"; //the configuration file that we need to parse.
    BufferedReader buffReader;

    private int port;
    private static ArrayList<Client> clientsList = new ArrayList();    //list of all system's clients

    /**
     * Constructor.
     * Parses the KDC Configration file.
     */
    public KDC_ClientList() {
        if(!parseKdcConfigFile())
            System.out.println("KDC>> Please check the configuretion file and restart server.");
    }

    /**
     *
     * @return The KDC's clients list.
     */
    public static ArrayList<Client> getClientsList() {
        return clientsList;
    }

    /**
     *
     * @return The port that KDC is set to use.
     */
    public int getServersPort() {
        return port;
    }


    /**
     * Parses KDC's logfile and adds its clients to a list.
     * @return True if the list was filled succesfully. Flase otherwise.
     */
    private boolean parseKdcConfigFile(){
        boolean isKDCLogFile = false;
        String line = "";

        try{
            buffReader = new BufferedReader( new FileReader (new File(configFileName)));

            while(buffReader.ready()){
                line = buffReader.readLine().trim();

                //check to see if <client> tag exists
                if (line.startsWith("<KDC>")){
                    isKDCLogFile = true;
                }

                if(isKDCLogFile){
                    if(line.startsWith("<port>")){
                        port = Integer.valueOf( line.substring(6, line.length()-7) );
                    }

                    if(line.startsWith("<client-list>")){
                        do{
                            line = buffReader.readLine().trim();

                            if(line.startsWith("<client>")){
                                Client client = new Client();
                                                                
                                do{
                                    line = buffReader.readLine().trim();
                                    if(line.startsWith("<name>")){
                                        client.setcName(line.substring(6, line.length()-7));
                                    }
                                    if(line.startsWith("<ckey>")){
                                        client.setcKey(line.substring(6,  line.length()-7));
                                    }
                                }while(!line.startsWith("</client>"));
                                
                                //ADD CLIENT TO THE LIST
                                if(!clientsList.add(client))
                                    System.out.println("KDC>> Error building clients' list. Some clienst were not added.");
                            }
                        } while (!line.startsWith("</client-list>"));
                    }
                }
            }
            System.out.println("KDC>> Configuration file succesfuly loaded.");
            return true;
        } catch (FileNotFoundException fnfEx){
            System.out.println("KDC>> Could not FIND ConfigFile.");
            return false;
        } catch (IOException ioEx){
            System.out.println("KDC>> Could not OPEN ConfigFile.");
            return false;
        }
    }

    /**
     *
     * @return A list of the names of active clients of the KDC.
     */
    @Override
    public String toString() {
        String listAsString = "";
        for (Client c : clientsList)
            listAsString += c.getcName() + "\n";
        
        return listAsString;
    }
}
