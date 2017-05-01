package com.nowabwagel.disbackend;

public class Util {

	public static String sqlConcatStrings(String... strings) {
		String concat = "";

		for (int i = 0; i < strings.length; i++) {
			concat += strings[i];
			// Check if another index, if so add connector
			if ((i + 1) < strings.length) {
				concat += ",";
			}
		}

		return concat;
	}

	public static byte[] buildByteArrayFromParts(byte[]... bs) {
		int length = 0;
		for (int i = 0; i < bs.length; i++)
			length += bs[i].length;

		byte[] built = new byte[length];

		int index = 0;
		for (int a = 0; a < bs.length; a++) {
			for (int b = 0; b < bs[b].length; b++) {
				built[index] = bs[a][b];
				index++;
			}
		}
		return built;
	}
}
