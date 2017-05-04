package com.nowabwagel.backend;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

import com.nowabwagel.disbackend.Util;

import lowentry.ue4.classes.sockets.LatentResponse;
import lowentry.ue4.classes.sockets.SocketClient;
import lowentry.ue4.classes.sockets.SocketServer;
import lowentry.ue4.classes.sockets.SocketServerListener;
import lowentry.ue4.library.LowEntry;

public class MasterServer {
	// FIXME: Restructure Everything!
	
	public static final String MASTER_IP = "IP TO BE MADE";

	public static final String DATABASE_USER = "admin";
	public static final String DATABASE_PASSWORD = "l43Xu@Q^ymCA67yImX*0j!R6#xtng0Ka";
	public static final String MASTER_URL = "jdbc:h2:~/disgame";
	public static final String LOCAL_DRONE_URL = "jdbc:h2:tcp://localhost/~/disgame";
	public static final String REMOTE_DRONE_URL = "jdbc:h2:tcp://" + MASTER_IP + "/~/disgame";

	// TODO: isServer can be assigned when connected by having ip whitelist

	private Connection connection;

	private SocketServerListener listener;
	private SocketServer server;

	public static class ClientSession {
		public boolean isServer = false;
	}

	public MasterServer() {
		try {
			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection(MASTER_URL, DATABASE_USER, DATABASE_PASSWORD);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		listener = new SocketServerListener() {

			@Override
			public void clientConnected(SocketServer server, SocketClient client) {
				client.setAttachment(new ClientSession());
				System.out.println("ClientConnected: " + client);
			}

			@Override
			public void clientDisconnected(SocketServer server, SocketClient client) {
				System.out.println(client + " disconnected");
			}

			@Override
			public void receivedConnectionValidation(SocketServer server, SocketClient client) {
			}

			@Override
			public boolean startReceivingUnreliableMessage(SocketServer server, SocketClient client, int bytes) {
				return false;
			}

			@Override
			public void receivedUnreliableMessage(SocketServer server, SocketClient client, ByteBuffer bytes) {
				System.out.println("Message Unreliable Recived");
			}

			@Override
			public boolean startReceivingMessage(SocketServer server, SocketClient client, int bytes) {
				ClientSession session = client.getAttachment();
				if (!session.isServer) {
					return (bytes <= 1024);
				}

				return (bytes <= (10 * 1024));
			}

			@Override
			public void receivedMessage(SocketServer server, SocketClient client, byte[] bytes) {
				System.out.println("Message Recived");
			}

			@Override
			public boolean startReceivingFunctionCall(SocketServer server, SocketClient client, int bytes) {
				System.out.println("Request: " + bytes);
				return (bytes <= (10 * 1024));
			}

			@Override
			public byte[] receivedFunctionCall(SocketServer server, SocketClient client, byte[] bytes) {
				String message = LowEntry.bytesToStringUtf8(bytes);
				System.out.println("Getting Fuction Call \n" + message);

				String[] broken = message.split(":");
				if (broken[0].equals("serveraddress")) {
					// TODO: Load Balance Server Node Picking
					return LowEntry.stringToBytesUtf8("localhost");
				} else if (broken[0].equals("register")) {
					String username = broken[1];
					String email = broken[2];
					String password = broken[3];
					try {
						Statement st = connection.createStatement();

						ResultSet result = st.executeQuery("SELECT * FROM login WHERE user ='" + username + "'");
						if (result.isBeforeFirst()) {
							System.out.println("Username Taken: " + username);
							return LowEntry.stringToBytesUtf8("baduser");
						}

						result = st.executeQuery("SELECT * FROM login WHERE email = '" + email + "'");
						if (result.isBeforeFirst()) {
							System.out.println("Email Taken: " + email);
							return LowEntry.stringToBytesUtf8("bademail");
						}

						System.out.println(client + ": Hashing password");
						String passwd = BCrypt.hashpw(password, BCrypt.gensalt(10));
						System.out.println(client + ": Finished Hashing password");
						Instant instant = Instant.now();

						System.out.println("INSERT INTO login (user, email, passwd, creationdate) VALUES (" + Util.sqlConcatStrings(username, email, passwd, instant.toString()) + ")");
						System.out.println(st.executeUpdate("INSERT INTO login (user, email, passwd, creationdate) VALUES (" + Util.sqlConcatStrings(username, email, passwd, instant.toString()) + ")"));

						return LowEntry.stringToBytesUtf8("success");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else if (broken[0].equals("login")) {
					System.out.println("Attemping Login");

					if (broken.length == 3) {
						String username = broken[1];
						String password = broken[2];

						try {
							Statement st = connection.createStatement();

							ResultSet result = st.executeQuery("SELECT * FROM login WHERE user ='" + username + "'");
							System.out.println(result);
							if (result.next()) {
								System.out.println("Getting User");
								String passwd = result.getString("passwd");
								long user_id = result.getLong("id");
								if (BCrypt.checkpw(password, passwd)) {

									result = st.executeQuery("SELECT * FROM active_logins WHERE user_id ='" + user_id + "'");
									if (!result.isBeforeFirst()) {
										String session_key = Util.getUUID();

										result = st.executeQuery("SELECT * FROM active_logins WHERE session_key ='" + session_key + "'");
										while (result.isBeforeFirst()) {
											session_key = Util.getUUID();
											result = st.executeQuery("SELECT * FROM active_logins WHERE session_key ='" + session_key + "'");
										}
										Instant instant = Instant.now();
										st.executeUpdate("INSERT INTO active_logins (user_id, session_key, login_time) VALUES (" + Util.sqlConcatStrings(String.valueOf(user_id), session_key, instant.toString()) + ")");
										return LowEntry.stringToBytesUtf8(session_key);
									} else {
										return LowEntry.stringToBytesUtf8("alreadyloggedin");
									}
								} else {
									System.out.println("Password missmatch");
								}
							} else {
								System.out.println("Username not found");
							}

						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}

				return LowEntry.stringToBytesUtf8("failed");
			}

			@Override
			public boolean startReceivingLatentFunctionCall(SocketServer server, SocketClient client, int bytes) {
				ClientSession session = client.getAttachment();
				if (!session.isServer) {
					return (bytes <= 1024);
				}

				return (bytes <= (10 * 1024));
			}

			@Override
			public void receivedLatentFunctionCall(SocketServer server, SocketClient client, byte[] bytes, LatentResponse response) {
				System.out.println("Latent Function Call");
			}
		};

		try {
			server = new SocketServer(true, 7780, 7880, listener);

			while (true) {
				server.listen();
			}
		} catch (

		Throwable e) {
			e.printStackTrace();
		}

	}

}
