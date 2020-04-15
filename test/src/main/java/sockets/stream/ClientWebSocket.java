package sockets.stream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * this class is defined to broadcast blobs to other clients and streaming
 * @author abolfazlsadeqi2001
 *
 */
@ServerEndpoint("/client")
public class ClientWebSocket extends StreamWebSocketParent {
	public static Set<Session> clients = new HashSet<Session>();

	/**
	 * add new client to clients set<br>
	 * send the header blob to read all blobs by audio player<br>
	 * set some variables like time out
	 * @param session
	 */
	@OnOpen
	public void onOpen(Session session) {
		// add the current session to set of all sessions
		clients.add(session);
		// if header blob is defined send it to client which is very important to read other blobs
		if (StreamerWebSocket.getHeaderBlob() != null) {
			try {
				session.getBasicRemote().sendBinary(StreamerWebSocket.getHeaderBlob());// send the header blob
				session.getBasicRemote().sendText(String.valueOf(StreamerWebSocket.getCurrentMessageIndex()));// send the number of blobs that has been received by server (very important to find the position of cursor in new client)
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// set the limits for time and size
		session.setMaxBinaryMessageBufferSize(MAX_BINARRY_MESSAGE);
		session.setMaxIdleTimeout(MAX_TIME_OUT);
		session.setMaxTextMessageBufferSize(MAX_TEXT_MESSAGE);
	}

	public static void closeAllClients() {
		clients.forEach(client ->{
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	public static void broadCast(ByteBuffer buffer){
		clients.forEach(client -> {
			try {
				client.getBasicRemote().sendBinary(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * when an error is happened log it
	 * @param th
	 */
	@OnError
	public void onError (Throwable th) {
		System.out.println(th.getMessage());
	}
	
	/**
	 * after close connection remove it from our set
	 * @param session valued by Jee
	 * @param closeReason valued by Jee
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		clients.remove(session);
	}
}
