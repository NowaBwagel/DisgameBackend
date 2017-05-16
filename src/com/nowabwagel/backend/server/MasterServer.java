package com.nowabwagel.backend.server;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nowabwagel.backend.packetsystem.Packet;
import com.nowabwagel.backend.packetsystem.packets.RegistrationPacket;
import com.nowabwagel.backend.packetsystem.packets.LoginPacket;

import lowentry.ue4.classes.sockets.LatentResponse;
import lowentry.ue4.classes.sockets.SocketClient;
import lowentry.ue4.classes.sockets.SocketServer;
import lowentry.ue4.classes.sockets.SocketServerListener;
import lowentry.ue4.library.LowEntry;

public class MasterServer extends Server implements SocketServerListener {

	private List<SocketClient> serverClients;

	private SocketServer socketServer;
	private Connection connection;

	public class ServerClient {
		public int port;

		public ServerClient(int port) {
			this.port = port;
		}
	}

	public MasterServer(Connection connection) {
		this.serverClients = new ArrayList<>();
		this.connection = connection;

		try {
			socketServer = new SocketServer(true, 7780, 7880, this);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		socketServer.listen();
	}

	@Override
	public void clientConnected(SocketServer server, SocketClient client) {
	}

	@Override
	public void clientDisconnected(SocketServer server, SocketClient client) {

	}

	@Override
	public void receivedConnectionValidation(SocketServer server, SocketClient client) {

	}

	@Override
	public boolean startReceivingUnreliableMessage(SocketServer server, SocketClient client, int bytes) {

		return false;
	}

	@Override
	public void receivedUnreliableMessage(SocketServer server, SocketClient client, ByteBuffer bytes) {

	}

	@Override
	public boolean startReceivingMessage(SocketServer server, SocketClient client, int bytes) {

		return bytes <= (10 * 1024);
	}

	@Override
	public void receivedMessage(SocketServer server, SocketClient client, byte[] bytes) {
		String[] message = LowEntry.bytesToStringUtf8(bytes).split(":");
		if (message[0].equals("dedicated")) {
			int port = Integer.parseInt(message[1]);
			System.out.println("New Server Instance Registered: " + client.getIpString() + ":" + port);
			serverClients.add(client);
			client.setAttachment(new ServerClient(port));
		}
	}

	@Override
	public boolean startReceivingFunctionCall(SocketServer server, SocketClient client, int bytes) {
		System.out.println("Function call: " + bytes);
		return bytes <= (10 * 1024); // 10KB max
	}

	@Override
	public byte[] receivedFunctionCall(SocketServer server, SocketClient client, byte[] bytes) {
		System.out.println("Receieved Function Call");
		Packet packet;

		String[] message = LowEntry.bytesToStringUtf8(bytes).split(":");
		// TODO:Precheck if is user or if server to sort ifs
		if (message[0].equals("register"))
			packet = new RegistrationPacket(connection, bytes);
		else if (message[0].equals("login")) {
			packet = new LoginPacket(connection, bytes);
		} else if (message[0].equals("logout")) {
			System.out.println("loggout");
			packet = new Packet(connection, bytes);
		} else
			packet = new Packet(connection, bytes);

		try {
			return packet.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.err.println("Unknow packet recieved");
		return Packet.FAIL_PACKET;
	}

	@Override
	public boolean startReceivingLatentFunctionCall(SocketServer server, SocketClient client, int bytes) {

		return false;
	}

	@Override
	public void receivedLatentFunctionCall(SocketServer server, SocketClient client, byte[] bytes, LatentResponse response) {

	}
}
