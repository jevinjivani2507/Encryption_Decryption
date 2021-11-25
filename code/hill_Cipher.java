import java.util.*;
import javax.print.event.PrintEvent;
import javax.sound.sampled.SourceDataLine;

public class hill_Cipher {

    static int[][] keyMatrix = new int[3][3];
    static int[][] inverseMatrix = new int[3][3];
    static int[] msgMatrix = new int[3];
    static int[] encryptMatrix = new int[3];
    static int[] decryptMatrix = new int[3];

    static Map<Character, Integer> key = new HashMap<Character, Integer>();
    static Map<Integer, Character> itoc = new HashMap<Integer, Character>();
    static float determinant = 0;
    static char trail = '!';
    static String message;
    static String encryptedMessage = "";
    static String decrypedMessage = "";
    static String keyMessage = "";

    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);

        numberToChar();
        charToNumber();
        System.out.println("Select:");
        System.out.println("1.Encryption");
        System.out.println("2.Decryption");
        System.out.println("3.Exit");

        int choice = s.nextInt();

        if (choice == 1) {
            System.out.print("Enter message :: ");
            message = s.nextLine();
            message = s.nextLine();

            System.out.print("Enter key message :: ");
            keyMessage = s.nextLine();
            // keyMessage = s.nextLine();

            encrypt(message);
            System.out.println("Encrypted Message :: " + encryptedMessage);
        } else if (choice == 2) {
            System.out.print("Enter message :: ");
            message = s.nextLine();
            message = s.nextLine();

            System.out.print("Enter key message :: ");
            keyMessage = s.nextLine();

            trail = message.charAt(message.length() - 1);
            message = message.substring(0, message.length() - 1);

            decrypt(message);

            System.out.println("Decrypted message :: " + decrypedMessage);
        }
    }

    static void charToNumber() {
        char a = 'A';
        for (int i = 0; i < 26; i++) {
            key.put(a, i);
            a++;
        }
        a = 'a';
        for (int i = 0; i < 26; i++) {
            key.put(a, i + 26);
            a++;
        }
        a = '0';
        for (int i = 0; i < 10; i++) {
            key.put(a, i + 52);
            a++;
        }
        key.put('+', 62);
        key.put('=', 63);
        key.put(' ', 64);
        key.put('.', 65);
        key.put('?', 66);
        key.put('!', 67);
        key.put('/', 68);
    }

    static void numberToChar() {
        char a = 'A';
        for (int i = 0; i < 26; i++) {
            itoc.put(i, a);
            a++;
        }
        a = 'a';
        for (int i = 0; i < 26; i++) {
            itoc.put(i + 26, a);
            a++;
        }
        a = '0';
        for (int i = 0; i < 10; i++) {
            itoc.put(i + 52, a);
            a++;
        }
        itoc.put(62, '+');
        itoc.put(63, '=');
        itoc.put(64, ' ');
        itoc.put(65, '.');
        itoc.put(66, '?');
        itoc.put(67, '!');
        itoc.put(68, '/');
    }

    static void group(String msg) {
        if (msg.length() % 3 == 1) {
            message += '!';
            message += '!';
            trail = '@';
        } else if (msg.length() % 3 == 2) {
            message += '!';
            trail = '!';
        } else {
            trail = ')';
        }
        System.out.println("Message After :: " + message);
    }

    static void createKeyMatrix() {
        keyMatrix[0][0] = 1;
        keyMatrix[0][1] = 3;
        keyMatrix[0][2] = 3;
        keyMatrix[1][0] = 1;
        keyMatrix[1][1] = 4;
        keyMatrix[1][2] = 3;
        keyMatrix[2][0] = 1;
        keyMatrix[2][1] = 3;
        keyMatrix[2][2] = 4;
        int a = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                keyMatrix[i][j] = key.get(keyMessage.charAt(a));
                a++;
            }
        }

        if (determinant != 1) {
            keyMatrix[0][1] = 1;
            keyMatrix[0][2] = 0;
            keyMatrix[1][0] = keyMatrix[0][0] - 1;
            keyMatrix[1][1] = 1;
            keyMatrix[1][2] = 0;
            keyMatrix[2][0] = 0;
            keyMatrix[2][1] = 0;
            keyMatrix[2][2] = 1;
        }

        for (int i = 0; i < 3; i++) {
            determinant += (keyMatrix[0][i] * (keyMatrix[1][(i + 1) % 3] * keyMatrix[2][(i + 2) % 3]
                    - keyMatrix[1][(i + 2) % 3] * keyMatrix[2][(i + 1) % 3]));
        }
    }

    static void inverse() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                inverseMatrix[i][j] = (int) (((keyMatrix[(j + 1) % 3][(i + 1) % 3]
                        * keyMatrix[(j + 2) % 3][(i + 2) % 3])
                        - (keyMatrix[(j + 1) % 3][(i + 2) % 3] * keyMatrix[(j + 2) % 3][(i + 1) % 3])) / determinant);
            }
        }
    }

    static void createMessageMatrix(Character[] msg, int[] matrix) {
        for (int i = 0; i < 3; i++) {
            matrix[i] = key.get(msg[i]);
        }
    }

    static void createEncryptMatrix(int[][] key, int[] msg) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                encryptMatrix[i] += (keyMatrix[i][j] * msgMatrix[j]);
            }
        }
        for (int i = 0; i < 3; i++) {
            encryptMatrix[i] %= 69;
        }
    }

    static void encrypt(String msg) {
        Character[] tempMsg = new Character[3];
        group(msg);
        createKeyMatrix();
        for (int i = 0; i < message.length(); i += 3) {
            tempMsg[0] = message.charAt(i);
            tempMsg[1] = message.charAt(i + 1);
            tempMsg[2] = message.charAt(i + 2);

            createMessageMatrix(tempMsg, msgMatrix);
            createEncryptMatrix(keyMatrix, msgMatrix);

            for (int j = 0; j < 3; j++) {
                encryptedMessage += itoc.get(encryptMatrix[j]);
            }
            encryptMatrix[0] = 0;
            encryptMatrix[1] = 0;
            encryptMatrix[2] = 0;
        }
        encryptedMessage += trail;
    }

    static void createDecryptMatrix(int[][] key, int[] msg) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                decryptMatrix[i] += (inverseMatrix[i][j] * msgMatrix[j]);
            }
            while (decryptMatrix[i] < 0) {
                decryptMatrix[i] += 69;
            }
        }
        for (int i = 0; i < 3; i++) {
            decryptMatrix[i] %= 69;
        }
    }

    static void decrypt(String msg) {
        Character[] tempMsg = new Character[3];
        createKeyMatrix();
        inverse();

        for (int i = 0; i < message.length(); i += 3) {
            tempMsg[0] = message.charAt(i);
            tempMsg[1] = message.charAt(i + 1);
            tempMsg[2] = message.charAt(i + 2);
            createMessageMatrix(tempMsg, msgMatrix);
            createDecryptMatrix(keyMatrix, msgMatrix);
            for (int j = 0; j < 3; j++) {
                decrypedMessage += itoc.get(decryptMatrix[j]);
            }
            decryptMatrix[0] = 0;
            decryptMatrix[1] = 0;
            decryptMatrix[2] = 0;
        }
        if (trail == '!') {
            decrypedMessage = decrypedMessage.substring(0, decrypedMessage.length() - 1);
        } else if (trail == '@') {
            decrypedMessage = decrypedMessage.substring(0, decrypedMessage.length() - 2);
        }
    }

}