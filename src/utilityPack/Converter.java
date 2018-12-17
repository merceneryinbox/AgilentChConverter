package utilityPack;

public class Converter {
	
	final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static short getSignedInt16From2ByteArrayWithStringHexeValue(byte[] byteArray) {
		return (short) hexString2Decimal(byteArray2HexString(byteArray));
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
