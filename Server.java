package pckg;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.swing.JTextField;

import java.awt.TextField;


public class Server
{
    private static final int PORT = 7778;

    private ServerSocket serverSocket = null;

    private Socket socket;

    private Socket socket2;

    private PrintStream output;

    private PrintStream output2;

    private Scanner input;

    private Scanner input2;

    private ServerThread t1;

    private ServerThread t2;
    
    private JTextField tf  = null;
    
    public Server()
    {
    	
    }

    public Server(JTextField tf)
    {
    	this.tf = tf;
    }


    public void startServer() throws IOException, InterruptedException
    {
        setServer();
        getConnection();
        setThreads();
        whilePlaying();
    }


    public void setServer() throws IOException
    {
        System.out.println( "Setting Server" );
        serverSocket = new ServerSocket( PORT );
        System.out.println( "Server Set" );
        if (tf != null)
        {
            tf.setText("Server is now on!");
        }
    }


    public void getConnection() throws IOException
    {
        System.out.println( "Waiting for client connection..." );
        socket = serverSocket.accept();
        System.out.println( "Client 1 has connected!" );
        setStream1();
        output.println( "knight.png" );
        output.flush();

        System.out.println( "Now waiting on Client 2..." );
        socket2 = serverSocket.accept();
        System.out.println( "Client 2 had connected!" );
        setStream2();
        output2.println( "knight2.png" );
        output2.flush();
    }


    public void setStream1() throws IOException
    {
        System.out.println( "Creating stream1..." );
        output = new PrintStream( socket.getOutputStream() );
        output.flush();
        input = new Scanner( socket.getInputStream() );
        System.out.println( "Stream1 has been created!" );
    }


    public void setStream2() throws IOException
    {
        System.out.println( "Creating stream2..." );
        output2 = new PrintStream( socket2.getOutputStream() );
        output2.flush();
        input2 = new Scanner( socket2.getInputStream() );
        System.out.println( "Stream2 has been created!" );
    }


    public void setThreads()
    {
        t1 = new ServerThread( "t1" );
        t2 = new ServerThread( "t2" );
    }


    public void whilePlaying() throws InterruptedException
    {
        output.println( "go" );
        output2.println( "go" );
        System.out.println( "Game started!" );
        t1.start();
        t2.start();
    }


    public class ServerThread extends Thread
    {
        String name;


        public ServerThread( String n )
        {
            name = n;
        }


        public synchronized void run()
        {
            if ( name.equals( "t1" ) )
            {
                while ( true )
                {
                    String message = input.nextLine();
                    output2.println( message );
                    if (message.equals("quitt"))
                    {
                    	try {
							serverSocket.close();
	                    	socket.close();
	                    	socket2.close();
	                    	output.close();
	                    	output2.close();
	                    	input.close();
	                    	input2.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                    output2.flush();

                }
            }
            else if ( name.equals( "t2" ) )
            {
                while ( true )
                {
                    String message = input2.nextLine();
                    output.println( message );
                    if (message.equals("quitt"))
                    {
                    	try {
							serverSocket.close();
	                    	socket.close();
	                    	socket2.close();
	                    	output.close();
	                    	output2.close();
	                    	input.close();
	                    	input2.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }
                    output.flush();
                }
            }
        }
    }
}
