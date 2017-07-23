package scripts.nmz.util;


import org.powerbot.script.rt4.Inventory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for the bot
 */
public class Configuration {
    public static final int ROCK_CAKE_ID = 7510;
    //default config to apply
    private static final Configuration DEFAULT = new Configuration(18, 9, 0);

    //store
    private final Map<Potion, Integer> potions;
    //immutable finals - public
    //stores the index of the skill inside of the skills enum
    public final int skill;

    public Configuration(int absorbtions, int overloads, int skill) {
        potions = new HashMap<Potion, Integer>();
        potions.put(Potion.ABSORBTION, absorbtions);
        potions.put(Potion.OVERLOAD, overloads);
        this.skill = skill;
    }

    /**
     * Save the configurations to the specified file
     */
    public void save(final String fileName) {
        try {
            final File file = new File(fileName);
            final FileOutputStream stream = new FileOutputStream(file);

            stream.write(potions.get(Potion.ABSORBTION));
            stream.write(potions.get(Potion.OVERLOAD));
            stream.write(skill);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the current configuration is loaded
     */

    public boolean isLoaded(Inventory inventory) {
        boolean absorbs = inventory.select().id(Potion.ABSORBTION.fullId()).count() == potions.get(Potion.ABSORBTION);
        boolean overloads = inventory.select().id(Potion.OVERLOAD.fullId()).count() == potions.get(Potion.OVERLOAD);
        boolean rC = inventory.select().id(ROCK_CAKE_ID).count() > 0;
        return absorbs && overloads && rC;
    }

    /**
     *  Load configuration
     */

    public static Configuration load(final String fileName) {
        try {
            final File file = new File(fileName);
            final InputStream stream = new FileInputStream(file);
            return load(stream);
        } catch (IOException e) {
            return DEFAULT;
        }
    }


    /**
     * Allows the user to load the saved configurations
     */

    private static Configuration load(final InputStream stream) throws IOException {
        final int absorbtions = stream.read();
        final int overloads = stream.read();
        final int skill = stream.read();

        return new Configuration(absorbtions, overloads, skill);
    }

    public Map<Potion, Integer> getPotions() {
        return potions;
    }

}