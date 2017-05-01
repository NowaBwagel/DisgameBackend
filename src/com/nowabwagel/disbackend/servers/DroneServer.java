package com.nowabwagel.disbackend.servers;

import java.sql.Connection;

public class DroneServer {

	private Connection connection;

	public DroneServer(Connection connection) {
		this.connection = connection;
	}
}
