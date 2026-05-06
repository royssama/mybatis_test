package nexcore.framework.core.data;

import java.util.List;
import java.util.Map;

/**
 * Example-only subset of the NEXCORE IRecordSet contract.
 */
public interface IRecordSet {

    String getName();

    List<Map<String, String>> getRows();

    int getRowCount();
}
