// Yichao Qiu | yiq23 | Assignment 5
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.BigInteger;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

    public static final int PORT = 8765;

    JTextArea outputArea;
    JLabel prompt;
    JTextField inputField;
    String myName, serverName;
    Socket connection;
    ObjectOutputStream myWriter;
    ObjectInputStream myReader;

    private byte[] encryptName;
    BigInteger E, N, key, encryptKey;

    SymCipher cipher;
    String cType;


    public static void main(String[] args) {
        SecureChatClient JR = new SecureChatClient();
        JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    public SecureChatClient() {
        try {

            myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
            serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
            InetAddress addr = InetAddress.getByName(serverName);
            connection = new Socket(addr, PORT); // Connect to server with new

            myWriter = new ObjectOutputStream(connection.getOutputStream());

            myWriter.flush();

            myReader = new ObjectInputStream(connection.getInputStream()); // initialize myReader to read from server

            E = (BigInteger) myReader.readObject();
            N = (BigInteger) myReader.readObject();

            // Output the keys E and N from the server

            System.out.println("Key E: " + E);
            System.out.println("Key N: " + N);

            cType = (String) myReader.readObject(); // grab desired encryption type

            // Output the type of symmetric encryption (Add128 or Substitute) to the console
            System.out.println("Encryption Method: " + cType);

            if (cType.equalsIgnoreCase("sub")) {
                cipher = new Substitute();
            } else if (cType.equalsIgnoreCase("add")) {
                cipher = new Add128();
            }

            // get the key

            key = new BigInteger(1, cipher.getKey()); // grab key from SymCipher instance

            System.out.println("Symmetric Key: " + key);

            encryptKey = key.modPow(E, N);

            myWriter.writeObject(encryptKey); // send key to server to use for encryption
            myWriter.flush(); // flush to avoid deadlocking

            encryptName = cipher.encode(myName); // encrypt username

            myWriter.writeObject(encryptName);
            myWriter.flush();

            // improved chat client 
            this.setTitle(myName);

            Box b = Box.createHorizontalBox();
            outputArea = new JTextArea(8, 30);
            outputArea.setEditable(false);
            b.add(new JScrollPane(outputArea));

            outputArea.append("Welcome to the Chat Group, " + myName + "\n");

            inputField = new JTextField("");
            inputField.addActionListener(this);

            prompt = new JLabel("Type your messages below:");
            Container c = getContentPane();

            c.add(b, BorderLayout.NORTH);
            c.add(prompt, BorderLayout.CENTER);
            c.add(inputField, BorderLayout.SOUTH);

            Thread outputThread = new Thread(this);
            outputThread.start();

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    try {
                        myWriter.writeObject(cipher.encode("CLIENT CLOSING"));
                        myWriter.flush();
                    } catch (IOException io) {
                        System.out.println("Problem closing client!");
                    }
                    System.exit(0);
                }
            });

            setSize(500, 200);
            setVisible(true);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String currMsg = e.getActionCommand(); // Get input value
        inputField.setText("");

        try {
            currMsg = myName + ": " + currMsg;// myWriter.println(myName + ":" + currMsg); 

            // similar to the run but backwards cuz we are sending it
            byte[] byteMsg = cipher.encode(currMsg);

            myWriter.writeObject(byteMsg);
            myWriter.flush();

            byte[] bytes = currMsg.getBytes();
            System.out.println("Original String Message: " + currMsg);// The original String message
            System.out.println("Array of bytes: " + Arrays.toString(bytes));// The corresponding array of bytes
            System.out.println("Encrypted array of bytes: " + Arrays.toString(byteMsg));// The encrypted array of bytes

        } catch (IOException io) {
            System.err.println("Error: Could not send message to server!");
        }

    }
    public void run() {
        while (true) {
            try {

                // first get the message from the server
                byte[] cryptMsg = (byte[]) myReader.readObject(); // grab encrypted msg from server

                String currMsg = cipher.decode(cryptMsg); // pass to SymCipher object for decryption
                outputArea.append(currMsg + "\n");

                byte[] bytes = currMsg.getBytes(); // convert currMsg to string for output to console

                System.out.println("Recieved array of bytes: " + Arrays.toString(cryptMsg));// The array of bytes received
                System.out.println("Decrypted array of bytes: " + Arrays.toString(bytes)); // The decrypted array of bytes
                System.out.println("Corresponding string: " + currMsg);// The corresponding String
            }
            catch (Exception e) {
                System.out.println(e + ", closing client!");
                break;
            }
        }
        System.exit(0);
    }

}