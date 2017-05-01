package com.nowabwagel.disbackend.packetmanager;

import com.nowabwagel.disbackend.packetmanager.Packet.ReturnCode;

public abstract class ResponsePacket {
	private ReturnCode code;

	public ResponsePacket(ReturnCode code) throws Exception {
		this.code = code;
		if ((code.getId() > 255) || (code.getId() < 0)) {
			throw new Exception("CreateUserResponse: Code out of Bounds");
		}
	}

	public abstract byte[] getBytes();

}
