/**
 * Hunter Class<br /><br />
 * This class represents the treasure hunter character (the player) in the Treasure Hunt game.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Hunter {
    //instance variables
    private String hunterName;
    private String[] kit;
    private String[] treasures;
    private int gold;

    /**
     * The base constructor of a Hunter assigns the name to the hunter and an empty kit.
     *
     * @param hunterName The hunter's name.
     * @param startingGold The gold the hunter starts with.
     */
    public Hunter(String hunterName, int startingGold, boolean testMode) {
        this.hunterName = hunterName;
        if (!testMode) {
            if (TreasureHunter.returnDifficulty().equals("samurai")) {
                kit = new String[8];
            }
            else {
                kit = new String[7];
            }; // only 7 possible items can be stored in kit
        } else {
            kit = new String[]{"water", "rope", "machete", "horse", "boat", "boots", "shovel"};
        }
        treasures = new String[3];
        gold = startingGold;
    }

    public Hunter(String hunterName, int startingGold) {
        this.hunterName = hunterName;
        kit = new String[7]; // only 5 possible items can be stored in kit
        treasures = new String[3];
        gold = startingGold;
    }

    //Accessors
    public String getHunterName() {
        return hunterName;
    }

    public String[] getTreasures() {
        return treasures;
    }

    public int getGold() {
        return gold;
    }

    /**
     * Updates the amount of gold the hunter has.
     *
     * @param modifier Amount to modify gold by.
     */
    public void changeGold(int modifier) {
        gold += modifier;
        if (gold < 0) {
            gold = 0;
        }
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    /**
     * Buys an item from a shop.
     *
     * @param item The item the hunter is buying.
     * @param costOfItem The cost of the item.
     * @return true if the item is successfully bought.
     */

    public boolean buyItem(String item, int costOfItem) {
        if (costOfItem == 0&&!item.equals("sword") || gold < costOfItem&&!hasItemInKit("sword") || hasItemInKit(item)) {
            return false;
        }
        if (!hasItemInKit("sword")) {
            gold -= costOfItem;
        } else {
            int message = (int) (Math.random() * 3) + 1;
            if (message == 1) {
                System.out.println("The shopkeeper saw your sword and got scared... you got the " + Colors.PURPLE + item + Colors.RESET + " for free.");
            }
            else if (message == 2) {
                System.out.println("You gained the shopkeeper's respect after he saw that sharp thing... you got the " + Colors.PURPLE + item + Colors.RESET + " for free.");
            } else {
                System.out.println("The shopkeeper was happy after seeing your hand... you got the " + Colors.PURPLE + item + Colors.RESET + " for free.");
            }
        }
        addItem(item);
        return true;
    }

    /**
     * The Hunter is selling an item to a shop for gold.<p>
     * This method checks to make sure that the seller has the item and that the seller is getting more than 0 gold.
     *
     * @param item The item being sold.
     * @param buyBackPrice the amount of gold earned from selling the item
     * @return true if the item was successfully sold.
     */
    public boolean sellItem(String item, int buyBackPrice) {
        if (buyBackPrice <= 0&&!item.equals("sword")|| !hasItemInKit(item)) {
            return false;
        }
        gold += buyBackPrice;
        removeItemFromKit(item);
        return true;
    }

    /**
     * Removes an item from the kit by setting the index of the item to null.
     *
     * @param item The item to be removed.
     */
    public void removeItemFromKit(String item) {
        int itmIdx = findItemInKit(item);

        // if item is found
        if (itmIdx >= 0) {
            kit[itmIdx] = null;
        }
    }

    /**
     * Checks to make sure that the item is not already in the kit.
     * If not, it assigns the item to an index in the kit with a null value ("empty" position).
     *
     * @param item The item to be added to the kit.
     * @return true if the item is not in the kit and has been added.
     */
    private boolean addItem(String item) {
        if (!hasItemInKit(item)) {
            int idx = emptyPositionInKit();
            kit[idx] = item;
            return true;
        }
        return false;
    }

    public void addTreasure(int idx, String item) {
        treasures[idx] = item;
    }

    /**
     * Checks if the kit Array has the specified item.
     *
     * @param item The search item
     * @return true if the item is found.
     */
    public boolean hasItemInKit(String item) {
        for (String tmpItem : kit) {
            if (item.equals(tmpItem)) {
                // early return
                return true;
            }
        }
        return false;
    }

    public boolean hasItemInTreasures(String item) {
        for (String tmpItem : treasures) {
            if (item.equals(tmpItem)) {
                // early return
                return true;
            }
        }
        return false;
    }

     /**
     * Returns a printable representation of the inventory, which
     * is a list of the items in kit, with a space between each item.
     *
     * @return The printable String representation of the inventory.
     */
    public String getInventory() {
        String printableKit = "";
        String space = " ";

        for (String item : kit) {
            if (item != null) {
                printableKit += Colors.PURPLE + item + Colors.RESET + space;
            }
        }
        return printableKit;
    }

    public String getTreasuresInventory() {
        String printableKit = "";
        String space = " ";

        for (int i = 0; i<treasures.length-1; i++) {
            if (treasures[i] != null) {
                printableKit += Colors.GREEN + "a " +  treasures[i] + Colors.RESET + "," + space;
            }
        }
        if (treasures[treasures.length-1]!=null) {
            printableKit += Colors.GREEN + "and a " + treasures[treasures.length - 1] + Colors.RESET;
        } else {
            printableKit = printableKit.substring(0, printableKit.length()-2);
        }
        return printableKit;
    }

    /**
     * @return A string representation of the hunter.
     */
    public String infoString() {
        String str = hunterName + " has " + gold + Colors.YELLOW + " gold" + Colors.RESET;
        if (!kitIsEmpty()) {
            str += " and " + getInventory();
        }
        str += "\nTreasures found: ";
        if (treasuresIsEmpty()) {
            str += "none";
        } else {
            str += getTreasuresInventory();
        }
        return str;
    }

    /**
     * Searches kit Array for the index of the specified value.
     *
     * @param item String to look for.
     * @return The index of the item, or -1 if not found.
     */
    private int findItemInKit(String item) {
        for (int i = 0; i < kit.length; i++) {
            String tmpItem = kit[i];

            if (item.equals(tmpItem)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if the kit is empty - meaning all elements are null.
     *
     * @return true if kit is completely empty.
     */
    private boolean kitIsEmpty() {
        for (String string : kit) {
            if (string != null) {
                return false;
            }
        }
        return true;
    }

    private boolean treasuresIsEmpty() {
        for (String string : treasures) {
            if (string != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Finds the first index where there is a null value.
     *
     * @return index of empty index, or -1 if not found.
     */
    private int emptyPositionInKit() {
        for (int i = 0; i < kit.length; i++) {
            if (kit[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public int emptyPositionInTreasures() {
        for (int i = 0; i < treasures.length; i++) {
            if (treasures[i] == null) {
                return i;
            }
        }
        return -1;
    }

}