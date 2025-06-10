package org.sikuli.ide.blocks.impl;

import org.sikuli.ide.blocks.core.Block;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Rectangle; // For placeholder
// Added imports:
import javax.swing.TransferHandler;
import javax.swing.JComponent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.sikuli.ide.blocks.transfer.BlockTransferable;
import org.sikuli.ide.blocks.transfer.BlockTransferHandler; // Added import

public class ClickBlock extends Block {
    private String imagePathOrPlaceholder; // In PoC, just a string
    // Placeholder for image area
    private Rectangle imageArea;

    public ClickBlock(String imageRepresentation) {
        super("Click", "action");
        this.imagePathOrPlaceholder = imageRepresentation == null ? "[image]" : imageRepresentation;
        setPreferredSize(new java.awt.Dimension(180, 60));

        // Make this block draggable
        setTransferHandler(new BlockTransferHandler(this.getClass().getName()));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                if (handler != null) {
                    handler.exportAsDrag(comp, e, TransferHandler.COPY);
                }
            }
        });
    }

    public void setImagePath(String imagePath) {
        this.imagePathOrPlaceholder = imagePath;
        repaint();
    }

    public String getImagePath() {
        return imagePathOrPlaceholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Block body
        g2d.setColor(new Color(200, 230, 255)); // Light blue
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
        g2d.setColor(Color.DARK_GRAY);
        g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

        // Label "Click"
        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        int labelY = fm.getAscent() + 5;
        g2d.drawString("Click:", 5, labelY);

        // Placeholder for image area
        int imageAreaX = 5;
        int imageAreaY = labelY + 5;
        int imageAreaWidth = getWidth() - 10;
        int imageAreaHeight = getHeight() - labelY - 10;
        imageArea = new Rectangle(imageAreaX, imageAreaY, imageAreaWidth, imageAreaHeight);

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(imageArea.x, imageArea.y, imageArea.width, imageArea.height);
        g2d.setColor(Color.GRAY);
        g2d.drawRect(imageArea.x, imageArea.y, imageArea.width, imageArea.height);

        // Draw placeholder text in image area
        g2d.setColor(Color.DARK_GRAY);
        String placeholder = imagePathOrPlaceholder;
        if (fm.stringWidth(placeholder) > imageArea.width - 4) {
             while (fm.stringWidth(placeholder + "...") > imageArea.width - 4 && placeholder.length() > 0) {
                placeholder = placeholder.substring(0, placeholder.length() - 1);
            }
            placeholder += "...";
        }
        g2d.drawString(placeholder, imageArea.x + 2, imageArea.y + fm.getAscent() + (imageArea.height - fm.getHeight()) / 2);
    }
}
