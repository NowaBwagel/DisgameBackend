package com.nowabwagel.disbackend.packetmanager;

import java.sql.Connection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketManager implements Runnable {

	private Connection connection;

	private ConcurrentLinkedQueue<Packet> queuedPackets;

	public PacketManager(Connection connection) {
		this.connection = connection;
		queuedPackets = new ConcurrentLinkedQueue<Packet>();
	}

	public void enqueue(Packet p) {
		queuedPackets.add(p);
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public void run() {

		Packet packet;
		while (true) {
			packet = queuedPackets.poll();

			if (packet != null) {
				packet.execute();
			} else {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
