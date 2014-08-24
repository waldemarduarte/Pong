import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private int hitBallDeltaX = 0;
    private int hitBallDeltaY = 0;
    
    private int startStopPlayerOnePadPosition = playerOnePadPositionY;
    private int startStopPlayerTwoPadPosition = playerTwoPadPositionY;
    
    boolean amIPlayerOne = true;
    boolean someActionOtherPlayer = false;
    boolean updateBallPosition = false;
    
    //Client or Server utilities
    BufferedReader inFromOtherPlayer;
    DataOutputStream outToOtherPlayer;
    String otherPlayerMessage = "";
    String otherPlayerChange = "";
    String myMessageToSend = "";
    
    //construct a Pong Client
    public Pong( 
            boolean amIPlayerOne, 
            BufferedReader inFromOtherPlayer,
            DataOutputStream outToOtherPlayer ) throws Exception {
        
        this.amIPlayerOne = amIPlayerOne;
        this.inFromOtherPlayer = inFromOtherPlayer;
        this.outToOtherPlayer = outToOtherPlayer;
        
        neededStufs();
    }
    
    //construct a Pong Server
    
    public void  neededStufs() {
        setBackground( Color.GREEN );

        //listen to key presses
        setFocusable( true );
        addKeyListener( this );

        //call step() 60 fps
        javax.swing.Timer timer = new javax.swing.Timer( 1000/60, this );
        timer.start();
    }
    
    public void receiveMessageFromOtherPlayer() throws Exception {
        System.out.println( "Input: " + otherPlayerMessage );
        String splitedMessage[] = otherPlayerMessage.split( "," );        
        //Structure:
        //showTitleScreen,playing,gameOver,playerOneScore,playerTwoScore,
        //w,s,pad,ballX,ballY,deltaX,deltaY
        for ( int x = 0; x < splitedMessage.length; x++ ) {
            if ( amIPlayerOne ) {
                switch ( x ) {
//                    case 0:
//                        showTitleScreen = Boolean.parseBoolean( splitedMessage[x] );
//                        break;
//                    case 1:
//                        playing = Boolean.parseBoolean( splitedMessage[x] );
//                        break;
//                    case 2:
//                        gameOver = Boolean.parseBoolean( splitedMessage[x] );
//                        break;
//                    case 3:
//                        playerOneScore = Integer.parseInt( splitedMessage[x] );
//                        break;
//                    case 4:
//                        playerTwoScore = Integer.parseInt( splitedMessage[x] );
//                        break;
                    case 5:
                        upPressed = Boolean.parseBoolean( splitedMessage[x] );
                        break;
                    case 6:
                        downPressed = Boolean.parseBoolean( splitedMessage[x] );
                        break;
                    case 7:
                        playerTwoPadPositionY = Integer.parseInt( splitedMessage[x] );
                        startStopPlayerTwoPadPosition = playerTwoPadPositionY;
                        break;
//                    case 8:
//                        ballPositionX = Integer.parseInt( splitedMessage[x] );
//                        hitBallPositionX = ballPositionX;
//                        break;
//                    case 9:
//                        ballPositionY = Integer.parseInt( splitedMessage[x] );
//                        hitBallPositionY = ballPositionY;
//                        break;
//                    case 10:
//                        ballDeltaX = Integer.parseInt( splitedMessage[x] );
//                        hitBallDeltaX = ballDeltaX;
//                        break;
//                    case 11:
//                        ballDeltaY = Integer.parseInt( splitedMessage[x] );
//                        hitBallDeltaY = ballDeltaY;
//                        break;
//                    case 12:
//                        updateBallPosition = Boolean.parseBoolean( splitedMessage[x] );
//                        break;
                    default:
                        break;
                }
            }
            else {
                boolean updBallPos = Boolean.parseBoolean( splitedMessage[11] );
                switch ( x ) {
                    case 0:
                        showTitleScreen = Boolean.parseBoolean( splitedMessage[x] );
                        break;
                    case 1:
                        playing = Boolean.parseBoolean( splitedMessage[x] );
                        break;
                    case 2:
                        gameOver = Boolean.parseBoolean( splitedMessage[x] );
                        break;
                    case 3:
                        playerOneScore = Integer.parseInt( splitedMessage[x] );
                        break;
                    case 4:
                        playerTwoScore = Integer.parseInt( splitedMessage[x] );
                        break;
                    case 5:
                        wPressed = Boolean.parseBoolean( splitedMessage[x] );
                        break;
                    case 6:
                        sPressed = Boolean.parseBoolean( splitedMessage[x] );
                        break;
                    case 7:
                        playerOnePadPositionY = Integer.parseInt( splitedMessage[x] );
                        startStopPlayerOnePadPosition = playerOnePadPositionY;
                        break;
                    case 8:
                        if ( updBallPos ) {
                            ballPositionX = Integer.parseInt( splitedMessage[x] );
                            hitBallPositionX = ballPositionX;
                        }
                        break;
                    case 9:
                        if ( updBallPos ) {
                            ballPositionY = Integer.parseInt( splitedMessage[x] );
                            hitBallPositionY = ballPositionY;
                        }
                        break;
                    case 10:
                        if ( updBallPos ) {
                            ballDeltaX = Integer.parseInt( splitedMessage[x] );
                            hitBallDeltaX = ballDeltaX;
                        }
                        break;
                    case 11:
                        if ( updBallPos ) {
                            ballDeltaY = Integer.parseInt( splitedMessage[x] );
                            hitBallDeltaY = ballDeltaY;
                        }
                        break;
//                    case 12:
//                        updateBallPosition = Boolean.parseBoolean( splitedMessage[x] );
//                        break;
                    default:
                        break;
                }
            }      
        }
        someActionOtherPlayer = false;
    }
    
    public void sendMessageToOtherPlayer() throws Exception {
        if ( amIPlayerOne ) {
            hitBallPositionX = ballPositionX;
            hitBallPositionY = ballPositionY;
            hitBallDeltaX = ballDeltaX;
            hitBallDeltaY = ballDeltaY;
        }
        //Structure:
        //showTitleScreen,playing,gameOver,playerOneScore,playerTwoScore,
        //w,s,pad,ballX,ballY,deltaX,deltaY
        if ( amIPlayerOne ) {
            myMessageToSend = 
                    showTitleScreen + "," + 
                    playing + "," + 
                    gameOver + "," + 
                    playerOneScore + "," + 
                    playerTwoScore + "," + 
                    wPressed + "," + 
                    sPressed + "," + 
                    startStopPlayerOnePadPosition + "," + 
                    hitBallPositionX + "," + 
                    hitBallPositionY + "," + 
                    hitBallDeltaX + "," + 
                    hitBallDeltaY + "," + 
                    updateBallPosition;
        }
        else {
            myMessageToSend = 
                    showTitleScreen + "," + 
                    playing + "," + 
                    gameOver + "," + 
                    playerOneScore + "," + 
                    playerTwoScore + "," + 
                    upPressed + "," + 
                    downPressed + "," + 
                    startStopPlayerTwoPadPosition + "," + 
                    hitBallPositionX + "," + 
                    hitBallPositionY + "," + 
                    hitBallDeltaX + "," + 
                    hitBallDeltaY + "," + 
                    updateBallPosition;
        }
        updateBallPosition = false;
        outToOtherPlayer.writeBytes( myMessageToSend + '\n' );
        //System.out.println( "Sending Message: " + myMessageToSend );
    }

    public void actionPerformed( ActionEvent e ) {
        step();
    }

    public void step() {
        
        if ( someActionOtherPlayer ) {
            try {
                receiveMessageFromOtherPlayer();
            } catch ( Exception ex ) {
                Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
            }
        }

        if( playing ) {
            //move player 1
            if ( wPressed ) {
                if ( playerOnePadPositionY - paddleSpeed > gameTableToptEnd ) {
                    playerOnePadPositionY -= paddleSpeed;
                }
//                if ( amIPlayerOne ) {
//                    try {
//                        sendMessageToOtherPlayer();
//                    } catch ( Exception ex ) {
//                        Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
//                    }
//                }
            }
            if ( sPressed ) {
                if ( playerOnePadPositionY + paddleSpeed + playerOnePadHeight < gameTableBottomtEnd /*getHeight()*/ ) {
                    playerOnePadPositionY += paddleSpeed;
                }
//                if ( amIPlayerOne ) {
//                    try {
//                        sendMessageToOtherPlayer();
//                    } catch ( Exception ex ) {
//                        Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
//                    }
//                }
            }

            //move player 2
            if ( upPressed ) {
                if ( playerTwoPadPositionY - paddleSpeed > gameTableToptEnd ) {
                    playerTwoPadPositionY -= paddleSpeed;
                }
//                if ( !amIPlayerOne ) {
//                    try {
//                        sendMessageToOtherPlayer();
//                    } catch ( Exception ex ) {
//                        Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
//                    }
//                }
            }
            if ( downPressed ) {
                if ( playerTwoPadPositionY + paddleSpeed + playerTwoPadHeight < gameTableBottomtEnd /*getHeight()*/  ) {
                    playerTwoPadPositionY += paddleSpeed;
                }
//                if ( !amIPlayerOne ) {
//                    try {
//                        sendMessageToOtherPlayer();
//                    } catch ( Exception ex ) {
//                        Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
//                    }
//                }
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
                
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
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
                
                updateBallPosition = true;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
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
                
                updateBallPosition = true;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
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
                g.drawString( "You are Player 1 (Left side)", 270, 350 );
            }
            else {
                g.drawString( "You are Player 2 (Right side)", 270, 350 );
            }

            g.drawString( "Press 'ENTER' to play.", 290, 400 );
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
            
            g.drawString( "ballX: " + String.valueOf( hitBallPositionX ), 250, gameTableBottomtEnd - 40 );
            g.drawString( "ballY: " + String.valueOf( hitBallPositionY ), 250, gameTableBottomtEnd - 20 );
            g.drawString( "DeltaX: " + String.valueOf( hitBallDeltaX ), 350, gameTableBottomtEnd - 40 );
            g.drawString( "DeltaY: " + String.valueOf( hitBallDeltaY ), 350, gameTableBottomtEnd - 20 );

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
                if ( amIPlayerOne ) {
                    g.drawString( "You Won! :D", 270, 200 );
                }
                else {
                    g.drawString( "You lost... :(", 270, 200 );
                }
            }
            else {
                if ( amIPlayerOne ) {
                    g.drawString( "You lost... :(", 270, 200 );
                }
                else {
                    g.drawString( "You Won! :D", 270, 200 );
                }
            }
            
            g.setFont( new Font( Font.DIALOG, Font.BOLD, 18 ) );
            
            if ( amIPlayerOne ) {
                g.drawString( "You are Player 1 (Left side)", 255, 350 );
            }
            else {
                g.drawString( "You are Player 2 (Right side)", 250, 350 );
            }
            
            g.setFont( new Font( Font.DIALOG, Font.BOLD, 18 ) );
            g.drawString( "Press 'ENTER' to go to main.", 250, 400 );
            
            //draw the scores
            g.drawString( "Player #1: " + String.valueOf( playerOneScore ), 200, gameTableBottomtEnd + 40 );
            g.drawString( "Player #2: " + String.valueOf( playerTwoScore ), 500, gameTableBottomtEnd + 40 );
        }
    }

    public void keyTyped( KeyEvent e ) {}

    public void keyPressed( KeyEvent e ) {
        if ( showTitleScreen ) {
            if ( e.getKeyCode() == KeyEvent.VK_ENTER && amIPlayerOne ) {
                showTitleScreen = false;
                playing = true;
            }
        }
        else if( playing ) {
            if ( e.getKeyCode() == KeyEvent.VK_W && amIPlayerOne ) {
                wPressed = true;
                startStopPlayerOnePadPosition = playerOnePadPositionY;
                
                updateBallPosition = false;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
                
            }
            else if ( e.getKeyCode() == KeyEvent.VK_S && amIPlayerOne ) {
                sPressed = true;
                startStopPlayerOnePadPosition = playerOnePadPositionY;
                
                updateBallPosition = false;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
            else if ( e.getKeyCode() == KeyEvent.VK_UP && !amIPlayerOne ) {
                upPressed = true;
                startStopPlayerTwoPadPosition = playerTwoPadPositionY;
                
                updateBallPosition = false;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
            else if ( e.getKeyCode() == KeyEvent.VK_DOWN && !amIPlayerOne ) {
                downPressed = true;
                startStopPlayerTwoPadPosition = playerTwoPadPositionY;
                
                updateBallPosition = false;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
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
            if ( e.getKeyCode() == KeyEvent.VK_W && amIPlayerOne ) {
                wPressed = false;
                startStopPlayerOnePadPosition = playerOnePadPositionY;
                
                updateBallPosition = false;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
            else if ( e.getKeyCode() == KeyEvent.VK_S && amIPlayerOne ) {
                sPressed = false;
                startStopPlayerOnePadPosition = playerOnePadPositionY;
                
                updateBallPosition = false;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
            else if ( e.getKeyCode() == KeyEvent.VK_UP && !amIPlayerOne ) {
                upPressed = false;
                startStopPlayerTwoPadPosition = playerTwoPadPositionY;
                
                updateBallPosition = false;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
            else if ( e.getKeyCode() == KeyEvent.VK_DOWN && !amIPlayerOne ) {
                downPressed = false;
                startStopPlayerTwoPadPosition = playerTwoPadPositionY;
                
                updateBallPosition = false;
                try {
                    sendMessageToOtherPlayer();
                } catch ( Exception ex ) {
                    Logger.getLogger( Pong.class.getName() ).log( Level.SEVERE, null, ex );
                }
            }
        }
    }

}