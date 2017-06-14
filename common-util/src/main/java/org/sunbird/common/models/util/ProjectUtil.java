/**
 *
 */
package org.sunbird.common.models.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will contains all the common utility
 * methods.
 *
 * @author Manzarul
 */
public class ProjectUtil {

    /**
     * format the date in YYYY-MM-DD hh:mm:ss:SSZ
     */
    public static final SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss:SSSZ");
    private static AtomicInteger atomicInteger = new AtomicInteger();

    /**
     * @author Manzarul
     */
    public enum Environment {
        dev(1), qa(2), prod(3);
        int value;

        private Environment(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    /**
     * @author Amit Kumar
     */
    public enum Status {
        ACTIVE(1), INACTIVE(0);

        private int value;

        Status(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    /**
     * @author Amit Kumar
     */
    public enum ProgressStatus {
        NOT_STARTED(0), STARTED(1), COMPLETED(2);

        private int value;

        ProgressStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    /**
     * @author Amit Kumar
     */
    public enum ActiveStatus {
        ACTIVE(true), INACTIVE(false);

        private boolean value;

        ActiveStatus(boolean value) {
            this.value = value;
        }

        public boolean getValue() {
            return this.value;
        }
    }

    /**
     * @author Amit Kumar
     */
    public enum CourseMgmtStatus {
        DRAFT("draft"), LIVE("live"), RETIRED("retired");

        private String value;

        CourseMgmtStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * @author Amit Kumar
     */
    public enum Source {
        WEB("web"), ANDROID("android"), IOS("ios");

        private String value;

        Source(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }
    
    /**
     * This method will check incoming value is null or empty
     * it will do empty check by doing trim method. in case of
     * null or empty it will return true else false.
     *
     * @param value
     * @return
     */
    public static boolean isStringNullOREmpty(String value) {
        if (value == null || "".equals(value.trim())) {
            return true;
        }
        return false;
    }

    /**
     * This method will provide formatted date
     *
     * @return
     */
    public static String getFormattedDate() {
        return format.format(new Date());
    }


    private static Pattern pattern;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    static {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    /**
     * Validate email with regular expression
     *
     * @param email
     * @return true valid email, false invalid email
     */
    public static boolean isEmailvalid(final String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    /**
     * This method will generate user auth token based on user name , source and timestamp
     *
     * @param userName String
     * @param source   String
     * @return String
     */
    public static String createUserAuthToken(String userName, String source) {
        String data = userName + source + System.currentTimeMillis();
        UUID authId = UUID.nameUUIDFromBytes(data.getBytes());
        return authId.toString();
    }

    /**
     * This method will generate unique id based on current time stamp and some
     * random value mixed up.
     *
     * @param environmentId int
     * @return String
     */
    public static String getUniqueIdFromTimestamp(int environmentId) {
    	Random random = new Random();
        long env = (environmentId+random.nextInt(99999))/ 10000000;
        long uid = System.currentTimeMillis() + random.nextInt(999999);
        uid = uid << 13;
        return env + "" + uid + "" + atomicInteger.getAndIncrement();
    }

    /**
     * This method will generate the unique id .
     *
     * @return
     */
    public synchronized static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
     
    public enum Method {GET,POST,PUT,DELETE}
    public static final String FILE_NAME [] = {"cassandratablecolumn.properties","elasticsearch.config.properties","cassandra.config.properties","dbconfig.properties","externalresource.properties"};
    
    /**
     * Enum to hold the index name for Elastic search.
     * @author Manzarul
     *
     */
	public enum EsIndex {
		sunbird("sunbird");
		private String indexName;

		private EsIndex(String name) {
			this.indexName = name;
		}

		public String getIndexName() {
			return indexName;
		}

		public void setIndexName(String indexName) {
			this.indexName = indexName;
		}
	}
	
	/**
	 * This enum will hold all the ES type name.
	 * @author Manzarul
	 *
	 */
	public enum EsType {
		course("course"), content("content");
		private String typeName;

		private EsType(String name) {
			this.typeName = name;
		}

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
	}
	
	/**
	 * This enum will hold all the Section data type name.
	 * @author Amit Kumar
	 *
	 */
	public enum SectionDataType {
		course("course"), content("content");
		private String typeName;

		private SectionDataType(String name) {
			this.typeName = name;
		}

		public String getTypeName() {
			return typeName;
		}

		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
	}
}