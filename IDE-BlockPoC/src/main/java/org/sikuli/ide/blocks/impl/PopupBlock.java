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
// DnD imports from previous step - keep them
import javax.swing.TransferHandler;
import org.sikuli.ide.blocks.transfer.BlockTransferHandler;


public class PopupBlock extends Block {
    private String message;
    private JTextField editorField;
    private Rectangle messageDisplayRect; // Area where the message text is shown or edited

    public PopupBlock(String initialMessage) {
        super("Popup", "action");
        this.message = initialMessage == null ? "Hello, SikuliX!" : initialMessage;
        setPreferredSize(new java.awt.Dimension(220, 50));
        setLayout(null); // For placing JTextField

        // Make this block draggable (from previous step)
        setTransferHandler(new BlockTransferHandler(this.getClass().getName()));
        // Combined MouseListener for both drag and edit
        addMouseListener(new MouseAdapter() {
             @Override
            public void mousePressed(MouseEvent e) {
                // Part of drag initiation
                JComponent comp = (JComponent) e.getSource();
                TransferHandler handler = comp.getTransferHandler();
                // Don't start drag if this click is intended for editing
                if (handler != null && !(messageDisplayRect != null && messageDisplayRect.contains(e.getPoint()) && e.getClickCount() == 2) ) {
                    handler.exportAsDrag(comp, e, TransferHandler.COPY);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && messageDisplayRect != null && messageDisplayRect.contains(e.getPoint())) {
                     if (editorField == null) {
                        startEditing();
                     }
                }
            }
        });
    }

    private void startEditing() {
        editorField = new JTextField(message);
        // Ensure messageDisplayRect is calculated
        if (messageDisplayRect == null) {
             FontMetrics fm = getFontMetrics(getFont());
             int labelY = fm.getAscent() + 5;
             int msgAreaX = 5;
             int msgAreaY = labelY + fm.getDescent() + 5;
             int msgAreaWidth = getWidth() - 10;
             int msgAreaHeight = getHeight() - msgAreaY - 5;
             messageDisplayRect = new Rectangle(msgAreaX, msgAreaY, msgAreaWidth, msgAreaHeight);
        }
        editorField.setBounds(messageDisplayRect);
        editorField.setBorder(new LineBorder(Color.BLACK));

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
            setMessage(editorField.getText());
        }
        remove(editorField);
        editorField = null;
        revalidate();
        repaint();
    }

    public void setMessage(String message) {
        this.message = message;
         if (editorField != null) {
            editorField.setText(message);
        }
        repaint();
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(220, 220, 255)); // Light purple
        g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
        g2d.setColor(Color.DARK_GRAY);
        g2d.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

        g2d.setColor(Color.BLACK);
        FontMetrics fm = g2d.getFontMetrics();
        int labelY = fm.getAscent() + 5;
        g2d.drawString("Popup:", 5, labelY);

        int msgAreaX = 5;
        int msgAreaY = labelY + fm.getDescent() + 5;
        int msgAreaWidth = getWidth() - 10;
        int msgAreaHeight = getHeight() - msgAreaY - 5;
        // Define the messageDisplayRect each time paintComponent is called
        // This ensures it's correct even if the block is resized.
        messageDisplayRect = new Rectangle(msgAreaX, msgAreaY, msgAreaWidth, msgAreaHeight);

        if (editorField == null) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(messageDisplayRect.x, messageDisplayRect.y, messageDisplayRect.width, messageDisplayRect.height);
            g2d.setColor(Color.GRAY);
            g2d.drawRect(messageDisplayRect.x, messageDisplayRect.y, messageDisplayRect.width, messageDisplayRect.height);

            g2d.setColor(Color.BLACK);
            String clippedMessage = message;
            if (fm.stringWidth(clippedMessage) > messageDisplayRect.width - 4) { // Check against calculated rect
                 while (fm.stringWidth(clippedMessage + "...") > messageDisplayRect.width - 4 && clippedMessage.length() > 0) {
                    clippedMessage = clippedMessage.substring(0, clippedMessage.length() - 1);
                }
                clippedMessage += "...";
            }
            // Position text within the messageDisplayRect
            g2d.drawString(clippedMessage, messageDisplayRect.x + 2, messageDisplayRect.y + fm.getAscent() + (messageDisplayRect.height - fm.getHeight()) / 2);
        }
        // If editorField is active, it paints itself over this area.
    }
}
