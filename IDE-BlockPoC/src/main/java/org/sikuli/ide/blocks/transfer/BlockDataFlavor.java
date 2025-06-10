package org.sikuli.ide.blocks.transfer;

import java.awt.datatransfer.DataFlavor;

public class BlockDataFlavor extends DataFlavor {
    // Custom DataFlavor for transferring block type information (e.g., class name)
    public static final DataFlavor BLOCK_TYPE_FLAVOR =
        new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.lang.String", "BlockType");

    public BlockDataFlavor() {
        super();
    }
}
