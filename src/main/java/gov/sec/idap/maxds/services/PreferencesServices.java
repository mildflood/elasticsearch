package gov.sec.idap.maxds.services;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import gov.sec.idap.maxds.model.FeedbackForm;
import gov.sec.idap.maxds.model.PostRequestResult;
import gov.sec.idap.maxds.model.Profile;
import gov.sec.idap.maxds.model.TermNames;
import gov.sec.idap.maxds.util.Util;
import gov.sec.prototype.edm.domain.Preferences;

@Service("preferencesServices")
public class PreferencesServices {

	final static String NA_VALUE = "NA";

	@Autowired
	Util util;

	final static Logger logger = LoggerFactory.getLogger(PreferencesServices.class);

	public List<Preferences> fetchPreferences(String userName) {
		List<Preferences> list = null;
		try {
			String sql = "SELECT * FROM norm2_ops.MAXDSPreferences46 where USERID=?";
			System.out.println("Query  >> "+sql);
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			list = jdbcTemplate.query(sql, new Object[] { userName }, new RowMapper<Preferences>() {
				public Preferences mapRow(ResultSet rs, int rowNum) {
					Preferences pref = new Preferences();
					try {
						pref.setProcessId(rs.getInt("PREFERENCEID"));
						pref.setCompanyName(rs.getString("COMPANYNAME"));
						pref.setCode(rs.getString("CODE"));
						pref.setTermName(rs.getString("TERMNAME"));
						pref.setResearchLink(rs.getString("RESEARCH_LINK"));
						pref.setResultLink(rs.getString("RESULTS_LINK"));
						pref.setPreferenceName(rs.getString("PREFERENCENAME"));
						pref.setUserid(rs.getString("USERID"));
						pref.setQuaterly(rs.getString("ISQUATERLY"));
						pref.setValidationStatus(rs.getString("VALIDATION_STATUS"));
						pref.setFsqvLink(rs.getString("FSQV_LINK"));
					} catch (Exception exp) {
					}
					return pref;
				}

			});
			
			//list = jdbcTemplate.queryForList(sql,Preferences.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
	
	
	public List<Preferences> fetchSharedPreferences2(String userName,String preferenceName,String code) {
		List<Preferences> list = null;
		try {
			String sql = "SELECT * FROM  norm2_ops.MAXDSSharedPreferences46 where USERID=? and PREFERENCENAME=? and CODE=?";
			System.out.println("Query  >> "+sql);
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			list = jdbcTemplate.query(sql, new Object[] { userName,preferenceName,code }, new RowMapper<Preferences>() {
				public Preferences mapRow(ResultSet rs, int rowNum) {
					Preferences pref = new Preferences();
					try {
						pref.setProcessId(rs.getInt("PREFERENCEID"));
						pref.setCompanyName(rs.getString("COMPANYNAME"));
						pref.setCode(rs.getString("CODE"));
						pref.setTermName(rs.getString("TERMNAME"));
						pref.setResearchLink(rs.getString("RESEARCH_LINK"));
						pref.setResultLink(rs.getString("RESULTS_LINK"));
						pref.setPreferenceName(rs.getString("PREFERENCENAME"));
						pref.setUserid(rs.getString("USERID"));
						pref.setQuaterly(rs.getString("ISQUATERLY"));
						pref.setValidationStatus(rs.getString("VALIDATION_STATUS"));
					} catch (Exception exp) {
					}
					return pref;
				}

			});
			
			//list = jdbcTemplate.queryForList(sql,Preferences.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public List<Preferences> fetchSharedPreferences(String userName) {
		List<Preferences> list = null;
		try {
			String sql = "SELECT * FROM  norm2_ops.MAXDSSharedPreferences46 where USERID=? ";
			System.out.println("Query  >> "+sql);
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			list = jdbcTemplate.query(sql, new Object[] { userName}, new RowMapper<Preferences>() {
				public Preferences mapRow(ResultSet rs, int rowNum) {
					Preferences pref = new Preferences();
					try {
						pref.setProcessId(rs.getInt("PREFERENCEID"));
						pref.setCompanyName(rs.getString("COMPANYNAME"));
						pref.setCode(rs.getString("CODE"));
						pref.setTermName(rs.getString("TERMNAME"));
						pref.setResearchLink(rs.getString("RESEARCH_LINK"));
						pref.setResultLink(rs.getString("RESULTS_LINK"));
						pref.setPreferenceName(rs.getString("PREFERENCENAME"));
						pref.setUserid(rs.getString("USERID"));
						pref.setQuaterly(rs.getString("ISQUATERLY"));
						pref.setValidationStatus(rs.getString("VALIDATION_STATUS"));
					} catch (Exception exp) {
					}
					return pref;
				}

			});
			
			//list = jdbcTemplate.queryForList(sql,Preferences.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return list;
	}
	

	public List<TermNames> fetchTermNames() {
		List<TermNames> termnames = new ArrayList<TermNames>();
		logger.info("Start fetch termnames  ");

		TermNames termNames1 = new TermNames();
		termNames1.setTermId("XRD");
		termNames1.setName("Research and Development Expense");

		TermNames termNames2 = new TermNames();
		termNames2.setTermId("STKCO");
		termNames2.setName("Share Based Compensation Expense");

		TermNames termNames3 = new TermNames();
		termNames3.setTermId("DA");
		termNames3.setName("Derivative Assets");

		TermNames termNames4 = new TermNames();
		termNames4.setTermId("SALES");
		termNames4.setName("Sales Revenue, Services, Net");

		TermNames termNames5 = new TermNames();
		termNames5.setTermId("CWCLC");
		termNames5.setName("Contract with Customer, Liability, Current");

		termnames.add(termNames1);
		termnames.add(termNames2);
		termnames.add(termNames3);
		termnames.add(termNames4);
		termnames.add(termNames5);

		logger.info("End fetch termnames  ");
		return termnames;
	}

	public void clearProcessedPreferences(List<Integer> clearList) {

		logger.info("Start clearProcessedPreferences " + clearList);
		for (Integer preferenceId : clearList) {
			String query = "update norm2_ops.MAXDSPreferences46 set RESULTS_LINK=?,VALIDATION_STATUS=?,RESEARCH_LINK=?,FSQV_LINK=? WHERE PREFERENCEID=?";

			try {
				JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
				jdbcTemplate.update(query, this.NA_VALUE, this.NA_VALUE, this.NA_VALUE, this.NA_VALUE, preferenceId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		logger.info("End clearProcessedPreferences ");

	}

	public void updateProcessDetails(List<Preferences> preferences) {

		logger.debug("Updating Processed Results.");
		for (Preferences preference : preferences) {
			String updateSql = "update norm2_ops.MAXDSPreferences46 set RESULTS_LINK=?,VALIDATION_STATUS=?,RESEARCH_LINK=?,FSQV_LINK=? WHERE PREFERENCEID=?";
			try {
				JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
				jdbcTemplate.update(updateSql, preference.getResultLink(), preference.getValidationStatus(),
						preference.getResearchLink(), preference.getFsqvLink(), preference.getProcessId());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.debug("End of updateProcessDetails ");

	}
	
	
	public void updateProfile(Profile profile) {
		logger.debug("Updating Processed Results.");
			String updateSql = "update norm2_ops.MAXDSPreferences46 set COMPANYNAME=?,TERMNAME=?,CODE=?,PREFERENCENAME=? WHERE PREFERENCEID=?";
			String code = ""; //update code in the table. 
			String companyName = "";
			if (StringUtils.isNotBlank(profile.getTermName())) {
				String[] tmp = StringUtils.substringsBetween(profile.termName, "(", ")");
				code = tmp[tmp.length-1];
			}
			if(StringUtils.isNotBlank(profile.getCompanyName()) && profile.getCompanyName().contains("),"))
			{
				companyName = profile.getCompanyName();
				companyName = companyName.replace("),", ")$");
			} else {
				companyName = profile.getCompanyName();
			}
			try {
				JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
				jdbcTemplate.update(updateSql, companyName, profile.getTermName(),code, profile.preName, profile.getProfileId());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		logger.debug("End of updateProcessDetails ");

	}

	public PostRequestResult deletePreferences(List<Integer> preferenceInfo) {
		logger.debug("Start of Delete Preference");
		for (Integer preferenceId : preferenceInfo) {
			String deleteSql = "DELETE FROM norm2_ops.MAXDSPreferences46 WHERE PREFERENCEID=  ?";
			try {
				JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
				jdbcTemplate.update(deleteSql, preferenceId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.debug("End of Delete Preference");

		return PostRequestResult.GetSuccessResultWithReturnObject("");

	}
	
	public PostRequestResult deleteSharedPreferences(List<Integer> preferenceInfo) {
		logger.debug("Start of Shared Delete Preference");
		for (Integer preferenceId : preferenceInfo) {
			String deleteSql = "DELETE FROM norm2_ops.MAXDSSharedPreferences46 WHERE PREFERENCEID=  ?";
			try {
				JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
				jdbcTemplate.update(deleteSql, preferenceId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.debug("End of Delete Preference");

		return PostRequestResult.GetSuccessResultWithReturnObject("");

	}
	
	

	public PostRequestResult savePreferences(Preferences saveInfo) {
		logger.info("Saving Preference: " + saveInfo.toString());
		String companyName = saveInfo.getCompanyName();
		if(saveInfo.getCompanyName().contains("),"))
		{
			companyName = companyName.replace("),", ")$");
		}
		
		String insertSql = "INSERT INTO norm2_ops.MAXDSPreferences46 (COMPANYNAME,TERMNAME,CODE,PREFERENCENAME,USERID,isQuaterly) VALUES (?,?,?,?,?,?)";
		try {
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			jdbcTemplate.update(insertSql, companyName,
					(saveInfo.getTermName().isEmpty()) ? "**All Terms**" : saveInfo.getTermName(),
					(saveInfo.getCode().isEmpty()) ? "**All Term Codes**" : saveInfo.getCode(),
					saveInfo.getPreferenceName(), saveInfo.getUserid(), saveInfo.getQuaterly());
		} 
		
		catch (IOException e) 
		{
			logger.info(e.getMessage());
			System.out.println("Skipping this Preference as it already exists in User Preferences"+saveInfo.toString());
		}

		logger.debug("End of Save preference");
		return PostRequestResult.GetSuccessResultWithReturnObject(saveInfo);
	}

	public PostRequestResult saveSharedPreferences(Preferences saveInfo) {
		logger.info("Saving Preference: " + saveInfo.toString());
		String companyName = saveInfo.getCompanyName();
		if(saveInfo.getCompanyName().contains("),"))
		{
			companyName = companyName.replace("),", ")$");
		}
		
		String insertSql = "INSERT INTO norm2_ops.MAXDSSharedPreferences46 (COMPANYNAME,TERMNAME,CODE,PREFERENCENAME,USERID,isQuaterly) VALUES (?,?,?,?,?,?)";
		try 
		{
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			jdbcTemplate.update(insertSql, companyName,
					(saveInfo.getTermName().isEmpty()) ? "**All Terms**" : saveInfo.getTermName(),
					(saveInfo.getCode().isEmpty()) ? "**All Term Codes**" : saveInfo.getCode(),
					saveInfo.getPreferenceName(), saveInfo.getUserid(), saveInfo.getQuaterly());
		} 
		catch (IOException e) 
		{
			System.out.println(e.getMessage());
		}

		logger.debug("End of Save preference");
		return PostRequestResult.GetSuccessResultWithReturnObject(saveInfo);
	}
	
	public PostRequestResult saveFeedback(FeedbackForm saveInfo) 
	{
		logger.info("saveFeedback: "+saveInfo.toString());
		 if (saveInfo.getIssue() == null || saveInfo.getCategory() == null || saveInfo.getName() == null) 
		 {
	            return PostRequestResult.GetErrorResult("Insufficent Inforation to send feedback, Please provide Issuetype,category and  Name");
	     }
		 else 
		 {
	        	String query = "INSERT INTO norm2_ops.MAXDFeedBack (ISSUETYPE,CATEGORY,NAME,EMAIL,PHONE,MESSAGE,USERID) VALUES (?,?,?,?,?,?,?)";
	        	 try 
	        	 {
	        		 	JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
	        			jdbcTemplate.update(query, saveInfo.getIssue(),saveInfo.getCategory(),saveInfo.getName(),saveInfo.getEmail(), saveInfo.getPhone(), saveInfo.getMessage(),saveInfo.getUser());

	             }
	        	 catch (Exception e)
	        	 {
	        		 if(e.getMessage().contains("Violation of UNIQUE KEY constraint 'ak_company_name_code'")) {
	        			 logger.error("feedback exception ::"+e.getMessage().substring(e.getMessage().indexOf("."), e.getMessage().length()));
	        		 }
	             }
	        }
		 logger.debug("End of feedback preference");
		 
		 return PostRequestResult.GetSuccessResultWithReturnObject(saveInfo);
	}


	public List<String> getAllEntities() {
		List<String> termRuleList = new ArrayList<String>();
		try {
			String sql = "select CONCAT(companyName,'(',cik,')') from norm2_ops.ViewSolrEntities";
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			termRuleList = jdbcTemplate.queryForList(sql, String.class);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return termRuleList;
	}
	
	public List<String> getAllIndustries() {
		List<String> industries = new ArrayList<String>();
		try {
			String sql = "select distinct  CONCAT(ISNULL(sic, 'null'),'-', industry) from norm2_ops.ViewSolrEntities";
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			industries = jdbcTemplate.queryForList(sql, String.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return industries;
	}
	public List<String> getAllDivisions() {
		List<String> divisions = new ArrayList<String>();
		try {
			String sql = "select distinct division from norm2_ops.ViewSolrEntities where division is not NULL order by division asc";
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			divisions = jdbcTemplate.queryForList(sql, String.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return divisions;
	}
	public List<String> getAllSectors() {
		List<String> sectors = new ArrayList<String>();
		try {
			String sql = "select distinct sector from norm2_ops.ViewSolrEntities where sector is not NULL order by sector asc";
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			sectors = jdbcTemplate.queryForList(sql, String.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sectors;
	}
	public List<String> getAllFilerCategories() {
		List<String> filerCategories = new ArrayList<String>();
		try {
			String sql = "select distinct filerCategory from norm2_ops.ViewSolrEntities order by filerCategory asc";
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			filerCategories = jdbcTemplate.queryForList(sql, String.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filerCategories;
	}
	
	public List<String> getAllDivisionSectors() {
		List divisionSectors = new ArrayList();
		try {
			String sql = "select distinct CONCAT(ISNULL(division, 'null'),' -> ', sector) as divisionSector from norm2_ops.ViewSolrEntities where division is not NULL  order by divisionSector asc";
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			divisionSectors = jdbcTemplate.queryForList(sql, String.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return divisionSectors;
	}
	
	public List<Map<String, Object>> getPeerDetails(String companyName) {
		List<Map<String, Object>> peerDetails = new ArrayList();
		try {
			String sql = "select  *, CONCAT(ISNULL(sic, 'null'),'-', industry) as sicIndustry from norm2_ops.ViewSolrEntities where companyName= '" + companyName + "'";
			JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
			peerDetails = jdbcTemplate.queryForList(sql);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return peerDetails;
	}
	/*
	 * public static void main(String args[]) { PreferencesServices
	 * preferencesServices = new PreferencesServices();
	 * 
	 * List<Preferences> result = preferencesServices.fetchPreferences();
	 * System.out.println("Result >> "+result.size()); for(Preferences pe : result)
	 * {
	 * 
	 * System.out.println("Name : "+pe.getCompanyName());
	 * 
	 * }
	 * 
	 * 
	 * List<CompanyNames> companyNames = preferencesServices.fetchCompanyList();
	 * for(CompanyNames companyNames2:companyNames) {
	 * logger.info(companyNames2.toString()); }
	 * 
	 * 
	 * }
	 */
	
	
    public List<Map<String, Object>> getFeedback() throws IOException {
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
		return jdbcTemplate.queryForList("select * from norm2_ops.MAXDFeedBack");
	}
    
    public List<Map<String, Object>> getFeedbackByUser(String username)  throws IOException {
		JdbcTemplate jdbcTemplate = util.getJdbcTemplate();
		return jdbcTemplate.queryForList("select * from norm2_ops.MAXDFeedBack where USERID = '" + username + "'");
	}
    
}
