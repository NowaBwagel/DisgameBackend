package com.nowabwagel.dataserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class DataServerMain {
	
	public static final void main(String[] args) {
		com.nowabwagel.backend.dataclasses.Character testCharacter = new com.nowabwagel.backend.dataclasses.Character(
				"Test Character");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(testCharacter);
			oos.flush();

			byte[] bytes = baos.toByteArray();
			System.out.println(bytes.length + "| " + bytes);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
