package scripts.nmz.task;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import org.powerbot.script.ClientAccessor;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt4.ClientContext;

/**
 * Created by on 7/7/2017.
 */
public abstract class Task extends ClientAccessor<ClientContext> {

    public Task(final ClientContext context) {
        super(context);
    }

    public abstract boolean activate();
    public abstract void execute();
}
