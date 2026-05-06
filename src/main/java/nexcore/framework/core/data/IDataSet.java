package nexcore.framework.core.data;

import java.util.List;
import java.util.Map;

/**
 * Example-only subset of the NEXCORE IDataSet contract.
 * Replace this file with the real company framework dependency in production.
 */
public interface IDataSet {

    void putField(String name, Object value);

    Object getField(String name);

    Map<String, Object> getFields();

    Map<String, Object> getFieldMap();

    void putFieldMap(Map<String, Object> fieldMap);

    void putRecordSet(String recordSetName, IRecordSet recordSet);

    IRecordSet getRecordSet(String recordSetName);

    Map<String, IRecordSet> getRecordSetMap();

    void addRow(String recordSetName, Map<String, Object> row);

    List<Map<String, Object>> getRows(String recordSetName);

    Map<String, List<Map<String, Object>>> getRecordSets();

    int getRecordCount();

    Map<String, Object> getRecord(int index);
}
