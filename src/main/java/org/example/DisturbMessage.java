package org.example;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DisturbMessage {
    public static String disturbMessage(String disturbMessage, double probability, ErrorType errorType) {
        Random rand = new Random();

        double nextDouble = rand.nextDouble();

        if (!(nextDouble < probability)) {
            return disturbMessage;
        }

        StringBuilder stringBuilder = new StringBuilder(disturbMessage);

        return switch (errorType) {
            case DIRECTIONAL_0_TO_1: {
                for (int i = 0; i < stringBuilder.length(); i++) {
                    if (stringBuilder.charAt(i) == '0') {
                        stringBuilder.setCharAt(i, '1');
                        yield stringBuilder.toString();
                    }
                }
            }
            case DIRECTIONAL_1_TO_0: {
                for (int i = 0; i < stringBuilder.length(); i++) {
                    if (stringBuilder.charAt(i) == '1') {
                        stringBuilder.setCharAt(i, '0');
                        yield stringBuilder.toString();
                    }
                }
            }
            case BIDIRECTIONAL: {
                Set<Integer> excludedIndexes = new HashSet<>();

                for (int i = 0, disturbCount = rand.nextInt(1, 3); i < stringBuilder.length() && disturbCount > 0; i++) {
                    if (stringBuilder.charAt(i) == '1') {
                        excludedIndexes.add(i);
                        stringBuilder.setCharAt(i, '0');
                        disturbCount--;
                    }
                }

                for (int i = 0, disturbCount = rand.nextInt(1, 3); i < stringBuilder.length() && disturbCount > 0; i++) {
                    if (stringBuilder.charAt(i) == '0' && !excludedIndexes.contains(i)) {
                        stringBuilder.setCharAt(i, '1');
                        disturbCount--;
                    }
                }

                yield stringBuilder.toString();
            }
        };
    }
}
