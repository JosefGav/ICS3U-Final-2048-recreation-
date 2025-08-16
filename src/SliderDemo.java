// https://www.javatpoint.com/java-jslider
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.Random;
import java.util.Arrays;

import javax.swing.*;
public class SliderDemo extends JFrame{
    public SliderDemo() {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 3, 8, 4);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        JPanel panel=new JPanel();
        panel.add(slider);
        add(panel);

    }
    public static void main(String s[]) {
        SliderDemo frame=new SliderDemo();
        frame.pack();
        frame.setVisible(true);
    }
}