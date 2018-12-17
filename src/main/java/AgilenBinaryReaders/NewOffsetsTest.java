package AgilenBinaryReaders;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NewOffsetsTest {
	final static Charset charset = Charset.forName("UTF-8");
	static byte[] intensityAbsolutTmpBuffer = new byte[24];
	static int inputFileByteReadPointOffset = 348;

	public static void main(String[] args) throws IOException {
		byte[] fullChFileAsOneByteArray = Files.readAllBytes(Paths.get("c:\\work\\OpenChrome\\Sample 1\\002-1401.D\\DAD1A.ch"));
		StringBuilder tmpStrBuilder = new StringBuilder("");
		for (int i = 0; i < intensityAbsolutTmpBuffer.length; i++, inputFileByteReadPointOffset++) {
			byte tmpByte = fullChFileAsOneByteArray[inputFileByteReadPointOffset];
			intensityAbsolutTmpBuffer[i] = tmpByte;
			tmpStrBuilder.append((char) tmpByte);
		}
		//		int intensityCurrentStepIncreaseValue = hexString2Decimal(byteArray2HexString(intensityAbsolutTmpBuffer));
		//		int i = Converter.getDecimalFromHexByteArray(intensityAbsolutTmpBuffer);
		//		String s = new String(intensityAbsolutTmpBuffer);
		System.out.println(tmpStrBuilder.toString());

	}

}
