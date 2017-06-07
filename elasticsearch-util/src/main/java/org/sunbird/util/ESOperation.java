package org.sunbird.util;

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
        SIMPLE_FIELD_QUERY("simpleFieldQuery"),SHOULD_EXISTS_FIELD("shouldExistsField");
        String op;

         Operations(String op){
            this.op=op;
        }

        public String getValue(){
             return op;
        }
    }
}
