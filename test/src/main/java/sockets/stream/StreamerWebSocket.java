package sockets.stream;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/streamer")
public class StreamerWebSocket extends StreamWebSocketParent {

	private static ByteBuffer firstBlob = null;
	private static boolean isStreamerConnected = false;
	private static int currentMessageIndex;
	
	public static int getCurrentMessageIndex() {
		return currentMessageIndex;
	}
	
	public static ByteBuffer getHeaderBlob() {
		return firstBlob;
	}
	
	@OnOpen
	public void onOpen(Session session) {
		if(!isStreamerConnected) {
			// set the limits for time and size
			session.setMaxBinaryMessageBufferSize(MAX_BINARRY_MESSAGE);
			session.setMaxIdleTimeout(MAX_TIME_OUT);
			session.setMaxTextMessageBufferSize(MAX_TEXT_MESSAGE);
			// set the streamer connected
			isStreamerConnected = true;
		}else {
			try {
				session.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@OnMessage
	public void onMessage(Session session, byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		// if the first blob has not been defined(contain the header to read other blobs) define it
		if (firstBlob == null) {
			firstBlob = buffer;
		}
		// increase the message index
		currentMessageIndex++;
		// broad cast the received message
		ClientWebSocket.broadCast(buffer);
	}
	
	@OnError
	public void onError (Throwable th) {
		System.out.println(th.getMessage());
	}
	
	@OnClose
	public void onClose(Session session) {
		// close all clients
		ClientWebSocket.closeAllClients();
		/*
		 * a new streamer use another sound recorder =>
		 * another sound recorder has its own header blob =>
		 * each header blob specified for a specific sound and
		 * it cannot handle another sound =>
		 * the header blob must be empty to be set by new streamer
		 */
		firstBlob = null;
		// set is streamer connected to false to accept another streamer connection
		isStreamerConnected = false;
		// as the stream end the current index message must be 0 to don't have any conflict with another streamer
		currentMessageIndex = 0;
	}
}
