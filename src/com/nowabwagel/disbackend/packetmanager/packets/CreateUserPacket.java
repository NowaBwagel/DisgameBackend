package com.nowabwagel.disbackend.packetmanager.packets;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

import org.mindrot.jbcrypt.BCrypt;

import com.nowabwagel.disbackend.Util;
import com.nowabwagel.disbackend.packetmanager.Packet;
import com.nowabwagel.disbackend.packetmanager.PacketManager;

public class CreateUserPacket extends Packet {

	private String username;
	private String email;
	private String password;

	public CreateUserPacket(PacketManager manager, String username, String email, String password) {
		super(manager);
		this.username = username;
		this.email = email;
		this.password = password;
	}

	@Override
	public ReturnCode execute() {
		// FIXME:getStatement
		Statement statement = null;// getManager().getBackend().g;

		try {
			ResultSet result = statement.executeQuery("SELECT user FROM login WHERE 'user' =" + username + "'");
			if (!result.isBeforeFirst()) {
				return ReturnCode.LOGIN_BAD_USER;
			}

			result = statement.executeQuery("SELECT email FROM login WHERE 'email' = '" + email + "'");
			if (!result.next()) {
				return ReturnCode.LOGIN_BAD_EMAIL;
			}

			String passwd = BCrypt.hashpw(password, BCrypt.gensalt(10));
			Instant instant = Instant.now();

			statement.execute("INSERT INTO login (user, email, passwd, creationdate) VALUES (" + Util.sqlConcatStrings(username, email, passwd) + instant + ")");

		} catch (SQLException e) {
			e.printStackTrace();
			return ReturnCode.INTERNAL_ERROR;
		}

		return ReturnCode.PASS;
	}

}
