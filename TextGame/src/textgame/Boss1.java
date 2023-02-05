package textgame;

/**
 *
 * @author Max
 */
public class Boss1 {
    private String enemyName = "Boss1";
    private int enemyHealth = 1000;
    private int enemyDamage = 500;
    private int enemyLevel = 30;
    
    public Boss1() {
    }
    
    public Boss1(String enemyName, int enemyHealth, int enemyDamage, int enemyLevel) {
        this.enemyName = enemyName;
        this.enemyHealth = enemyHealth;
        this.enemyDamage = enemyDamage;
        this.enemyLevel = enemyLevel;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public void setEnemyName(String enemyName) {
        this.enemyName = enemyName;
    }

    public int getEnemyHealth() {
        return enemyHealth;
    }

    public void setEnemyHealth(int enemyHealth) {
        this.enemyHealth = enemyHealth;
    }

    public int getEnemyDamage() {
        return enemyDamage;
    }

    public void setEnemyDamage(int enemyDamage) {
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
        return "Enemy stats" 
               + "\nName: " + enemyName
               + "\nHealth: " + enemyHealth
               + "\nMax attack damage: ???\n"
               + "\nLevel: " + enemyLevel + '}';
    }
    
}
