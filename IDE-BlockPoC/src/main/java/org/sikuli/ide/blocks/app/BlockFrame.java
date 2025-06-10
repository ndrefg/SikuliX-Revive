package org.sikuli.ide.blocks.app;

import org.sikuli.ide.blocks.palette.BlockPalette;
import org.sikuli.ide.blocks.workspace.BlockCanvas;
// Add these imports:
import org.sikuli.ide.blocks.impl.CommentBlock;
import org.sikuli.ide.blocks.impl.PopupBlock;
import org.sikuli.ide.blocks.impl.ClickBlock;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
// Added imports for JButton and ActionListener
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlockFrame extends JFrame {
    private BlockPalette palette;
    private BlockCanvas canvas;

    public BlockFrame() {
        setTitle("SikuliX Block PoC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        palette = new BlockPalette();
        canvas = new BlockCanvas();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, palette, canvas);
        splitPane.setDividerLocation(220);
        add(splitPane, BorderLayout.CENTER);

        JButton runButton = new JButton("Run Sequence");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas != null) {
                    canvas.runSimulation();
                }
            }
        });
        add(runButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlockFrame frame = new BlockFrame();

            // Add prototype blocks to the palette
            frame.palette.addPrototypeBlock(new CommentBlock("Sample comment..."));
            frame.palette.addPrototypeBlock(new PopupBlock("Default message"));
            frame.palette.addPrototypeBlock(new ClickBlock("placeholder.png"));

            frame.setVisible(true);
        });
    }
}
