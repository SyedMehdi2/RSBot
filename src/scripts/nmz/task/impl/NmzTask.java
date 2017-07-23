package scripts.nmz.task.impl;

import org.powerbot.script.*;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;
import scripts.nmz.NightmareZone;
import scripts.nmz.task.Task;
import scripts.nmz.util.Configuration;
import scripts.nmz.util.Potion;

import java.util.concurrent.Callable;

/**
 */
public class NmzTask extends Task {

    //random absorb every time
    private int nextAbsorb = Random.nextInt(42, 95);

    public boolean shouldOverload = true;
    //walk to corner to preseve absorbs
    private Tile corner;

    public NmzTask(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.players.local().tile().x() > NightmareZone.NMZ_MIN_X;
    }

    @Override
    public void execute() {
        //make sure on inventory
        ctx.game.tab(Game.Tab.INVENTORY);


        if(shouldAbsorb()) {
            Potion.ABSORBTION.drink(ctx);
            nextAbsorb = Random.nextInt(42, 95);
            Condition.sleep(Random.nextInt(600, 1000));

        }
        //first drink overload
        if(shouldOverload()) {
            Potion.OVERLOAD.drink(ctx);
            Condition.sleep(Random.nextInt(6000, 7600));
            //randomly check stats
            //rs only sends a packet when switching tabs so hovering skills etc is pointless
            if(Math.random() < .25) {
                ctx.game.tab(Game.Tab.STATS);
                Condition.sleep(Random.nextInt(550, 700));
                ctx.game.tab(Game.Tab.INVENTORY);
            }

            shouldOverload = false;

            while(shouldRockCake()) {//initializing rock rake/doing after overload
                eatRockCake(false);
            }

        }
        if(corner == null)
            walkToCorner();
        else if(corner.distanceTo(ctx.players.local()) > 50) //new NMZ
            walkToCorner();


        if(shouldRockCake()) {
            eatRockCake(true);
        }

    }

    public boolean shouldRockCake() {
        return !shouldOverload() && ctx.combat.health() > 1;
    }


    //check if should absorb potion
    public boolean shouldAbsorb() {
        String widgetText = ctx.widgets.widget(202).component(1).component(9).text();

        if(widgetText.equals(""))
            return true;
        int currAbsorb = Integer.parseInt(widgetText);
        System.out.println("Curr Absorb " +currAbsorb+  "Next: "+nextAbsorb);
        return currAbsorb < nextAbsorb;
    }

    /**
     * @param delay should there be an anti-ban delay?
     */
    private void eatRockCake(boolean delay) {
        if(delay)
            Condition.sleep(Random.nextInt(3000, 7000));
        Item rockCake = ctx.inventory.select().id(Configuration.ROCK_CAKE_ID).poll();
        rockCake.hover();
        Condition.sleep(Random.nextInt(25, 50));
        if(!shouldRockCake()) //don't override the overload
            return;
        rockCake.interact("Guzzle");
        //wait until done guzzling
        Condition.sleep(Random.nextInt(100, 175));
    }

    /**
     *
     */
    private void walkToCorner() {
        //lazily instantiate - means this is start
        final Player local = ctx.players.local();
        final Tile curr = local.tile();
        //get the corner of the room
        corner = new Tile(curr.x() + 11, curr.y(), curr.floor());
        if(!local.tile().equals(corner)) {
            ctx.movement.step(corner);
        }

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return local.tile().equals(corner);
            }
        }, 200, 15);
    }

    private boolean shouldOverload() {
        return ctx.combat.health() > 50 || shouldOverload;
    }

    public void handleOverloadMessage() {
        shouldOverload = true;
    }

    public void handleRestart() {
        corner = null;
    }




}
