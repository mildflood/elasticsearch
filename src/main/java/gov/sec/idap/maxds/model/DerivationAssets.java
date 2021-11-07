package gov.sec.idap.maxds.model;

import gov.sec.idap.maxds.api.vo.DerivationTerms;

public class DerivationAssets {
	
	private String asset;
	private String value;
	
	public String getAsset() {
		return asset;
	}
	public void setAsset(String asset) {
		this.asset = asset;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public DerivationTerms createVO() {
		DerivationTerms ret = new DerivationTerms();
		ret.setTerm(this.asset);
		ret.setValue(this.value);
		return ret;
	}
	
}
