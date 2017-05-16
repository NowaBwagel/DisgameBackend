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

public class LoginPacket extends Packet {

	private boolean valid;
	private Connection connection;

	private String username;
	private String password;
	private long user_id;

	public LoginPacket(Connection connection, byte[] message) {
		super(connection, message);

		this.connection = connection;

		String[] parts = LowEntry.bytesToStringUtf8(message).split(":");
		if (parts.length == 3) {
			valid = true;
			username = parts[1];
			password = parts[2];
		}
	}

	@Override
	public byte[] execute() throws SQLException {
		System.out.println("Login Packet Execute");
		if (!valid) {
			System.out.println("Invalid Login Packet");
			return super.execute();
		}

		// Because UE4 Checks for login fail as "failed" can use this.
		if (!isValidLoginCombo()) {
			System.out.println("Invalid Combo");
			return super.execute();
		}
		if (isAlreadyLogged()) {
			System.out.println("Already Logged In");
			// TODO: Log when user trys to login already logged user
			return LowEntry.stringToBytesUtf8("alreadyloggedin");
		}

		String session_key = activateUserSession();

		System.out.println("Login Packet Returning");
		return LowEntry.stringToBytesUtf8(session_key);

	}

	/**
	 * Check if username and password provided is a valid login combo. Also get
	 * user_id for other checks.
	 * 
	 * @return if login success
	 * @throws SQLException
	 */
	private boolean isValidLoginCombo() throws SQLException {
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM LOGIN WHERE USER = ?");
		statement.setString(1, username);
		ResultSet result = statement.executeQuery();

		if (result.next()) {
			String passwd = result.getString("passwd");
			user_id = result.getLong("id");

			result.close();
			if (BCrypt.checkpw(password, passwd)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check user_id with table active_login
	 * 
	 * @return If user_id already in active_logins
	 * @throws SQLException
	 */
	private boolean isAlreadyLogged() throws SQLException {
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM ACTIVE_LOGINS WHERE USER_ID = ?");
		statement.setString(1, Long.toString(user_id));
		ResultSet result = statement.executeQuery();

		if (!result.isBeforeFirst()) {
			result.close();
			return false;
		} else {
			result.close();
			return true;
		}

	}

	/**
	 * Generate Session Keys until one is not being currently used
	 * 
	 * @return Valid Session Key
	 * @throws SQLException
	 */
	private String getValidSessionKey() throws SQLException {
		String session_key = Util.getSessionKey();
		// Check if session_key is already used in active_logins
		PreparedStatement statement = connection.prepareStatement("SELECT * FROM ACTIVE_LOGINS WHERE SESSION_KEY = ?");
		statement.setString(1, session_key);
		ResultSet result = statement.executeQuery();

		while (result.isBeforeFirst()) {
			Util.logMessage("Invalid Session Key Generated: " + session_key);
			result.close();
			session_key = Util.getSessionKey();
			statement.setString(1, session_key);
			result = statement.executeQuery();
		}

		return session_key;
	}

	/**
	 * Activate User
	 * 
	 * @return Session Key
	 * @throws SQLException
	 */
	private String activateUserSession() throws SQLException {
		String user_id = String.valueOf(this.user_id);
		String session_key = getValidSessionKey();
		String login_time = Instant.now().toString();

		PreparedStatement statement = connection.prepareStatement("INSERT INTO ACTIVE_LOGINS (USER_ID, SESSION_KEY, LOGIN_TIME) VALUES (?, ?, ?)");
		statement.setString(1, user_id);
		statement.setString(2, session_key);
		statement.setString(3, login_time);
		statement.executeUpdate();

		return session_key;
	}

}
