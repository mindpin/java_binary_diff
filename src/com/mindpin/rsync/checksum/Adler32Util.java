package com.mindpin.rsync.checksum;

import java.util.zip.Adler32;

public class Adler32Util {
	public static long checksum(byte[] b){
		Adler32 a32 = new Adler32();
		a32.update(b);
		return a32.getValue();
	}
}
