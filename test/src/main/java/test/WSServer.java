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

@ServerEndpoint("/main")
public class WSServer {
	public static Set<Session> clients = new HashSet<Session>();
	public static ByteBuffer firstBlob = null;
	public static Session serverSession = null;
	public static int index;

	@OnOpen
	public void onOpen(Session session) {
		clients.add(session);

		if (firstBlob != null) {
			try {
				session.getBasicRemote().sendBinary(firstBlob);
				session.getBasicRemote().sendText(String.valueOf(index));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		session.setMaxBinaryMessageBufferSize(100000000);
		session.setMaxIdleTimeout(100000);
		session.setMaxTextMessageBufferSize(10000000);
	}

	@OnMessage
	public void onMessage(Session session, byte[] message) throws Exception {
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

	@OnError
	public void onError (Throwable th) {
		System.out.println(th.getMessage());
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		clients.remove(session);
	}
}
