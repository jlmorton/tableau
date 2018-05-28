package net.jlmorton.tableau;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class CsvInputSource implements RowInputSource {
    private final CsvParser csvParser = getCsvParser();
    private AtomicInteger rowCounter = new AtomicInteger(0);

    private String[] previouslyParsedRow;

    @SuppressWarnings("WeakerAccess")
    public CsvInputSource(File csvFile) {
        csvParser.beginParsing(csvFile);
    }

    @Override
    public boolean hasNext() {
        if (previouslyParsedRow != null) {
            return true;
        }

        previouslyParsedRow = csvParser.parseNext();
        return previouslyParsedRow != null;
    }

    @Override
    public int getCurrentRowNumber() {
        return rowCounter.get();
    }

    @Override
    public List<String> getNextRow() {
        rowCounter.getAndIncrement();

        if (!Objects.isNull(previouslyParsedRow)) {
            String[] tmp = previouslyParsedRow;
            previouslyParsedRow = null;

            return Arrays.asList(tmp);
        }

        return Arrays.asList(csvParser.parseNext());
    }

    private CsvParser getCsvParser() {
        CsvParserSettings csvParserSettings = new CsvParserSettings();
        csvParserSettings.getFormat().setLineSeparator("\n");
        csvParserSettings.setHeaderExtractionEnabled(true);
        return new CsvParser(csvParserSettings);
    }
}
