package test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/main")
public class WSServer {
	public static Set<Session> clients = new HashSet<Session>();
	
	@OnOpen
	public void onOpen(Session session) {
		clients.add(session);
		
		session.setMaxBinaryMessageBufferSize(100000000);
		session.setMaxIdleTimeout(100000);
		session.setMaxTextMessageBufferSize(10000000);
	}
	
	@OnMessage
	public void onMessage(Session session,byte[] message) throws Exception {
		// share bytes
		ByteBuffer buffer = ByteBuffer.wrap(message);
		clients.stream().filter(client -> !client.equals(session)).forEach(client -> {
			try {
				client.getBasicRemote().sendBinary(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		// save blob
		FileOutputStream fos = new FileOutputStream("/home/abolfazlsadeqi2001/blobs.ogg", true);
		fos.write(message);
		fos.close();
	}
	
	@OnClose
	public void onClose(Session session,CloseReason closeReason) {
		clients.remove(session);
	}
}
