package org.example;

import java.util.Random;

public class DisturbMessage {
    public static String disturbMessage(String disturbMessage, double probability) {
        Random rand = new Random();

        double nextDouble = rand.nextDouble();

        if (!(nextDouble < probability)) {
            return disturbMessage;
        }

        int length = disturbMessage.length();
        int toChange = rand.nextInt(0, length);
        char charAt = disturbMessage.charAt(toChange);

        if (charAt == '0') {
            charAt = '1';
        } else if (charAt == '1') {
            charAt = '0';
        }

        StringBuilder stringBuilder = new StringBuilder(disturbMessage);

        stringBuilder.setCharAt(toChange, charAt);

        return stringBuilder.toString();
    }
}
