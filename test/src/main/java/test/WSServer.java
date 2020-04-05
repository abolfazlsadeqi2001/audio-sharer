package test;

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
	Set<Session> clients = new HashSet<Session>();
	
	@OnOpen
	public void onOpen(Session session) {
		clients.add(session);
		System.out.println(clients.size());
		/*System.out.println("new client joint : "+session.getId());*/
		
		session.setMaxBinaryMessageBufferSize(100000);
		session.setMaxIdleTimeout(1000000);
		session.setMaxTextMessageBufferSize(10000000);
	}
	
	@OnMessage
	public void onMessage(Session session,byte[] message) {
		ByteBuffer buffer = ByteBuffer.wrap(message);
		clients.stream().filter(client -> !client.equals(session)).forEach(client -> {
			try {
				client.getBasicRemote().sendBinary(buffer);
				System.out.println("sent");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
	
	@OnClose
	public void onClose(Session session,CloseReason closeReason) {
		clients.remove(session);
		/*System.out.println(closeReason.getReasonPhrase());
		System.out.println("new client exit : "+session.getId());*/
	}
}
