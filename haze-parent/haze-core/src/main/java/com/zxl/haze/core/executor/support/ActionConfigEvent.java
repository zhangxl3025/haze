package com.zxl.haze.core.executor.support;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * @author purgeyao
 * @since 1.0
 */
public class ActionConfigEvent extends ApplicationEvent {

    private String eventDesc;

    private Map<String, Map<String, String>> propertyMap;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ActionConfigEvent(Object source, String eventDesc, Map<String,Map<String, String>> propertyMap) {
        super(source);
        this.eventDesc = eventDesc;
        this.propertyMap = propertyMap;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public Map<String,Map<String, String>> getPropertyMap() {
        return propertyMap;
    }

    public void setPropertyMap(Map<String, Map<String, String>> propertyMap) {
        this.propertyMap = propertyMap;
    }

}
