package net.jlmorton.tableau;

import com.tableausoftware.TableauException;

public interface Publisher {
    void publish() throws TableauException;
}
