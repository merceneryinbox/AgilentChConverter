package AgilenBinaryReaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FseekTest {
    public static void main(String[] args) throws IOException {
        //resources opening
        File fileOut = new File("C:\\work\\DAD1A_TWO_COLOMNS.csv");
        fileOut.createNewFile();

        RandomAccessFile
                randomAccessFile =
                new RandomAccessFile("C:\\work\\OpenChrome\\Sample 1\\002-1401.D\\DAD1A.ch", "r");
        String header = "Time(ms),Intensity(abs)";
        char[] headerCharArray = header.toCharArray();

        byte[] intensityAbsolutBuffer = new byte[2];

        // start values set
        int timeRespectToIntensityPoint = 325;
        int timeDeltaIncrease = 400;

        // steps set
        int intensityAbsolutOffsetPoint = 6146;
        int intensityDeltaIncrease = 2;

        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {

            // header creation
            for (int i = 0; i < headerCharArray.length; i++) {
                fileOutputStream.write(headerCharArray[i]);
            }
            // temp var set
            long randomFileLength = randomAccessFile.length();
            int randomFileLengthCount = 0;

            // main loop start until counter less than .ch file length
            while (randomFileLengthCount < randomFileLength) {
                randomAccessFile.seek(intensityAbsolutOffsetPoint);

                // file length counter set
                randomFileLengthCount++;

                // create new line for output
                String newResultTimeMsIntensityAbsString = "";

                // reader pointer in source .ch file set

                // read bytes of intensity value from set position into temporary buffer
                randomAccessFile.read(intensityAbsolutBuffer);

                // create intensity point string to convert to Decimal value inside new string for output file
                String hexIntensityAbsVal = new String(intensityAbsolutBuffer);
                System.out.println(hexIntensityAbsVal);
//                Integer convertedIntensityAbsFromHex = Integer.parseInt(hexIntensityAbsVal, 16);

                // prepare time point value inside new string for output file
                newResultTimeMsIntensityAbsString =
                        timeRespectToIntensityPoint + "," + hexIntensityAbsVal + "\n";
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
}
