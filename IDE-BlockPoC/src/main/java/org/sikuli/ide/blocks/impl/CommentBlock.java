package org.sikuli.ide.blocks.impl;

import org.sikuli.ide.blocks.core.Block;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
// DnD imports from previous step - keep them if they were there
import javax.swing.TransferHandler;
import org.sikuli.ide.blocks.transfer.BlockTransferHandler;


public class CommentBlock extends Block {
    private String commentText;
    private JTextField editorField;
    private Rectangle textDisplayRect; // Area where static text is drawn

    public CommentBlock(String text) {
        super("Comment", "comment");
        this.commentText = text == null ? "..." : text;
        setPreferredSize(new java.awt.Dimension(200, 30));
        setLayout(null); // For placing JTextField

        // Make this block draggable (from previous step)
        setTransferHandler(new BlockTransferHandler(this.getClass().getName()));
        // Combined MouseListener for both drag and edit
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Part of drag initiation from previous step
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                if (handler != null && e.getClickCount() != 2) { // Don't drag on double click if it's for editing
                    handler.exportAsDrag(comp, e, TransferHandler.COPY);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && textDisplayRect != null && textDisplayRect.contains(e.getPoint())) {
                    if (editorField == null) { // Prevent multiple editors
                       startEditing();
                    }
                }
            }
        });
    }

    private void startEditing() {
        editorField = new JTextField(commentText);
        // Ensure textDisplayRect is calculated before this call
        if (textDisplayRect == null) {
            // Fallback, should be set by paintComponent first
            // For a robust solution, calculate it here if null using getFontMetrics
            FontMetrics fm = getFontMetrics(getFont());
            int textX = 5;
            textDisplayRect = new Rectangle(textX, 0, getWidth() - textX - 2, getHeight());
        }
        editorField.setBounds(textDisplayRect);
        editorField.setBorder(new LineBorder(Color.BLACK)); // Make it visible

        editorField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    stopEditing(true);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stopEditing(false);
                }
            }
        });
        editorField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                stopEditing(true);
            }
        });

        add(editorField);
        editorField.requestFocusInWindow();
        revalidate();
        repaint();
    }

    private void stopEditing(boolean applyChanges) {
        if (editorField == null) return;

        if (applyChanges) {
            setText(editorField.getText());
        }
        remove(editorField);
        editorField = null;
        revalidate();
        repaint();
    }

    public void setText(String text) {
        this.commentText = text;
        if (editorField != null) {
            editorField.setText(text);
        }
        repaint();
    }

    public String getText() {
        return commentText;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(255, 255, 220)); // Light yellow
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
        g2d.setColor(Color.DARK_GRAY);
        g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

        FontMetrics fm = g2d.getFontMetrics();
        int textX = 5; // Start of the text, including "//"
        int textY = fm.getAscent() + (getHeight() - fm.getHeight()) / 2;
        // Define the clickable area for editing more precisely
        // For "// comment", the editable part is "comment"
        String prefix = "// ";
        int prefixWidth = fm.stringWidth(prefix);

        // textDisplayRect should cover the area where the commentText part is shown
        // The x should be after the "// "
        textDisplayRect = new Rectangle(textX + prefixWidth -2, 0, getWidth() - (textX + prefixWidth) - 2, getHeight());


        if (editorField == null) {
            g2d.setColor(Color.BLACK);
            String fullText = prefix + commentText;
            String clippedText = fullText;
            // Clipping should consider the entire visible area for text
            if (fm.stringWidth(clippedText) > getWidth() - textX - 2) { // Use component width for clipping full text
                while (fm.stringWidth(clippedText + "...") > getWidth() - textX - 2 && clippedText.length() > 0) {
                    clippedText = clippedText.substring(0, clippedText.length() - 1);
                }
                clippedText += "...";
            }
            g2d.drawString(clippedText, textX, textY);
        }
         // If editorField is active, it will paint itself.
    }
}
