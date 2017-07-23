package scripts.nmz.util;

import org.powerbot.script.rt4.Constants;

/**
 * Created by  on 7/22/2017.
 * No confusion with 'Skill' class ref inside ClientContext
 */
public enum Skills {
    ATTACK("Attack", Constants.SKILLS_ATTACK),
    STRENGTH("Strength", Constants.SKILLS_STRENGTH),
    DEFENCE("Defence", Constants.SKILLS_DEFENSE),
    RANGED("Range", Constants.SKILLS_RANGE),
    MAGIC("Mage", Constants.SKILLS_MAGIC);

    public static final Skills[] CACHE = values();

    public final int id;
    public final String name;
    Skills(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override public String toString() {
        return name;
    }


    public static Skills forIndex(int id) {
        return CACHE[id];
    }
}
