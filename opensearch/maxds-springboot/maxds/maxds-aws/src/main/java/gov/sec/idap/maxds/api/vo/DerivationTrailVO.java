package gov.sec.idap.maxds.api.vo;

import java.util.List;

public class DerivationTrailVO {
		private String header;
		private List<DerivationTerms> terms;
		
		public String getHeader() {
			return header;
		}
		public void setHeader(String header) {
			this.header = header;
		}
		public List<DerivationTerms> getTerms() {
			return terms;
		}
		public void setTerms(List<DerivationTerms> terms) {
			this.terms = terms;
		}
		
		
}
