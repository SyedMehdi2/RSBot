package scripts.nmz.gui;

import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;
import scripts.nmz.util.Configuration;
import sun.security.krb5.Config;

import java.awt.*;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 */
public class PaintManager extends ClientAccessor<ClientContext> {

    public PaintManager(ClientContext ctx) {
        super(ctx);
    }

    public void repaint(Graphics g, int expGained, long millis) {
        //transparent box
        final Color color = new Color(50, 150, 175, 127);

        g.setColor(Color.BLACK);

        g.drawRect(4, 2784 , 250, 60);

        g.setColor(color);

        g.fillRect(4, 278, 250, 60);

        //text
        g.setColor(Color.WHITE);

        double denom = ((millis/(double)1000))/3600; //per hour
        int expPh = (int)(expGained/denom);
        g.drawString("Exp Gained (Per Hour): " + expGained + " (" + expPh +")", 10, 300);

        //System.out.println(millis);
        g.drawString( String.format("Time Running: %02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))), 10, 325);


    }
}
