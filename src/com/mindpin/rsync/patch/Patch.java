package com.mindpin.rsync.patch;

import java.util.ArrayList;
import java.util.List;

import com.mindpin.rsync.chunk.Chunk;
import com.mindpin.rsync.patch.i.PatchPart;


public class Patch {
	
	public List<PatchPart> parts;
	
	public Patch(){
		parts = new ArrayList<PatchPart>();
	}
	
	public void add(List<Byte> bytes){
		parts.add(new PatchPartData(bytes));
	}
	
	public void add(Chunk chunk){
		parts.add(new PatchPartChunk(chunk.id));
	}
	
	public int part_count(){
		return parts.size();
	}
	
	public long bytes_size(){
		long re = 0;
		for(PatchPart p : parts){
			re += p.bytes_size();
		}
		return re;
	}
}
