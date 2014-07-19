package AssistantClasses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Giannouloudis Stergios
 * This class contains two "Properties" (HashTables)
 * that each holds the matching between chars and bits
 * and counterwise.
 */
public class CharBitPairing {

    public static Properties charToBitProperties, bitToCharProperties;
    static FileOutputStream os;
    static FileInputStream is;
    static File charToBitFile, bitToCharFile;

    public CharBitPairing() {
        
        //create some neccesery files
        charToBitProperties = new Properties();
        bitToCharProperties = new Properties();
        charToBitFile = new File("charToBit.xml");
        bitToCharFile = new File("bitToChar.xml");

        charToBit();
        bitToChar();
    }

    /**
     * Creates an xml file containing a correspondance
     * between characters and their bit representation
     * AND loads it.
     */
    private static void charToBit() {
        if (!charToBitFile.exists())
            createCharToBitPropertyFile();
        
        try {
            is = new FileInputStream(charToBitFile);
            charToBitProperties.loadFromXML(is);
            is.close();
        } catch (FileNotFoundException ex) {
            System.out.println("charToBit file not found.");
        } catch (IOException ex) {
            System.out.println("IO error reading the charToBit file.");
        }
    }

    /**
     * Maps some characters to bit-strings
     * and creates an xml file.
     * @return True
     */
    private static boolean createCharToBitPropertyFile() {
        charToBitProperties.setProperty(" ", "00100000");//32
        charToBitProperties.setProperty("!", "00100001");//33
        charToBitProperties.setProperty("(", "00101000");//40
        charToBitProperties.setProperty(")", "00101001");//41
        charToBitProperties.setProperty(",", "00101100");//44
        charToBitProperties.setProperty("-", "00101101");//45
        charToBitProperties.setProperty(".", "00101110");//46
        charToBitProperties.setProperty("/", "00101111");//47
        charToBitProperties.setProperty("0", "00110000");//48
        charToBitProperties.setProperty("1", "00110001");
        charToBitProperties.setProperty("2", "00110010");
        charToBitProperties.setProperty("3", "00110011");
        charToBitProperties.setProperty("4", "00110100");
        charToBitProperties.setProperty("5", "00110101");
        charToBitProperties.setProperty("6", "00110110");
        charToBitProperties.setProperty("7", "00110111");
        charToBitProperties.setProperty("8", "00111000");
        charToBitProperties.setProperty("9", "00111001");//57
        charToBitProperties.setProperty(":", "00111010");
        charToBitProperties.setProperty("=", "00111101");//61
        charToBitProperties.setProperty("?", "00111111");//63
        charToBitProperties.setProperty("A", "01000001");//65
        charToBitProperties.setProperty("B", "01000010");
        charToBitProperties.setProperty("C", "01000011");
        charToBitProperties.setProperty("D", "01000100");
        charToBitProperties.setProperty("E", "01000101");
        charToBitProperties.setProperty("F", "01000110");
        charToBitProperties.setProperty("G", "01000111");
        charToBitProperties.setProperty("H", "01001000");
        charToBitProperties.setProperty("I", "01001001");
        charToBitProperties.setProperty("J", "01001010");
        charToBitProperties.setProperty("K", "01001011");
        charToBitProperties.setProperty("L", "01001100");
        charToBitProperties.setProperty("M", "01001101");
        charToBitProperties.setProperty("N", "01001110");
        charToBitProperties.setProperty("O", "01001111");
        charToBitProperties.setProperty("P", "01010000");
        charToBitProperties.setProperty("Q", "01010001");
        charToBitProperties.setProperty("R", "01010010");
        charToBitProperties.setProperty("S", "01010011");
        charToBitProperties.setProperty("T", "01010100");
        charToBitProperties.setProperty("U", "01010101");
        charToBitProperties.setProperty("V", "01010110");
        charToBitProperties.setProperty("W", "01010111");
        charToBitProperties.setProperty("X", "01011000");
        charToBitProperties.setProperty("Y", "01011001");
        charToBitProperties.setProperty("Z", "01011010");//90
        charToBitProperties.setProperty("a", "01100001");//97
        charToBitProperties.setProperty("b", "01100010");
        charToBitProperties.setProperty("c", "01100011");
        charToBitProperties.setProperty("d", "01100100");
        charToBitProperties.setProperty("e", "01100101");
        charToBitProperties.setProperty("f", "01100110");
        charToBitProperties.setProperty("g", "01100111");
        charToBitProperties.setProperty("h", "01101000");
        charToBitProperties.setProperty("i", "01101001");
        charToBitProperties.setProperty("j", "01101010");
        charToBitProperties.setProperty("k", "01101011");
        charToBitProperties.setProperty("l", "01101100");
        charToBitProperties.setProperty("m", "01101101");
        charToBitProperties.setProperty("n", "01101110");
        charToBitProperties.setProperty("o", "01101111");
        charToBitProperties.setProperty("p", "01110000");
        charToBitProperties.setProperty("q", "01110001");
        charToBitProperties.setProperty("r", "01110010");
        charToBitProperties.setProperty("s", "01110011");
        charToBitProperties.setProperty("t", "01110100");
        charToBitProperties.setProperty("u", "01110101");
        charToBitProperties.setProperty("v", "01110110");
        charToBitProperties.setProperty("w", "01110111");
        charToBitProperties.setProperty("x", "01111000");
        charToBitProperties.setProperty("y", "01111001");
        charToBitProperties.setProperty("z", "01111010");//122

        //write it to xml
        try {
            os = new FileOutputStream(charToBitFile) ;
            charToBitProperties.storeToXML(os, null);
            os.close();
        } catch (IOException ex) {
            System.out.println("Could not open stream or create file \"charToBit\"");
            return false;
        }
        return true;
    }

    /**
     * Creates an xml file containing a correspondance
     * between bit representation and a char
     * AND loads it.
     */
    private static void bitToChar() {
        if (!bitToCharFile.exists())
            createBitToCharPropertyFile();
        
        try {
            is = new FileInputStream(bitToCharFile);
            bitToCharProperties.loadFromXML(is);
            is.close();
        } catch (FileNotFoundException ex) {
            System.out.println("charToBit file not found.");
        } catch (IOException ex) {
            System.out.println("IO error reading the charToBit file.");
        }
    }

    /**
     * Initialzes the <code>Property</code> and creates the file.
     * @return
     */
    private static boolean createBitToCharPropertyFile() {
        bitToCharProperties.setProperty("00100000", " ");//32
        bitToCharProperties.setProperty("00100001", "!");//33
        bitToCharProperties.setProperty("00101000", "(");//40
        bitToCharProperties.setProperty("00101001", ")");//41
        bitToCharProperties.setProperty("00101100", ",");//44
        bitToCharProperties.setProperty("00101101", "-");//45
        bitToCharProperties.setProperty("00101110", ".");//46
        bitToCharProperties.setProperty("00101111", "/");//47
        bitToCharProperties.setProperty("00110000", "0");//48
        bitToCharProperties.setProperty("00110001", "1");
        bitToCharProperties.setProperty("00110010", "2");
        bitToCharProperties.setProperty("00110011", "3");
        bitToCharProperties.setProperty("00110100", "4");
        bitToCharProperties.setProperty("00110101", "5");
        bitToCharProperties.setProperty("00110110", "6");
        bitToCharProperties.setProperty("00110111", "7");
        bitToCharProperties.setProperty("00111000", "8");
        bitToCharProperties.setProperty("00111001", "9");//57
        bitToCharProperties.setProperty("00111010", ":");
        bitToCharProperties.setProperty("00111101", "=");//61
        bitToCharProperties.setProperty("00111111", "?");//63
        bitToCharProperties.setProperty("01000001", "A");//65
        bitToCharProperties.setProperty("01000010", "B");
        bitToCharProperties.setProperty("01000011", "C");
        bitToCharProperties.setProperty("01000100", "D");
        bitToCharProperties.setProperty("01000101", "E");
        bitToCharProperties.setProperty("01000110", "F");
        bitToCharProperties.setProperty("01000111", "G");
        bitToCharProperties.setProperty("01001000", "H");
        bitToCharProperties.setProperty("01001001", "I");
        bitToCharProperties.setProperty("01001010", "J");
        bitToCharProperties.setProperty("01001011", "K");
        bitToCharProperties.setProperty("01001100", "L");
        bitToCharProperties.setProperty("01001101", "M");
        bitToCharProperties.setProperty("01001110", "N");
        bitToCharProperties.setProperty("01001111", "O");
        bitToCharProperties.setProperty("01010000", "P");
        bitToCharProperties.setProperty("01010001", "Q");
        bitToCharProperties.setProperty("01010010", "R");
        bitToCharProperties.setProperty("01010011", "S");
        bitToCharProperties.setProperty("01010100", "T");
        bitToCharProperties.setProperty("01010101", "U");
        bitToCharProperties.setProperty("01010110", "V");
        bitToCharProperties.setProperty("01010111", "W");
        bitToCharProperties.setProperty("01011000", "X");
        bitToCharProperties.setProperty("01011001", "Y");
        bitToCharProperties.setProperty("01011010", "Z");//90
        bitToCharProperties.setProperty("01100001", "a");//97
        bitToCharProperties.setProperty("01100010", "b");
        bitToCharProperties.setProperty("01100011", "c");
        bitToCharProperties.setProperty("01100100", "d");
        bitToCharProperties.setProperty("01100101", "e");
        bitToCharProperties.setProperty("01100110", "f");
        bitToCharProperties.setProperty("01100111", "g");
        bitToCharProperties.setProperty("01101000", "h");
        bitToCharProperties.setProperty("01101001", "i");
        bitToCharProperties.setProperty("01101010", "j");
        bitToCharProperties.setProperty("01101011", "k");
        bitToCharProperties.setProperty("01101100", "l");
        bitToCharProperties.setProperty("01101101", "m");
        bitToCharProperties.setProperty("01101110", "n");
        bitToCharProperties.setProperty("01101111", "o");
        bitToCharProperties.setProperty("01110000", "p");
        bitToCharProperties.setProperty("01110001", "q");
        bitToCharProperties.setProperty("01110010", "r");
        bitToCharProperties.setProperty("01110011", "s");
        bitToCharProperties.setProperty("01110100", "t");
        bitToCharProperties.setProperty("01110101", "u");
        bitToCharProperties.setProperty("01110110", "v");
        bitToCharProperties.setProperty("01110111", "w");
        bitToCharProperties.setProperty("01111000", "x");
        bitToCharProperties.setProperty("01111001", "y");
        bitToCharProperties.setProperty("01111010", "z");//122

        //write it to xml
        try {
            os = new FileOutputStream(bitToCharFile) ;
            bitToCharProperties.storeToXML(os, null);
            os.close();
        } catch (IOException ex) {
            System.out.println("Could not open stream or create file \"bitToChar\"");
            return false;
        }
        return true;
    }

    public static Properties getBitToCharProperties() {
        if (!bitToCharProperties.isEmpty())
            return bitToCharProperties;
        return null;
    }

    public static Properties getCharToBitProperties() {
        if (!charToBitProperties.isEmpty())
            return charToBitProperties;
        return null;
    }

    /**
     * Converts a bit sequence into String
     * @param bitsSequence The bits we want to convert
     * @return The pairing string
     */
    public static String convertBitToChar(String bitsSequence){
        String finalString = "";
        for (int i=0; i<bitsSequence.length();i+=8){
            String propertyKey = bitsSequence.substring(i, i+8);
            finalString+=bitToCharProperties.getProperty(propertyKey);
        }
        return finalString;
    }

    /**
     * Converts a string into bit sequence
     * @param stringSeq The string of characters we wanto to convert
     * @return A bit sequence as String.
     */
    public static String convertCharToBit(String stringSeq){
        String finalBitSeq = "";
        for (int i=0;i<stringSeq.length();i++){
            String propertyKey = stringSeq.substring(i, i+1);
            finalBitSeq += charToBitProperties.getProperty(propertyKey);
        }
        return finalBitSeq;
    }

    /**
     * Gets the message (characters), converts each character one by one to an 8-bit string
     * and calls "Encrypt" to encryptTextMessage the character. Forms the final binary message
     * @param message The message to be encrypted
     * @return The encrypted message as a binary-string
     */
    public String encryptTextMessage(String message, String key) {
        StringBuilder encryptedMsg = new StringBuilder("");
        char msgAsChar[] = message.toCharArray();
        Encrypt encrypt = new Encrypt(key);
        for (char c : msgAsChar) {
            String bitSequence = "";
            //convert every char to bit
            bitSequence = CharBitPairing.getCharToBitProperties().getProperty(String.valueOf(c));
            //encryptTextMessage the bit sequence representing every character
            encrypt = new Encrypt(key);
            String encryptedChar = encrypt.encryptText(bitSequence);
            //build the whole sequence
            encryptedMsg.append(encryptedChar);
        }
        return encryptedMsg.toString();
    }

    /**
     * Takes a n-bit long message and for each 8-bit "word"
     * calls "Decrypt". Then converts binary Decrypt's results to characters.
     * Forms the final "character" message.
     * @param message
     * @return
     */
    public String decryptBinaryMessage(String message, String key) {
        StringBuilder decryptedMsg = new StringBuilder("");
        String bitSequence = "";
        Decrypt decryptedChar = new Decrypt(key);

        //convert 8-bit "words" to chars
        for (int i = 0; i < message.length(); i += 8) {
            //get the 8-bit that represent an encrypted character
            bitSequence = message.substring(i, i + 8);
            //get the character from the decrypted sequence.
            String character = CharBitPairing.getBitToCharProperties().getProperty(decryptedChar.decryptText(bitSequence));
            decryptedMsg.append(character);
        }
        return decryptedMsg.toString();
    }
}
