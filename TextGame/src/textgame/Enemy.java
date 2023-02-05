package textgame;

/**
 *
 * @author Max
 */
public class Enemy {

    private String enemyName;
    private double enemyHealth = 50;
    private double enemyMaxHealth = 50;
    private double enemyDamage = 20;
    private int enemyLevel = 1;

    public Enemy() {
    }

    public Enemy(String enemyName) {
        this.enemyName = enemyName;
    }

    public Enemy(String enemyName, double enemyHealth, double enemyMaxHealth, double enemyDamage, int enemyLevel) {
        this.enemyName = enemyName;
        this.enemyHealth = enemyHealth;
        this.enemyMaxHealth = enemyHealth;
        this.enemyDamage = enemyDamage;
        this.enemyLevel = enemyLevel;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
    }

    public double getEnemyHealth() {
        return enemyHealth;
    }

    public void setEnemyHealth(double enemyHealth) {
        this.enemyHealth = enemyHealth;
    }

    public double getEnemyMaxHealth() {
        return enemyMaxHealth;
    }

    public void setEnemyMaxHealth(double enemyMaxHealth) {
        this.enemyMaxHealth = enemyMaxHealth;
    }

    public double getEnemyDamage() {
        return enemyDamage;
    }

    public void setEnemyDamage(double enemyDamage) {
        this.enemyDamage = enemyDamage;
    }

    public int getEnemyLevel() {
        return enemyLevel;
    }

    public void setEnemyLevel(int enemyLevel) {
        this.enemyLevel = enemyLevel;
    }

    @Override
    public String toString() {
        return "\nEnemy: " + enemyName
                + "\nLevel " + enemyLevel
                + "\nHealth: " + enemyHealth + "/" + enemyMaxHealth;
    }

}
