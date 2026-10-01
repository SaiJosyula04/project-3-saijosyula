package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The model class for the Cryptogram game that handles game logic,
 * encryption, and state management.
 * 
 * @author Sai Josyula
 */
public class CryptogramModel {
    private String answer;
    private String encryptedString;
    private Map<Character, Character> encryptionKey;
    private Map<Character, Character> decryptionAttempt;
    private Map<Character, Character> reverseDecryption;
    
    /**
     * Constructs a new CryptogramModel by loading a random quote from quotes.txt
     * and generating an encryption key that ensures no letter maps to itself.
     */
    public CryptogramModel() { 
        this.encryptionKey = new ArrayMap<>();
        this.decryptionAttempt = new ArrayMap<>();
        this.reverseDecryption = new ArrayMap<>();
        
        try {
            List<String> quotes = loadQuotes();
            Random random = new Random();
            this.answer = quotes.get(random.nextInt(quotes.size()));
            
            generateEncryptionKey();
            encryptQuote();
            
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Using fallback quote instead.");
            useFallbackQuote();
        }
    }
    
    /**
     * Loads quotes from the quotes.txt file.
     * 
     * @return a list of quotes loaded from the file
     * @throws IOException if the file cannot be read or is empty
     */
    private List<String> loadQuotes() throws IOException {
        List<String> quotes = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("quotes.txt"));
        String line;
        
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                quotes.add(line.toUpperCase());
            }
        }
        reader.close();
        
        if (quotes.isEmpty()) {
            throw new IOException("quotes.txt is empty or contains only empty lines");
        }
        
        return quotes;
    }
    
    /**
     * Generates an encryption key that ensures no letter maps to itself.
     * Uses a random shuffle with validation to prevent self-mapping.
     */
    private void generateEncryptionKey() {
        List<Character> letters = new ArrayList<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            letters.add(c);
        }
        
        // Generate mapping ensuring no letter maps to itself
        boolean validMapping = false;
        while (!validMapping) {
            Collections.shuffle(letters);
            validMapping = true;
            
            // Check if any letter maps to itself
            for (int i = 0; i < 26; i++) {
                char original = (char) ('A' + i);
                char encrypted = letters.get(i);
                if (original == encrypted) {
                    validMapping = false;
                    break;
                }
            }
        }
        
        // Create the encryption key
        for (int i = 0; i < 26; i++) {
            char original = (char) ('A' + i);
            char encrypted = letters.get(i);
            encryptionKey.put(original, encrypted);
        }
    }
    
    /**
     * Encrypts the answer quote using the generated encryption key.
     */
    private void encryptQuote() {
        StringBuilder encryptedBuilder = new StringBuilder();
        for (char c : answer.toCharArray()) {
            if (Character.isLetter(c)) {
                encryptedBuilder.append(encryptionKey.get(c));
            } else {
                encryptedBuilder.append(c);
            }
        }
        encryptedString = encryptedBuilder.toString();
    }
    
    /**
     * Sets up a fallback quote when file reading fails.
     */
    private void useFallbackQuote() {
        this.answer = "TALK IS CHEAP. SHOW ME THE CODE. - LINUS TORVALDS";
        
        // Create encryption key with no self-mapping for fallback
        String originalLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String encryptedLetters = "JWSIZNKRBWPYTHXLFMCGODUEVQ";
        
        for (int i = 0; i < originalLetters.length(); i++) {
            char orig = originalLetters.charAt(i);
            char enc = encryptedLetters.charAt(i);
            if (orig != enc) { // Ensure no self-mapping
                encryptionKey.put(orig, enc);
            }
        }
        
        encryptQuote();
    }
    
    /**
     * Attempts to set a replacement mapping for decryption.
     * 
     * @param encryptedChar the encrypted letter to replace
     * @param replacementChar the proposed decryption letter
     * @return true if the replacement was successful, false otherwise
     */
    public boolean setReplacement(char encryptedChar, char replacementChar) {
        encryptedChar = Character.toUpperCase(encryptedChar);
        replacementChar = Character.toUpperCase(replacementChar);
        
        // Validate input
        if (!Character.isLetter(encryptedChar) || !Character.isLetter(replacementChar)) {
            System.out.println("Error: Both arguments must be letters");
            return false;
        }
        
        // Prevent self-mapping
        if (encryptedChar == replacementChar) {
            System.out.println("Error: Cannot map a letter to itself");
            return false;
        }
        
        // Check if encrypted letter exists in puzzle
        if (!encryptedString.contains(String.valueOf(encryptedChar))) {
            System.out.println("Error: '" + encryptedChar + "' does not appear in the encrypted quote");
            return false;
        }
        
        // Check for duplicate mappings
        if (decryptionAttempt.containsKey(encryptedChar)) {
            char currentMapping = decryptionAttempt.get(encryptedChar);
            if (currentMapping == replacementChar) {
                return true; // Already correctly mapped
            }
            System.out.println("Error: '" + encryptedChar + "' is already mapped to '" + currentMapping + "'");
            return false;
        }
        
        if (reverseDecryption.containsKey(replacementChar)) {
            char currentlyMappedTo = reverseDecryption.get(replacementChar);
            System.out.println("Error: '" + replacementChar + "' is already used for '" + currentlyMappedTo + "'");
            return false;
        }
        
        // Check if the guess is correct
        char correctMapping = getCorrectMapping(encryptedChar);
        if (replacementChar == correctMapping) {
            decryptionAttempt.put(encryptedChar, replacementChar);
            reverseDecryption.put(replacementChar, encryptedChar);
            System.out.println("Correct! '" + encryptedChar + "' -> '" + replacementChar + "'");
            return true;
        } else {
            System.out.println("Incorrect guess! '" + encryptedChar + "' should map to a different letter");
            return false;
        }
    }

    /**
     * Gets the encrypted quote string.
     * 
     * @return the encrypted version of the quote
     */
    public String getEncryptedString() {
        return encryptedString;
    }

    /**
     * Gets the user's current decryption progress.
     * 
     * @return a string showing guessed letters and blanks for unknowns
     */
    public String getDecryptedString() {
        StringBuilder decryptedBuilder = new StringBuilder();
        for (char c : encryptedString.toCharArray()) {
            if (Character.isLetter(c) && decryptionAttempt.containsKey(c)) {
                decryptedBuilder.append(decryptionAttempt.get(c));
            } else if (Character.isLetter(c)) {
                decryptedBuilder.append('_');
            } else {
                decryptedBuilder.append(c);
            }
        }
        return decryptedBuilder.toString();
    }

    /**
     * Gets the original answer quote.
     * 
     * @return the unencrypted original quote
     */
    public String getAnswer() {
        return answer;
    }
    
    /**
     * Checks if the puzzle has been completely solved.
     * 
     * @return true if all letters have been correctly guessed
     */
    public boolean isComplete() {
        return getDecryptedString().equals(answer);
    }
    
    /**
     * Gets the correct mapping for an encrypted character.
     * 
     * @param encryptedChar the encrypted character
     * @return the correct decryption character, or space if not found
     */
    public char getCorrectMapping(char encryptedChar) {
        for (char original : encryptionKey.keySet()) {
            if (encryptionKey.get(original) == encryptedChar) {
                return original;
            }
        }
        return ' ';
    }
    
    /**
     * Calculates the frequency of each letter in the encrypted quote.
     * 
     * @return a map containing each letter and its frequency count
     */
    public Map<Character, Integer> getLetterFrequencies() {
        Map<Character, Integer> frequencies = new ArrayMap<>();
        
        // Initialize all letters to 0
        for (char c = 'A'; c <= 'Z'; c++) {
            frequencies.put(c, 0);
        }
        
        // Count occurrences in encrypted string
        for (char c : encryptedString.toCharArray()) {
            if (Character.isLetter(c)) {
                frequencies.put(c, frequencies.get(c) + 1);
            }
        }
        
        return frequencies;
    }
    
    /**
     * Provides a hint by revealing one correct mapping not yet guessed.
     * 
     * @return a map containing one hint mapping, or null if all are guessed
     */
    public Map<Character, Character> getHint() {
        for (char encryptedChar : encryptionKey.values()) {
            if (!decryptionAttempt.containsKey(encryptedChar)) {
                char correctMapping = getCorrectMapping(encryptedChar);
                Map<Character, Character> hint = new ArrayMap<>();
                hint.put(encryptedChar, correctMapping);
                return hint;
            }
        }
        return null; // All mappings guessed
    }
}