package scripts.nmz;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import scripts.nmz.gui.Frame;
import scripts.nmz.gui.PaintManager;
import scripts.nmz.task.impl.BankTask;
import scripts.nmz.task.impl.NmzTask;
import scripts.nmz.task.Task;
import scripts.nmz.util.Configuration;
import scripts.nmz.util.Skills;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;


/**
 */
@Script.Manifest(description="Nightmare Zone Bot", name="NMZ",  properties="author=Icandoit; client=4; topic=999;")

public class NightmareZone extends PollingScript<ClientContext> implements PaintListener, MessageListener{

    /**
     * X value will always be higher than this in NMZ
     */
    public static final int NMZ_MIN_X = 5000;

    public static final String CONFIG_FILE_PATH = System.getProperty("user.home") + "/" + "nmzconfig.bin";

    private final List<Task> tasks = new LinkedList<Task>();

    private PaintManager manager = new PaintManager(ctx);

    private Configuration config;

    //used for repainting
    private int originalXp = -1;
    private long startMillis;


    /**
     * Load config and then ask for new config from frame
     */


    @Override
    public void start() {
        manager = new PaintManager(ctx);
        final Configuration configLoad = Configuration.load(CONFIG_FILE_PATH);
        final Frame frame = new Frame(this);
        frame.loadConfigurations(configLoad);

    }

    @Override
    public void poll() {
        //this means the player hasn't hit "start" yet
        if(config == null)
            return;

        //this means that the other tasks haven't been loaded yet
        if(tasks.isEmpty()) {
            tasks.add(new NmzTask(ctx));
            tasks.add(new BankTask(ctx, config));
        }
        //run tasks
        for(Task task : tasks) {
            if(task.activate())
                task.execute();
        }

    }

    /**
     * @see PaintManager#repaint(Graphics, int, long)
     */
    @Override
    public void repaint(Graphics graphics) {
        if(config == null)
            return;
        if(originalXp == -1) {
            originalXp = ctx.skills.experience(Skills.forIndex(config.skill).id);
            startMillis = System.currentTimeMillis();
        }
        long diff = System.currentTimeMillis() - startMillis;
        if(diff == 0)
            return;
        int currXp = ctx.skills.experience(Skills.forIndex(config.skill).id);
        manager.repaint(graphics, currXp - originalXp, diff);
    }

    public void setConfig(final Configuration config) {
        this.config = config;
    }


    //listen for messages
    @Override
    public void messaged(MessageEvent messageEvent) {
        //System.out.println(messageEvent.toString());
        if (messageEvent.text().equals("<col=ef1020>The effects of overload have worn off, and you feel normal again.")) {
            //this makes sure that NmzTask handles everything
            //too lazy to do it rihgt
            ((NmzTask)tasks.get(0)).shouldOverload = true;
        }
    }
}
