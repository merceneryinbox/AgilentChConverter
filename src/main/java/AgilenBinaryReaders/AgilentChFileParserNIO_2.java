package AgilenBinaryReaders;

import java.io.File;
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
    
    
    public static void main(String[] args) throws IOException {
    
        Path outPath = getOutPath(args);
        //resources opening
        File fileOut = new File(args.length != getIntensityDeltaIncreaseStep() ? defaultOutputFile : args[1]);
        fileOut.createNewFile();
        /////////////////////////////////////////////////////////////////////////////////////////////
        byte[] fullChInByteArray = Files.readAllBytes(Paths.get(
                args.length != getIntensityDeltaIncreaseStep() ? defaultOutputFile : args[getInitialIntensityValue()]));
        //////////////////////////////////////////////////////////////////////////////////////////////
        RandomAccessFile randomAccessFile = new RandomAccessFile(
                args.length != getIntensityDeltaIncreaseStep() ? defaultChFilePath : args[getInitialIntensityValue()],
                "r");
        byte[] intensityAbsolutBuffer = new byte[getIntensityDeltaIncreaseStep()];
        for (int i = 0; i < intensityAbsolutBuffer.length; i++) {
            intensityAbsolutBuffer[i] = fullChInByteArray[6148 + i];
        }
        System.out.println((short) hexString2Decimal(byteArray2HexString(intensityAbsolutBuffer)));
        System.exit(0);
        
        // time scale start value set
        int timeRespectToIntensityPoint = getTimeRespectToIntensityPoint();
        // step of intensity abs set
        int intensityValue = getInitialIntensityValue();
        // time scale step set
        int timeDeltaIncrease = getTimeDeltaIncrease();
        
        // start of valuable part of table hex = 1802, dec = 6146
        int intensityAbsolutOffsetPoint = getInitialIntensityAbsolutOffsetPoint();
        // intensity steps set
        int intensityDeltaIncrease = getIntensityDeltaIncreaseStep();
        
        try {
            // temp var set
            int randomFilePointer = getInitialIntensityValue();
            
            // common byte array
            char[] commonCharArray = new char[getCommonByteArraySize()];
            
            // create new line for output
            String newResultTimeMsIntensityAbsString = "";
            
            // main loop start until counter less than .ch file length
            while (randomFilePointer < getCommonByteArraySize()) {
                
                // file length counter set
                randomFilePointer++;
                
                // reader pointer in source .ch file set
                randomAccessFile.seek(intensityAbsolutOffsetPoint);
                
                // read bytes of intensity value from set position into temporary buffer
                randomAccessFile.read(intensityAbsolutBuffer, 0,
                                      intensityAbsolutBuffer.length);
                short currentIntensityStepValue = (short) hexString2Decimal(byteArray2HexString(intensityAbsolutBuffer));
                intensityValue = intensityValue + currentIntensityStepValue;
                // prepare time point value inside new string for output file
                newResultTimeMsIntensityAbsString = timeRespectToIntensityPoint + "," + intensityValue + "\n";
                char[] newLineArray = newResultTimeMsIntensityAbsString.toCharArray();
                for (int i = getInitialIntensityValue(); i < newLineArray.length; i++) {
                    commonCharArray[i] = newLineArray[i];
                }
                timeRespectToIntensityPoint = timeRespectToIntensityPoint + timeDeltaIncrease;
                intensityAbsolutOffsetPoint = intensityAbsolutOffsetPoint + intensityDeltaIncrease;
            }
            // header in file creation
            Files.write(outPath,
                        (charset.encode(CharBuffer.wrap(headerCharArray))
                                .array()),
                        StandardOpenOption.WRITE);
            Files.write(outPath,
                        (charset.encode(CharBuffer.wrap(commonCharArray))
                                .array()),
                        StandardOpenOption.APPEND);
        } finally {
            randomAccessFile.close();
            
        }
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
    
        char[] hexChars = new char[bytes.length * getIntensityDeltaIncreaseStep()];
        for (int j = getInitialIntensityValue(); j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * getIntensityDeltaIncreaseStep()] = hexArray[v >>> 4];
            hexChars[j * getIntensityDeltaIncreaseStep() + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    public static int getDecimalFromHexByteArray(byte[] bytes) {
    
        int result = getInitialIntensityValue();
        for (int i = getInitialIntensityValue(); i < bytes.length; i++) {
            result = result | (bytes[i] & 0xff) >> (i * 8);
        }
        return result;
    }
    
    public static int hexString2Decimal(String s) {
        
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase();
        int val = getInitialIntensityValue();
        for (int i = getInitialIntensityValue(); i < s.length(); i++) {
            char c = s.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }
}
