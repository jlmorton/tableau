package net.jlmorton.tableau;

import java.util.List;

public interface RowInputSource {
    boolean hasNext();

    int getCurrentRowNumber();

    List<String> getNextRow();
}
