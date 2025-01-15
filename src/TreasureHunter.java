import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private static String mode = "";
    private static boolean hardMode;
    private static boolean easyMode;
    private static boolean isTest;
    private static boolean gameOver = false;
    private boolean searched;
    private boolean townHunted;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        isTest = false;
        townHunted = false;
        searched = false;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        System.out.println("Welcome to " + Colors.CYAN + "TREASURE HUNTER" + Colors.RESET + "!");
        System.out.println("Going hunting for the big treasure, eh?");
        System.out.print("What's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();
        boolean repeat = true;
        while (repeat) {
            System.out.print("Which mode? (\"e\", \"n\", or \"h\".): ");
            mode = SCANNER.nextLine().toLowerCase();
            if (mode.equals("h")) {
                hardMode = true;
                hunter = new Hunter(name, 20);
                repeat = false;
            } else if (mode.equals("e")) {
                easyMode = true;
                hunter = new Hunter(name, 40);
                repeat = false;
            }
            else if (mode.equals("test")) {
                isTest = true;
                hunter = new Hunter(name, 0, isTest);
                repeat = false;
            }
            else if (mode.equals("n")) {
                // set hunter instance variable
                hunter = new Hunter(name, 20);
                repeat = false;
            }
            else {
                System.out.println("Sorry, that is not an option");
            }
        }
    }

    public static String returnDifficulty() {
        if (mode.equals("h")) {
            return "hard";
        } else if (mode.equals("e")) {
            return "easy";
        }
        else if (mode.equals("test")) {
            return "normal";
        }
        else if (mode.equals("n")) {
            return "normal";
        }
        else {
            return " ";
        }
    }
    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        }
        else if (easyMode) {
            markdown = 1;
            toughness = 0.25;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";
        while (!choice.equals("x")&&!gameOver) {
            System.out.println();
            System.out.println(currentTown.getLatestNews());
            System.out.println("***");
            System.out.println(hunter.infoString());
            System.out.println(currentTown.infoString());
            System.out.println(Colors.GREEN + "(B)" + Colors.RESET + "uy something at the shop.");
            System.out.println(Colors.GREEN + "(S)" + Colors.RESET + "ell something at the shop.");
            System.out.println(Colors.GREEN + "(E)" + Colors.RESET + "xplore surrounding terrain.");
            System.out.println(Colors.GREEN + "(M)" + Colors.RESET + "ove on to a different town.");
            System.out.println(Colors.GREEN + "(L)" + Colors.RESET + "ook for trouble!");
            System.out.println(Colors.GREEN + "(H)" + Colors.RESET + "unt for treasure.");
            System.out.println(Colors.GREEN + "(D)" + Colors.RESET + "ig for gold.");
            System.out.println("Give up the hunt and e" + Colors.GREEN + "(X)" + Colors.RESET + "it.");
            System.out.println();
            System.out.print("What's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("e")) {
            System.out.println(currentTown.getTerrain().infoString());
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown()) {
                // This town is going away so print its news ahead of time.
                System.out.println(currentTown.getLatestNews());
                enterTown();
            }
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble();
        } else if (choice.equals("x")) {
            System.out.println("Fare thee well, " + hunter.getHunterName() + "!");
        } else if (choice.equals("d")) {
            if(!hunter.hasItemInKit("shovel")) {
                System.out.println("You can't dig for gold without a shovel");
            } else {
                if (!currentTown.getSearched()) {
                    currentTown.dig();
                    searched = true;
                } else {
                    System.out.println("Sorry, you already dug here.");
                }
            }
        } else if (choice.equals("h")){
            if (!currentTown.isTownHunted()) {
                currentTown.hunt();
                currentTown.setTownHunted(true);
            } else {
                System.out.println("You have already searched this town!");
            }
        } else {
            System.out.println("Yikes! That's an invalid option! Try again.");
        }
    }

    public static void gameOver() {
        System.out.println("\nGame Over!");
        gameOver = true;
    }

}