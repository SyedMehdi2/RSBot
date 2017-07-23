package scripts;

import org.powerbot.bot.rt4.client.HashTable;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.Prayer;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wasay Mehdi on 7/3/2017.
 */

@Script.Manifest(name="PraySwitcher", description="Switch Prayers", properties="author=Wasay; client=4; topic=999;")

/**
 * Switches prayers for Jad
 */
public class PraySwitcher extends PollingScript<ClientContext> implements PaintListener {


    private static final int JAD_ID = /*3127*/2745;
    private JadState currState = null;
    private long start;

    @Override
    public void start() {
        start = System.currentTimeMillis();
    }

    @Override
    public void poll() {
        System.out.println(jad(ctx).animation());
        for(final JadState state : JadState.STATES) {
            if(state.inState(ctx)) {
                state.execute(ctx);
                currState = state;
                break; //break is very important, otherwise it will go to melee prayer
            }
        }
    }


    private static Npc jad(ClientContext ctx) {
        return ctx.npcs.select().id(JAD_ID).nearest().poll();
    }

    @Override
    public void repaint(Graphics graphics) {
        graphics.setColor(Color.MAGENTA);
        graphics.drawRect(3, 336-80, 200, 80);

        graphics.setColor(Color.WHITE);
        graphics.drawString("State: " + (currState == null ? "Finding Jad" : "Praying "+ currState.effect), 10, 300);
        long millis = System.currentTimeMillis() - start;
        graphics.drawString(String.format("Time: %d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))), 10, 325);

        //draw jad color, optional

        Point jad = jad(ctx).centerPoint();

        switch(currState) {
            case RANGED:
                graphics.setColor(Color.GREEN);
                break;
            case MAGE:
                graphics.setColor(Color.BLUE);
                break;
            case MELEE:
                graphics.setColor(Color.BLACK);
                break;
            default:
                return;
        }

        graphics.fillRect(jad.x - 25, jad.y - 25, 50, 50);

    }



    /**
     * Represents a state which the player finds themselves in
     */

    private enum JadState {
        RANGED(65, Prayer.Effect.PROTECT_FROM_MISSILES),
        MAGE(69, Prayer.Effect.PROTECT_FROM_MAGIC),
        MELEE(64, Prayer.Effect.PROTECT_FROM_MELEE) {
            //melee animation too fast
            @Override boolean inState(final ClientContext context) {
                //Distance to jad is close enough for melee range
                //Must consider Jad's size of bigness
                boolean goodDist = jad(context).tile().distanceTo(context.players.local().tile()) < 3;
                return goodDist /*&& !MAGE.inState(context) && !RANGED.inState(context)*/;
                //this part is commented out because it will ALWAYS be true, considering
                //how {@link PraySwitcher#poll} works - but the order of MELEE in this enum is VERY important
            }
        };

        private int animation;
        private Prayer.Effect effect;

        /**
         * Creates a new state for Jad
         * @param animation
         * @param effect - the prayer that must be activated to counteract
         */
        private JadState(int animation, Prayer.Effect effect) {
            this.animation = animation;
            this.effect = effect;
        }

        /**
         *  Cache a copy of JadState enum
         *  Technically mutable but SHOULD NOT BE CHANGED DURING RUNTIME
         */
        private static final List<JadState> STATES = Arrays.asList(values());

        boolean inState(final ClientContext context) {
            return !context.prayer.prayerActive(this.effect) && jad(context).animation() == this.animation;
        }

        //if in state, what should it do
        void execute(final ClientContext context) {
            //switch to prayer tab using hotkeys
            context.game.tab(Game.Tab.PRAYER, true);
            //activate the prayer
            context.prayer.prayer(effect,true);

            //context.prayer.prayerQuick(effect);
            //wait until jad is done with animation to continue
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return context.prayer.prayerActive(JadState.this.effect);
                }
            }, 300, 1);
        }

    }




}
