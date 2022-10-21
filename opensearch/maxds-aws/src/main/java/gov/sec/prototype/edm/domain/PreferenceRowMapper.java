package gov.sec.prototype.edm.domain;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PreferenceRowMapper implements RowMapper<Preferences> {

	@Override
	public Preferences mapRow(ResultSet rs, int rowNum) throws SQLException {
		Preferences preferences = new Preferences();
		preferences.setProcessId(rs.getInt("PREFERENCEID"));
		preferences.setCompanyName(rs.getString("COMPANYNAME"));
		preferences.setTermName(rs.getString("TERMNAME"));
		preferences.setCode(rs.getString("CODE"));
		preferences.setPreferenceName(rs.getString("PREFERENCENAME"));
		preferences.setResultLink(rs.getString("RESULTS_LINK"));
		preferences.setValidationStatus(rs.getString("VALIDATION_STATUS"));
		preferences.setResearchLink(rs.getString("RESEARCH_LINK"));
		preferences.setUserid(rs.getString("USERID"));
		preferences.setQuaterly(rs.getString("isQuaterly"));
		preferences.setFsqvLink(rs.getString("FSQV_LINK"));
		return preferences;
	}

}
