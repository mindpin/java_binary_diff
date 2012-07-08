package com.mindpin.rsync.patch;

import java.util.List;

import sun.misc.BASE64Encoder;

import com.mindpin.rsync.patch.i.PatchPart;

public class PatchPartData implements PatchPart {
	
	public byte[] bytes;
	
	public PatchPartData(List<Byte> bytes){
		int size = bytes.size();
		
		this.bytes = new byte[size];
		
		int i = 0;
		for(byte b : bytes){
			this.bytes[i] = b;
			i ++;
		}
	}

	@Override
	public int bytes_size() {
		return bytes.length;
	}

	@Override
	public String get_encode_str() {
		BASE64Encoder encoder = new BASE64Encoder();
		return "$" + encoder.encode(bytes);
	}
}
