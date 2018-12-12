package AgilenBinaryReaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) throws IOException {
        //resources opening
        File fileOut = new File("C:\\work\\DAD1A_TWO_COLOMNS.csv");
        fileOut.createNewFile();
        byte[] inputChFileBytes = Files.readAllBytes(Paths.get("C:\\work\\OpenChrome\\Sample 1\\002-1401.D\\DAD1A.ch"));

        String header = "Time(ms),Intensity(abs)\n";

        // start values set
        int timePointRespectToIntensity = 325;
        int timeDeltaIncrease = 400;

        // start point set hex = 1802 = dec = 6146
        int intensityAbsolutOffsetStart = 6146;
        // steps set
        int intensityDeltaIncrease = 2;

        String resultString = "";
        resultString = resultString + header + "\n";

        System.out.println(resultString);

        byte[] tmpInputFileByteArray = new byte[2];

        String s = "";
        for (int i = 0; i < tmpInputFileByteArray.length; i++) {
            tmpInputFileByteArray[i] = inputChFileBytes[intensityAbsolutOffsetStart + i];
            s = s + (tmpInputFileByteArray[i]);
        }
        System.out.println(Integer.decode(s));

//        String newStr = new String(tmpInputFileByteArray) + "\n";
//        resultString = resultString + newStr;
//
//        char[] resultStringCharArray = resultString.toCharArray();
//        byte[] tmpByteBuff = new byte[resultStringCharArray.length];
//
//        FileOutputStream fileOutputStream = new FileOutputStream(fileOut);
//
//        for (int i = 0; i < resultStringCharArray.length; i++) {
//            fileOutputStream.write(resultStringCharArray[i]);
//        }
//        fileOutputStream.close();
    }
}
