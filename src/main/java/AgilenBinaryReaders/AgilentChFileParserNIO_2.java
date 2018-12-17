package AgilenBinaryReaders;

import sun.nio.fs.DefaultFileSystemProvider;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.spi.FileSystemProvider;

public class AgilentChFileParserNIO_2 {

	// utility for convert
	final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	final static String defaultChFilePath = "C:\\work\\OpenChrome\\Sample 1\\002-1401.D\\DAD1A.ch";
	final static String defaultOutputFile = "C:\\work\\DAD1A_TWO_COLUMNS.csv";
	final static String header = "Time(ms),Intensity(abs)\n";
	final static Charset charset = Charset.forName("UTF-8");
	static Path chFilePath;

	// time scale start value set (represented as runtime computation so it might have been changed in using with
	// another chromatography file)
	static int totalTimeValueInCurrentStep = getInitialTimePoint();
	// time scale step set (represented as runtime computation so it might have been changed in using with another
	// chromatography file)
	static int timeDeltaIncrease = getTimeStep();

	// step of intensity abs set (represented as runtime computation so it might have been changed in using with
	// another chromatography file)
	static int totalIntensityValueInCurrentStep = getInitialIntensityValue();
	// intensity steps set (represented as runtime computation so it might have been changed in using with another
	// chromatography file)
	static int offsetDelta = getOffsetDelta();

	// start of valuable part of table hex = 1802, dec = 6146 (represented as runtime computation so it might have
	// been changed in using with another chromatography file)
	static int inputFileByteReadPointOffset = getInitialInputFileStartByteReadPointOffset();
	static int loopCounter;

	// create new line for output (represented as runtime computation so it might have been changed in using with
	// another chromatography file)
	static StringBuilder resultStringAppender = new StringBuilder(header);

	// byte buffer length for byte buffer which represents one hexadecimal number in binary chromatography file
	// (represented as runtime computation so it might have been changed in using with another chromatography file)
	static int intensityAbsTmpBufferSize = getIntensityAbsTmpBufferSize();
	// byte buffer consists of bytes which together are represents one hexadecimal DWORD number
	// (represented as runtime computation so it might have been changed in using with another chromatography file)
	static byte[] intensityAbsolutTmpBuffer = new byte[intensityAbsTmpBufferSize];

	public static void main(String[] args) throws Exception {
		AgilentChFileChooser fileChooser = new AgilentChFileChooser();
		fileChooser.setSize(600, 600);
		fileChooser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fileChooser.setVisible(false);
		chFilePath = fileChooser.getFileOrDirectoryPath();

		byte[] fullChFileAsOneByteArray = Files.readAllBytes(Paths.get(args.length != 2 ? (chFilePath == null ? defaultChFilePath : chFilePath.toString()) : args[1]));
		int fullInputFileArrayLength = fullChFileAsOneByteArray.length;
		try {
			// main loop start until counter less than .ch file length
			while (inputFileByteReadPointOffset < fullInputFileArrayLength) {

				// read two bytes = absolute intensity value into two-bytes buffer from current offset of full input
				// file buffer for further new line forming
				for (int i = 0; i < intensityAbsTmpBufferSize; i++) {
					intensityAbsolutTmpBuffer[i] = fullChFileAsOneByteArray[inputFileByteReadPointOffset++];
				}

				// get converted from 2 bytes  big-endian DWORD hex string to -> unsigned int16 to -> signed int 16 =
				// absolute intensity addition to total intensity
				// compute total scale value for intensity absolute in current step
				short intensityCurrentStepIncreaseValue = (short) hexString2Decimal(byteArray2HexString(intensityAbsolutTmpBuffer));

				// create new intensity point for current time point
				totalIntensityValueInCurrentStep = totalIntensityValueInCurrentStep + intensityCurrentStepIncreaseValue;

				// append new line to write in output .csv file to temp buffer
				resultStringAppender.append(totalTimeValueInCurrentStep + "," + totalIntensityValueInCurrentStep + "\n");

				// prepare next time point value on axis and next input file read offset
				setNextTimePointInTimeScale();
				// file length counter set
				loopCounter++;
			}

			// store time points correspond to intensity into result csv file
			Files.write(getOutPath(), (charset.encode(CharBuffer.wrap(resultStringAppender.toString().toCharArray())).array()), StandardOpenOption.CREATE);
			FileSystemProvider fileSystemProvider = DefaultFileSystemProvider.create();
			//			fileSystemProvider.
			System.exit(0);
		} catch (FileNotFoundException fnf) {
			System.err.println("Input file " + defaultChFilePath + "seem to be in some another place or it's have been opened in time of access." + "\nPlease check this before " + "new attempt !");
		}
	}

	private static void setNextTimePointInTimeScale() {

		totalTimeValueInCurrentStep = totalTimeValueInCurrentStep + timeDeltaIncrease;
	}

	private static int getIntensityAbsTmpBufferSize() {

		return 2;
	}

	private static int getOffsetDelta() {

		return 2;
	}

	private static int getInitialInputFileStartByteReadPointOffset() {

		return 6146;
	}

	private static int getTimeStep() {

		return 400;
	}

	private static int getInitialIntensityValue() {

		return 0;
	}

	private static int getInitialTimePoint() {

		return 325;
	}

	private static Path getOutPath() throws IOException {
		String outPutPath = generateOutPutPathName(chFilePath);
		Path filePath = Paths.get(outPutPath);
		return filePath;
	}

	private static String generateOutPutPathName(Path chFilePath) {
		String directoryOfInputPath = chFilePath.getParent().toString();
		String inputFileName = chFilePath.getFileName().toString();

		// Change fileName
		String outputFileName = inputFileName + "_.csv";
		return directoryOfInputPath + outputFileName;
	}

	public static String byteArray2HexString(byte[] bytes) {

		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static int getDecimalFromHexByteArray(byte[] bytes) {

		int result = 0;
		for (int i = 0; i < bytes.length; i++) {
			result = result | (bytes[i] & 0xff) >> (i * 8);
		}
		return result;
	}

	public static int hexString2Decimal(String s) {

		String digits = "0123456789ABCDEF";
		s = s.toUpperCase();
		int val = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int d = digits.indexOf(c);
			val = 16 * val + d;
		}
		return val;
	}
}
