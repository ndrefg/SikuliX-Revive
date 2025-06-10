package org.sikuli.ide.blocks.palette;

import org.sikuli.ide.blocks.core.Block;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box; // New import
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.ArrayList;

public class BlockPalette extends JPanel {
    private List<Block> prototypeBlocks;

    public BlockPalette() {
        prototypeBlocks = new ArrayList<>();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.GRAY);
        setPreferredSize(new Dimension(200, 600));
        // Prototype blocks will be added here later
    }

    public void addPrototypeBlock(Block block) {
        prototypeBlocks.add(block);
        // For the palette, we might add a representation or a factory instance
        this.add(block);
        this.add(Box.createRigidArea(new Dimension(0, 5))); // Add spacing
        revalidate();
        repaint();
    }
}
