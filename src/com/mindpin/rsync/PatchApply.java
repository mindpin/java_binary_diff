package com.mindpin.rsync;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import com.mindpin.rsync.chunk.Chunk;
import com.mindpin.rsync.patch.Patch;
import com.mindpin.rsync.patch.PatchPartChunk;
import com.mindpin.rsync.patch.PatchPartData;
import com.mindpin.rsync.patch.i.PatchPart;

public class PatchApply {

	Patch patch;
	File src_file;
	File result_file;
	
	public PatchApply(File src_file, Patch patch, File result_file){
		this.patch = patch;
		this.src_file = src_file;
		this.result_file = result_file;
	}
	
	public void apply() throws IOException{
		result_file.delete();
		
		RandomAccessFile read_raf = new RandomAccessFile(src_file, "r");
		RandomAccessFile write_raf = new RandomAccessFile(result_file, "rw");
		
		long src_file_length = src_file.length();
		
		long sum = 0;
		for(PatchPart part : patch.parts){
			if(part instanceof PatchPartData){
				write_raf.write(((PatchPartData)part).bytes);
				sum += ((PatchPartData)part).bytes_size();
			}
			
			if(part instanceof PatchPartChunk){
				int off = ((PatchPartChunk)part).id * ChunkParser.CHUNK_LENGTH;
				long remained_length = src_file_length - off;
				
				int length = (remained_length < ChunkParser.CHUNK_LENGTH) ? (int)remained_length : ChunkParser.CHUNK_LENGTH;
				
				byte[] bytes = new byte[length];
				
				read_raf.seek(off);
				read_raf.read(bytes);
				write_raf.write(bytes);
				
				sum += length;
			}
		}
		
		write_raf.close();
		read_raf.close();
		System.out.println("写入结果文件字节数: " + sum);
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
//		File src_file = new File("d:/差分比较实验/差分比较实验1.rar");
//		File target_file = new File("d:/差分比较实验/差分比较实验2.rar");
//		File result_file = new File("d:/差分比较实验/差分比较实验result.rar");
	
		File src_file = new File("d:/差分比较实验/月球_a.bmp");
		File target_file = new File("d:/差分比较实验/月球_c.bmp");
		File result_file = new File("d:/差分比较实验/月球_c_result.bmp");
		
		HashMap<Long, List<Chunk>> chunk_map = new ChunkParser(src_file).parse();
		Patch patch = new PatchMaker(target_file, chunk_map).make();
		
		new PatchApply(src_file, patch, result_file).apply();
	}
}
