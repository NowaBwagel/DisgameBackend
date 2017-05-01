package com.nowabwagel.disbackend.packetmanager;

import java.util.HashMap;
import java.util.Map;

public abstract class Packet {

	private PacketManager manager;

	public Packet(PacketManager manager) {
		this.manager = manager;
	}

	public enum PacketType {
		SERVER_IDENTIFY_AS_DRONE(15), LOGIN_SIGNIN_REQUEST(16);

		private static Map<Integer, PacketType> packets = new HashMap<Integer, PacketType>();

		static {
			for (PacketType p : PacketType.values()) {
				packets.put(p.getId(), p);
			}
		}

		private int id;

		PacketType(int id) {
			this.id = id;
		}

		private int getId() {
			return id;
		}

		public static PacketType getTypeFromId(int id) {
			return packets.get(id);
		}
	}

	public enum ReturnCode {
		INTERNAL_ERROR(0), PASS(1), SERVER_MESSAGE(2), LOGIN_BAD_USER(3), LOGIN_BAD_EMAIL(4);
		private int id;

		ReturnCode(int id) {
			this.id = id;
		}

		public Integer getId() {
			return id;
		}
	}

	public abstract ReturnCode execute();

	public PacketManager getManager() {
		return manager;
	}
}
