package nexcore.framework.core.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecordSet implements IRecordSet {

    private final String name;
    private final List<Map<String, Object>> rows = new ArrayList<>();

    public RecordSet(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addRow(Map<String, Object> row) {
        rows.add(row);
    }

    @Override
    public List<Map<String, Object>> getRows() {
        return rows;
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }
}
