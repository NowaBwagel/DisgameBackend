package com.nowabwagel.backend.packetsystem.packets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

import org.mindrot.jbcrypt.BCrypt;

import com.nowabwagel.backend.Util;
import com.nowabwagel.backend.packetsystem.Packet;

import lowentry.ue4.library.LowEntry;

public class RegistrationPacket extends Packet {

	private boolean valid;

	private Connection connection;

	private String username;
	private String email;
	private String password;

	public RegistrationPacket(Connection connection, String username, String email, String password) {
		super(connection, null);
		this.connection = connection;
		this.username = username;
		this.email = email;
		this.password = password;
		this.valid = true;
	}

	public RegistrationPacket(Connection connection, byte[] message) {
		super(connection, message);

		this.connection = connection;

		String[] parts = LowEntry.bytesToStringUtf8(message).split(":");
		if (parts.length == 4) {
			valid = true;
			username = parts[1];
			email = parts[2];
			password = parts[3];
		} else {
			valid = false;
		}
	}

	public byte[] execute() throws SQLException {
		if (!valid)
			return super.execute();

		if (!checkValidUsername())
			return LowEntry.stringToBytesUtf8("baduser");
		if (!checkValidEmail())
			return LowEntry.stringToBytesUtf8("bademail");

		System.out.println("Hashing password");
		String passwd = BCrypt.hashpw(password, BCrypt.gensalt(10));
		System.out.println("Finished Hashing password");
		Instant instant = Instant.now();

		System.out.println("Creating Login: " + username + " | " + email);
		PreparedStatement statement = connection.prepareStatement("INSERT INTO LOGIN (USER, EMAIL, PASSWD, CREATIONDATE) VALUES (?, ?, ? ,?)");
		statement.setString(1, username);
		statement.setString(2, email);
		statement.setString(3, passwd);
		statement.setString(4, instant.toString());
		statement.executeUpdate();

		return LowEntry.stringToBytesUtf8("success");
	}

	private boolean checkValidUsername() throws SQLException {
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM LOGIN WHERE USER = ?");
		statement.setString(1, username);

		ResultSet result = statement.executeQuery();

		if (result.isBeforeFirst()) {
			System.out.println("Conflicting Username: " + username);
			return false;
		}

		return true;
	}

	private boolean checkValidEmail() throws SQLException {
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM LOGIN WHERE EMAIL = ?");
		statement.setString(1, email);
		ResultSet result = statement.executeQuery();

		if (result.isBeforeFirst()) {
			System.out.println("Conflicting Email: " + email);
			return false;
		}

		return true;
	}

}
