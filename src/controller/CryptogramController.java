package controller;

import model.CryptogramModel;
import java.util.Map;

/**
 * The controller class for the Cryptogram game that mediates between
 * the view (UI) and the model (game logic and data).
 * 
 * @author Sai Josyula
 */
public class CryptogramController {
    private CryptogramModel model;
    
    /**
     * Constructs a CryptogramController with the specified model.
     * 
     * @param model the CryptogramModel instance to control
     */
    public CryptogramController(CryptogramModel model) {
        this.model = model;
    }
    
    /**
     * Checks if the game is over (all letters have been correctly guessed).
     * 
     * @return true if the game is complete, false otherwise
     */
    public boolean isGameOver() { 
        return model.isComplete();
    }
    
    /**
     * Attempts to make a letter replacement in the cryptogram.
     * 
     * @param letterToReplace the encrypted letter to replace
     * @param replacementLetter the proposed decryption letter
     * @return true if the replacement was successful, false otherwise
     */
    public boolean makeReplacement(char letterToReplace, char replacementLetter) { 
        return model.setReplacement(letterToReplace, replacementLetter);
    }
    
    /**
     * Gets the encrypted quote string.
     * 
     * @return the encrypted version of the quote
     */
    public String getEncryptedQuote() { 
        return model.getEncryptedString();
    }
    
    /**
     * Gets the user's current progress in decrypting the quote.
     * 
     * @return a string showing correctly guessed letters and blanks for unknowns
     */
    public String getUsersProgress() { 
        return model.getDecryptedString();
    }
    
    /**
     * Gets the original answer (unencrypted quote).
     * 
     * @return the original quote
     */
    public String getAnswer() {
        return model.getAnswer();
    }
    
    /**
     * Displays the frequency of each letter in the encrypted quote.
     * Formats the output with 7 letters per line for 4 lines.
     */
    public void displayFrequencies() {
        Map<Character, Integer> frequencies = model.getLetterFrequencies();
        System.out.println("\nLetter frequencies in encrypted quote:");
        
        int count = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            int freq = frequencies.getOrDefault(c, 0);
            System.out.print(c + ": " + freq + " ");
            count++;
            
            if (count % 7 == 0) {
                System.out.println();
            }
        }
        System.out.println();
    }
    
    /**
     * Provides a hint by revealing one correct mapping that hasn't been guessed yet.
     */
    public void giveHint() {
        Map<Character, Character> hint = model.getHint();
        if (hint != null) {
            char encrypted = hint.keySet().iterator().next();
            char decrypted = hint.get(encrypted);
            System.out.println("Hint: '" + encrypted + "' maps to '" + decrypted + "'");
        } else {
            System.out.println("No hints available - you've guessed all letters!");
        }
    }
}