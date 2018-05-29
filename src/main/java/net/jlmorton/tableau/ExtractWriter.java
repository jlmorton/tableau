package net.jlmorton.tableau;

import com.tableausoftware.TableauException;

public interface ExtractWriter {
    void writeExtract() throws TableauException;

    void closeExtract();
}
