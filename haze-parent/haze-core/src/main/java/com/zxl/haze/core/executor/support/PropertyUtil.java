package com.zxl.haze.core.executor.support;

import org.springframework.core.env.*;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.*;

/**
 * @since 1.0
 */
public class PropertyUtil {

    public static final String BEFORE = "before";

    public static final String AFTER = "after";

    private static Set<String> standardSources = new HashSet<>(
            Arrays.asList(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME,
                    StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                    StandardServletEnvironment.JNDI_PROPERTY_SOURCE_NAME,
                    StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME,
                    StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME,
                    "configurationProperties"));


    public static Map<String, Map<String, String>> contrastAll(MutablePropertySources beforeSources,
                                          MutablePropertySources afterSources) {
        Map<String, Object> before = extract(beforeSources);
        return changesAll(before, extract(afterSources));
    }

    public static Map<String, Object> contrast(MutablePropertySources beforeSources,
                                         MutablePropertySources afterSources) {
        Map<String, Object> before = extract(beforeSources);
        return changes(before, extract(afterSources));
    }

    private static Map<String, Object> extract(MutablePropertySources propertySources) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
        for (PropertySource<?> source : propertySources) {
            sources.add(0, source);
        }
        for (PropertySource<?> source : sources) {
            if (!standardSources.contains(source.getName())) {
                extract(source, result);
            }
        }
        return result;
    }

    private static void extract(PropertySource<?> parent, Map<String, Object> result) {
        if (parent instanceof CompositePropertySource) {
            try {
                List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
                for (PropertySource<?> source : ((CompositePropertySource) parent)
                        .getPropertySources()) {
                    sources.add(0, source);
                }
                for (PropertySource<?> source : sources) {
                    extract(source, result);
                }
            } catch (Exception ignored) {
            }
        } else if (parent instanceof EnumerablePropertySource) {
            for (String key : ((EnumerablePropertySource<?>) parent).getPropertyNames()) {
                result.put(key, parent.getProperty(key));
            }
        }
    }

    public static Map<String, Map<String, String>> changesAll(Map<String, Object> before,
                                            Map<String, Object> after) {

        HashMap<String, Map<String, String>> result = new HashMap<>(16);
        for (String key : before.keySet()) {
            if (!after.containsKey(key)) {
                result.put(key, null);
            } else if (!Objects.equals(before.get(key), after.get(key))) {
                HashMap<String, String> valueMap = new HashMap<>(16);
                valueMap.put(BEFORE, String.valueOf(before.get(key)));
                valueMap.put(AFTER, String.valueOf(after.get(key)));
                result.put(key, valueMap);
            }
        }
        return result;
    }

    public static Map<String, Object> changes(Map<String, Object> before,
                                        Map<String, Object> after) {
        Map<String, Object> result = new HashMap<>();
        for (String key : before.keySet()) {
            if (!after.containsKey(key)) {
                result.put(key, null);
            } else if (!Objects.equals(before.get(key), after.get(key))) {
                result.put(key, after.get(key));
            }
        }
        for (String key : after.keySet()) {
            if (!before.containsKey(key)) {
                result.put(key, after.get(key));
            }
        }
        return result;
    }


}
