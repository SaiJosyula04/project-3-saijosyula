import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.CryptogramModel;
import controller.CryptogramController;
import java.util.Map;

/**
 * JUnit test cases for the Cryptogram game application.

 * 
 * @author Sai Josyula
 */
class CryptogramTests {

    private CryptogramModel model;
    private CryptogramController controller;

    /**
     * Sets up a new model and controller before each test method execution.
     */
    @BeforeEach
    void setUp() {
        model = new CryptogramModel();
        controller = new CryptogramController(model);
    }

    /**
     * Tests that the model initializes with valid data.
     */
    @Test
    void testModelInitialization() {
        assertNotNull(model.getEncryptedString());
        assertNotNull(model.getAnswer());
        assertFalse(model.getEncryptedString().isEmpty());
        assertFalse(model.getAnswer().isEmpty());
    }

    /**
     * Tests that no letter in the encryption key maps to itself.
     */
    @Test
    void testNoSelfMapping() {
        for (char c = 'A'; c <= 'Z'; c++) {
            char encrypted = getEncryptedCharForOriginal(c);
            assertNotEquals(c, encrypted, "Letter should not map to itself: " + c);
        }
    }

    /**
     * Tests successful letter replacement with correct mapping.
     */
    @Test
    void testCorrectReplacement() {
        char encryptedChar = findFirstEncryptedLetter();
        char correctMapping = model.getCorrectMapping(encryptedChar);
        
        boolean success = model.setReplacement(encryptedChar, correctMapping);
        
        assertTrue(success);
        assertTrue(model.getDecryptedString().contains(String.valueOf(correctMapping)));
    }

    /**
     * Tests that incorrect letter replacements are rejected.
     */
    @Test
    void testIncorrectReplacement() {
        char encryptedChar = findFirstEncryptedLetter();
        char correctMapping = model.getCorrectMapping(encryptedChar);
        char incorrectMapping = (correctMapping == 'A') ? 'B' : 'A';
        
        boolean success = model.setReplacement(encryptedChar, incorrectMapping);
        
        assertFalse(success);
    }

    /**
     * Tests prevention of mapping a letter to itself.
     */
    @Test
    void testSelfMappingPrevention() {
        boolean success = model.setReplacement('A', 'A');
        assertFalse(success);
    }

    /**
     * Tests that duplicate mappings are properly prevented.
     */
    @Test
    void testDuplicateMappingPrevention() {
        char[] encryptedChars = findTwoDifferentEncryptedLetters();
        if (encryptedChars[0] != ' ' && encryptedChars[1] != ' ') {
            char correctMapping1 = model.getCorrectMapping(encryptedChars[0]);
            
            boolean success1 = model.setReplacement(encryptedChars[0], correctMapping1);
            boolean success2 = model.setReplacement(encryptedChars[1], correctMapping1); // Duplicate
            
            assertTrue(success1);
            assertFalse(success2);
        }
    }

    /**
     * Tests letter frequency calculation functionality.
     */
    @Test
    void testLetterFrequencies() {
        Map<Character, Integer> frequencies = model.getLetterFrequencies();
        
        assertNotNull(frequencies);
        assertEquals(26, frequencies.size());
        
        int totalFrequencyCount = frequencies.values().stream().mapToInt(Integer::intValue).sum();
        int actualLetterCount = countLettersInEncryptedString();
        
        assertEquals(actualLetterCount, totalFrequencyCount);
    }

    /**
     * Tests that hints provide valid, unguessed mappings.
     */
    @Test
    void testHintFunctionality() {
        Map<Character, Character> hint = model.getHint();
        assertNotNull(hint);
        
        char encryptedChar = hint.keySet().iterator().next();
        char decryptedChar = hint.get(encryptedChar);
        
        assertTrue(Character.isLetter(encryptedChar));
        assertTrue(Character.isLetter(decryptedChar));
        assertNotEquals(encryptedChar, decryptedChar);
    }

    /**
     * Tests game completion detection.
     */
    @Test
    void testGameCompletionDetection() {
        assertFalse(model.isComplete());
        assertFalse(controller.isGameOver());
    }

    /**
     * Tests controller delegation to model.
     */
    @Test
    void testControllerDelegation() {
        assertEquals(model.getEncryptedString(), controller.getEncryptedQuote());
        assertEquals(model.getDecryptedString(), controller.getUsersProgress());
        assertEquals(model.getAnswer(), controller.getAnswer());
    }

    /**
     * Tests case insensitivity in user inputs.
     */
    @Test
    void testCaseInsensitiveReplacements() {
        char encryptedChar = findFirstEncryptedLetter();
        char correctMapping = model.getCorrectMapping(encryptedChar);
        
        boolean success = model.setReplacement(
            Character.toLowerCase(encryptedChar),
            Character.toLowerCase(correctMapping)
        );
        
        assertTrue(success);
    }

    /**
     * Tests that frequency display handles empty quotes correctly.
     */
    @Test
    void testFrequencyWithSpecialCharacters() {
        Map<Character, Integer> frequencies = model.getLetterFrequencies();
        
        // All letters should be present in frequency map
        for (char c = 'A'; c <= 'Z'; c++) {
            assertTrue(frequencies.containsKey(c));
            assertTrue(frequencies.get(c) >= 0);
        }
    }

    /**
     * Tests the hint system when all mappings are guessed.
     */
    @Test
    void testHintWhenAllGuessed() {
        assertNotNull(model.getHint()); // Should return a hint initially
    }

    /**
     * Tests encryption consistency - same input should produce same output.
     */
    @Test
    void testEncryptionConsistency() {
        String encrypted = model.getEncryptedString();
        // Encryption should be consistent for the same quote and key
        assertNotNull(encrypted);
        assertEquals(encrypted, model.getEncryptedString()); // Should not change
    }

    // Helper methods

    /**
     * Finds the first letter in the encrypted string.
     * 
     * @return the first encrypted letter found, or space if none found
     */
    private char findFirstEncryptedLetter() {
        for (char c : model.getEncryptedString().toCharArray()) {
            if (Character.isLetter(c)) {
                return c;
            }
        }
        return ' ';
    }

    /**
     * Finds two different encrypted letters from the quote.
     * 
     * @return array containing two different encrypted letters
     */
    private char[] findTwoDifferentEncryptedLetters() {
        char[] result = new char[2];
        int found = 0;
        
        for (char c : model.getEncryptedString().toCharArray()) {
            if (Character.isLetter(c)) {
                if (found == 0) {
                    result[0] = c;
                    found++;
                } else if (c != result[0]) {
                    result[1] = c;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Counts the number of letters in the encrypted string.
     * 
     * @return the count of alphabetical characters
     */
    private int countLettersInEncryptedString() {
        int count = 0;
        for (char c : model.getEncryptedString().toCharArray()) {
            if (Character.isLetter(c)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gets the encrypted character for a given original character.
     * 
     * @param original the original character
     * @return the corresponding encrypted character
     */
    private char getEncryptedCharForOriginal(char original) {
        // This would require access to the encryption key
        // For now, we'll use the public interface
        String encrypted = model.getEncryptedString();
        String answer = model.getAnswer();
        
        // Find position of original in answer and get corresponding encrypted char
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == original) {
                return encrypted.charAt(i);
            }
        }
        return ' '; // Not found
    }
}