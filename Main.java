import java.awt.BorderLayout;
import javax.swing.JFrame;
import java.util.*;
import java.io.*;
import java.net.*;

public class Main{
 
    public static void main( String[] args ) throws Exception {
    
    	JFrame frame = new JFrame( "Pong CC8" );
    	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    	frame.setLayout( new BorderLayout() );
    	
    	Pong pong = new Pong();
    	frame.add( pong, BorderLayout.CENTER );
    	
    	frame.setSize( 800, 600 );
    	frame.setVisible( true );
    	
    }
}