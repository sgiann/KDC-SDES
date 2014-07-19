package AssistantClasses;

import java.util.BitSet;

/**
 * The class splits decryption process in two parts.
 * The first part is implemented by the constructor where given the key,
 * the basic structures and values of the process are calculated.
 * The second part is implemented by decryptText function
 * where the given ciphertext is decoded using the values mentioned above.
 * @author Giannouloudis Stergios
 */
public class Decrypt {

    private static BitSet initialKey, cipherTextAsBS, reorderedKey;
    private BitSet ls1l, ls1r, key1;
    private BitSet ls2l, ls2r, key2;;
    private BitSet ip, ep, epXORkey, P4;
    private BitSet firstRightOutput, stageOneOutputXORp4, stageOneOutput, stageTwoOutput;
    private BitSet plainText;

    private final int S0[][] = {{1, 0, 3, 2},
        {3, 2, 1, 0},
        {0, 2, 1, 3},
        {3, 1, 3, 2}};
    private final int S1[][] = {{0, 1, 2, 3},
        {2, 0, 1, 3},
        {3, 0, 1, 0},
        {2, 1, 0, 3}};


    public Decrypt(String key) {

        /*Phase 1. Create subKey1 and subKey2*/
        convertKeyToBitSet(key);

        permuteInitialKey();

        createLS1();

        createKey1(ls1l, ls1r);

        createLS2();

        createKey2(ls2l, ls2r);
    }

    /**
     * This method does not comply with the original design of the class
     * and thats why it uses "toString()" at the end.
     * It takes the 8-bit cipherText and transforms it to plainText
     * 
     * @param cipherText The ciphertext
     * @return The plaintext
     */
    public String decryptText(String cipherText){
        String result = "";

        /*Start Decryption proccess*/
        fillCipherText(cipherText);

        formIP();

        ep();

        epXORkey(key2);

        sOperations();

        firstRightOutput = new BitSet(8);
        firstRightOutput = ipXORp4(ip);

        stageOneOutput = new BitSet(8);
        stageOneOutput = stageOutput(ip);

        /*SECOND LOP OF DECRYPTION*/
        ep(stageOneOutput);

        epXORkey(key1);

        sOperations();

        stageOneOutputXORp4 = new BitSet(8);
        stageOneOutputXORp4 = ipXORp4(stageOneOutput);

        createSecondStageOutput();

        createPlainText();
        result = this.toString();
        return result;
    }

    /**
     * Takes the 16-bit encrypted key
     * Decrypts it partialy using decryptText().
     * Removes the 6 extra digits
     *
     * @param encKey
     * @return The 10-bit decrypted key.
     */
    public String decryptKey(String encKey ){
        String result = "";

        result += decryptText(encKey.substring(0, 8));
        result += decryptText(encKey.substring(8, encKey.length()));

        result = result.substring(0, 10);

        return result;
    }

    /**
     * Coverts the key from <code>String</code> to <code>BitSet</code>
     * @param key The key to be converted
     */
    public static void convertKeyToBitSet(String key) {
        String zero = "0";
        initialKey = new BitSet(10);

        for (int i = 0; i < key.length(); i++) {
            if (zero.equals(String.valueOf(key.charAt(i)))) {
                initialKey.set(i, false);
            } else {
                initialKey.set(i, true);
            }
        }
    }

    /**
     * Performs the initial permutation to the key
     */
    private void permuteInitialKey() {
        reorderedKey = new BitSet(10);

        reorderedKey.set(0, initialKey.get(2));
        reorderedKey.set(1, initialKey.get(4));
        reorderedKey.set(2, initialKey.get(1));
        reorderedKey.set(3, initialKey.get(6));
        reorderedKey.set(4, initialKey.get(3));
        reorderedKey.set(5, initialKey.get(9));
        reorderedKey.set(6, initialKey.get(0));
        reorderedKey.set(7, initialKey.get(8));
        reorderedKey.set(8, initialKey.get(7));
        reorderedKey.set(9, initialKey.get(5));
    }

    /**
     * Breaks the <code>reorderedKey</code> BitSet in two seperate BitSets<br />
     * and performs a left shift by one place.
     * @param reorderedKey
     */
    private void createLS1() {
        this.ls1l = new BitSet(5);
        this.ls1r = new BitSet(5);

        ls1l.set(0, reorderedKey.get(1));
        ls1l.set(1, reorderedKey.get(2));
        ls1l.set(2, reorderedKey.get(3));
        ls1l.set(3, reorderedKey.get(4));
        ls1l.set(4, reorderedKey.get(0));

        ls1r.set(0, reorderedKey.get(6));
        ls1r.set(1, reorderedKey.get(7));
        ls1r.set(2, reorderedKey.get(8));
        ls1r.set(3, reorderedKey.get(9));
        ls1r.set(4, reorderedKey.get(5));

    }

    /**
     * Takes two BitSets and creates the first sub-key.
     * @param pl1Left
     * @param pl1Right
     */
    private void createKey1(BitSet pl1Left, BitSet pl1Right) {
        this.key1 = new BitSet(8);

        key1.set(0, pl1Right.get(0));
        key1.set(1, pl1Left.get(2));
        key1.set(2, pl1Right.get(1));
        key1.set(3, pl1Left.get(3));
        key1.set(4, pl1Right.get(2));
        key1.set(5, pl1Left.get(4));
        key1.set(6, pl1Right.get(4));
        key1.set(7, pl1Right.get(3));
    }

    /**
     * Creates the LS2 BitSets by performing a 2-place left shift on ls1 BitSets.
     */
    private void createLS2() {
        this.ls2l = new BitSet(5);
        this.ls2r = new BitSet(5);

        ls2l.set(0, ls1l.get(2));
        ls2l.set(1, ls1l.get(3));
        ls2l.set(2, ls1l.get(4));
        ls2l.set(3, ls1l.get(0));
        ls2l.set(4, ls1l.get(1));

        ls2r.set(0, ls1r.get(2));
        ls2r.set(1, ls1r.get(3));
        ls2r.set(2, ls1r.get(4));
        ls2r.set(3, ls1r.get(0));
        ls2r.set(4, ls1r.get(1));
    }

    /**
     * takes two BitSets and creates the second sub-key.
     * @param ls2l
     * @param ls2r
     */
    private void createKey2(BitSet ls2l, BitSet ls2r) {
        this.key2 = new BitSet(8);

        key2.set(0, ls2r.get(0));
        key2.set(1, ls2l.get(2));
        key2.set(2, ls2r.get(1));
        key2.set(3, ls2l.get(3));
        key2.set(4, ls2r.get(2));
        key2.set(5, ls2l.get(4));
        key2.set(6, ls2r.get(4));
        key2.set(7, ls2r.get(3));
    }

    /**
     * Converts the plaintext from <code>String</code> to <code>BitSet</code>
     * @param cipherText The plaintexto to be converted
     */
    private void fillCipherText(String cipherText) {

        String zero = "0";
        cipherTextAsBS = new BitSet(8);

        for (int i = 0; i < cipherText.length(); i++) {
            if (zero.equals(String.valueOf(cipherText.charAt(i)))) {
                cipherTextAsBS.set(i, false);
            } else {
                cipherTextAsBS.set(i, true);
            }
        }
    }

    /**
     * Creates a bitset to contain the Initial Permutation
     */
    private void formIP() {
        ip = new BitSet(8);

        ip.set(0, cipherTextAsBS.get(1));
        ip.set(1, cipherTextAsBS.get(5));
        ip.set(2, cipherTextAsBS.get(2));
        ip.set(3, cipherTextAsBS.get(0));
        ip.set(4, cipherTextAsBS.get(3));
        ip.set(5, cipherTextAsBS.get(7));
        ip.set(6, cipherTextAsBS.get(4));
        ip.set(7, cipherTextAsBS.get(6));
    }

    private void ep() {
        ep = new BitSet(8);

        ep.set(0, ip.get(7));
        ep.set(1, ip.get(4));
        ep.set(2, ip.get(5));
        ep.set(3, ip.get(6));
        ep.set(4, ip.get(5));
        ep.set(5, ip.get(6));
        ep.set(6, ip.get(7));
        ep.set(7, ip.get(4));
    }

    /**
     * Performs the XOR operator between E/P and the given sub-key
     * @param key
     */
    private void epXORkey(BitSet key) {
        epXORkey = new BitSet(8);
        epXORkey = ep;
        epXORkey.xor(key);
    }

    /**
     * This function initialy converts the binary numbers< br/>
     * into decimal in order to determine the rows and cols< br/>
     * of the S0 and S1 tables. Then takes its content.< br/>
     * Finally calls <code>formP4()</code>.
     */
    private void sOperations() {
        int s0RowIndex, s0ColIndex, s1RowIndex, s1ColIndex;

        //find the S0 tables row index
        if (epXORkey.get(0)) {
            if (epXORkey.get(3)) {
                s0RowIndex = 3;
            } else {
                s0RowIndex = 2;
            }
        } else {
            if (epXORkey.get(3)) {
                s0RowIndex = 1;
            } else {
                s0RowIndex = 0;
            }
        }
        //find the S0 tables column index
        if (epXORkey.get(1)) {
            if (epXORkey.get(2)) {
                s0ColIndex = 3;
            } else {
                s0ColIndex = 2;
            }
        } else {
            if (epXORkey.get(2)) {
                s0ColIndex = 1;
            } else {
                s0ColIndex = 0;
            }
        }
        //find the S1 row index
        if (epXORkey.get(4)) {
            if (epXORkey.get(7)) {
                s1RowIndex = 3;
            } else {
                s1RowIndex = 2;
            }
        } else {
            if (epXORkey.get(7)) {
                s1RowIndex = 1;
            } else {
                s1RowIndex = 0;
            }
        }
        //find the S1 column index
        if (epXORkey.get(5)) {
            if (epXORkey.get(6)) {
                s1ColIndex = 3;
            } else {
                s1ColIndex = 2;
            }
        } else {
            if (epXORkey.get(6)) {
                s1ColIndex = 1;
            } else {
                s1ColIndex = 0;
            }
        }

        //get the content of S1 and S2
        Integer s0content = S0[s0RowIndex][s0ColIndex];
        Integer s1content = S1[s1RowIndex][s1ColIndex];

        /* from the contents above, we have to find the
         * 4-bit binary number to whom we will apply
         * the permutation P4!
         */
        formP4(s0content, s1content);
    }

    /**
     * Converts the S tables' contents into binary numbers< br/>
     * and forms the P4 BitSet
     * @param s0content
     * @param s1content
     */
    private void formP4(Integer s0content, Integer s1content) {
        BitSet temp = new BitSet(4);
        P4 = new BitSet(4);

        switch (s0content) {
            case 3:
                temp.set(0);
                temp.set(1);
                break;
            case 2:
                temp.set(0);
                temp.set(1, false);
                break;
            case 1:
                temp.set(0, false);
                temp.set(1);
                break;
            case 0:
                temp.set(0, false);
                temp.set(1, false);
                break;
            default:
                temp.set(0, false);
                temp.set(1, false);
                break;
        }

        switch (s1content) {
            case 3:
                temp.set(2);
                temp.set(3);
                break;
            case 2:
                temp.set(2);
                temp.set(3, false);
                break;
            case 1:
                temp.set(2, false);
                temp.set(3);
                break;
            case 0:
                temp.set(2, false);
                temp.set(3, false);
                break;
            default:
                temp.set(2, false);
                temp.set(3, false);
                break;
        }

        //permutate to p4
        P4.set(0, temp.get(1));
        P4.set(1, temp.get(3));
        P4.set(2, temp.get(2));
        P4.set(3, temp.get(0));
    }

    /**
     * Performs the XOR operator between the given <code>BitSet</code> and the P4 <code>BitSet</code>.
     * @param ip The 8-bit long input <code>BitSet</code> of the stage.
     * @return A 4-bit long <code>BitSet</code>.
     */
    private BitSet ipXORp4(BitSet ip) {
        BitSet rightOutput = new BitSet(4);

        rightOutput.set(0, ip.get(0));
        rightOutput.set(1, ip.get(1));
        rightOutput.set(2, ip.get(2));
        rightOutput.set(3, ip.get(3));

        rightOutput.xor(P4);

        return rightOutput;
    }

    /**
     * Forms the output of the first stage.
     * @param ip The input 8-bit long <code>BitSet</code> for that stage
     * @return An 8-bit long <code>BitSet</code>
     */
    private BitSet stageOutput(BitSet ip) {
        BitSet output = new BitSet(8);

        for (int i = 0; i < 4; i++) {
            output.set(i, ip.get(i + 4));
        }

        for (int i = 4; i < 8; i++) {
            output.set(i, firstRightOutput.get(i - 4));
        }

        return output;
    }

    /**
     * Applies expantion/permutation to the given BitSet
     * @param ip The IP bitset on wich we apply expantion/permutation.
     */
    private void ep(BitSet ip) {
        BitSet temp = new BitSet(4);
        ep = new BitSet(8);

        for (int i = 4; i < 8; i++) {
            temp.set(i - 4, ip.get(i));
        }

        ep.set(0, temp.get(3));
        ep.set(1, temp.get(0));
        ep.set(2, temp.get(1));
        ep.set(3, temp.get(2));
        ep.set(4, temp.get(1));
        ep.set(5, temp.get(2));
        ep.set(6, temp.get(3));
        ep.set(7, temp.get(0));

    }

    /**
     * Generates the output of the Second stage
     */
    private void createSecondStageOutput() {
        stageTwoOutput = new BitSet(8);

        for (int i = 0; i < 4; i++) {
            stageTwoOutput.set(i, stageOneOutputXORp4.get(i));
        }
        for (int i = 4; i < 8; i++) {
            stageTwoOutput.set(i, stageOneOutput.get(i));
        }
    }


    private void createPlainText() {
        plainText = new BitSet(8);

        plainText.set(0, stageTwoOutput.get(3));
        plainText.set(1, stageTwoOutput.get(0));
        plainText.set(2, stageTwoOutput.get(2));
        plainText.set(3, stageTwoOutput.get(4));
        plainText.set(4, stageTwoOutput.get(6));
        plainText.set(5, stageTwoOutput.get(1));
        plainText.set(6, stageTwoOutput.get(7));
        plainText.set(7, stageTwoOutput.get(5));
    }

    @Override
    public String toString() {
        String encodedMessage = "";
        for(int i=0; i<8; i++){
            if(plainText.get(i))
                encodedMessage += "1";
            else
                encodedMessage += "0";
        }
        return encodedMessage;
    }
}
