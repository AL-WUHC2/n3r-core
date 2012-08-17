package org.n3r.core.patchca;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FontDemo extends JFrame {
    //=================================================================== fields
    private FontPanel _displayArea;// Where the sample text is displayed.

    //... The following controls are instance variables because they are
    //    referenced at "runtime" in the listeners.  Alternate solutions:
    //    * Declare them as *final* local variables.
    //    * Obtain them from the Event object passed to the listeners, which
    //      is a good solution when a listener is shared (but not here).
    private JSlider _sizeSlider; // Sets the font size.
    private JComboBox _fontCombo; // For selecting one of the installed fonts.
    private JCheckBox _antialiasedCB; // To draw with antiliasing on/off.

    //================================================================ constants
    private static final int INITIAL_SIZE = 18;
    private static final int INITIAL_STYLE = Font.PLAIN;
//    private static final String INITIAL_STYLENAME = "Font.PLAIN";
    private static final String INITIAL_FONTNAME = "Monospaced";

    //============================================================== constructor
    public FontDemo() {
        //... Create a FontPanel to display the fonts.
        _displayArea = new FontPanel(INITIAL_FONTNAME, INITIAL_STYLE,
                INITIAL_SIZE, false);

        //... Get all font families
        String[] fontNames = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        //... Make vector of all fonts that can display basic chars.
        //    Vector (not newer ArrayList) is used by JComboBox.
        Vector<String> visFonts = new Vector<String>(fontNames.length);
        for (String fontName : fontNames) {
            Font f = new Font(fontName, Font.PLAIN, 12);
            if (f.canDisplay('a')) {
                //... Display only fonts that have the alphabetic characters.
                visFonts.add(fontName);
            }
            else {
                //    On my machine there are almost 20 fonts (eg, Wingdings)
                //    that don't display text.
                //System.out.println("No alphabetics in " + fontName);
            }
        }

        _fontCombo = new JComboBox(visFonts); // JComboBox of fonts
        _fontCombo.setSelectedItem(INITIAL_FONTNAME); // Select initial font
        _fontCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _displayArea.setFontName((String) _fontCombo.getSelectedItem());
            }
        });

        //... Style: Create radio buttons for the 4 style options.
        JRadioButton stylePlainRB = new JRadioButton("Font.PLAIN", true);
        JRadioButton styleItalicRB = new JRadioButton("Font.ITALIC", false);
        JRadioButton styleBoldRB = new JRadioButton("Font.BOLD", false);
        JRadioButton styleItalicBoldRB = new JRadioButton("Font.ITALIC+Font.BOLD"
                , false);

        //... Add the buttons to a button group
        ButtonGroup styleGroup = new ButtonGroup();
        styleGroup.add(stylePlainRB);
        styleGroup.add(styleItalicRB);
        styleGroup.add(styleBoldRB);
        styleGroup.add(styleItalicBoldRB);

        //... Add listeners.
        stylePlainRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _displayArea.setFontStyle(Font.PLAIN);
            }
        });
        styleItalicRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _displayArea.setFontStyle(Font.ITALIC);
            }
        });
        styleBoldRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _displayArea.setFontStyle(Font.BOLD);
            }
        });
        styleItalicBoldRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _displayArea.setFontStyle(Font.ITALIC + Font.BOLD);
            }
        });

        //... Antialiasing checkbox
        _antialiasedCB = new JCheckBox("Antialiased");
        _antialiasedCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _displayArea.setAntialiasing(_antialiasedCB.isSelected());
            }
        });

        //... Create a slider for setting the size value
        _sizeSlider = new JSlider(JSlider.HORIZONTAL, 5, 60, INITIAL_SIZE);
        _sizeSlider.setMajorTickSpacing(10); // sets numbers for big tick marks
        _sizeSlider.setMinorTickSpacing(1); // smaller tick marks
        _sizeSlider.setPaintTicks(true); // display the ticks
        _sizeSlider.setPaintLabels(true); // show the numbers
        _sizeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _displayArea.setFontSize(_sizeSlider.getValue());
            }
        });

        //... Controls arranged in BoxLayout.
        //    This is pretty ugly, but I didn't feel like struggling with
        //    GridBagLayout, and didn't want to use any of the superior
        //    external layout managers in this program.
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        //... Add components to controls panel.
        addToBox(controls, Component.LEFT_ALIGNMENT
                , new JLabel("Font:"), _fontCombo
                , new JLabel("Size:"), _sizeSlider
                , new JLabel("Style:"), stylePlainRB, styleItalicRB
                , styleBoldRB, styleItalicBoldRB
                , _antialiasedCB);

        //... Put display+controls in the content pane.
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout(5, 5));
        content.add(_displayArea, BorderLayout.CENTER);
        content.add(controls, BorderLayout.WEST);
        content.setBorder(new EmptyBorder(12, 12, 12, 12));

        //... Set window characteristics
        setContentPane(content);
        Font wingsdingsFont = new Font( "dialog", Font.PLAIN, 25 ); // Must be a logical font name
        setFont(wingsdingsFont);
        String text = "Smile: \u263A, Phone: \u2706";
       
       
        setTitle(text/*"Font Demo"*/);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    //================================================================= addToBox
    // Utility method to add elements to a BoxLayout container.
    private void addToBox(Container cont, float align, JComponent... comps) {
        for (JComponent comp : comps) {
            comp.setAlignmentX(align);
            cont.add(comp);
        }
    }

    //===================================================================== main
    public static void main(String[] args) {
        JFrame myWindow = new FontDemo();
        myWindow.setVisible(true);
    }
}
