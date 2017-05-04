package com.nowabwagel.disbackend.servers;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.nowabwagel.disbackend.Util;
import com.nowabwagel.disbackend.packetmanager.Packet;
import com.nowabwagel.disbackend.packetmanager.Packet.PacketType;
import com.nowabwagel.disbackend.packetmanager.PacketManager;
import com.nowabwagel.disbackend.packetmanager.ResponsePacket;
import com.nowabwagel.disbackend.packetmanager.packets.CreateUserPacket;

import lowentry.ue4.classes.sockets.LatentResponse;
import lowentry.ue4.classes.sockets.SocketClient;
import lowentry.ue4.classes.sockets.SocketServer;
import lowentry.ue4.classes.sockets.SocketServerListener;
import lowentry.ue4.library.LowEntry;

/**
 * Handles logins, and giving ip:port of drones to clients for gameplay.
 * 
 * @author noahb
 *
 */
public class MasterServer implements SocketServerListener {

	public static final int MAX_DRONE_SEND = (1024 * 500); // 500KB
	public static final int MAX_CLIENT_SEND = (1024 * 10);// 10KB

	private SocketServer server;
	private Connection connection;

	private Thread packetThread;
	private PacketManager packetManager;
	private ConcurrentLinkedQueue<ResponsePacket> queuedResponses;

	private List<SocketClient> drones;
	private List<SocketClient> clients;

	public MasterServer(Connection connection) {
		this.connection = connection;
		queuedResponses = new ConcurrentLinkedQueue<ResponsePacket>();
		drones = new ArrayList<>();
		clients = new ArrayList<>();
		packetManager = new PacketManager(connection);
		try {
			server = new SocketServer(true, 7780, 7880, this);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
		packetThread = new Thread(packetManager);
		packetThread.start();
	}

	@Override
	public void clientConnected(SocketServer server, SocketClient client) {
		if (clients.contains(client)) {
			byte[] message = Util.buildByteArrayFromParts(LowEntry.integerToBytes(Packet.ReturnCode.SERVER_MESSAGE.getId()), LowEntry.stringToBytesUtf8("Already Connected to MasterServer"));
			client.sendMessage(message);
		} else {
			clients.add(client);
			byte[] message = Util.buildByteArrayFromParts(LowEntry.integerToBytes(Packet.ReturnCode.SERVER_MESSAGE.getId()), LowEntry.stringToBytesUtf8("Now Connected to MasterServer"));
			client.sendMessage(message);
		}

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
		if (drones.contains(client)) {
			return bytes <= MAX_DRONE_SEND;
		}
		return bytes <= MAX_CLIENT_SEND;
	}

	@Override
	public void receivedMessage(SocketServer server, SocketClient client, byte[] bytes) {
		int requestId = LowEntry.bytesToInteger(bytes);
		String message = LowEntry.bytesToStringUtf8(bytes, 3, bytes.length - 4);

		PacketType type = Packet.PacketType.getTypeFromId(requestId);

		String[] split = message.split(":");
		switch (type) {
		case LOGIN_SIGNIN_REQUEST:
			packetManager.enqueue(new CreateUserPacket(packetManager, split[0], split[1], split[2]));
			break;
		case SERVER_IDENTIFY_AS_DRONE:
			break;
		default:
			break;

		}

	}

	@Override
	public boolean startReceivingFunctionCall(SocketServer server, SocketClient client, int bytes) {

		return false;
	}

	@Override
	public byte[] receivedFunctionCall(SocketServer server, SocketClient client, byte[] bytes) {

		return null;
	}

	@Override
	public boolean startReceivingLatentFunctionCall(SocketServer server, SocketClient client, int bytes) {

		return false;
	}

	@Override
	public void receivedLatentFunctionCall(SocketServer server, SocketClient client, byte[] bytes, LatentResponse response) {

	}

}
