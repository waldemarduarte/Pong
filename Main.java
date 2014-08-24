import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.util.*;
import java.io.*;
import java.net.*;

public class Main {
 
    public static void main( String[] args ) throws Exception {
    
    	JFrame frame = new JFrame( "Pong CC8" );
    	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    	frame.setLayout( new BorderLayout() );
        
        boolean hasIP = false;
        String ip = "";
        if ( args.length > 0 ) {
            hasIP = true;
            ip = args[0];
        }
    	
        ClientOrServerTCP clientOrServerTCP = new ClientOrServerTCP( frame, hasIP, ip );
        
        Thread thread = new Thread( clientOrServerTCP );
        thread.start();
    }
}

final class ClientOrServerTCP implements Runnable {
    //Client
    BufferedReader inFromUser;
    Socket clientSocket;
    BufferedReader inFromServer;
    DataOutputStream outToServer;
    String serverMessage = "";
    
    //Server
    ServerSocket incomeSocket;
    Socket connectionSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    String clientMessage = "";
    
    //Socket, IP
    int socket = 2407;
    String ip = "localhost";
    
    //Pong
    Pong pong;
    JFrame frame;
    
    //Player one?
    boolean amIPlayerOne = true;

    public ClientOrServerTCP( JFrame frame, boolean hasIP, String ip ) throws Exception {
        this.frame = frame;
        if ( hasIP ) {
            this.ip = ip;
        }
        System.out.println( "IP To Connect: " + this.ip );
        //System.out.println( "ServerIP: " + InetAddress.getLocalHost() ); //Doesnt work
        
        try {
            System.out.println( "=====Try TCP Client=====" );

            inFromUser = new BufferedReader( new InputStreamReader( System.in ) );
            System.out.println( "*****Trying Connection To Server In Socket " + socket  + " *****" );
            
            clientSocket = new Socket( ip, socket );
            
            System.out.println( "=====Successful Connection To Server=====" );
            outToServer = new DataOutputStream( clientSocket.getOutputStream() );
            inFromServer = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
            
            //System.out.print( "Client: ");
            //String messageToServer = inFromUser.readLine();//THIS <-----------------------------------------------------
            //outToServer.writeBytes( messageToServer + '\n' );//THIS <-----------------------------------------------------
            
            //serverMessage = inFromServer.readLine();//THIS <-----------------------------------------------------
            //System.out.println( "Server: " + serverMessage );
            
            amIPlayerOne = false;
            this.pong = new Pong( amIPlayerOne, inFromServer, outToServer );
        }
        catch ( Exception e ) {
            System.out.println( "=====Server isnt up, you will be the Server=====" );
            
            incomeSocket = new ServerSocket( socket );

            System.out.println( "=====Try TCP Server=====" );
            
            System.out.println( "*****Waiting Connection From Client In Socket " + socket  + " *****" );
            connectionSocket = incomeSocket.accept();
            
            System.out.println( "=====Successful Connection From Client=====" );
            inFromClient = new BufferedReader( new InputStreamReader( connectionSocket.getInputStream() ) );
            outToClient = new DataOutputStream( connectionSocket.getOutputStream() );
            //System.out.println( "Waiting Client" );
            //clientMessage = inFromClient.readLine();//THIS <-----------------------------------------------------
            
            //outToClient.writeBytes( clientMessage );//THIS <-----------------------------------------------------
            //System.out.println( clientMessage );
            amIPlayerOne = true;
            this.pong = new Pong( amIPlayerOne, inFromClient, outToClient );
        }
        
        this.frame.add( this.pong, BorderLayout.CENTER );
    	this.frame.setSize( 800, 600 );
    	this.frame.setVisible( true );
        
        System.out.println( "amIPlayerOne: " + amIPlayerOne );
    }

    public void run () {

        try {
            if ( amIPlayerOne ) {
                while ( true ) {
                    clientMessage = inFromClient.readLine();
                    //System.out.println( "Client Message in Thread" + clientMessage );
                    this.pong.otherPlayerMessage = clientMessage;
                    this.pong.someActionOtherPlayer = true;
                    
                }
            }
            else {
                while ( true ) {
                    serverMessage = inFromServer.readLine();
                    //System.out.println( "Server Message in Thread" + serverMessage );
                    this.pong.otherPlayerMessage = serverMessage;
                    this.pong.someActionOtherPlayer = true;
                    
                }
            }
            
            
        }
        catch ( Exception e ) {
            System.out.println( "Exception with amIPlayerOne:" + amIPlayerOne );
            //System.out.println( "Error:===>" + printStackTrace( e ) );
            //System.out.println( "ERROR: Exception" );
        }
    }
}


