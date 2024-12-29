package org.example;

public class BergerCode {
    public static String encode(String message) {
        if (message.length() != 16 || !message.matches("[01]+")) {
            throw new IllegalArgumentException("Wejściowy ciąg musi mieć dokładnie 16 bitów (0 lub 1).");
        }

        int zeroCount = 0;
        for (char bit : message.toCharArray()) {
            if (bit == '0') {
                zeroCount++;
            }
        }

        StringBuilder bergerCode = new StringBuilder(Integer.toBinaryString(zeroCount));

        while (bergerCode.length() < 5) {
            bergerCode.insert(0, "0");
        }

        return bergerCode.toString();
    }

    public static String parseMessage(String message) throws InvalidBergerCodeException {
        if (message == null || message.length() != 21) {
            throw new InvalidBergerCodeException("Berger code message can't be empty or its length is different than 21 for text");
        }

        String parsedMessage = message.substring(0, message.length() - 5);
        String bergerCode = message.substring(message.length() - 5);

        String calculatedBergerCode = encode(parsedMessage);

        if (!bergerCode.equals(calculatedBergerCode)) {
            throw new InvalidBergerCodeException("Berger code is incorrect");
        }

        return parsedMessage;
    }
}
