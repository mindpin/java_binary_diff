package com.mindpin.rsync;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.mindpin.rsync.checksum.Adler32Util;
import com.mindpin.rsync.checksum.MD5Util;
import com.mindpin.rsync.chunk.Chunk;


public class ChunkParser {

	final public static int CHUNK_LENGTH = 512;
	File in_file;
	HashMap<Long, List<Chunk>> chunk_map;
	
	public ChunkParser(File in_file){
		this.in_file = in_file;
		this.chunk_map = new HashMap<Long, List<Chunk>>();
	}
	
	public HashMap<Long, List<Chunk>> parse() throws IOException, NoSuchAlgorithmException{
		long file_length = in_file.length();
		
		RandomAccessFile raf = new RandomAccessFile(in_file, "r");
//		FileInputStream fs = new FileInputStream(in_file);
		
		int sum = 0;
		int id = 0;
		long remained_length = file_length;
		int bytes_len;
		
		byte[] bytes;
		long cs32;
		byte[] md5;
		
		while(remained_length > 0){	
			bytes_len = (remained_length < CHUNK_LENGTH) ? (int)remained_length : CHUNK_LENGTH;
			
			bytes = new byte[bytes_len];
			raf.read(bytes);
			
			cs32 = Adler32Util.checksum(bytes);
			md5 = MD5Util.get_md5(bytes);
			add(cs32, new Chunk(id, md5, bytes));
			
			remained_length -= bytes_len;
			
			sum += bytes_len;
			id ++;
		}
		
		raf.close();
		
		System.out.println("读取原始文件总字节数: " + sum);
		return chunk_map;
	}
	
	public void add(long cs32, Chunk chunk){
		if(chunk_map.containsKey(cs32)){
			chunk_map.get(cs32).add(chunk);
		}else{
			List<Chunk> list = new ArrayList<Chunk>();
			list.add(chunk);
			chunk_map.put(cs32, list);
		}
	}
	
	
//	测试结果：
//	
//	原始文件总字节数: 1049568
//	耗时:0.093 秒
//	chunk数据字节数: 82000
//	
//	原始文件总字节数: 200061022
//	耗时:4.875 秒
//	chunk数据字节数: 15623920
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		File file;
		long t1;
		long t2;
		HashMap<Long, List<Chunk>> map;
		
		// 1MB 0.094 秒
		file = new File("d:/差分比较实验/差分比较实验1.rar");
		System.out.println("原始文件总字节数: " + file.length());
		t1 = new Date().getTime();
		map = new ChunkParser(file).parse();
		t2 = new Date().getTime();
		System.out.println("耗时:" + (t2 - t1) / 1000.0 + " 秒");
		System.out.println("chunk数据字节数: " + map.size() * (8 + 32));
//		System.out.println(map);
		
		// 190MB 4.734 秒
//		file = new File("d:/差分比较实验/world1.zip");
//		System.out.println("原始文件总字节数: " + file.length());
//		t1 = new Date().getTime();
//		map = new ChunkParser(file).parse();
//		t2 = new Date().getTime();
//		System.out.println("耗时:" + (t2 - t1) / 1000.0 + " 秒");
//		System.out.println("chunk数据字节数: " + map.size() * (8 + 32));
		
		// 1G 以上的文件会 out of memory
//		file = new File("D:/Download/乐可乐可2中文版@USP草帽.rar");
//		System.out.println("原始文件总字节数: " + file.length());
//		t1 = new Date().getTime();
//		map = new ChunkParser(file).parse();
//		t2 = new Date().getTime();
//		System.out.println("耗时:" + (t2 - t1) / 1000.0 + " 秒");
	}
}
