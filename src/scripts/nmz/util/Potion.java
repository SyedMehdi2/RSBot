package scripts.nmz.util;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

/**
 */
public enum Potion {
    //first id full - rest don't matter
    ABSORBTION(11734, 11735, 11736, 11737),
    OVERLOAD(11730, 11731, 11732, 11733) {
        @Override public void drink(final ClientContext ctx) {
            super.drink(ctx);
            Condition.sleep(Random.nextInt(4600, 6000));
        }
    };

    public static final Potion[] cache = values();


    private final int[] ids;

    Potion(int... ids) {
        this.ids = ids;
    }

    //drink the potion

    public void drink(final ClientContext ctx) {
        Item item = ctx.inventory.select().id(ids).poll();
        item.hover();

        Condition.sleep(Random.nextInt(100, 250));

        item.interact("Drink");

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().animation() == -1;
            }
        }, 150, 20);
    }

    //for bankin n shizz
    public int fullId() {
        return ids[0];
    }
}
