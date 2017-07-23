package scripts.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.*;
import scripts.nmz.task.Task;

import java.util.concurrent.Callable;

/**
 * Created by Wasay Mehdi on 7/7/2017.
 */
public class CookingTask extends Task {

    private final String rawName;
    private final Antiban antiban;

    public CookingTask(final ClientContext context, String rawName) {
        super(context);
        this.rawName = rawName;
        this.antiban = new Antiban(context);
    }

    public boolean activate() {
        return  ctx.players.local().animation() == -1 && ctx.inventory.select().name(rawName).count() > 0;
    }

    public void execute() {
        if(Math.random() < .3)
            antiban.doAntibanAction(rand(1, 10));

        ctx.game.tab(Game.Tab.INVENTORY);

        GameObject range = ctx.objects.select().name("Clay oven").nearest().poll();
        Item food = ctx.inventory.select().name(rawName).poll();
        if(!range.inViewport()) {
            ctx.movement.step(range);
            ctx.camera.turnTo(range);
        }
        food.interact("Use");
        //376, 402
        range.hover();
        Condition.sleep(Random.nextInt(100, 250));
        range.interact(false,"Use", "-> Clay oven");



        Condition.wait(new Callable<Boolean>() {
            public Boolean call() {
                return ctx.widgets.component(303, 3).visible();
            }
        }, 250, 20);

        if (ctx.widgets.component(303, 3).valid()) {
            ctx.widgets.component(303, 3).hover();
            Condition.sleep(Random.nextInt(100, 250));
            ctx.widgets.component(303, 3).interact("Make X");
        }

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.chat.pendingInput();
            }
        },100, 15);
        ctx.chat.sendInput(randynum());

        Condition.wait(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return ctx.players.local().animation() == -1;
            }
        });

        antiban.doAntibanAction(rand(1, 10));


    }

    public int randynum() {
        final StringBuilder builder = new StringBuilder();
        int r = rand(3, 6);
        for(int i = 0; i < rand(3, 6); i++) {
            builder.append(r);

        }
        return Integer.parseInt(builder.toString());
    }
    
    public int rand(int min, int rand) {
        return (int)(Math.random()* rand) + min;
    }
}
