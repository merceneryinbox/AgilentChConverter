package AgilenBinaryReaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class AgilentChFileParserNIO_2 {
    
    // utility for convert
    final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    final static String defaultChFilePath = "C:\\work\\OpenChrome\\Sample 1\\002-1401.D\\DAD1A.ch";
    final static String defaultOutputFile = "C:\\work\\DAD1A_TWO_COLOMNS.csv";
    final static String header = "Time(ms),Intensity(abs)\n";
    final static char[] headerCharArray = header.toCharArray();
    final static Charset charset = Charset.forName("UTF-8");
    
    static int commonCharArrayIndex = 0;
    
    // time scale start value set
    static int totalTimeValueInCurrentStep = getInitialTimePoint();
    // time scale step set
    static int timeDeltaIncrease = getTimeStep();
    
    // step of intensity abs set
    static int totalIntensityValueInCurrentStep = getInitialIntensityValue();
    // intensity steps set
    static int offsetDelta = getOffsetDelta();
    
    // start of valuable part of table hex = 1802, dec = 6146
    static int randomFilePointer, randomFileOffset = getInitialRandomFileStartReadPointOffset();
    
    static RandomAccessFile inputRandomAccessFile;
    
    // create new line for output
    static String newResultTimeMsIntensityAbsString = "";
    
    static int intensityAbsTmpBufferSize = getIntensityAbsTmpBufferSize();
    static byte[] intensityAbsolutTmpBuffer = new byte[intensityAbsTmpBufferSize];
    
    public static void main(String[] args) throws IOException, InterruptedException {
        
        Path outPath = getOutPath(args);
        //resources opening
        outPutFileOpen(args);
    
        // header in file creation
        Files.write(outPath,
                    (charset.encode(CharBuffer.wrap(headerCharArray))
                            .array()),
                    StandardOpenOption.WRITE);
        // test zone
        /////////////////////////////////////////////////////////////////////////////////////////////
        //        byte[] fullChInByteArray = Files.readAllBytes(Paths.get(args.length != 2 ? defaultOutputFile :
        // args[1]));
        //        System.out.println(fullChInByteArray.length);
        //////////////////////////////////////////////////////////////////////////////////////////////
        //        int startPoint = 6146;
        //        for (int j = 0; j < fullChInByteArray.length - 1; j++, startPoint++) {
        //            for (int i = 0; i < intensityAbsolutTmpBuffer.length; i++) {
        //                intensityAbsolutTmpBuffer[i] = fullChInByteArray[startPoint + i];
        //            }
        //            totalIntensityValueInCurrentStep = totalIntensityValueInCurrentStep +
        //                             ((short) hexString2Decimal(byteArray2HexString(intensityAbsolutTmpBuffer)));
        //            newResultTimeMsIntensityAbsString = totalTimeValueInCurrentStep + "," +
        // totalIntensityValueInCurrentStep + "\n";
        //            System.out.println(newResultTimeMsIntensityAbsString);
        //            Thread.sleep(700);
        //            setNextTimePointInTimeScale();
        //        }
        //        System.out.println(newResultTimeMsIntensityAbsString);
        //        System.exit(0);
        // test zone
        
        try {
            inputRandomAccessFile = getInputCHFileToParse(args);
            // common byte array size define
            final long commonByteArraySize = getCommonByteArraySize(inputRandomAccessFile);
            // common byte array
            char[] commonCharArray = new char[(int) commonByteArraySize];
            // main loop start until counter less than .ch file length
            while (randomFilePointer < 12191) {
        
                // set reader pointer in source .ch file set
                inputRandomAccessFile.seek(randomFileOffset);
        
                // read bytes of intensity value from set position into temporary intensity buffer
                inputRandomAccessFile.read(intensityAbsolutTmpBuffer, 0, intensityAbsTmpBufferSize);
        
                // get converted from 2 bytes  big endian DWORD hex string to -> unsigned int16 to -> signed int 16
                short currentIntensityValue = (short) hexString2Decimal(byteArray2HexString(intensityAbsolutTmpBuffer));
                // compute total scale value for intensity absolute for current step
                totalIntensityValueInCurrentStep = totalIntensityValueInCurrentStep + currentIntensityValue;
        
                // update new line for write to temp buffer
                newResultTimeMsIntensityAbsString = "" + totalTimeValueInCurrentStep + "," +
                                                    totalIntensityValueInCurrentStep + "\n";
                char[] newLineArray = newResultTimeMsIntensityAbsString.toCharArray();
                for (int i = 0; i < newLineArray.length; i++) {
                    commonCharArray[commonCharArrayIndex++] = newLineArray[i];
                }
                // prepare time point value inside new string for output file
                setNextTimePointInTimeScale();
                setNextOffsetForRandomFile();
                
                // file length counter set
                randomFilePointer++;
            }
    
            Files.write(outPath,
                        (charset.encode(CharBuffer.wrap(commonCharArray))
                                .array()),
                        StandardOpenOption.APPEND);
        } finally {
            inputRandomAccessFile.close();
        }
    }
    
    private static void setNextOffsetForRandomFile() {
        
        randomFileOffset = randomFileOffset + offsetDelta;
    }
    
    private static void setNextTimePointInTimeScale() {
        
        totalTimeValueInCurrentStep = totalTimeValueInCurrentStep + timeDeltaIncrease;
    }
    
    private static void outPutFileOpen(String[] args) throws IOException {
    
        File fileOut = new File(args.length != 2 ? defaultOutputFile : args[1]);
        fileOut.createNewFile();
    }
    
    private static RandomAccessFile getInputCHFileToParse(String[] args) throws FileNotFoundException {
        
        return new RandomAccessFile(args.length != 2 ? defaultChFilePath : args[0], "r");
    }
    
    private static int getIntensityAbsTmpBufferSize() {
        
        return 2;
    }
    
    private static long getCommonByteArraySize(RandomAccessFile randomAccessFile) throws IOException {
        
        return 12191 * 300;
    }
    
    private static int getOffsetDelta() {
        
        return 2;
    }
    
    private static int getInitialRandomFileStartReadPointOffset() {
        
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
    
    private static Path getOutPath(String[] args) {
    
        return Paths.get(args.length != getOffsetDelta() ? defaultOutputFile : args[1]);
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
