import java.util.Scanner;
import model.CryptogramModel;
import controller.CryptogramController;

/**
 * The main class for the Cryptogram puzzle game.
 * This class handles the user interface and game flow, coordinating between
 * the controller and model components.0
 * @author Sai Josyula
 */
public class Cryptogram {

    /**
     * The main method that starts the Cryptogram game.
     * It initializes the model and controller, displays the game interface,
     * and processes user commands until the game is completed or exited.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        CryptogramModel model = new CryptogramModel();
        CryptogramController controller = new CryptogramController(model);
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Welcome to the Cryptogram Puzzle!");
        System.out.println("Try to decrypt the following quote:\n");
        
        displayFormattedQuote(controller);
        
        // Main game loop
        while (!controller.isGameOver()) {
            System.out.print("Enter a command (type 'help' to see commands): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Thanks for playing!");
                break;
            } else if (input.equalsIgnoreCase("help")) {
                displayHelp();
            } else if (input.equalsIgnoreCase("freq")) {
                controller.displayFrequencies();
            } else if (input.equalsIgnoreCase("hint")) {
                controller.giveHint();
            } else if (input.contains("replace") || input.contains("=")) {
                processReplacementCommand(input, controller);
            } else {
                System.out.println("Unknown command. Type 'help' for available commands.");
            }
            
            if (controller.isGameOver()) {
                break;
            }
            
            displayFormattedQuote(controller);
        }
        
        // Game over - user won!
        if (controller.isGameOver()) {
            System.out.println("\nCongratulations! You solved the cryptogram!");
            System.out.println("Original quote: " + controller.getAnswer());
        }
        
        scanner.close();
    }
    
    /**
     * Displays the formatted quote and user's progress, ensuring proper line wrapping
     * for quotes longer than 80 characters.
     * 
     * @param controller the game controller containing quote data
     */
    private static void displayFormattedQuote(CryptogramController controller) {
        String encrypted = controller.getEncryptedQuote();
        String progress = controller.getUsersProgress();
        
        System.out.println("\nCurrent Progress:");
        formatAndDisplayStrings(encrypted, progress);
        System.out.println();
    }
    
    /**
     * Formats and displays two strings (encrypted and progress) with proper line wrapping
     * at word boundaries to ensure they align correctly.
     * 
     * @param encrypted the encrypted quote string
     * @param progress the user's current progress string
     */
    private static void formatAndDisplayStrings(String encrypted, String progress) {
        int maxLineLength = 80;
        
        // Split both strings into lines that fit within maxLineLength
        String[] encryptedLines = splitStringByLength(encrypted, maxLineLength);
        String[] progressLines = splitStringByLength(progress, maxLineLength);
        
        // Ensure both arrays have the same number of lines
        int maxLines = Math.max(encryptedLines.length, progressLines.length);
        
        for (int i = 0; i < maxLines; i++) {
            if (i < progressLines.length) {
                System.out.println(progressLines[i]);
            } else {
                System.out.println();
            }
            if (i < encryptedLines.length) {
                System.out.println(encryptedLines[i]);
            } else {
                System.out.println();
            }
            System.out.println();
        }
    }
    
    /**
     * Splits a string into multiple lines, breaking at word boundaries to ensure
     * no line exceeds the specified maximum length.
     * 
     * @param str the string to split
     * @param maxLength the maximum length for each line
     * @return an array of strings, each no longer than maxLength
     */
    private static String[] splitStringByLength(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return new String[]{str};
        }
        
        java.util.ArrayList<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        // Split into words while preserving punctuation and spaces
        String[] words = str.split("(?<=\\s)|(?=\\s)");
        
        for (String word : words) {
            if (currentLine.length() + word.length() <= maxLength) {
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                // If a single word is longer than maxLength, break it
                if (word.length() > maxLength) {
                    int start = 0;
                    while (start < word.length()) {
                        int end = Math.min(start + maxLength, word.length());
                        lines.add(word.substring(start, end));
                        start = end;
                    }
                } else {
                    currentLine.append(word);
                }
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
    
    /**
     * Displays the help menu with available commands.
     */
    private static void displayHelp() {
        System.out.println("\nAvailable commands:");
        System.out.println("replace X by Y  - Replace letter X by letter Y in the solution");
        System.out.println("X = Y           - Shortcut for replace command");
        System.out.println("freq            - Display letter frequencies in the encrypted quote");
        System.out.println("hint            - Display one correct mapping not yet guessed");
        System.out.println("exit            - End the game early");
        System.out.println("help            - Show this help message");
    }
    
    /**
     * Processes replacement commands in various formats.
     * 
     * @param input the user input containing the replacement command
     * @param controller the game controller to execute the replacement
     */
    private static void processReplacementCommand(String input, CryptogramController controller) {
        char letterToReplace = ' ';
        char replacementLetter = ' ';
        
        try {
            if (input.contains("=")) {
                // Handle X = Y format
                String[] parts = input.split("=");
                if (parts.length == 2) {
                    letterToReplace = parts[0].trim().toUpperCase().charAt(0);
                    replacementLetter = parts[1].trim().toUpperCase().charAt(0);
                }
            } else if (input.contains("replace")) {
                // Handle "replace X by Y" format
                String[] parts = input.split("\\s+");
                if (parts.length >= 4) {
                    letterToReplace = parts[1].toUpperCase().charAt(0);
                    replacementLetter = parts[3].toUpperCase().charAt(0);
                }
            }
            
            if (Character.isLetter(letterToReplace) && Character.isLetter(replacementLetter)) {
                boolean success = controller.makeReplacement(letterToReplace, replacementLetter);
                if (!success) {
                    System.out.println("Invalid replacement. Please try again.");
                }
            } else {
                System.out.println("Invalid command format. Use 'replace X by Y' or 'X = Y'");
            }
        } catch (Exception e) {
            System.out.println("Error processing command. Please use format: 'replace X by Y' or 'X = Y'");
        }
    }
}