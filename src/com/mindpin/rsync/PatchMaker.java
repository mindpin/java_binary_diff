package com.mindpin.rsync;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mindpin.rsync.checksum.Adler32Util;
import com.mindpin.rsync.chunk.Chunk;
import com.mindpin.rsync.patch.Patch;


public class PatchMaker {

	File target_file;
	HashMap<Long, List<Chunk>> chunk_map;
	
	// src + patch = target
	// 根据 src 的 chunk_map
	// 扫描 target 一次
	// 就得到了如何 从 src -> target 的 patch 数据
	
	public PatchMaker(File target_file, HashMap<Long, List<Chunk>> chunk_map){
		this.target_file = target_file;
		this.chunk_map = chunk_map;
	}
	
	public Patch make() throws IOException, NoSuchAlgorithmException{
		Patch patch = new Patch();
		
		long file_length = target_file.length();
		RandomAccessFile raf = new RandomAccessFile(target_file, "r");
		List<Byte> diff_data = new ArrayList<Byte>();
		
		byte[] bytes = {};		
		long remained_length = file_length;
		
		int sum = 0;
		boolean next_block = true;
		
		Chunk chunk;
		while(remained_length > 0){	
			if(next_block){
				bytes = read_next_block(raf, remained_length);
				remained_length -= bytes.length;
			}else{
				bytes = read_next_byte(raf, bytes);
				remained_length --;
			}
			
			chunk = match(chunk_map, bytes);
			
			if(chunk == null){
				// 未匹配，数组[0]放入 diff
				diff_data.add(bytes[0]);
							
				next_block = false;
			}else{
				// 匹配，将现在的 diff 加入 patch, 再将 chunk 加入 patch
				patch.add(diff_data);
				sum += diff_data.size();
				diff_data = new ArrayList<Byte>();
				
				patch.add(chunk);
				sum += chunk.length;
				
				next_block = true;
			}
		}
		
		// 结束了，将此时 block_bytes 里剩下的内容（第一个字节除外，因为已经放过了），全部放入 diff，再加入 patch
		for(int i = 1; i < bytes.length; i++){
			diff_data.add(bytes[i]);
		}
		
		patch.add(diff_data);
		sum += diff_data.size();
		
		raf.close();
		
		System.out.println("扫描目标文件总字节数: " + sum);
		
		return patch;
	}
	
	// 向前读一字节，把新内容放入 block_bytes
	private byte[] read_next_byte(RandomAccessFile raf, byte[] block_bytes) throws IOException{
		byte[] next_byte = new byte[1];
		raf.read(next_byte);
		
		for(int i = 0; i < block_bytes.length - 1; i++){
			block_bytes[i] = block_bytes[i + 1];
		}
		block_bytes[block_bytes.length - 1] = next_byte[0];
		
		return block_bytes;
	}
	
	private byte[] read_next_block(RandomAccessFile raf, long remained_length) throws IOException{
		int bytes_len = (remained_length < ChunkParser.CHUNK_LENGTH) ? (int)remained_length : ChunkParser.CHUNK_LENGTH;
		byte[] block_bytes = new byte[bytes_len];
		raf.read(block_bytes);
		
		return block_bytes;
	}
	
	// 根据传入的字节数组，去尝试匹配，如果匹配到，返回匹配元数据，如果没匹配到，返回空
	private Chunk match(HashMap<Long, List<Chunk>> chunk_map, byte[] bytes) throws NoSuchAlgorithmException{
		long cs32 = Adler32Util.checksum(bytes);
		
		if(chunk_map.containsKey(cs32)){
			for(Chunk chunk : chunk_map.get(cs32)){
				if(chunk.is_bytes_equal(bytes)){
					// 匹配了一个block
					return chunk;
				}
			}
		}
		
		return null;
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		File src_file = new File("d:/差分比较实验/差分比较实验1.rar");
		File target_file = new File("d:/差分比较实验/差分比较实验2.rar");
		
		HashMap<Long, List<Chunk>> chunk_map = new ChunkParser(src_file).parse();
		Patch patch = new PatchMaker(target_file, chunk_map).make();
		System.out.println(patch);
	}
}
