package nexcore.framework.core.data;

import java.util.Map;

/**
 * Example-only subset of the NEXCORE IOnlineContext contract.
 */
public interface IOnlineContext {

    void setAttribute(String name, Object value);

    Object getAttribute(String name);

    Map<String, Object> getAttributes();

    void setDataSet(IDataSet dataSet);

    IDataSet getDataSet();
}
