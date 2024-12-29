package org.example;

public class BergerCode {
    public static String encode(String message) {
        int countZeros = 0;

        for (char c : message.toCharArray()) {
            if (c == '0') countZeros++;
        }

        String binaryString = Integer.toBinaryString(countZeros);
        int length = 4 - binaryString.length();

        return "0".repeat(Math.max(0, length)) + binaryString;
    }

    public static String parseMessage(String message) throws InvalidBergerCodeException {
        if (message == null || message.length() != 24) {
            throw new InvalidBergerCodeException("Berger code message can't be empty or its length is different than 24");
        }

        String parsedMessage = message.substring(0, message.length() - 4);
        String bergerCode = message.substring(message.length() - 4);

        String calculatedBergerCode = encode(parsedMessage);

        if (!bergerCode.equals(calculatedBergerCode)) {
            throw new InvalidBergerCodeException("Berger code is incorrect");
        }

        return parsedMessage;
    }
}
