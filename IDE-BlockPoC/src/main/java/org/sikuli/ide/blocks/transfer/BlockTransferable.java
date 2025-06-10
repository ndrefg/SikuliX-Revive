package org.sikuli.ide.blocks.transfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class BlockTransferable implements Transferable {
    private String blockTypeClassName; // Fully qualified class name of the block

    public BlockTransferable(String blockTypeClassName) {
        this.blockTypeClassName = blockTypeClassName;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{BlockDataFlavor.BLOCK_TYPE_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(BlockDataFlavor.BLOCK_TYPE_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return blockTypeClassName;
        }
        throw new UnsupportedFlavorException(flavor);
    }
}
