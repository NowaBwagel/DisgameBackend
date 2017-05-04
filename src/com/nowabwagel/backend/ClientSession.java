package com.nowabwagel.backend;

import lowentry.ue4.classes.sockets.SocketClient;

public class ClientSession {

	private static String[] serverIpWhitelist = {};

	private boolean isServer;

	public ClientSession(SocketClient client) {
		String ip = client.getIpString();

		for (int i = 0; i < serverIpWhitelist.length; i++)
			if (ip.equals(serverIpWhitelist[i]))
				isServer = true;

	}
}
