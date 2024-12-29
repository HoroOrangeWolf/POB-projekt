package org.example;

public class BergerCode {
    public static String encode(String message) {
        int countZeros = 0;

        for (char c : message.toCharArray()) {
            if (c == '0') countZeros++;
        }

        return String.format("%4s", Integer.toBinaryString(countZeros)).replace(' ', '0');
    }

    public static String parseMessage(String message) throws InvalidBergerCodeException {
        if (message == null || message.length() != 20) {
            throw new InvalidBergerCodeException("Berger code message can't be empty or its length is different than 20 for text");
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
