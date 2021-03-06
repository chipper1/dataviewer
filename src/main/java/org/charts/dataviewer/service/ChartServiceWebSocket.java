package org.charts.dataviewer.service;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket
public class ChartServiceWebSocket {

	private static final Logger log = LoggerFactory.getLogger(ChartServiceWebSocket.class);

	private String uuid;
	private Session session;

	public ChartServiceWebSocket(String uuid) {
		this.uuid = uuid;
	}

	@OnWebSocketConnect
	public void handleConnect(Session session) {
		this.session = session;
		log.debug("@OnWebSocketConnect | Session id :  {}", this.hashCode());
		ChartsOpenedConnections.getInstance().addConnection(this);
	}

	@OnWebSocketClose
	public void handleClose(int statusCode, String reason) {
		log.debug("Connection Closed with statusCode = [{}] , reason = [{}], UUID : [{}]", statusCode, reason, uuid);
		ChartsOpenedConnections.getInstance().closeConnection(this);
	}

	@OnWebSocketMessage
	public void handleMessage(String message) {
		log.debug("@OnWebSocketMessage | Received message :  {} @ {}", message, this.hashCode());
		ChartsOpenedConnections.getInstance().resendLastMsg(message, getSession()); // Message is uid
	}

	@OnWebSocketError
	public void handleError(Throwable error) {
		log.error("@OnWebSocketError ", error);
	}

	public void sendMessage(String message) {
		try {
			if (session.isOpen()) {
				session.getRemote().sendString(message);
			}
		} catch (IOException e) {
			log.error("Error in sendMessage() ", e);
		}
	}

	public String getUniqueID() {
		return uuid;
	}

	public Session getSession() {
		return session;
	}

}
