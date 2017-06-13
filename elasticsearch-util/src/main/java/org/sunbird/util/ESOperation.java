package org.sunbird.util;

import org.sunbird.common.models.util.JsonKey;

/**
 * Created by arvind on 6/6/17.
 */
public class ESOperation {

        private String type;
        private Object value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public enum Operations{
        STARTS_WITH("startsWith"),RANGE_QUERY("rangeQuery"),
        SIMPLE_FIELD_QUERY(JsonKey.FILTERS),SHOULD_EXISTS(JsonKey.EXISTS),SHOULD_NOT_EXISTS(JsonKey.NOT_EXISTS),
        MULTI_VALUE_QUERY("MULTI_VALUE_QUERY"),FILTERS(JsonKey.FILTERS);
        String op;

         Operations(String op){
            this.op=op;
        }

        public String getValue(){
             return op;
        }
    }
}
