package com.nowabwagel.disbackend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.nowabwagel.disbackend.servers.DroneServer;
import com.nowabwagel.disbackend.servers.MasterServer;

public class DisBackend {

	public static final String MASTER_IP = "IP TO BE MADE";

	public static final String DATABASE_USER = "admin";
	public static final String DATABASE_PASSWORD = "l43Xu@Q^ymCA67yImX*0j!R6#xtng0Ka";
	public static final String MASTER_URL = "jdbc:h2:~/disgame";
	public static final String LOCAL_DRONE_URL = "jdbc:h2:tcp://localhost/~/disgame";
	public static final String REMOTE_DRONE_URL = "jdbc:h2:tcp://" + MASTER_IP + "/~/disgame";

	private Connection connection;
	
	private MasterServer masterServer;
	private DroneServer droneServer;

	public DisBackend(boolean master, boolean local) {
		// Connect to H2 SQL
		try {
			Class.forName("org.h2.Driver");

			if (master) {
				connection = DriverManager.getConnection(MASTER_URL, DATABASE_USER, DATABASE_PASSWORD);
			} else {
				if (local) {
					connection = DriverManager.getConnection(LOCAL_DRONE_URL, DATABASE_USER, DATABASE_PASSWORD);
				} else {
					connection = DriverManager.getConnection(REMOTE_DRONE_URL, DATABASE_USER, DATABASE_PASSWORD);
				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Start launching listening servers, and Mangers
		if (master) {
			masterServer = new MasterServer(connection);
		}
		droneServer = new DroneServer(connection);
	}
}
