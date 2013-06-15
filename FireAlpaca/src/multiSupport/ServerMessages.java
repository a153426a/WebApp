package multiSupport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;


public class ServerMessages {

public static final short SERVER_MESSAGE_ADD_POINT = ClientMessages.CLIENT_FLAG_COUNT;
	
	public static class AddPointServerMessage extends ServerMessage{

		// Member variables to be read in from clients and sent to the server
		private int mID;
		private float mX;
		private float mY;
		
		// Empty constructor needed for message pool allocation
		public AddPointServerMessage(){
			// Do nothing...
		}
		
		// Constructor
		public AddPointServerMessage(final int pID, final float pX, final float pY){
			this.mID = pID;
			this.mX = pX;
			this.mY = pY;
		}
		
		// A Setter is needed to change values when we obtain a message from the message pool
		public void set(final int pID, final float pX, final float pY){
			this.mID = pID;
			this.mX = pX;
			this.mY = pY;
		}
		
		// Getters
		public int getID(){
			return this.mID;
		}
		public float getX(){
			return this.mX;
		}
		public float getY(){
			return this.mY;
		}
		
		// Get the message flag
		@Override
		public short getFlag() {
			return SERVER_MESSAGE_ADD_POINT;
		}

		// Apply the read data to the message's member variables
		@Override
		protected void onReadTransmissionData(DataInputStream pDataInputStream)
				throws IOException {
			this.mID = pDataInputStream.readInt();
			this.mX = pDataInputStream.readFloat();
			this.mY = pDataInputStream. readFloat();
		}

		// Write the message's member variables to the output stream
		@Override
		protected void onWriteTransmissionData(
				DataOutputStream pDataOutputStream) throws IOException {
			pDataOutputStream.writeInt(this.mID);
			pDataOutputStream.writeFloat(this.mX);
			pDataOutputStream.writeFloat(this.mY);
		}
	}
	
}
