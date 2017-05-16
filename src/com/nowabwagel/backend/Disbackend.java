package com.nowabwagel.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.nowabwagel.backend.packetsystem.packets.RegistrationPacket;
import com.nowabwagel.backend.server.MasterServer;
import com.nowabwagel.backend.server.Server;

public class Disbackend {
	public static final String MASTER_IP = "IP TO BE MADE";

	public static final String DATABASE_USER = "admin";
	public static final String DATABASE_PASSWORD = "l43Xu@Q^ymCA67yImX*0j!R6#xtng0Ka";
	public static final String DATABASE_URL = "jdbc:h2:~/disgame";

	private Server server;
	private Connection connection;

	public Disbackend(boolean isDrone) {
		try {
			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

			connection.createStatement().execute("DROP TABLE IF EXISTS LOGIN");
			connection.createStatement().execute("CREATE TABLE LOGIN (ID BIGINT NOT NULL AUTO_INCREMENT, USER VARCHAR, EMAIL VARCHAR, PASSWD VARCHAR, CREATIONDATE VARCHAR)");
			connection.createStatement().execute("DROP TABLE IF EXISTS ACTIVE_LOGINS");
			connection.createStatement().execute("CREATE TABLE ACTIVE_LOGINS (USER_ID BIGINT NOT NULL, SESSION_KEY VARCHAR NOT NULL, CHARACTER_ID INT DEFAULT NULL, LOGIN_TIME VARCHAR)");
			connection.createStatement().execute("DROP TABLE IF EXISTS CHARACTERS");
			connection.createStatement().execute("CREATE TABLE CHARACTERS (CHARACTER_ID BIGINT NOT NULL AUTO_INCREMENT, OWNER_ID BIGINT, CHARACTER OTHER)");

			new RegistrationPacket(connection, "nowabwagel", "noah.bergl@gmail.com", "FunnyFarm123").execute();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (isDrone) {

		} else
			server = new MasterServer(connection);

		while (true) {
			server.run();
		}
	}
}
