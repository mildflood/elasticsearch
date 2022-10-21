package gov.sec.idap.maxds.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;
import gov.sec.idap.maxds.model.DerivationAssets;
import gov.sec.idap.maxds.model.DerivationTrail;

import gov.sec.prototype.edm.domain.Preferences;

@Component
public class ProfileProcessUtils {

	@Autowired
	private Util util;
	
	public List<Preferences> updateInProgress(String processId){
		List<Preferences> inProgressList = new ArrayList<Preferences>();
		Preferences pref = new Preferences();
    	pref.setValidationStatus("In Progress");
    	pref.setResearchLink("NA");
    	pref.setFsqvLink("NA");
    	pref.setResultLink("NA");
    	pref.setProcessId(Integer.parseInt(processId));
    	inProgressList.add(pref);
		return inProgressList;
	}
	public List<Preferences> updateComplete(String processId,String entityId){
		List<Preferences> completedList = new ArrayList<Preferences>();
		Preferences preferences = new Preferences();
		preferences.setResearchLink("https://www.sec.gov/cgi-bin/browse-edgar?action=getcompany&type=&dateb=&owner=exclude&count=40&search_text=&CIK="+entityId);
		
		//add fdqv link
		String fsqvLink = util.getFsqvLinkProperty();
		preferences.setFsqvLink(fsqvLink + "&cik=" + entityId);
        //preferences.setFsqvLink("https://md-up-webidap.ix.sec.gov:18443/fsqv-solr/filingSearchSolr?cik="+entityId);
		
        preferences.setValidationStatus("Processed");
        preferences.setResultLink("Processed");
        preferences.setProcessId(Integer.parseInt(processId));
        List<Preferences> list = new ArrayList<Preferences>();
        completedList.add(preferences);
		return completedList;
	}
	private DerivationTrail calculateDerivationTotal(DerivationTrail derivationTrail){
		List<DerivationAssets> listOfAssets = derivationTrail.getAssets();
		int totalValue = 0;
		for(DerivationAssets asset : listOfAssets) {
			totalValue = totalValue+Integer.parseInt(asset.getValue());
			asset.setValue("{"+asset.getValue()+"}");
		}
		DerivationAssets assets = new DerivationAssets();
		assets.setAsset("Total");
		assets.setValue(String.valueOf(totalValue));
		listOfAssets.add(assets);
		return derivationTrail;
	}
	public List<TermResultsDoc> generateExperssionTrails(List<TermResultsDoc> results){
		results.forEach((termsults)->{
			termsults.derivationTrails = buildDerivationTrails(termsults);
			//termsults.derivationTrails =  calculateDerivationTotal(termsults.derivationTrails);
		});
		return results;
	}
	private DerivationTrail buildDerivationTrails(TermResultsDoc termdoc){
		String resolvedExp = termdoc.getResolvedExpression();
		String[] assetsAndValues = getAssets(termdoc.resolvedExpression);
		return getDerivationDetails(checkDetails(resolvedExp),assetsAndValues);
	}
	private String[] getAssets(String reslovedExpression) {
		String[] result = new String[1];
		if(reslovedExpression.contains("+")) {
			return reslovedExpression.split("[+]");
		}else {
			result[0]=reslovedExpression;
			return result;
		}
	}
	private String checkDetails(String expression) {
		if(expression.contains("Details")) {
			return  expression.substring(0,expression.indexOf("Details"));
		}else {
			if(expression!= null && expression!= "" && expression.indexOf("=")!=-1) {
				return expression.substring(0,expression.indexOf("="));
			}
			return "";
		}
	}
	private DerivationTrail getDerivationDetails(String header,String[] assetsAndValues) {
		DerivationTrail trail = new DerivationTrail();
		trail.setHeader(header);
		List<DerivationAssets> terms = new ArrayList<DerivationAssets>();
		if(assetsAndValues.length > 1) {
		for(String values: assetsAndValues) {
			DerivationAssets term = new DerivationAssets();
			String[] result =values.split("[{]");
			if(result.length > 0 && result[0]!="") {
				term.setAsset(result[0].toString());
			}
			if(result.length > 1 && result[1]!="") {
				String value =result[1].toString();
				term.setValue(value.substring(0, value.indexOf("}")));
			}
			terms.add(term);
			}
		}else {
			DerivationAssets term = new DerivationAssets();
			String result =assetsAndValues[0].toString();
			if(result.indexOf("=")!=-1) {
				String[] valeus = result.split("[=]");
				term.setAsset(valeus[0].toString());
				term.setValue(valeus[1].toString());
				terms.add(term);
			}
		}
		trail.setAssets(terms);
		return trail;
	}
	
	public static void main(String args[]) {
		
		
		
		/*
		 * String test = "hello+hell+how+data_+dish"; if(test.contains("+")) { String[]
		 * rest = test.split("[+]"); for(String r : rest) { System.out.println(r); }
		 * }else { System.out.println("Hi"); }
		 */
		
		
		  String withDetails=
		  "ifrs-full_IntangibleAssetsOtherThanGoodwill = 13,577,000(Virtual Parent) Details="
		  +
		  "CarryingAmountAccumulatedDepreciationAmortisationAndImpairmentAndGrossCarryingAmount=AccumulatedDepreciationAmortisationAndImpairment;ClassesOfIntangibleAssetsOtherThanGoodwill=CustomerrelatedIntangibleAssets {-438,000} "
		  +
		  "+ CarryingAmountAccumulatedDepreciationAmortisationAndImpairmentAndGrossCarryingAmount=GrossCarryingAmount;ClassesOfIntangibleAssetsOtherThanGoodwill=CustomerrelatedIntangibleAssets {484,000} "
		  +
		  "+ CarryingAmountAccumulatedDepreciationAmortisationAndImpairmentAndGrossCarryingAmount=AccumulatedDepreciationAmortisationAndImpairment;ClassesOfIntangibleAssetsOtherThanGoodwill=TechnologybasedIntangibleAssets {-62,480,000} "
		  +
		  "+ CarryingAmountAccumulatedDepreciationAmortisationAndImpairmentAndGrossCarryingAmount=GrossCarryingAmount;ClassesOfIntangibleAssetsOtherThanGoodwill=TechnologybasedIntangibleAssets {75,926,000} "
		  +
		  "+ CarryingAmountAccumulatedDepreciationAmortisationAndImpairmentAndGrossCarryingAmount=GrossCarryingAmount;ClassesOfIntangibleAssetsOtherThanGoodwill=Employeecontracts {3,631,000} "
		  +
		  "+ CarryingAmountAccumulatedDepreciationAmortisationAndImpairmentAndGrossCarryingAmount=GrossCarryingAmount;ClassesOfIntangibleAssetsOtherThanGoodwill=Patentstrademarksandotherrights {220,000} "
		  +
		  "+ CarryingAmountAccumulatedDepreciationAmortisationAndImpairmentAndGrossCarryingAmount=AccumulatedDepreciationAmortisationAndImpairment;ClassesOfIntangibleAssetsOtherThanGoodwill=Patentstrademarksandotherrights {-135,000} "
		  +
		  "+ CarryingAmountAccumulatedDepreciationAmortisationAndImpairmentAndGrossCarryingAmount=AccumulatedDepreciationAmortisationAndImpairment;ClassesOfIntangibleAssetsOtherThanGoodwill=Employeecontracts {-3,631,000}	"
		  ; String
		  withoutDetails="ifrs-full_IntangibleAssetsOtherThanGoodwill = 120,789,000";
		  TermResultsDoc withdoc = new TermResultsDoc();
		  withdoc.resolvedExpression=withDetails; 
		  TermResultsDoc withoutdoc = new TermResultsDoc(); 
		  withoutdoc.resolvedExpression=withoutDetails;
		  List<TermResultsDoc> list = new ArrayList<TermResultsDoc>();
		  list.add(withdoc); 
		  list.add(withoutdoc); 
		  ProfileProcessUtils processUtils = new ProfileProcessUtils(); List<TermResultsDoc> 
		  results = processUtils.generateExperssionTrails(list);
		 
		  for(TermResultsDoc r: list) {
			  System.out.println("Derivations :: "+r.derivationTrails.getHeader());
			  List<DerivationAssets> ass = r.derivationTrails.getAssets();
			  for(DerivationAssets asset : ass) {
				  System.out.println("Asset :: "+asset.getAsset());
				  System.out.println("value :: "+asset.getValue());
			  }
		  }
		
	}
}
