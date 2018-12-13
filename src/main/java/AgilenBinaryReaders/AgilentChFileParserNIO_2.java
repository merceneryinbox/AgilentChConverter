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
        //resources opening
        File fileOut = new File(args.length != 2 ? defaultOutputFile : args[1]);
        fileOut.createNewFile();
        /////////////////////////////////////////////////////////////////////////////////////////////
        byte[] fullChInByteArray = Files.readAllBytes(Paths.get(args.length != 2 ? defaultOutputFile : args[0]));
        //////////////////////////////////////////////////////////////////////////////////////////////
        RandomAccessFile randomAccessFile = new RandomAccessFile(args.length != 2 ? defaultChFilePath : args[0], "r");
        byte[] intensityAbsolutBuffer = new byte[2];
        //        for (int i = 0; i < intensityAbsolutBuffer.length; i++) {
        //            intensityAbsolutBuffer[i] = fullChInByteArray[6148 + i];
        //        }
        //        System.out.println((short) hexString2Decimal(byteArray2HexString(intensityAbsolutBuffer)));
        //        System.exit(0);
        
        // time scale start value set
        int timeRespectToIntensityPoint = 325;
        // step of intensity abs set
        int intensityValue = 0;
        // time scale step set
        int timeDeltaIncrease = 400;
        
        // start of valuable part of table hex = 1802, dec = 6146
        int intensityAbsolutOffsetPoint = 6146;
        // intensity steps set
        int intensityDeltaIncrease = 2;
        
        try {
            // temp var set
            int randomFileLengthCount = 0;
            
            // common byte array
            char[] commonCharArray = new char[12181];
            
            // create new line for output
            String newResultTimeMsIntensityAbsString = "";
            
            // main loop start until counter less than .ch file length
            while (randomFileLengthCount < 12181) {
    
                // file length counter set
                randomFileLengthCount++;
    
                // reader pointer in source .ch file set
                randomAccessFile.seek(intensityAbsolutOffsetPoint);
    
                // read bytes of intensity value from set position into temporary buffer
                randomAccessFile.read(intensityAbsolutBuffer, 0, intensityAbsolutBuffer.length);
                short currentIntensityStepValue
                        = (short) hexString2Decimal(byteArray2HexString(intensityAbsolutBuffer));
                intensityValue = intensityValue + currentIntensityStepValue;
                // prepare time point value inside new string for output file
                newResultTimeMsIntensityAbsString = timeRespectToIntensityPoint + "," + intensityValue + "\n";
                char[] newLineArray = newResultTimeMsIntensityAbsString.toCharArray();
                for (int i = 0; i < newLineArray.length; i++) {
                    commonCharArray[i] = newLineArray[i];
                }
                timeRespectToIntensityPoint = timeRespectToIntensityPoint + timeDeltaIncrease;
                intensityAbsolutOffsetPoint = intensityAbsolutOffsetPoint + intensityDeltaIncrease;
            }
            // header in file creation
            Files.write(getOutPath(args),
                        (charset.encode(CharBuffer.wrap(headerCharArray))
                                .array()),
                        StandardOpenOption.WRITE);
            Files.write(getOutPath(args),
                        (charset.encode(CharBuffer.wrap(commonCharArray))
                                .array()),
                        StandardOpenOption.APPEND);
        } finally {
            randomAccessFile.close();
            
        }
    }
    
    private static Path getOutPath(String[] args) {
        
        return Paths.get(args.length != 2 ? defaultOutputFile : args[1]);
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
