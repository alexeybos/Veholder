package org.skillsmart.veholder.utils;

import java.util.HashMap;
import java.util.Map;

public class GUIDWorker {

    private static final Long DB_ID_PREFIX = 10000000000000000L;
    private Map<Long, Long> enterprisesUidMap = new HashMap<>();
    private Map<Long, Long> vehiclesUidMap = new HashMap<>();
    private Map<Long, Long> brandsUidMap = new HashMap<>();
    private Map<Long, Long> tripsUidMap = new HashMap<>();

    public static Long getGUID(Long id) {
        return DB_ID_PREFIX + id;
    }

    public static Long getNativeId(Long guid) {
        return guid - DB_ID_PREFIX;
    }
}
