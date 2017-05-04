package com.nowabwagel.disbackend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.nowabwagel.backend.MasterServer;

public class Main {

	public static void main(String[] args) {
		
		new MasterServer();
		
		args = new String[1];
		args[0] = "-init";

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("-master")) {
				new DisBackend(true, true);
			} else if (args[0].equalsIgnoreCase("-local")) {
				new DisBackend(false, true);
			} else if (args[0].equalsIgnoreCase("-init")) {
				initDatabases();
			} else {
				new DisBackend(false, false);
			}
		}
	}

	private static void initDatabases() {

		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.getConnection("jdbc:h2:~/disgame", DisBackend.DATABASE_USER, DisBackend.DATABASE_PASSWORD);
			Statement st = conn.createStatement();

			// Create Tables
			System.out.println("Creating login Table");
			st.execute("CREATE TABLE login(id bigint NOT NULL AUTO_INCREMENT, user varchar(24), email varchar(255), passwd varchar(255), creationdate varchar(100))");
			System.out.println("Creating active_login Table");
			st.execute("CREATE TABLE active_logins(user_id bigint NOT NULL, session_key varchar(24) NOT NULL, character_id int(11) DEFAULT NULL, login_time varchar(100))");

			// Set Primary Keys
			System.out.println("Setting login Primary Key");
			st.execute("ALTER TABLE login ADD PRIMARY KEY (id)");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
