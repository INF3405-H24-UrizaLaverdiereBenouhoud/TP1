package ca.qc.urizalaverdierebenouhoud;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    private static final Logger serverLogger = Logger.getLogger(Server.class.getName());
    private static ServerSocket serverSocket;

    private static final String DEFAULT_SERVER_ADDRESS = "0.0.0.0";
    private static final int DEFAULT_SERVER_PORT = 5003;

    private static String serverAddress = DEFAULT_SERVER_ADDRESS;

    public static void main(String[] args) throws Exception {
        System.out.print("Provide server's IP address (" + DEFAULT_SERVER_ADDRESS + "):");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String ip = reader.readLine();
        if (!ip.isEmpty()) {
            serverAddress = ip;
        }

        serverSocket = new ServerSocket(); // initialize server
        startServer(serverSocket);
        //serveur mis en écoute sur ce point
        try
        {
            while (true)
            {
                Socket client = serverSocket.accept(); //blocs code until connection request is made

                //    Should create switch case with with Byte readByte(); of DataInputStream
                DataInput message = new DataInputStream(client.getInputStream());
                interpretStreamContent(message);

               //should send confirmation message is received

            }
        }
        finally
        {
            serverSocket.close();
        }
    }

    /**
     *  Interprets the content of the stream sent by the client
     * @param in the DataInput object to read from
     * @throws IOException if the message could not be read.
     */
    private static void interpretStreamContent(DataInput in) throws IOException
        {
            switch (readFirstByte(in))
            {
                case 0: //login

                    break;

                case 1: // send recent history
                    break;

                case 2: // client sent message
                    serverLogger.info("Client sent message task received");
                    // Stays here for debugging pupopose prcq le serveur fonctionne pour 1 personne
                    // mais pas encore avec plusieur clients
                    readMessage(in);
                    break;
                default:
                    serverLogger.warning("Could not find task bit for task");
            }
        }

    /**
     *  Reads the first task byte sent by the client
     * @param in the DataInput object to read from
     * @return the first byte of the stream
     */
    private static Byte readFirstByte(DataInput in)
    {
        try {
            Byte task = in.readByte();
            serverLogger.log(Level.INFO, "Task bit received: {0}", task);
            return task;
        }
        catch (EOFException eofException)
        {
            serverLogger.severe("Stream reached the end before reading all the bytes");
            System.exit(1);
        }
        catch (IOException ioException)
        {
            serverLogger.severe("IO exception when reading first byte of stream: " + ioException);
            System.exit(1);
        }
        return null;
    }

    /**
     *  Reads the message sent by the client
     * @param message the DataInput object to read from
     * @throws IOException if the message could not be read.
     *                      See {@link DataInput#readUTF()} for details about the exceptions
     */
    private static void readMessage(DataInput message) throws IOException
    {
        String receivedMessage = message.readUTF();
        serverLogger.info(receivedMessage);
    }

    /**
     *  Starts the server
     * @param server the ServerSocket object to start
     */
    private static void startServer(ServerSocket server)
    {
         try
         {
             server.setReuseAddress(true); // so socket does not enter timewait state
             InetAddress serverIP = InetAddress.getByName(serverAddress);
             server.bind(new InetSocketAddress(serverIP, DEFAULT_SERVER_PORT)); //define communication endpoint (point d'entré)
             serverLogger.log(Level.INFO, "The server is running on {0}:{1}", new Object[]{DEFAULT_SERVER_ADDRESS, DEFAULT_SERVER_PORT});
         }
         catch (SocketException socketException)
         {
             serverLogger.severe("Socket exception when setting reuse address to true for server: " + socketException);
             System.exit(1);
         }
         catch (UnknownHostException unknownHostException)
         {
                serverLogger.severe("Unknown host exception when getting IP address for server: " + unknownHostException);
                System.exit(1);
         }
         catch (IOException ioException)
         {
             serverLogger.severe("IO exception when binding IP address and port to server: " + ioException);
             System.exit(1);
         }
    }
    }
