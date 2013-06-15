package multiSupport;

import java.io.IOException;
import java.net.Socket;



import multiSupport.ServerMessages.AddPointServerMessage;

import org.andengine.engine.Engine;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.protocol.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector.ISocketConnectionServerConnectorListener;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;

import scene.MultiScene;

import android.util.Log;

import com.example.manager.SceneManager;

public class MultiClient implements ISocketConnectionServerConnectorListener {
	
private static final String TAG = "CLIENT";
	
	private Engine mEngine;
	private Scene mScene;
	
	// Server variables
	private String mServerIP;
	private int mServerPort;
	
	// ServerConnector object which deals with the communication between client --> server
	private ServerConnector<SocketConnection> mServerConnector;
	
	// Color ID for the points to be drawn. This value can be stored
	// client-side
	
	// Constructor
	public MultiClient(final String pServerIP, final int pServerPort, final Engine pEngine, final Scene pScene){
		this.mServerIP = pServerIP;
		this.mServerPort = pServerPort;
		this.mEngine = pEngine;
		this.mScene = pScene;
	}

	// Client initialization method, to be called on client start-up
	public void initClient(){
		// A separate thread is needed for network communication
		// in order to avoid blocking on the main thread
		this.mEngine.runOnUpdateThread(new Runnable(){

			@Override
			public void run() {
				try {
					
					// Create the socket with the specified Server IP and port
					Socket socket = new Socket(MultiClient.this.mServerIP, MultiClient.this.mServerPort);
					// Create the socket connection, establishing the input/output stream
					SocketConnection socketConnection = new SocketConnection(socket);
					// Create the server connector with the specified socket connection
					// and client connection listener
					MultiClient.this.mServerConnector = new SocketConnectionServerConnector(socketConnection, MultiClient.this);
					
					// Register a server message to the with a message handler
					MultiClient.this.mServerConnector.registerServerMessage(ServerMessages.SERVER_MESSAGE_ADD_POINT, AddPointServerMessage.class, new IServerMessageHandler<SocketConnection>(){
						
						// If a client receives the SERVER_MESSAGE_ADD_POINT server message,
						// Fire the following code...
						@Override
						public void onHandleMessage(
								ServerConnector<SocketConnection> pServerConnector,
								IServerMessage pServerMessage)
								throws IOException {
							
							// obtain the class casted server message
							AddPointServerMessage message = (AddPointServerMessage) pServerMessage;
							
							//the player move 
							((MultiScene)SceneManager.getInstance().getCurrentScene()).player.getBody().setLinearVelocity(message.getX() * 3,
									message.getY() * 3);;
						}
						
					});
					
					// Once we've created our server connector and registered our server messages,
					// we can call start() on the server connector's connection
					MultiClient.this.mServerConnector.getConnection().start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	// Send a client message through the server connector,
	// passing the message to the server
	public void sendMessage(ClientMessage pClientMessage){
		try {
			this.mServerConnector.sendClientMessage(pClientMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Terminate method, used to terminate both the client's connection
	// to the server as well as the client thread
	public void terminate(){
		if(this.mServerConnector != null)
		this.mServerConnector.terminate();
	}
	
	// Listener - states when we (as a client) have connected to a server
	@Override
	public void onStarted(ServerConnector<SocketConnection> pServerConnector) {
		Log.i(TAG, "Connected :" + pServerConnector.getConnection().getSocket().getInetAddress().getHostAddress().toString());
	}

	// Listener - states when we (as a client) have disconnected from a server
	@Override
	public void onTerminated(ServerConnector<SocketConnection> pServerConnector) {
		Log.i(TAG, "Disonnected :" + pServerConnector.getConnection().getSocket().getInetAddress().getHostAddress().toString());
	}

}
