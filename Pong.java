import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class Pong extends JPanel implements ActionListener, KeyListener {

    private boolean showTitleScreen = true;
    private boolean playing = false;
    private boolean gameOver = false;

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean wPressed = false;
    private boolean sPressed = false;

    private int ballPositionX = 60;
    private int ballPositionY = 250;
    private int ballDiameter = 20;
    private int ballDeltaX = 3;
    private int ballDeltaY = 3;

    private int playerOnePadPositionX = 25;
    private int playerOnePadPositionY = 250;
    private int playerOnePadWidth = 10;
    private int playerOnePadHeight = 50;

    private int playerTwoPadPositionX = 765;
    private int playerTwoPadPositionY = 250;
    private int playerTwoPadWidth = 10;
    private int playerTwoPadHeight = 50;

    private int paddleSpeed = 5;

    private int playerOneScore = 0;
    private int playerTwoScore = 0;
    
    private int pointsLimit = 5;
    
    private int gameTableToptEnd = 0;
    private int gameTableBottomtEnd = 500;
    private int gameTableLeftEnd = playerOnePadPositionX + playerOnePadWidth;
    private int gameTableRightEnd = playerTwoPadPositionX - playerTwoPadWidth;
    
    private int gameTableWidth = gameTableRightEnd - gameTableLeftEnd;
    private int gameTableHeight = gameTableBottomtEnd - gameTableToptEnd;
    
    Random rand = new Random();
    
    private int hitBallPositionX = 0;
    private int hitBallPositionY = 0;
    private int hitballDeltaX = 0;
    private int hitballDeltaY = 0;
    
    private int startStopPlayerOnePadPosition = playerOnePadPositionY;
    private int startStopPlayerTwoPadPosition = playerTwoPadPositionY;
    
    private boolean amIPlayerOne = true;
    
    //Client
    BufferedReader inFromUser;
    Socket clientSocket;
    DataOutputStream outToServer;
    BufferedReader inFromServer;
    String serverMessage = "";
    
    //Server
    ServerSocket incomeSocket;
    Socket connectionSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    String clientMessage = "";

    //construct a Pong
    public Pong() throws Exception {
        
        int socket = 2407;
        
        try {
            System.out.println( "=====Try TCP Client=====" );

            inFromUser = new BufferedReader( new InputStreamReader( System.in ) );
            System.out.println( "*****Trying Connection To Server In Socket " + socket  + " *****" );
            
            
            //clientSocket = new Socket( "localhost", socket );
            clientSocket = new Socket( "192.168.0.47", socket );//ASUS
            clientSocket = new Socket( "192.168.0.34", socket );//VAIO
            
            System.out.println( "=====Successful Connection To Server=====" );
            outToServer = new DataOutputStream( clientSocket.getOutputStream() );
            inFromServer = new BufferedReader( new InputStreamReader( clientSocket.getInputStream() ) );
            
            System.out.print( "Client: ");
            String messageToServer = inFromUser.readLine();//THIS <-----------------------------------------------------
            outToServer.writeBytes( messageToServer + '\n' );//THIS <-----------------------------------------------------
            
            //serverMessage = inFromServer.readLine();//THIS <-----------------------------------------------------
            //System.out.println( "Server: " + serverMessage );
            
            amIPlayerOne = false;
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
            System.out.println( "Waiting Text From Client");
            clientMessage = inFromClient.readLine();//THIS <-----------------------------------------------------
            
            //outToClient.writeBytes( clientMessage );//THIS <-----------------------------------------------------
            System.out.println( clientMessage );
            amIPlayerOne = true;
        }
        
        setBackground( Color.GREEN );

        //listen to key presses
        setFocusable( true );
        addKeyListener( this );

        //call step() 60 fps
        javax.swing.Timer timer = new javax.swing.Timer( 1000/60, this );
        timer.start();
    }

    public void actionPerformed( ActionEvent e ) {
        step();
    }

    public void step() {

        if( playing ) {
            //move player 1
            if ( wPressed ) {
                if ( playerOnePadPositionY - paddleSpeed > gameTableToptEnd ) {
                    playerOnePadPositionY -= paddleSpeed;
                }
            }
            if ( sPressed ) {
                if ( playerOnePadPositionY + paddleSpeed + playerOnePadHeight < gameTableBottomtEnd /*getHeight()*/ ) {
                    playerOnePadPositionY += paddleSpeed;
                }
            }

            //move player 2
            if ( upPressed ) {
                if ( playerTwoPadPositionY - paddleSpeed > gameTableToptEnd ) {
                    playerTwoPadPositionY -= paddleSpeed;
                }
            }
            if ( downPressed ) {
                if ( playerTwoPadPositionY + paddleSpeed + playerTwoPadHeight < gameTableBottomtEnd /*getHeight()*/  ) {
                    playerTwoPadPositionY += paddleSpeed;
                }
            }

            //where will the ball be after it moves?
            int nextBallLeft = ballPositionX + ballDeltaX;
            int nextBallRight = ballPositionX + ballDeltaX + ballDiameter;
            int nextBallTop = ballPositionY + ballDeltaY;
            int nextBallBottom = ballPositionY + ballDeltaY + ballDiameter;

            int playerOneRight = playerOnePadPositionX + playerOnePadWidth;
            int playerOneTop = playerOnePadPositionY;
            int playerOneBottom = playerOnePadPositionY + playerOnePadHeight;

            float playerTwoLeft = playerTwoPadPositionX;
            float playerTwoTop = playerTwoPadPositionY;
            float playerTwoBottom = playerTwoPadPositionY + playerTwoPadHeight;

            //ball bounces off top and bottom of screen
            if ( nextBallTop < 0 || nextBallBottom > gameTableBottomtEnd /*getHeight()*/ ) {
                ballDeltaY *= -1;
                
                hitBallPositionX = ballPositionX;
                hitBallPositionY = ballPositionY;
                hitballDeltaX = ballDeltaX;
                hitballDeltaY = ballDeltaY;
            }

            //will the ball go off the left side?
            if ( nextBallLeft < playerOneRight ) { 
                //is it going to miss the paddle?
                if ( nextBallTop > playerOneBottom || nextBallBottom < playerOneTop ) {

                    playerTwoScore ++;
                    
                    if ( playerTwoScore == pointsLimit ) {
                        playing = false;
                        gameOver = true;
                    }
                    
                    if ( playerTwoScore == 3 ) {
                        ballDeltaX = 5;
                    }
                    else if ( playerTwoScore == 4 ) {
                        ballDeltaX = 7;
                    }
                    
                    ballPositionX = ( gameTableLeftEnd + gameTableRightEnd ) / 2;
                    ballPositionY = ( gameTableToptEnd + gameTableBottomtEnd ) / 2;
                }
                else {
                    int n1 = rand.nextInt( 4 );
                    int n2 = rand.nextInt( 2 );
                    if ( n2 == 0 && ballDeltaY > n1 ) {
                        ballDeltaY -= n1;
                    }
                    else if ( n2 == 1 && ballDeltaY < n1 ) {
                        ballDeltaY += n1;
                    }
                    ballDeltaX *= -1;
                }
                hitBallPositionX = ballPositionX;
                hitBallPositionY = ballPositionY;
                hitballDeltaX = ballDeltaX;
                hitballDeltaY = ballDeltaY;
            }

            //will the ball go off the right side?
            if ( nextBallRight > playerTwoLeft ) {
                //is it going to miss the paddle?
                if ( nextBallTop > playerTwoBottom || nextBallBottom < playerTwoTop ) {

                    playerOneScore ++;
                    
                    if ( playerOneScore == pointsLimit ) {
                        playing = false;
                        gameOver = true;
                    }
                    
                    if ( playerOneScore == 3 ) {
                        ballDeltaX = 5;
                    }
                    else if ( playerOneScore == 4 ) {
                        ballDeltaX = 7;
                    }

                    ballPositionX = ( gameTableLeftEnd + gameTableRightEnd ) / 2;
                    ballPositionY = ( gameTableToptEnd + gameTableBottomtEnd ) / 2;
                }
                else {
                    int n1 = rand.nextInt( 4 );
                    int n2 = rand.nextInt( 2 );
                    if ( n2 == 0 && ballDeltaY > n1 ) {
                        ballDeltaY -= n1;
                    }
                    else if ( n2 == 1 && ballDeltaY < n1 ) {
                        ballDeltaY += n1;
                    }
                    ballDeltaX *= -1;
                }
                hitBallPositionX = ballPositionX;
                hitBallPositionY = ballPositionY;
                hitballDeltaX = ballDeltaX;
                hitballDeltaY = ballDeltaY;
            }

            //move the ball
            ballPositionX += ballDeltaX;
            ballPositionY += ballDeltaY;
        }

        //stuff has moved, tell this JPanel to repaint itself
        repaint();
    }

    //paint the game screen
    public void paintComponent( Graphics g ){

        super.paintComponent( g );
        g.setColor( Color.BLACK );

        if ( showTitleScreen ) {

            g.setFont( new Font( Font.DIALOG, Font.BOLD, 36 ) );
            g.setFont( new Font( Font.DIALOG, Font.BOLD, 36 ) );
            g.drawString( "Pong CC8", 300, 100 );
            
            g.setFont( new Font( Font.DIALOG, Font.BOLD, 22 ) );
            g.drawString( "Javier Prado 11002330", 275, 200 );
            g.drawString( "Alan Duarte  11002013", 275, 220 );

            g.setFont( new Font( Font.DIALOG, Font.BOLD, 18 ) );
            
            if ( amIPlayerOne ) {
                g.drawString( "You are Player 1 (Left side)", 300, 350 );
            }
            else {
                g.drawString( "You are Player 2 (Right side)", 300, 350 );
            }

            g.drawString( "Press 'ENTER' to play.", 300, 400 );
        }
        else if ( playing ) {
            
            int playerOneRight = playerOnePadPositionX + playerOnePadWidth;
            int playerTwoLeft =  playerTwoPadPositionX;
            
            //needed parameters to transfer between Client - Server (TCP)
            g.setFont( new Font( Font.DIALOG, Font.BOLD, 18 ) );
            g.drawString( "wKey: " + String.valueOf( wPressed ), 100, gameTableBottomtEnd - 60 );
            g.drawString( "sKey: " + String.valueOf( sPressed ), 100, gameTableBottomtEnd - 40 );
            g.drawString( "padOne: " + String.valueOf( startStopPlayerOnePadPosition ), 100, gameTableBottomtEnd - 20 );
            
            g.drawString( "upKey: " + String.valueOf( upPressed ), 600, gameTableBottomtEnd - 60 );
            g.drawString( "downKey: " + String.valueOf( downPressed ), 600, gameTableBottomtEnd - 40 );
            g.drawString( "padTwo: " + String.valueOf( startStopPlayerTwoPadPosition ), 600, gameTableBottomtEnd - 20 );
            
            g.drawString( "ballX: " + String.valueOf( hitBallPositionX ), 200, gameTableBottomtEnd - 40 );
            g.drawString( "ballY: " + String.valueOf( hitBallPositionY ), 500, gameTableBottomtEnd - 40 );
            g.drawString( "DeltaX: " + String.valueOf( hitballDeltaX ), 200, gameTableBottomtEnd - 20 );
            g.drawString( "DeltaY: " + String.valueOf( hitballDeltaY ), 500, gameTableBottomtEnd - 20 );

            //draw "goal lines" on each side
            g.drawLine( playerOneRight, 0, playerOneRight, gameTableBottomtEnd + 100 /*getHeight()*/ );
            g.drawLine( playerTwoLeft, 0, playerTwoLeft, gameTableBottomtEnd + 100 /*getHeight()*/ );
            
            //draw "bottom line"
            g.drawLine( playerOneRight, gameTableBottomtEnd, playerTwoLeft - 1, gameTableBottomtEnd /*getHeight()*/ );
            
            //draw line to scores separation
            int separatorLineMiddle = playerOneRight + ( gameTableWidth / 2 );
            g.drawLine( separatorLineMiddle, gameTableBottomtEnd, separatorLineMiddle, gameTableBottomtEnd + 100 /*getHeight()*/ );

            //draw the scores
            g.setFont( new Font( Font.DIALOG, Font.BOLD, 18 ) );
            g.drawString( "Player #1: " + String.valueOf( playerOneScore ), 200, gameTableBottomtEnd + 40 );
            g.drawString( "Player #2: " + String.valueOf( playerTwoScore ), 500, gameTableBottomtEnd + 40 );

            //draw the ball
            g.fillOval( ballPositionX, ballPositionY, ballDiameter, ballDiameter );

            //draw the paddles
            g.fillRect( playerOnePadPositionX, playerOnePadPositionY, playerOnePadWidth, playerOnePadHeight );
            g.fillRect( playerTwoPadPositionX, playerTwoPadPositionY, playerTwoPadWidth, playerTwoPadHeight );
        }
        else if ( gameOver ) {
            
            g.setFont( new Font( Font.DIALOG, Font.BOLD, 36 ) );
            if ( playerOneScore > playerTwoScore ) {
                g.drawString( "Player 1 Wins!", 270, 200 );
            }
            else {
                g.drawString( "Player 2 Wins!", 270, 200 );
            }
            
            //draw the scores
            g.setFont( new Font( Font.DIALOG, Font.BOLD, 18 ) );
            g.drawString( "Player #1: " + String.valueOf( playerOneScore ), 200, gameTableBottomtEnd + 40 );
            g.drawString( "Player #2: " + String.valueOf( playerTwoScore ), 500, gameTableBottomtEnd + 40 );

            g.setFont( new Font( Font.DIALOG, Font.BOLD, 18 ) );
            g.drawString( "Press 'ENTER' to restart the game.", 250, 400 );
        }
    }

    public void keyTyped( KeyEvent e ) {}

    public void keyPressed( KeyEvent e ) {
        if ( showTitleScreen ) {
            if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                showTitleScreen = false;
                playing = true;
            }
        }
        else if( playing ){
            if ( e.getKeyCode() == KeyEvent.VK_W ) {
                wPressed = true;
                startStopPlayerOnePadPosition = playerOnePadPositionY;
            }
            else if ( e.getKeyCode() == KeyEvent.VK_S ) {
                sPressed = true;
                startStopPlayerOnePadPosition = playerOnePadPositionY;
            }
            else if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                upPressed = true;
                startStopPlayerTwoPadPosition = playerTwoPadPositionY;
            }
            else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                downPressed = true;
                startStopPlayerTwoPadPosition = playerTwoPadPositionY;
            }
        }
        else if ( gameOver ) {
            if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                gameOver = false;
                showTitleScreen = true;
                playerOnePadPositionY = ( gameTableToptEnd + gameTableBottomtEnd ) / 2;
                playerTwoPadPositionY = ( gameTableToptEnd + gameTableBottomtEnd ) / 2;
                ballDeltaX = 3;
                ballDeltaY = 3;
                ballPositionX = ( gameTableLeftEnd + gameTableRightEnd ) / 2;
                ballPositionY = ( gameTableToptEnd + gameTableBottomtEnd ) / 2;
                playerOneScore = 0;
                playerTwoScore = 0;
            }
        }
    }

    public void keyReleased( KeyEvent e ) {
        if ( playing ) {
            if ( e.getKeyCode() == KeyEvent.VK_W ) {
                wPressed = false;
                startStopPlayerOnePadPosition = playerOnePadPositionY;
            }
            else if ( e.getKeyCode() == KeyEvent.VK_S ) {
                sPressed = false;
                startStopPlayerOnePadPosition = playerOnePadPositionY;
            }
            else if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                upPressed = false;
                startStopPlayerTwoPadPosition = playerTwoPadPositionY;
            }
            else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                downPressed = false;
                startStopPlayerTwoPadPosition = playerTwoPadPositionY;
            }
        }
    }

}