package AgilenBinaryReaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AgilentChFileParser {
    
    // utility for convert
    final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    final static String defaultChFilePath = "C:\\work\\OpenChrome\\Sample 1\\002-1401.D\\DAD1A.ch";
    final static String defaultOutputFile = "C:\\work\\DAD1A_TWO_COLOMNS.csv";
    final static String header = "Time(ms),Intensity(abs)\n";
    final static char[] headerCharArray = header.toCharArray();
    
    
    public static void main(String[] args) throws IOException, InterruptedException {
        //resources opening
        File fileOut = new File(args[1] == null ? defaultOutputFile : args[1]);
        fileOut.createNewFile();
    
        RandomAccessFile randomAccessFile = new RandomAccessFile(args[0] == null ? defaultChFilePath : args[0], "r");
        byte[] intensityAbsolutBuffer = new byte[2];
    
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
    
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
            // header infile creation
            for (int i = 0; i < headerCharArray.length; i++) {
                fileOutputStream.write(headerCharArray[i]);
            }
        
            // temp var set
            long randomFileLength = randomAccessFile.length();
            System.out.println("randomFileLength  = " + randomFileLength);
            int randomFileLengthCount = 0;
        
            // main loop start until counter less than .ch file length
            while (randomFileLengthCount < 12181) {
    
                // file length counter set
                randomFileLengthCount++;
    
                // create new line for output
                String newResultTimeMsIntensityAbsString = "";
    
                // reader pointer in source .ch file set
                randomAccessFile.seek(intensityAbsolutOffsetPoint);
    
                // read bytes of intensity value from set position into temporary buffer
                randomAccessFile.read(intensityAbsolutBuffer, 0, intensityAbsolutBuffer.length);
                String newIntensityLine = "";
                short currentIntensityStepValue
                        = (short) hex2Decimal(getHexStringFromByteArray(intensityAbsolutBuffer));
                intensityValue = intensityValue + currentIntensityStepValue;
                newIntensityLine = "" + intensityValue;
                // prepare time point value inside new string for output file
                newResultTimeMsIntensityAbsString = timeRespectToIntensityPoint + "," + newIntensityLine.trim() + "\n";
                char[] newLineArray = newResultTimeMsIntensityAbsString.toCharArray();
                for (int i = 0; i < newLineArray.length; i++) {
                    System.out.print(newLineArray[i]);
                    fileOutputStream.write(newLineArray[i]);
                }
                timeRespectToIntensityPoint = timeRespectToIntensityPoint + timeDeltaIncrease;
                intensityAbsolutOffsetPoint = intensityAbsolutOffsetPoint + intensityDeltaIncrease;
            }
        } finally {
            randomAccessFile.close();
        }
    }
    
    public static String getHexStringFromByteArray(byte[] bytes) {
        
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
    
    public static int hex2Decimal(String s) {
        
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
