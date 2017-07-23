package scripts;

import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import scripts.tasks.Banking;
import scripts.tasks.CookingTask;
import scripts.nmz.task.Task;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 */
@Script.Manifest(description="Cooker", name="KC",  properties="author=Wasay; client=4; topic=999;")
public class KarambwanCooker extends PollingScript<ClientContext> {

    private List<Task> taskList = new LinkedList<Task>();

    @Override
    public void start() {
        taskList.addAll(Arrays.asList(new CookingTask(ctx, "Raw Karambwan"), new Banking(ctx)));
    }

    @Override
    public void poll() {
        for(Task t : taskList) {
            if(t.activate())
                t.execute();
        }
    }



}
