import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Clients extends JFrame2 {
    private JTextArea DisplaysText;
    private SocketData socket;
    
    public Clients(){
        super( "Clients" );
        
        DisplaysText = new JTextArea();
        getContentPane().add(new JScrollPane(DisplaysText), BorderLayout.CENTER);
        setSize(400, 300);
        setVisible(true);
        
        try {
            socket = new SocketData( 5000 );
        } catch(SocketException socketException){
            socketException.printStackTrace();
            System.exit(1);
        }
    }
   
    private void waitForPackets(){
        while (true){
            try {
                
                byte data[] = new byte [100];
                DatagramPacket receivePacket = new DatagramPacket (data, data.length);
                socket.receive(receivePacket);
                
               
                displayMessage("\nPacket received:" +
                        "\nFrom host: " + receivePacket.getAddress() +
                        "\nHost port: " + receivePacket.getPort() +
                        "\nLength: " + receivePacket.getLength() +
                        "\nContaining:\n\t" + new String (receivePacket.getData(),
                        0, receivePacket.getLength()));
                
               
                sendPacketToServer(receivePacket);
                
            } catch(IOException ioException){
                displayMessage(ioException.toString() + "\n");
                ioException.printStackTrace();
            }
        }
    }
    
    private void sendPacketToServer(DatagramPacket receivePacket) throws IOException {
        displayMessage("\n\nEcho data to Server...");
        String chData ="";
        
        String Data = new String (receivePacket.getData());
        
        //Reverse string
        for (int x = receivePacket.getLength(); x >= 0; x--){
            char chr = Data.charAt(x);
            chData += chr;
        }
        chData = chData.trim();
        
        char[] chArray = chData.toCharArray();

        byte data[] = new String(chArray).getBytes("UTF-8");
        
        DatagramPacket sendPacket = new DatagramPacket(
        data, data.length, receivePacket.getAddress(), receivePacket.getPort());
             
        socket.send(sendPacket);//send packet
        displayMessage ("Packet sent\n");
    }
    
    private void displayMessage(final String messageToDisplay) {
        
        SwingUtilities.invokeLater (new Runnable(){
            public void run()
            {
                DisplaysText.append(messageToDisplay);
                DisplaysText.setCaretPosition(DisplaysText.getText().length());
            }
        }
        )
    }
    
    public static void main(String args[]){
        Clients application = new Clients();
        application.setDefaultCloseOperation(JFrame2.EXIT_ON_CLOSE);
        application.waitForPackets();
    }
}
