package nexcore.framework.core.data;

import java.util.List;
import java.util.Map;

/**
 * Example-only subset of the NEXCORE IRecordSet contract.
 */
public interface IRecordSet {

    String getName();

    void addRow(Map<String, Object> row);

    List<Map<String, Object>> getRows();

    int getRowCount();
}
