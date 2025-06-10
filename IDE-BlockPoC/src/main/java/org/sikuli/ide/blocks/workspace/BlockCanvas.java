package org.sikuli.ide.blocks.workspace;

import org.sikuli.ide.blocks.core.Block;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
// Added imports for DND:
import javax.swing.TransferHandler;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.Point;
import org.sikuli.ide.blocks.transfer.BlockDataFlavor;
// Specific block imports for factory mechanism
import org.sikuli.ide.blocks.impl.CommentBlock;
import org.sikuli.ide.blocks.impl.PopupBlock;
import org.sikuli.ide.blocks.impl.ClickBlock;


public class BlockCanvas extends JPanel {
    private List<Block> blocks;
    private static final int VERTICAL_SNAP_SPACING = 8; // pixels
    private static final int SNAP_THRESHOLD = 20; // pixels for y-proximity to consider snapping

    public BlockCanvas() {
        blocks = new ArrayList<>();
        setLayout(null); // Using null layout for absolute positioning of blocks
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(800, 600));
        setTransferHandler(new CanvasTransferHandler()); // Enable drop target
    }

    public void addBlock(Block block, int x, int y) {
        // Ensure block has preferred size set before setting bounds
        block.setBounds(x, y, block.getPreferredSize().width, block.getPreferredSize().height);
        blocks.add(block);
        this.add((JComponent) block); // Add to JPanel for display
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // TODO: Draw grid, connections between blocks, etc.
    }

    public void runSimulation() {
        System.out.println("\n--- Running Block Sequence Simulation ---");
        // Ensure blocks are processed in visual order (e.g., top to bottom, then left to right)
        // For now, we assume the 'blocks' list is ordered by addition, which might not be visual order
        // A more robust way would be to sort blocks by Y then X coordinates before simulation.
        // List<Block> sortedBlocks = new ArrayList<>(blocks);
        // sortedBlocks.sort(Comparator.comparing(Block::getY).thenComparing(Block::getX));

        for (Block block : blocks) { // Using the current order for simplicity in PoC
            System.out.println("-----------------------------------------"); // Separator
            System.out.println("Block Type: " + block.getBlockName() + " (Class: " + block.getClass().getSimpleName() + ")");
            if (block instanceof CommentBlock) {
                System.out.println("  Comment: " + ((CommentBlock) block).getText());
            } else if (block instanceof PopupBlock) {
                System.out.println("  Popup Message: " + ((PopupBlock) block).getMessage());
            } else if (block instanceof ClickBlock) {
                System.out.println("  Click Image: " + ((ClickBlock) block).getImagePath());
            } else {
                System.out.println("  (Unknown block type for simulation)");
            }
        }
        System.out.println("-----------------------------------------");
        System.out.println("--- Simulation Complete ---\n");
    }

    // Inner class in BlockCanvas.java
    private class CanvasTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            // Check if the data flavor is supported (our custom block flavor)
            return support.isDataFlavorSupported(BlockDataFlavor.BLOCK_TYPE_FLAVOR);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            Transferable transferable = support.getTransferable();
            try {
                // Get the class name string from the transferable
                String blockTypeClassName = (String) transferable.getTransferData(BlockDataFlavor.BLOCK_TYPE_FLAVOR);
                Block newBlock = null;

                // Basic factory mechanism based on class name string
                if (CommentBlock.class.getName().equals(blockTypeClassName)) {
                    newBlock = new CommentBlock("New Comment");
                } else if (PopupBlock.class.getName().equals(blockTypeClassName)) {
                    newBlock = new PopupBlock("New Popup");
                } else if (ClickBlock.class.getName().equals(blockTypeClassName)) {
                    newBlock = new ClickBlock("new_image.png");
                }
                // Add more else if for other block types as they are created

                if (newBlock != null) {
                    Point dropPoint = support.getDropLocation().getDropPoint();
                    int targetX = dropPoint.x;
                    int targetY = dropPoint.y;

                    Block bestSnapTarget = null;
                    // int closestSnapY = Integer.MAX_VALUE; // Not used in the simplified logic below

                    if (!blocks.isEmpty()) {
                        // Find the block whose bottom edge is closest to and above the drop point's Y,
                        // and is horizontally aligned.
                        for (Block existingBlock : blocks) {
                             // Check horizontal alignment: drop x is within existing block's x-range (with some tolerance)
                             boolean horizontallyAligned = (dropPoint.x >= existingBlock.getX() - newBlock.getPreferredSize().width / 2 &&
                                                            dropPoint.x <= existingBlock.getX() + existingBlock.getWidth() - newBlock.getPreferredSize().width / 2 + newBlock.getPreferredSize().width);
                                                            // Simpler: dropPoint.x > existingBlock.getX() - SNAP_THRESHOLD && dropPoint.x < existingBlock.getX() + existingBlock.getWidth() + SNAP_THRESHOLD

                            if (horizontallyAligned) {
                                // Check if dropPoint.y is "below" the existing block's bottom edge (within threshold)
                                int existingBlockBottomY = existingBlock.getY() + existingBlock.getHeight();
                                if (dropPoint.y > existingBlockBottomY - SNAP_THRESHOLD &&
                                    dropPoint.y < existingBlockBottomY + existingBlock.getHeight() + SNAP_THRESHOLD) { // Generous vertical zone for snapping

                                    if (bestSnapTarget == null ||
                                        existingBlockBottomY > (bestSnapTarget.getY() + bestSnapTarget.getHeight()) ) {
                                        // This existing block is a better candidate if its bottom is lower (closer to typical drop)
                                        // or if it's the first one found. This logic is to find the "highest" block to snap under
                                        // if multiple overlap horizontally.
                                        // Let's refine: we want the one whose bottom is closest *above* the drop point.
                                         bestSnapTarget = existingBlock; // Initial candidate
                                    }
                                }
                            }
                        }
                         // Refined logic for picking the bestSnapTarget:
                        // Iterate again or refine the loop above to pick the one whose (bottom_y + VERTICAL_SNAP_SPACING)
                        // is closest to dropPoint.y
                        Block finalBestSnap = null;
                        int minVerticalDistanceToSnapPoint = Integer.MAX_VALUE;

                        for (Block existingBlock : blocks) {
                            boolean horizontallyAligned = (dropPoint.x >= existingBlock.getX() - newBlock.getPreferredSize().width / 2 &&
                                                           dropPoint.x <= existingBlock.getX() + existingBlock.getWidth() + newBlock.getPreferredSize().width / 2);

                            if (horizontallyAligned) {
                                int snapPointY = existingBlock.getY() + existingBlock.getHeight() + VERTICAL_SNAP_SPACING;
                                int verticalDistance = Math.abs(dropPoint.y - snapPointY);

                                if (verticalDistance < SNAP_THRESHOLD * 2) { // Only consider if reasonably close
                                    if (finalBestSnap == null || verticalDistance < minVerticalDistanceToSnapPoint) {
                                        minVerticalDistanceToSnapPoint = verticalDistance;
                                        finalBestSnap = existingBlock;
                                    } else if (verticalDistance == minVerticalDistanceToSnapPoint) {
                                        // Tie-breaking: prefer the one further to the left or higher up
                                        if (existingBlock.getX() < finalBestSnap.getX() ||
                                            (existingBlock.getX() == finalBestSnap.getX() && existingBlock.getY() < finalBestSnap.getY())) {
                                            finalBestSnap = existingBlock;
                                        }
                                    }
                                }
                            }
                        }
                        bestSnapTarget = finalBestSnap;
                    }

                    if (bestSnapTarget != null) {
                        targetX = bestSnapTarget.getX(); // Align X with the snapped block's left edge
                        targetY = bestSnapTarget.getY() + bestSnapTarget.getHeight() + VERTICAL_SNAP_SPACING;
                    }
                    // If no suitable snap target is found, block is placed at original dropPoint (targetX, targetY)

                    addBlock(newBlock, targetX, targetY);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace(); // Or proper logging
            }
            return false;
        }
    }
}
