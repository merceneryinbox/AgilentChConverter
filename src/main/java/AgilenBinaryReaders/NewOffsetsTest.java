package AgilenBinaryReaders;

import utilityPack.Converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NewOffsetsTest {
	static byte[] intensityAbsolutTmpBuffer = new byte[4];
	static int inputFileByteReadPointOffset = 664;

	public static void main(String[] args) throws IOException {
		byte[] fullChFileAsOneByteArray = Files.readAllBytes(Paths.get("c:\\work\\OpenChrome\\Sample 1\\002-1401.D\\DAD1A.ch"));
		StringBuilder tmpStr = new StringBuilder("");
		for (int i = 0; i < intensityAbsolutTmpBuffer.length; i++, inputFileByteReadPointOffset++) {
			intensityAbsolutTmpBuffer[i] = fullChFileAsOneByteArray[inputFileByteReadPointOffset];

		}
		//		int intensityCurrentStepIncreaseValue = hexString2Decimal(byteArray2HexString(intensityAbsolutTmpBuffer));
		int i = Converter.getDecimalFromHexByteArray(intensityAbsolutTmpBuffer);
		String s = new String(intensityAbsolutTmpBuffer);
		System.out.println(s);

	}

}
