package scripts.nmz.task.impl;


import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;
import scripts.nmz.NightmareZone;
import scripts.nmz.task.Task;
import scripts.nmz.util.Configuration;
import scripts.nmz.util.Potion;

import javax.swing.*;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 */
public class BankTask extends Task {

    private static final Tile NEAR_DOMINIC = new Tile(2610, 3113);
    private static final Tile NEAR_BANK = new Tile(2611, 3092);


    private final Configuration config;
    public BankTask(ClientContext ctx, Configuration config) {
        super(ctx);
        this.config = config;
    }

    //if loaded, then can go into the NMZ
    public boolean activate() {
        return ctx.players.local().tile().x() < NightmareZone.NMZ_MIN_X;
    }

    @Override
    public void execute() {
        if(!config.isLoaded(ctx.inventory)) {
            executeBanking();
        } else {
            executeStarting();
        }


    }

    //bank for supplies
    public void executeBanking() {
        if(!ctx.bank.opened()) {
            walkTo(NEAR_BANK);
            final GameObject obj = ctx.objects.select().name("Bank booth").poll();
            ctx.camera.turnTo(obj);
            obj.interact("Bank");
        } else {
            ctx.bank.depositInventory();
            Condition.sleep(Random.nextInt(450, 750));
            for(Potion p : Potion.cache) {
                int amt = config.getPotions().get(p);
                if(ctx.bank.select().id(p.fullId()).poll().stackSize() >= amt) {
                    ctx.bank.withdraw(p.fullId(), amt);
                } else {
                    JOptionPane.showMessageDialog(null, "You've run out of supplies!");
                    ctx.controller.stop();
                }
            }

            ctx.bank.withdraw(Configuration.ROCK_CAKE_ID, 1);

            ctx.bank.close();


        }

    }

    //start a new game
    public void executeStarting() {
        walkTo(NEAR_DOMINIC);
        final Npc dominic = ctx.npcs.select().name("Dominic Onion").poll();
        ctx.camera.turnTo(dominic);
        ctx.movement.step(dominic);
        dominic.hover();
        Condition.sleep(Random.nextInt(100, 200));
        dominic.interact("Dream");

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.chat.chatting();
            }
        }, 250, 30);
        // dream already set up
        if(!ctx.chat.canContinue()) {
            //get thru dominic chat
            Iterator<ChatOption> it = ctx.chat.select().iterator();
            while (it.hasNext()) {
                ChatOption option = it.next();
                if (option.text().startsWith("Previous:")) {
                    option.select();
                    break;
                }
            }

            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.chat.canContinue();
                }
            }, 250, 10);

            ctx.chat.clickContinue(true);

            Condition.sleep(Random.nextInt(500, 650));

            //no money
            if(ctx.chat.canContinue()) {
                JOptionPane.showMessageDialog(null, "You don't have enough money in the deposit box!");
                ctx.controller.stop();
            }

            ctx.chat.select().text("Yes").poll().select();

        }
        ctx.chat.clickContinue();
        Condition.sleep(Random.nextInt(250, 500));

        //interact with vial
        GameObject o = ctx.objects.select().name("Potion").poll();

        o.interact("Drink");

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.widgets.component(129, 6).visible();
            }
        }, 200, 30);

        //check if enough coins
        Component component = ctx.widgets.component(207, 1).component(21);
        if(component.visible() && component.itemStackSize() < 26000) {
                JOptionPane.showMessageDialog(null, "You don't have enough money in the deposit box!");
                ctx.controller.stop();
        }
        //click on interface

        ctx.widgets.component(129, 6).interact("Continue");
        //wait to load in
        Condition.sleep(Random.nextInt(3500, 4500));


    }

    //walkto close enough tile
    private void walkTo(Tile tile) {
        final Tile close = tile.derive(Random.nextInt(-2, 2), Random.nextInt(-2, 2));
        if (ctx.players.local().tile().distanceTo(close) < 6) {
            return;
        }
        ctx.camera.turnTo(close);
        ctx.movement.step(close);
        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().tile().distanceTo(close) < 5;
            }
        }, 150, 20);
        Condition.sleep(Random.nextInt(3000, 4000));
    }

}
