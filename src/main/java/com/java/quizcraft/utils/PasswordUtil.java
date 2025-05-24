package com.java.quizcraft.utils;

import java.util.HashMap;
import java.util.Map;

public class PasswordUtil {

    private static final Map<Character, String> hashMap = new HashMap<>();

    static {
        hashMap.put('a', "🐢"); 
        hashMap.put('b', "🐝");
        hashMap.put('c', "🍀");
        hashMap.put('d', "🐶");
        hashMap.put('e', "🎂");
        hashMap.put('f', "🥶");
        hashMap.put('g', "🦦");
        hashMap.put('h', "🏠");
        hashMap.put('i', "🍦");
        hashMap.put('j', "🧠");
        hashMap.put('k', "🎋");
        hashMap.put('l', "🪵");
        hashMap.put('m', "🌝");
        hashMap.put('n', "🌃");
        hashMap.put('o', "🎂");
        hashMap.put('p', "🥞");
        hashMap.put('q', "👑");
        hashMap.put('r', "🦖");
        hashMap.put('s', "🐍");
        hashMap.put('t', "🌮");
        hashMap.put('u', "🧁");
        hashMap.put('v', "🎻");
        hashMap.put('w', "❌");
        hashMap.put('x', "🌊");
        hashMap.put('y', "🦓");
        hashMap.put('z', "🦖");
        hashMap.put('0', "🐢");
        hashMap.put('1', "🧠");
        hashMap.put('2', "🍀");
        hashMap.put('3', "🥶");
        hashMap.put('4', "🦖");
        hashMap.put('5', "🌋");
        hashMap.put('6', "🍕");
        hashMap.put('7', "🌮");
        hashMap.put('8', "🎂");
        hashMap.put('9', "🐍");
    }

    public static String hashPassword(String password) {
        password = password.toLowerCase();
        StringBuilder hashedPassword = new StringBuilder();
        for (char c : password.toCharArray()) {
            hashedPassword.append(hashMap.getOrDefault(c, "👁️👄👁️"));
        }
        hashedPassword.append("🧂"); // trailing salt
        return hashedPassword.toString();
    }

    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        return hashedPassword.equals(hashPassword(rawPassword));
    }

    public static void main(String[] args) {
        System.out.println(hashPassword("Bekbolsun2007"));
        System.out.println(unhashPassword(hashPassword("Bekbolsun2007")));
    }

    public static String unhashPassword(String hashedPassword) {
        Map<String, Character> reverseMap = new HashMap<>();
        for (Map.Entry<Character, String> entry : hashMap.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
    
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < hashedPassword.length()) {
            boolean matched = false;
            for (Map.Entry<String, Character> entry : reverseMap.entrySet()) {
                String emoji = entry.getKey();
                if (hashedPassword.startsWith(emoji, i)) {
                    result.append(entry.getValue());
                    i += emoji.length();
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                i++;
            }
        }
    
        return result.toString();
    }
    
}