package scripts.tasks;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import scripts.nmz.task.Task;

import java.util.concurrent.Callable;

public class Banking extends Task {

    final int RAW_ID = 3142;

    public Banking(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.players.local().animation() == -1 && ctx.inventory.select().id(RAW_ID).count() == 0;
    }

    @Override
    public void execute() {
        if(ctx.bank.opened()){
            Condition.sleep(Random.nextInt(100, 250));
            ctx.bank.depositInventory();
            Condition.sleep(Random.nextInt(100, 250));
            int r = Random.nextInt(0, 10);
            if(r < 3)
                ctx.bank.withdraw(3142, Bank.Amount.ALL_BUT_ONE);
            else if(r > 3 && r < 7)
                ctx.bank.withdraw(3142, Bank.Amount.ALL);
            else
                ctx.bank.withdraw(3142, 30);
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.inventory.select().id(RAW_ID).count() > 0;
                }
            }, 100, 15);
            ctx.bank.close();
        } else {
            GameObject bank = ctx.objects.select().id(21301).poll();
            if(!bank.inViewport()) {
                ctx.camera.turnTo(bank);
                ctx.movement.step(bank);
            }
            bank.interact("Use");
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.bank.opened();
                }
            }, 200, 10);
        }
    }
}
