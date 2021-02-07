package Utill;

public class Encoder {

	public String encodeString(String hiddenString) {
		char[] chars = hiddenString.toCharArray();
		char[] arr = new char[chars.length * 2];
		for (int i = 0, j = 0; i < arr.length; i++) {
			if (i % 2 == 0) {
				arr[i] = '§';
			} else {
				arr[i] = chars[j];
				j++;
			}
		}
		return new String(arr);
	}
	
	public String extractHiddenString(String input) {
		char[] chars = input.toCharArray();

		char[] arr = new char[input.length() / 2];
		for (int i = 0, j = 0; i < chars.length; i++) {
			if (i % 2 != 0) {
				arr[j] = chars[i];
				j++;
			}
		}
		return new String(arr);
	}
}
