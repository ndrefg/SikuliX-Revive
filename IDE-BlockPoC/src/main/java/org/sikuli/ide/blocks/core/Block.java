package org.sikuli.ide.blocks.core;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Dimension;

public abstract class Block extends JComponent {
    protected String blockName;
    protected String blockType; // e.g., "action", "control", "reporter"

    public Block(String name, String type) {
        this.blockName = name;
        this.blockType = type;
        // Basic default size, can be overridden by subclasses
        setPreferredSize(new Dimension(150, 30));
    }

    public String getBlockName() {
        return blockName;
    }

    public String getBlockType() {
        return blockType;
    }

    @Override
    protected abstract void paintComponent(Graphics g);

    // Placeholder for parameter handling
    // public abstract void setParameter(String paramName, Object value);
    // public abstract Object getParameter(String paramName);

    // Placeholder for data model of the block's state
    // public abstract Map<String, Object> getData();
    // public abstract void setData(Map<String, Object> data);
}
