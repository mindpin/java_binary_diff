package com.mindpin.rsync.patch;

import java.util.List;

import com.mindpin.rsync.patch.i.PatchPart;

public class PatchPartData implements PatchPart {
	
	public List<Byte> bytes;
	
	public PatchPartData(List<Byte> bytes){
		this.bytes = bytes;
	}

	@Override
	public int bytes_size() {
		return bytes.size();
	}
}
