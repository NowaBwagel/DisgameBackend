package com.nowabwagel.disbackend.packetmanager.packets.responses;

import com.nowabwagel.disbackend.packetmanager.Packet.ReturnCode;
import com.nowabwagel.disbackend.packetmanager.ResponsePacket;

public class CreateUserResponse extends ResponsePacket {

	public CreateUserResponse(ReturnCode code) throws Exception {
		super(code);
	}

	@Override
	public byte[] getBytes() {
		byte[] message = new byte[1];
		// message[0] = (byte) code.getId().intValue();

		return message;
	}

}
