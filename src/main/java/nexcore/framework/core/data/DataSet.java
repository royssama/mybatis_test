package nexcore.framework.core.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Example-only DataSet implementation. Replace with the real NEXCORE class when the jar is available.
 */
public class DataSet implements IDataSet {

    private final Map<String, Object> fields = new LinkedHashMap<>();
    private final Map<String, IRecordSet> recordSets = new LinkedHashMap<>();

    @Override
    public void putField(String name, Object value) {
        fields.put(name, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getField(String name) {
        return (T) fields.get(name);
    }

    @Override
    public Map<String, Object> getFields() {
        return fields;
    }

    @Override
    public Map<String, Object> getFieldMap() {
        return fields;
    }

    @Override
    public void putFieldMap(Map<String, Object> fieldMap) {
        if (fieldMap != null) {
            fields.putAll(fieldMap);
        }
    }

    @Override
    public void putRecordSet(String recordSetName, IRecordSet recordSet) {
        recordSets.put(recordSetName, recordSet);
    }

    @Override
    public IRecordSet getRecordSet(String recordSetName) {
        return recordSets.get(recordSetName);
    }

    @Override
    public Map<String, IRecordSet> getRecordSetMap() {
        return recordSets;
    }

    @Override
    public int getRecordCount() {
        IRecordSet recordSet = firstRecordSet();
        return recordSet == null ? 0 : recordSet.getRowCount();
    }

    @Override
    public Map<String, String> getRecord(int index) {
        IRecordSet recordSet = firstRecordSet();
        return recordSet == null ? Map.of() : recordSet.getRows().get(index);
    }

    @Override
    public void addRow(String recordSetName, Map<String, String> row) {
        recordSets.computeIfAbsent(recordSetName, RecordSet::new).addRow(row);
    }

    @Override
    public List<Map<String, String>> getRows(String recordSetName) {
        IRecordSet recordSet = recordSets.get(recordSetName);
        return recordSet == null ? List.of() : recordSet.getRows();
    }

    @Override
    public Map<String, List<Map<String, String>>> getRecordSets() {
        Map<String, List<Map<String, String>>> rowsByName = new LinkedHashMap<>();
        for (Map.Entry<String, IRecordSet> entry : recordSets.entrySet()) {
            rowsByName.put(entry.getKey(), entry.getValue().getRows());
        }
        return rowsByName;
    }

    private IRecordSet firstRecordSet() {
        return recordSets.values().stream().findFirst().orElse(null);
    }
}
