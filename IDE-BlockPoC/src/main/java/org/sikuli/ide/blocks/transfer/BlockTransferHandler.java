package org.sikuli.ide.blocks.transfer;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import java.awt.datatransfer.Transferable;

// This simple TransferHandler is for the source component (the prototype block in the palette)
public class BlockTransferHandler extends TransferHandler {
    private String blockTypeClassName;

    public BlockTransferHandler(String blockTypeClassName) {
        this.blockTypeClassName = blockTypeClassName;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return new BlockTransferable(blockTypeClassName);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY; // We are copying a prototype to create a new block
    }
}
