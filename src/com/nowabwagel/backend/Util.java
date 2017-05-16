package com.nowabwagel.backend;

import java.util.UUID;

import lowentry.ue4.classes.sockets.SocketClient;

public class Util {

	public static String getSessionKey() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static byte[] buildByteArrayFromParts(byte[]... bs) {
		int length = 0;
		for (int i = 0; i < bs.length; i++) {
			length += bs[i].length;
		}

		byte[] built = new byte[length];

		int index = 0;
		for (int a = 0; a < bs.length; a++) {
			for (int b = 0; b < bs[b].length; b++) {
				built[index] = bs[a][b];
				index++;
			}
		}
		return built;
	}

	public boolean isServerClient(SocketClient client) {
		return false;// FIXME: IMPLEMENT THIS
	}

	public static void logMessage(String message) {
		System.out.println(message);
	}
}
