package net.jlmorton.tableau;

import com.tableausoftware.TableauException;
import com.tableausoftware.common.Type;
import com.tableausoftware.extract.Row;
import com.tableausoftware.extract.TableDefinition;
import net.jlmorton.tableau.utilities.BooleanParser;
import net.jlmorton.tableau.utilities.DateParser;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class RowWriter {
    private static final ThreadLocal<Calendar> calendarRef = ThreadLocal.withInitial(GregorianCalendar::new);

    private RowWriter() {
    }

    static Row parseAndCreateRow(List<String> row, TableDefinition tableDefinition) throws TableauException {
        Row tableauRow = new Row(tableDefinition);
        for (int i = 0; i < row.size(); i++) {
            setRowData(tableauRow, i, tableDefinition.getColumnType(i), row.get(i));
        }
        return tableauRow;
    }

    static private void setRowData(Row row, int columnIndex, Type columnType, String text) throws TableauException {
        if (StringUtils.isBlank(text)) {
            return;
        }

        Calendar cal = calendarRef.get();
        switch (columnType) {
            case DATE:
                cal.setTime(DateParser.parse(text));
                row.setDate(columnIndex, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)); // January = 0
                break;

            case DATETIME:
                cal.setTime(DateParser.parse(text));

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1; // January = 0
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                int second = cal.get(Calendar.SECOND);
                int milliseconds = cal.get(Calendar.MILLISECOND);

                row.setDateTime(columnIndex, year, month, dayOfMonth, hourOfDay, minute, second, milliseconds);
                break;

            case INTEGER:
                row.setInteger(columnIndex, Integer.valueOf(text));
                break;

            case DOUBLE:
                row.setDouble(columnIndex, Double.valueOf(text));
                break;

            case BOOLEAN:
                row.setBoolean(columnIndex, BooleanParser.parse(text));
                break;

            default:
                row.setCharString(columnIndex, text);
                break;
        }
    }


}
