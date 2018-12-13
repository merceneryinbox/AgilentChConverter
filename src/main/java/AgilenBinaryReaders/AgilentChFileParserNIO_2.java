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
    
    // time scale start value set
    static int timeRespectToIntensityPoint = getTimeRespectToIntensityPoint();
    // step of intensity abs set
    static int intensityValue = getInitialIntensityValue();
    // time scale step set
    static int timeDeltaIncrease = getTimeDeltaIncrease();
    
    // start of valuable part of table hex = 1802, dec = 6146
    static int intensityAbsolutOffsetPoint = getInitialIntensityAbsolutOffsetPoint();
    // intensity steps set
    static int intensityDeltaIncrease = getIntensityDeltaIncreaseStep();
    
    // temp var set
    static int randomFilePointer = getInitialIntensityValue();
    
    // common byte array size define
    final static int commonByteArraySize = getCommonByteArraySize();
    
    // common byte array
    static char[] commonCharArray = new char[commonByteArraySize];
    
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
        //            intensityValue = intensityValue +
        //                             ((short) hexString2Decimal(byteArray2HexString(intensityAbsolutTmpBuffer)));
        //            newResultTimeMsIntensityAbsString = timeRespectToIntensityPoint + "," + intensityValue + "\n";
        //            System.out.println(newResultTimeMsIntensityAbsString);
        //            Thread.sleep(700);
        //            timePointNextStep();
        //        }
        //        System.out.println(newResultTimeMsIntensityAbsString);
        //        System.exit(0);
        // test zone
        
        
        RandomAccessFile randomAccessFile = getReadFileToDrive(args);
        
        try {
            // main loop start until counter less than .ch file length
            while (randomFilePointer < commonByteArraySize) {
                
                // reader pointer in source .ch file set
                randomAccessFile.seek(intensityAbsolutOffsetPoint);
        
                // read bytes of intensity value from set position into temporary intensity buffer
                randomAccessFile.read(intensityAbsolutTmpBuffer, 0, intensityAbsTmpBufferSize);
                short currentIntensityStepValue = (short) hexString2Decimal(byteArray2HexString(
                        intensityAbsolutTmpBuffer));
                intensityValue = intensityValue + currentIntensityStepValue;
        
                // update newline for write to temp buffer
                newResultTimeMsIntensityAbsString = timeRespectToIntensityPoint + "," + intensityValue + "\n";
                char[] newLineArray = newResultTimeMsIntensityAbsString.toCharArray();
                for (int i = 0; i < newLineArray.length; i++) {
                    commonCharArray[i] = newLineArray[i];
                }
                // prepare time point value inside new string for output file
                timePointNextStep();
                intensityPointNextStep();
                
                // file length counter set
                randomFilePointer++;
            }
    
            Files.write(outPath,
                        (charset.encode(CharBuffer.wrap(commonCharArray))
                                .array()),
                        StandardOpenOption.APPEND);
        } finally {
            randomAccessFile.close();
        }
    }
    
    private static void intensityPointNextStep() {
        
        intensityAbsolutOffsetPoint = intensityAbsolutOffsetPoint + intensityDeltaIncrease;
    }
    
    private static void timePointNextStep() {
        
        timeRespectToIntensityPoint = timeRespectToIntensityPoint + timeDeltaIncrease;
    }
    
    private static void outPutFileOpen(String[] args) throws IOException {
        
        File fileOut = new File(args.length != getIntensityDeltaIncreaseStep() ? defaultOutputFile : args[1]);
        fileOut.createNewFile();
    }
    
    private static RandomAccessFile getReadFileToDrive(String[] args) throws FileNotFoundException {
        
        return new RandomAccessFile(args.length != 2 ? defaultChFilePath : args[0], "r");
    }
    
    private static int getIntensityAbsTmpBufferSize() {
        
        return 2;
    }
    
    private static int getCommonByteArraySize() {
        
        return 12181;
    }
    
    private static int getIntensityDeltaIncreaseStep() {
        
        return 2;
    }
    
    private static int getInitialIntensityAbsolutOffsetPoint() {
        
        return 6146;
    }
    
    private static int getTimeDeltaIncrease() {
        
        return 400;
    }
    
    private static int getInitialIntensityValue() {
        
        return 0;
    }
    
    private static int getTimeRespectToIntensityPoint() {
        
        return 325;
    }
    
    private static Path getOutPath(String[] args) {
    
        return Paths.get(args.length != getIntensityDeltaIncreaseStep() ? defaultOutputFile : args[1]);
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
