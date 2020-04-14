package test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * this class is defined to broadcast blobs to other clients and streaming
 * @author abolfazlsadeqi2001
 *
 */
@ServerEndpoint("/main")
public class WSServer {
	public static Set<Session> clients = new HashSet<Session>();
	public static ByteBuffer firstBlob = null;
	public static Session serverSession = null;
	private static final int MAX_BINARRY_MESSAGE = 400 * 1024;// the heaviest size that has been gained
	private static final int MAX_TEXT_MESSAGE = 1024;// 1KB as default
	private static final int MAX_TIME_OUT = 30 * 1000;
	public static int index;

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

		if (firstBlob != null) {// if header blob doesn't equal to null
			try {
				session.getBasicRemote().sendBinary(firstBlob);// send the header blob
				session.getBasicRemote().sendText(String.valueOf(index));// send the number of blobs that has been received by server (very important to find the position of cursor in new client)
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// set the limits for time and size
		session.setMaxBinaryMessageBufferSize(MAX_BINARRY_MESSAGE);
		session.setMaxIdleTimeout(MAX_TIME_OUT);
		session.setMaxTextMessageBufferSize(MAX_TEXT_MESSAGE);
	}

	/**
	 * get the blob from streamer broadcast it<br>
	 * if there isn't a first blob(header container very important to play audio if not exists it cannot play) set the current blob as first
	 * @param session
	 * @param message
	 * @throws Exception
	 */
	@OnMessage
	public void onMessage(Session session, byte[] message) throws Exception {
		System.out.println(message.length/1024);
		// share bytes
		ByteBuffer buffer = ByteBuffer.wrap(message);
		if (firstBlob == null) {
			firstBlob = buffer;
		}
		index++;
		clients.stream().filter(client -> !client.equals(session)).forEach(client -> {
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
