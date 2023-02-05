package textgame;

/*
 * Incomplete free time project
 * Game ideas here:
 * Text-based adventure game basics         - done
 * XP mechanic                              - done
 * Save all stats with I/O or database      - done
 * Other stats? (heal power, mana, crit)
 * UI
 * Boss enemies
 */
import java.util.Random;
import java.util.Scanner;
import java.io.*;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;
import java.sql.*;

public class GameClient {

    static Random rand = new Random();
    static String[] weakEnemyList = {"enemy1", "enemy2", "enemy3"};

    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        
        String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        String DB_URL = "jdbc:mysql://192.168.1.72/";
        
        
        Player player1 = new Player(); // Make a player object
        // int playerId;
        int[] fightChoiceList = {1, 2, 3};

        String dbUser = args[0];   // The name of the MySQL account to use.
        String passWord = args[1];   // The password for the MySQL account.

        // Initialize these here so they are in scope for closing in the finally block.
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        CallableStatement cs = null;
        
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(GameInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(GameInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(GameInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(GameInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new GameInterface().setVisible(true);
//            }
//        });

        try {
            conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
            stmt = conn.createStatement();

            String input = "";
            String name = "";
            Boolean playerIsLoaded = false; // check if player made or loaded a character

            System.out.println("\n---------------------"
                    + "\n       Sign in      "
                    + "\n---------------------");
            
            GET_CHARACTER:
            while (playerIsLoaded == false) {
                while (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
                    System.out.print("Do you have an existing character? (y/n) (-1 to quit): ");
                    input = console.nextLine();
                    if (input.equals("-1")) {
                        System.exit(0);
                    }
                }
                // if player has a character
                if (input.equalsIgnoreCase("y")) {
                    while (playerIsLoaded == false) {

                        // while name is blank
                        while (name.length() < 1) {
                            System.out.print("Enter character name, or -1 to go back: ");
                            name = console.nextLine();

                            // go back to GET_CHARACTER
                            if (name.equals("-1")) {
                                input = "";
                                name = "";
                                continue GET_CHARACTER;
                            }

                            /* load existing character from database into result set
                             * or put name not found into result set
                             */
                            String sql = "CALL gamedata.loadCharacter(?);";
                            cs = conn.prepareCall(sql);
                            cs.setString(1, name);
                            rs = cs.executeQuery();
                            rs.next();

                            // if character not in database
                            if (rs.getString(1).equals("Name not found, try again.")) {
                                System.out.println(rs.getString(1));
                                name = "";
                            } else { // set player data
                                player1.setPlayerName(rs.getString(2));
                                player1.setPlayerHealth(rs.getDouble(3));
                                player1.setPlayerMaxHealth(rs.getDouble(4));
                                player1.setPlayerDamage(rs.getDouble(5));
                                player1.setPotions(rs.getInt(6));
                                player1.setPlayerLevel(rs.getInt(7));
                                player1.setPlayerXp(rs.getDouble(8));
                                player1.setXpRequired();
                                rs.close();
                                playerIsLoaded = true;  // player successfully loaded
                            } // end set player data
                        }// end while name.length() < 1
                    }// end while loadPlayerExists == false
                } else { // end 'if' for player has a character, and else for player doesn't have a character

                    // while name is blank
                    while (name.length() < 1) {
                        System.out.print("Enter character name, or -1 to go back: ");
                        name = console.nextLine();

                        // go back to GET_CHARACTER
                        if (name.equals("-1")) {
                            input = "";
                            name = "";
                            continue GET_CHARACTER;
                        }

                        //check if name exists in database
                        // 1 == name match in database, 0 == no match in daatabase
                        String sql = "SELECT EXISTS("
                                + "SELECT * "
                                + "FROM gamedata.players "
                                + "WHERE name =  '" + name + "')";

                        rs = stmt.executeQuery(sql);
                        rs.next();

                        if (rs.getInt(1) == 1) {
                            System.out.println("Name taken, try again.");
                            name = "";
                        } else { // create new character
                            player1.setPlayerName(name); // Set name of player.

                            sql = "INSERT INTO gamedata.players(name, curHealth, maxHealth, attackDamage, level, experience) "
                                    + "VALUES('" + player1.getPlayerName() + "', " + player1.getPlayerHealth() + ", " + player1.getPlayerMaxHealth()
                                    + ", " + player1.getPlayerDamage() + ", " + player1.getPotions() + ", " + player1.getPlayerLevel() + ", " + player1.getPlayerXp() + ");";

                            stmt.executeUpdate(sql);
                            playerIsLoaded = true;
                        }// end else for create new character
                    }// end while name.length() < 1
                }// end else for no existing character
            }// end while name != "-1"
            // sql boilerplate
        } catch (SQLException se) {
            System.out.println("SQL Exception: " + se.getMessage());
            System.out.println("SQLState Code: " + se.getSQLState());
            System.out.println("Error Code: " + se.getErrorCode());
            se.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (cs != null) {
                    cs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se2) {
            }
        }//end finally

        Enemy starterEnemy = new Enemy(weakEnemyList[rand.nextInt(weakEnemyList.length)]); // Make a weak enemy object

        System.out.println("\n----------------------"
                + "\n      Game start     "
                + "\n----------------------");
        //System.out.println(player1.toString()); // print player info

        boolean running = true;
        // GAME:
        while (running) {
            int menuChoice = -1;
            try {
                while (menuChoice < 1 || 4 < menuChoice) {
                    printMenuOptions();
                    menuChoice = console.nextInt();
                }
            } catch (InputMismatchException e) {
                System.out.println("You must choose a number.");
            }
            OUTER:
            switch (menuChoice) {
                case 1 -> {
                    starterEnemy.setEnemyHealth(starterEnemy.getEnemyMaxHealth());
                    System.out.println("\nAn enemy has appeared!");
                    System.out.print("Would you like to fight? (y/n): ");
                    String wantToFight = console.next();
                    
                    switch (wantToFight.toLowerCase()) {
                        case "y", "yes" -> {
                            while (starterEnemy.getEnemyHealth() > 0) {
                                printBattleInfo(player1, starterEnemy);
                                int fightChoice = -1;
                                while (!contains(fightChoiceList, fightChoice)) { // option 2: fightChoice != 1 && fightChoice != 2 && fightChoice != 3) {
                                    try {
                                        printFightOptions();
                                        fightChoice = console.nextInt();
                                    } catch (InputMismatchException e) {
                                        System.out.println("You must choose a number.");
                                    }
                                }// End choice while
                                switch (fightChoice) {

                                    // attack
                                    case 1 -> {
                                        // player attacks enemy
                                        System.out.println("\nYou attacked " + starterEnemy.getEnemyName() + " and hit for " + player1.getPlayerDamage() + " damage.");
                                        starterEnemy.setEnemyHealth(starterEnemy.getEnemyHealth() - player1.getPlayerDamage());

                                        // if enemy has health, enemy attacks player
                                        enemyAttack(starterEnemy, player1);

                                        // if player has 0 health, send them to menu
                                        if (player1.getPlayerHealth() == 0) {
                                            player1.setPlayerHealth(player1.getPlayerMaxHealth());
                                            break OUTER;
                                        }
                                        
                                        // if enemy died, print post battle info and give xp.
                                        postBattleInfo(player1, starterEnemy);
                                    }// end case 1
                                    // run
                                    case 2 -> {
                                        System.out.println("\nYou tried to run.");
                                        try {
                                            TimeUnit.SECONDS.sleep(1);

                                            int escape = rand.nextInt(100) + 1;
                                            if (escape <= 66) {
                                                System.out.println("You successfully escaped from " + starterEnemy.getEnemyName() + "!");
                                                break OUTER;
                                            } else {
                                                System.out.println("You failed to escape!");

                                                enemyAttack(starterEnemy, player1);
                                                TimeUnit.SECONDS.sleep(1);
                                                
                                                // if player has 0 health, send them to menu
                                                if (player1.getPlayerHealth() == 0) {
                                                    player1.setPlayerHealth(player1.getPlayerMaxHealth());
                                                    break OUTER;
                                                }
                                            }
                                        } catch (InterruptedException ex) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }// End case 2
                                    // use potion
                                    case 3 -> {
                                        if (player1.getPotions() > 0) {
                                            if (player1.getMissingHealth() >= (player1.getPlayerDamage() *3)) {
                                                System.out.println("\nYou healed for " + player1.getPlayerDamage() * 3 + " hp!");
                                                player1.setPlayerHealth(player1.getPlayerHealth() + (player1.getPlayerDamage() * 3));
                                                player1.setPotions(player1.getPotions() - 1);
                                            
                                                enemyAttack(starterEnemy, player1);
                                                
                                                // if player has 0 health, send them to menu
                                                if (player1.getPlayerHealth() == 0) {
                                                    player1.setPlayerHealth(player1.getPlayerMaxHealth());
                                                    break OUTER;
                                                }
                                            }
                                            else if (0 < player1.getMissingHealth() && player1.getMissingHealth() < (player1.getPlayerDamage() *3)) {
                                                System.out.println("\nYou healed for " + player1.getMissingHealth() + " hp!");
                                                player1.setPlayerHealth(player1.getPlayerMaxHealth());
                                                player1.setPotions(player1.getPotions() - 1);
                                            
                                                enemyAttack(starterEnemy, player1);
                                                
                                                // if player has 0 health, send them to menu
                                                if (player1.getPlayerHealth() == 0) {
                                                    player1.setPlayerHealth(player1.getPlayerMaxHealth());
                                                    break OUTER;
                                                }
                                            }
                                            else {
                                                System.out.println("\nYou are full health!");
                                            }
                                        }
                                        else {
                                            System.out.println("You are out of potions!");
                                        }
                                    }
                                    default ->
                                        System.out.println("Invalid input, try again.");
                                }// End inner switch (fightChoice)
                            }
                        }// end of middle switch case where player wants to fight
                        case "n", "no" -> {
                            break OUTER;
                        }
                        default ->
                            System.out.println("Invalid input, try again.");
                    }
                }
                case 2 -> {
                    System.out.println("\nYou have rested. Health restored to full.");
                    player1.setPlayerHealth(player1.getPlayerMaxHealth());
                }
                case 3 -> {
                    System.out.println(player1.toString()); // show player stats
                }
                case 4 -> {
                    conn = null;
                    stmt = null;
                    try {
                        conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
                        stmt = conn.createStatement();
                        String savePlayer = "UPDATE gamedata.players "
                                + "SET name = '" + player1.getPlayerName()
                                + "', curHealth = " + player1.getPlayerHealth()
                                + ", maxHealth = " + player1.getPlayerMaxHealth()
                                + ", attackDamage = " + player1.getPlayerDamage()
                                + ", potionCount = " + player1.getPotions()
                                + ", level = " + player1.getPlayerLevel()
                                + ", experience = " + player1.getPlayerXp() + ";";

                        stmt.executeUpdate(savePlayer);
                    } catch (SQLException se) {
                        System.out.println("SQL Exception: " + se.getMessage());
                        System.out.println("SQLState Code: " + se.getSQLState());
                        System.out.println("Error Code: " + se.getErrorCode());
                        se.printStackTrace();
                    } finally {
                        try {
                            if (stmt != null) {
                                stmt.close();
                            }
                            if (conn != null) {
                                conn.close();
                            }
                        } catch (SQLException se2) {
                        }
                    }//end finally
                    running = false;
                }
                default ->
                    System.out.println("Invalid input, try again.");
            }// end outer switch (menuChoice)
        }// End Game while
        System.out.println("\nThanks for playing!");
        console.close();
    }//end main

    /**
     * Method to print battle information. This includes enemy and player name,
     * level, and health.
     *
     * @param player1
     * @param starterEnemy
     */
    public static void printBattleInfo(Player player1, Enemy starterEnemy) {
        System.out.println(starterEnemy.toString());
        System.out.println("\nPlayer: " + player1.getPlayerName()
                + "\nLevel " + player1.getPlayerLevel()
                + "\nHealth: " + player1.getPlayerHealth() + "/" + player1.getPlayerMaxHealth());
    }// end printBattleInfo

    public static void printMenuOptions() {
        System.out.print("\n1: Enter dungeon"
                + "\n2: Rest"
                + "\n3: Show stats"
                + "\n4: Save and quit"
                + "\nChoose an option: ");
    }

    public static void printFightOptions() {
        System.out.print("\n1: Attack"
                + "\n2: Run"
                + "\n3: Use potion"
                + "\nChoose an option: ");
    }

    public static void postBattleInfo(Player player1, Enemy starterEnemy) {
        if (starterEnemy.getEnemyHealth() < 1) {
            System.out.println("You have defeated " + starterEnemy.getEnemyName() + ".");
            System.out.println("You have gained " + (starterEnemy.getEnemyMaxHealth() * 0.2) + " xp!\n");
            player1.setPlayerXp(player1.getPlayerXp() + (starterEnemy.getEnemyMaxHealth() * 0.2));

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            // level up if player xp is greater than xp required for next level
            while (player1.getPlayerXp() >= player1.getXpRequired()) {
                player1.setPlayerXp(player1.getPlayerXp() - player1.getXpRequired());
                player1.setPlayerLevel(player1.getPlayerLevel() + 1);
                player1.setXpRequired();
                System.out.println("Congrats, you have leveled up to " + player1.getPlayerLevel() + "!");
                player1.setPlayerMaxHealth(player1.getPlayerMaxHealth() + 10);
                player1.setPlayerHealth(player1.getPlayerMaxHealth());
                player1.setPlayerDamage(player1.getPlayerDamage() + 5);
                System.out.println("Your stats have increased!");
            }
            
            int potionDrop = rand.nextInt(100 + 1);
            if (potionDrop <= 25 && player1.getPotions() < 10) {
                player1.setPotions(player1.getPotions() + 1);
                System.out.println("You have found a potion!"
                        + "\nYou now have " + player1.getPotions() + " potions.");
            }
        }
    }// end postBattleInfo

    public static void enemyAttack(Enemy attacker, Player player1) {
        if (attacker.getEnemyHealth() > 0 && player1.getPlayerHealth() > 0) {
            System.out.println(attacker.getEnemyName() + " hit you for " + attacker.getEnemyDamage() + " damaage.");
            if (player1.getPlayerHealth() - attacker.getEnemyDamage() > 0) {
                player1.setPlayerHealth(player1.getPlayerHealth() - attacker.getEnemyDamage());
            }
            else {
                player1.setPlayerHealth(0);
                System.out.println("You awaken in an inn covered in bandages."
                        + "\nYou must have fainted in battle.");
            }
        }
    }

    public static boolean contains(final int[] array, final int v) {

        boolean result = false;

        for (int i : array) {
            if (i == v) {
                result = true;
                break;
            }
        }
        return result;
    }
}//end class
