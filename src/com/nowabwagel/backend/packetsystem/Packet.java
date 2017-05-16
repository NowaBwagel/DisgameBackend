package com.nowabwagel.backend.packetsystem;

import java.sql.Connection;
import java.sql.SQLException;

import lowentry.ue4.library.LowEntry;

public class Packet {

	public static final byte[] FAIL_PACKET = LowEntry.stringToBytesUtf8("failed");
	public static final byte[] SUCCESS_PACKET = LowEntry.stringToBytesUtf8("success");

	public Packet(Connection connection, byte[] message) {
	}

	public byte[] execute() throws SQLException {
		return FAIL_PACKET;
	}
}
