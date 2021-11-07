package gov.sec.idap.maxds.model;

import java.util.ArrayList;
import java.util.List;

import gov.sec.idap.maxds.api.vo.DerivationTerms;
import gov.sec.idap.maxds.api.vo.DerivationTrailVO;

public class DerivationTrail {

	private String header;
	private List<DerivationAssets> assets;
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public List<DerivationAssets> getAssets() {
		return assets;
	}
	public void setAssets(List<DerivationAssets> assets) {
		this.assets = assets;
	}
	
	public DerivationTrailVO createVO() {
		DerivationTrailVO vo = new DerivationTrailVO();
		vo.setHeader(this.header);
		List<DerivationTerms> terms = new ArrayList<DerivationTerms>();
		for (DerivationAssets a: assets) {
			terms.add(a.createVO());
		}
		vo.setTerms(terms);
		
		return vo;
	}
}
