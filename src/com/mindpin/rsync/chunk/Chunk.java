package com.mindpin.rsync.chunk;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.mindpin.rsync.checksum.MD5Util;

public class Chunk {
	public int id;
	public byte[] md5;
	public int length;
	
	public Chunk(int id, byte[] md5, byte[] bytes){
		this.id = id;
		this.md5 = md5;
		this.length = bytes.length;
	}
	
	public String toString(){
		return MD5Util.md5_bytes_to_string(md5) + "=>" + length;
	}
	
	public boolean is_bytes_equal(byte[] bytes) throws NoSuchAlgorithmException{
		return Arrays.equals(MD5Util.get_md5(bytes), md5);
	}
}
