package textgame;

/**
 *
 * @author Max
 */
public class Player {
    private String playerName = "";
    private double playerHealth = 100;
    private double playerMaxHealth = 100;
    private double playerDamage = 20;
    private double playerXp = 0;
    private int playerLevel = 1;
    private double xpRequired = 100;
    private int potions = 3;
    
    public Player() {
    }
    
    public Player(String playerName) {
        this.playerName = playerName;
    }
    
    public Player(String playerName, double playerHealth, double playerMaxHealth, double playerDamage, double playerXp, int playerLevel) {
        this.playerName = playerName;
        this.playerHealth = playerHealth;
        this.playerMaxHealth = playerMaxHealth;
        this.playerDamage = playerDamage;
        this.playerXp = playerXp;
        this.playerLevel = playerLevel;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public double getPlayerHealth() {
        return playerHealth;
    }

    public void setPlayerHealth(double playerHealth) {
        this.playerHealth = playerHealth;
    }

    public double getPlayerMaxHealth() {
        return playerMaxHealth;
    }

    public void setPlayerMaxHealth(double playerMaxHealth) {
        this.playerMaxHealth = playerMaxHealth;
    }

    public double getPlayerDamage() {
        return playerDamage;
    }

    public void setPlayerDamage(double playerDamage) {
        this.playerDamage = playerDamage;
    }

    public double getPlayerXp() {
        return playerXp;
    }

    public void setPlayerXp(double playerXp) {
        this.playerXp = playerXp;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }
    
    public double getXpRequired() {
        return this.xpRequired;
    }
    
    public void setXpRequired() {
        this.xpRequired = playerLevel * 100;
    }

    public int getPotions() {
        return potions;
    }

    public void setPotions(int potions) {
        this.potions = potions;
    }
    
    public double getMissingHealth() {
        return this.playerMaxHealth - this.playerHealth;
    }

    @Override
    public String toString() {
        return "\n---------------------------"
               + "\n" + playerName + "'s Stats"
               + "\n---------------------------"
               + "\nHealth: " + playerHealth + "/" + playerMaxHealth
               + "\nDamage: " + playerDamage
               + "\nPotions: " + potions
               + "\nLevel: " + playerLevel
               + "\nXp: " + playerXp + "/" + xpRequired
               + "\n---------------------------";
    }
    
}// End player
