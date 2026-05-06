package nexcore.framework.core.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class OnlineContext implements IOnlineContext {

    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private IDataSet dataSet;

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public void setDataSet(IDataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public IDataSet getDataSet() {
        return dataSet;
    }
}
