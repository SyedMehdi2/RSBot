package scripts.nmz.gui;

import scripts.nmz.NightmareZone;
import scripts.nmz.util.Configuration;
import scripts.nmz.util.Potion;
import scripts.nmz.util.Skills;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

/**
 */
public class Frame {

    private final JFormattedTextField absorbtions;
        private final JFormattedTextField overloads;
        private final JComboBox skills = new JComboBox(Skills.CACHE);


    public Frame(final NightmareZone script) {

            final JFrame frame = new JFrame("NMZ Bot");

            final JPanel panel = new JPanel();

            panel.setLayout(new GridLayout(4, 2));

            final JLabel potsLabel = new JLabel("Absorbtions: ");
            final JLabel overloadsLabel = new JLabel("Overloads: ");
            final JLabel skillsLabel = new JLabel("Skill: ");

            final NumberFormatter format = getFormat();
            this.absorbtions = new JFormattedTextField(format);
            this.overloads = new JFormattedTextField(format);;

            panel.add(potsLabel); panel.add(absorbtions);
            panel.add(overloadsLabel); panel.add(overloads);
            panel.add(skillsLabel); panel.add(skills);

            final JButton save = new JButton("Save");
            final JButton start = new JButton("Start");

            //save to the specified fiel path

            save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getConfig().save(NightmareZone.CONFIG_FILE_PATH);
                }
            });

            //start the bot

            start.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean over = getConfig().getPotions().get(Potion.ABSORBTION) + getConfig().getPotions().get(Potion.OVERLOAD) > 27;
                    if(over) {
                        JOptionPane.showMessageDialog(null, "Not enough inventory slots");
                        return;
                    }
                    script.setConfig(getConfig());
                    frame.dispose();
                }
            });

            panel.add(save);
            panel.add(start);


            frame.setPreferredSize(new Dimension(500, 200));
            frame.setLocationRelativeTo(null);
            frame.setContentPane(panel);
            frame.pack();
            frame.setVisible(true);

        }

        //load configs
    public void loadConfigurations(final Configuration config) {
        absorbtions.setValue(config.getPotions().get(Potion.ABSORBTION));
        overloads.setValue(config.getPotions().get(Potion.OVERLOAD));
        skills.setSelectedIndex(config.skill);

    }

    //format to int onyly
    private NumberFormatter getFormat() {
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(28);
        formatter.setAllowsInvalid(false);
        // If you want the value to be committed on each keystroke instead of focus lost
        formatter.setCommitsOnValidEdit(true);

        return formatter;
    }

    private Configuration getConfig() {
        final Configuration config = new Configuration(Integer.parseInt(absorbtions.getText()),
                Integer.parseInt(overloads.getText()),
                ((Skills)skills.getSelectedItem()).ordinal());
        return config;
    }
}
