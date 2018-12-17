package AgilenBinaryReaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NewOffsetsTest {
	static char[] intensityAbsolutTmpBuffer = new char[4];
	static int inputFileByteReadPointOffset = 252;

	public static void main(String[] args) throws IOException {
		byte[] fullChFileAsOneByteArray = Files.readAllBytes(Paths.get("c:\\work\\OpenChrome\\Sample 1\\002-1401.D\\DAD1A.ch"));
		for (int i = 0; i < intensityAbsolutTmpBuffer.length; i++) {
			intensityAbsolutTmpBuffer[i] = (char) fullChFileAsOneByteArray[inputFileByteReadPointOffset++];
		}
		String s = new String(intensityAbsolutTmpBuffer);
		System.out.println(s);

	}
}
