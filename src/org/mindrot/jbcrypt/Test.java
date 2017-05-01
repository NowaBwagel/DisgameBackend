package org.mindrot.jbcrypt;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Test {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

	public static void main(String[] args) {
		long start = System.nanoTime();
		String hashed = BCrypt.hashpw("Fuck My Asshole", BCrypt.gensalt(10));
		String hashed2 = BCrypt.hashpw("Fuck My Asshole", BCrypt.gensalt(10));

		float diffMS = (System.nanoTime() - start) / 1000000.0f;

		System.out.println(diffMS + "ms");
		System.out.println(hashed.length() + " | " + hashed);
		System.out.println(hashed2.length() + " | " + hashed2);

		// method 1
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		System.out.println(timestamp);

		// method 2 - via Date
		Date date = new Date();
		System.out.println(new Timestamp(date.getTime()));

		// return number of milliseconds since January 1, 1970, 00:00:00 GMT
		System.out.println(timestamp.getTime());

		// format timestamp
		System.out.println(sdf.format(timestamp));

		Instant instant = Instant.now();

	}
}
