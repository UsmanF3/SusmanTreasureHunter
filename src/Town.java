/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean gameOver;
    private String treasure;
    private boolean townHunted;
    private boolean townSearched;
    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        treasure = null;
        printMessage = "";
        townSearched = false;
        townHunted = false;
        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTownSearched(boolean townSearched) {
        this.townSearched = townSearched;
    }

    public String getLatestNews() {
        return printMessage;
    }

    public void setPrintMessage(String printMessage) {
        this.printMessage = printMessage;
    }

    public boolean getSearched() {
        return townSearched;
    }
    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        double rnd = Math.random();
        if (rnd < .25) {
            treasure = "Crown";
        } else if (rnd < .5) {
            treasure = "Trophy";
        } else if (rnd < .75) {
            treasure = "Gem";
        } else {
            treasure = "Dust";
        }
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    public boolean isTownHunted() {
        return townHunted;
    }

    public void setTownHunted(boolean townHunted) {
        this.townHunted = townHunted;
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    public Shop getShop() {
        return shop;
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            if (TreasureHunter.returnDifficulty().equals("easy")) {
                noTroubleChance = 0.75;
            } else if (TreasureHunter.returnDifficulty().equals("hard")) {
                noTroubleChance = 0.6;
            } else {
                noTroubleChance = 0.66;
            }
        } else {
            if (TreasureHunter.returnDifficulty().equals("easy")) {
                noTroubleChance = 0.25;
            } else if (TreasureHunter.returnDifficulty().equals("hard")) {
                noTroubleChance = 0.4;
            } else {
                noTroubleChance = 0.33;
            }
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
            if (hunter.hasItemInKit("sword")) {
                int message = (int) (Math.random() * 3) + 1;
                if (message == 1) {
                    printMessage += ("You unsheathe your sword... the brawler is intimidated. He gives you his " + Colors.YELLOW + "gold" + Colors.RESET);
                }
                else if (message == 2) {
                    printMessage += ("The brawler thought this would be easy until he saw what you had... He gave you his" + Colors.YELLOW + " gold" + Colors.RESET + " out of fear");
                }
                else {
                    printMessage += ("The brawler ended up giving you his" + Colors.YELLOW + " gold" + Colors.RESET + ". You really scared him away.");
                }

            }
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance || hunter.hasItemInKit("sword")) {
                if (!hunter.hasItemInKit("sword"))
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + goldDiff + Colors.YELLOW + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                hunter.changeGold(-goldDiff);
                printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                if (hunter.getGold()==0) {
                    printMessage+="\nYou lost and couldn't afford to pay up..." + Colors.RESET;
                    System.out.println(printMessage);
                    System.out.println("\nGame Over!");
                    TreasureHunter.gameOver();
                } else {
                    printMessage += Colors.RESET + "\nYou lost the brawl and pay " + goldDiff + Colors.YELLOW + " gold." + Colors.RESET;
                }
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < (1.0 / 6)) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < (2.0 / 6)) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < 0.5) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < (4.0 / 6)) {
            return new Terrain("Desert", "Water");
        } else if (rnd < (5.0 / 6)){
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        if (TreasureHunter.returnDifficulty().equals("easy")) {
            return false;
        }
        return (rand < 0.5);
    }

    public void dig() {
        int successful = (int) (Math.random() * 2) + 1;
        if (successful == 2) {
            int amount = (int) (Math.random() * 20) + 1;
            printMessage += ("You dug up " + amount + Colors.YELLOW + " gold!" + Colors.RESET);
            hunter.changeGold(amount);
            townSearched = true;
        } else {
            printMessage += ("You dug but only found dirt.");
        }
    }

    public void hunt() {
        if (treasure.equals("Dust")) {
            printMessage += ("You dug up dust...");
        } else {
            if (!hunter.hasItemInTreasures(treasure)) {
                printMessage += ("You got a " + treasure + "!");
                int treasureIdx = hunter.emptyPositionInTreasures();
                hunter.addTreasure(treasureIdx, treasure);
                if (treasureIdx==hunter.getTreasures().length-1) {
                    System.out.println("Congratulations, you have found the" + Colors.PURPLE + " last " + Colors.RESET + "of the " + Colors.YELLOW + "three treasures. " + Colors.GREEN + "You win!" + Colors.RESET);
                    TreasureHunter.gameOver();
                } else {
                    //nothing
                }
            } else {
                printMessage += ("\nYou found a " + treasure + "! However, you already own one of these..");
            }
        }
    }

}