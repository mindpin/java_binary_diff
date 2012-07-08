package com.mindpin.rsync.checksum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	
	private static char MD5_CHARS[] = { 
		'0', '1', '2', '3', 
		'4', '5', '6', '7', 
		'8', '9', 'a', 'b', 
		'c', 'd', 'e', 'f' 
	};
	
	public static byte[] get_md5(byte[] b) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(b);
		return md.digest();
	}
	
	public static String md5_bytes_to_string(byte[] b){
		StringBuffer sb = new StringBuffer(2 * b.length);
		for(byte i : b){
			append_hex_pair(i, sb);
		}
		return sb.toString();
	}
	
	private static void append_hex_pair(byte b, StringBuffer sb) {
		char c0 = MD5_CHARS[(b & 0xf0) >> 4];
		char c1 = MD5_CHARS[b & 0xf];
		sb.append(c0).append(c1);
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		String md5_a = MD5Util.md5_bytes_to_string(MD5Util.get_md5("a".getBytes()));
		System.out.println(md5_a);
		// "a" 的 md5 值是 0cc175b9c0f1b6a831c399e269772661
	}
}
