package gov.sec.idap.maxds.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;

@Component
public class Util {
	public static boolean isFieldModified(String s1, String s2) {
		boolean retVal = false;

		if (isEmpty(s1)) {
			if (!isEmpty(s2)) {
				retVal = true;
			}
		} else if (isEmpty(s2)) {
			if (!isEmpty(s1)) {
				retVal = true;
			}
		} else {
			if (!s1.equals(s2)) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static boolean isFieldModified(BigDecimal bd1, BigDecimal bd2) {
		boolean retVal = false;
		if (isNull(bd1)) {
			if (!isNull(bd2)) {
				retVal = true;
			}
		} else if (isNull(bd2)) {
			if (!isNull(bd1)) {
				retVal = true;
			}
		} else {
			if (bd1.compareTo(bd2) != 0) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static boolean isFieldModified(Float f1, Float f2) {
		boolean retVal = false;
		if (isNull(f1)) {
			if (!isNull(f2)) {
				retVal = true;
			}
		} else if (isNull(f2)) {
			if (!isNull(f1)) {
				retVal = true;
			}
		} else {
			if (f1.compareTo(f2) != 0) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static boolean isFieldModified(Long l1, Long l2) {
		boolean retVal = false;
		if (isNull(l1)) {
			if (!isNull(l2)) {
				retVal = true;
			}
		} else if (isNull(l2)) {
			if (!isNull(l1)) {
				retVal = true;
			}
		} else {
			if (l1.compareTo(l2) != 0) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static boolean isEmpty(String s) {
		boolean retVal = false;
		if (s == null) {
			retVal = true;
		} else if (s.trim().equals("")) {
			retVal = true;
		}
		return retVal;
	}

	public static boolean isNull(Object obj) {
		if (obj == null) {
			return true;
		} else {
			return false;
		}
	}
	

	@Value("${spring.datasource.url}")
	private String jdbcDatabaseurl;
	@Value("${spring.datasource.username}")
	private String jdbcUsername;
	@Value("${spring.datasource.password}")
	private String jdbcPassword;
	@Value("${maxds.profileId}")
	private String maxdsProfileId;
	
	private JdbcTemplate jdbcTemplate;
	
	
	public static String getInfo() throws IOException {
		return Util.resourceToString("classpath:maxdsdb.properties");
	}

	private static String resourceToString(String location) throws IOException {
		InputStream inputStream = null;
		ResourceLoader resourceLoader = new FileSystemResourceLoader();
		Resource resource = resourceLoader.getResource(location);
		inputStream = resource.getInputStream();
		String result = new BufferedReader(new InputStreamReader(inputStream)).lines().map(x -> hidePwd(x)).collect(Collectors.joining("</br>"));
		return result;
	}

	private static String hidePwd(String str) {
		if (str.contains("mongo.password"))
			return new String("mongo.password=*************");
		else
			return str;
	}
	
	public JdbcTemplate getJdbcTemplate() throws IOException {
//    	initProperties();
		JdbcTemplate jdbcTemplate = null;
		//on-prim DB
		SimpleDriverDataSource dataSource_sql = new SimpleDriverDataSource();
		dataSource_sql.setDriverClass(com.microsoft.sqlserver.jdbc.SQLServerDriver.class);
		dataSource_sql.setUrl(jdbcDatabaseurl);
		dataSource_sql.setUsername(jdbcUsername);
		dataSource_sql.setPassword(jdbcPassword);
		jdbcTemplate = new JdbcTemplate(dataSource_sql);
		
		//Cloud DB
//		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//		driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
//		driverManagerDataSource.setUrl(jdbcDatabaseurl);
//		driverManagerDataSource.setUsername(jdbcUsername);
//		driverManagerDataSource.setPassword(jdbcPassword);
//		jdbcTemplate = new JdbcTemplate(driverManagerDataSource);
		
		return jdbcTemplate;
	}
    
	//This is obsolete and not being used anymore
    private void initProperties() throws IOException {
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("application.properties");
		Properties prop = new Properties();
		prop.load(input);
		
		//on-prim db
		this.jdbcDatabaseurl = prop.getProperty("spring.datasource.url", "jdbc:sqlserver://sp-ws-14sql02;databaseName=Idap");
		this.jdbcUsername = prop.getProperty("spring.datasource.username", "idap");
		this.jdbcPassword = prop.getProperty("spring.datasource.password", "GOhome**");
		
		//Cloud db
//		this.jdbcDatabaseurl = prop.getProperty("spring.datasource.url", "jdbc:postgresql://idap.cfyhk1fsdtp8.us-east-1.rds.amazonaws.com:5432/postgres");
//		this.jdbcUsername = prop.getProperty("spring.datasource.username", "postgres");
//		this.jdbcPassword = prop.getProperty("spring.datasource.password", "srms1234");
		
		this.maxdsProfileId = prop.getProperty("maxds.profileId", "default");
	}
    
    public List<String>getResultSet(boolean isAdmin) throws IOException {
    	
    	jdbcTemplate = getJdbcTemplate();
    	return jdbcTemplate.queryForList(queryBuilder(isAdmin),String.class);
    }
    private String queryBuilder(boolean isAdmin) {
    	
    	String adminString = null;
    	if(isAdmin)
    		adminString= "maxdsadmin";
    	else
    		adminString="maxdsuser";
    		
    	String GET_USER_QUERY = "select ops1.UserRole.idapUserId from ops1.UserRole where ops1.UserRole.idapRoleId=" +"'" + adminString +"'"+" and ops1.UserRole.profileId"
    			+ "='" + maxdsProfileId +"'"
    			//on-prim db
    			+ " and ops1.UserRole.dtEnd >= getDate()";
    			//cloud db
    			//+ " and ops1.UserRole.dtEnd >= current_date ";
    	
    	return GET_USER_QUERY;
    }

	public String getMaxdsProfileId() {
		return this.maxdsProfileId;
	}
	
    public String getFsqvLinkProperty() {
    	String fsqvLink = null;
    	try {
    	jdbcTemplate = getJdbcTemplate();
		String sql = "select propertyValue from ops1.Property where propertyKey='fsqv.url.filingSearch' AND profileId=?";
		fsqvLink = jdbcTemplate.queryForObject(sql, new Object[] { this.maxdsProfileId }, String.class);
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
    	return fsqvLink;
	}

}
