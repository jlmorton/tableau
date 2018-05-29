package net.jlmorton.tableau;

public interface RowInputSource {
    boolean hasNext();

    int getCurrentRowNumber();

    String[] getNextRow();
}
