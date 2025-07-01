package com.observe.os1.v1.newconnection;

public class TmpPasswordGenerator {

    public static String genPassword(int wordLength, int wordCount){
        StringBuilder password = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        for (int i = 0; i < wordCount; i++) {
            for (int j = 0; j < wordLength; j++) {
                int index = (int) (Math.random() * characters.length());
                password.append(characters.charAt(index));
            }
            if (i < wordCount - 1) {
                password.append("-");
            }
        }

        return password.toString();
    }
}
