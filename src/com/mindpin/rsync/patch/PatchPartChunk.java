package com.mindpin.rsync.patch;

import com.mindpin.rsync.patch.i.PatchPart;


public class PatchPartChunk implements PatchPart {
	
	public int id;
	
	public PatchPartChunk(int id){
		this.id = id;
	}

	@Override
	public int bytes_size() {
		return 1;
	}
}
