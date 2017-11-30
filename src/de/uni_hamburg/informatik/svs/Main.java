package de.uni_hamburg.informatik.svs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final Integer[] WORD1 = { 9,  0,  4, 10};
    private static final Integer[] WORD2 = {10, 20, 28,  9};
    private static final Integer[] WORD3 = {10, 16,  2,  2};
    private static final Integer[] WORD4 = {10, 20,  5,  8};
    private static final Integer[] WORD5 = {26, 26,  3,  0};
    private static final Integer[] WORD6 = {28, 16,  3, 17};

    public static void main(String[] args) {
        // --- Init data structures ---
        List<Integer[]> words = new ArrayList<>(6);
        words.add(WORD1); words.add(WORD2); words.add(WORD3); words.add(WORD4); words.add(WORD5); words.add(WORD6);
        List<String> dict = Collections.emptyList();
        try {
            dict = Files.lines(Paths.get(".//resources//german.dic"),
                    StandardCharsets.ISO_8859_1)
                    // Words must be 4 letters long and Germans nouns must begin with a capital letter
                    .filter(str -> (str.length() == 4) && (str.charAt(0) == str.toUpperCase().charAt(0)))
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println(e.toString() + ": " + e.getMessage());
            System.exit(-1);
        }

        int[] key = decode(words, dict);
        // --- Output ---
        words.forEach(word -> System.out.println(Arrays.toString(word) + " -> " + decodeWord(word, key)));
        System.out.println("Key: " + Arrays.toString(key) + " -> " + new String(key, 0, key.length));
    }

    /**
     * Decode encrypted <code>words</code> by guessing a potential plaintext from <code>dict</code>
     * @param words
     * @param dict
     * @return
     */
    private static int[] decode(List<Integer[]> words, List<String> dict) {
        Integer[] guess = words.remove(0);
        for (String word : dict) {
            // Guess first word and derive key
            int[] key = {guess[0] ^ word.charAt(0),
                         guess[1] ^ word.charAt(1),
                         guess[2] ^ word.charAt(2),
                         guess[3] ^ word.charAt(3)};
            if (areWords(words, dict, key)) {
                words.add(0, guess);
                return key;
            }
        }
        throw new RuntimeException("No key found");
    }

    /**
     * Are the encrypted <code>words</code> actual words according to the <code>dict</code> when decoded with <code>key</code>?
     * @param words List of encrypted ciphertexts
     * @param dict List of words providing a unilingual dictionary
     * @param key
     * @return
     */
    private static boolean areWords(List<Integer[]> words, List<String> dict, int[] key) {
        for (Integer[] cipher : words) {
            if (!dict.contains(decodeWord(cipher, key))) return false;
        }
        return true;
    }

    /**
     * Decodes an encrypted <code>cipher</code> with <code>key</code>
     * @param cipher
     * @param key
     * @return
     */
    private static String decodeWord(Integer[] cipher, int[] key) {
        char[] plain = {(char) (cipher[0] ^ key[0]),
                        (char) (cipher[1] ^ key[1]),
                        (char) (cipher[2] ^ key[2]),
                        (char) (cipher[3] ^ key[3])};
        return new String(plain);
    }
}
