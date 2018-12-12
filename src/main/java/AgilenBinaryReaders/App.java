package AgilenBinaryReaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class App {
    // utility for convert
    final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static void main(String[] args) throws IOException, InterruptedException {
        //resources opening
        File fileOut = new File("C:\\work\\DAD1A_TWO_COLOMNS.csv");
        fileOut.createNewFile();

        RandomAccessFile
                randomAccessFile =
                new RandomAccessFile("C:\\work\\OpenChrome\\Sample 1\\002-1401.D\\DAD1A.ch", "r");
        String header = "Time(ms),Intensity(abs)\n";
        byte[] intensityAbsolutBuffer = new byte[2];

        // time scale start value set
        int timeRespectToIntensityPoint = 325;
        // time scale step set
        int timeDeltaIncrease = 400;

        // start of valuable part of table hex = 1802, dec = 6146
        int intensityAbsolutOffsetPoint = 6146;
        // intensity steps set
        int intensityDeltaIncrease = 2;

        System.out.println(header);
        char[] headerCharArray = header.toCharArray();
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {

            // header creation
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

                newIntensityLine = "" + (short) hex2Decimal(getHexStringFromByteArray(intensityAbsolutBuffer));
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
