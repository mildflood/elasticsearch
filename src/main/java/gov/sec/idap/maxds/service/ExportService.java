/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.sec.idap.maxds.domain.ExportInput;
import gov.sec.idap.maxds.domain.ExportItem;
import gov.sec.idap.maxds.domain.ExportOutput;
import gov.sec.idap.maxds.domain.ValidationStatus;
//import gov.sec.idap.maxds.solr.document.TermResultsDoc;
import gov.sec.idap.maxds.elasticsearch.document.TermResultsDoc;

@Service("exportService")
public class ExportService {

    private static final String NEW_LINE_SEPARATOR = "\n";

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TermResultService termResultService;

    public ExportOutput getExportTermResults(ExportInput input) {
        return getExportTermResults(input, 100);
    }

    public void exportSingleTermEntityCsvToOutputStream(ServletOutputStream stream, ExportInput input) {
        try {

            List<TermResultsDoc> results = getTermResultsForEntityAndTermId(input);

            List<String> header = new ArrayList();
            header.add("Company Name");
            header.add("CIK");
            header.add("Term Name");

            header.add("Fiscal Year");
            header.add("Fiscal Quarter");
            header.add("Period End Date");
            header.add("Value");
            header.add("Variance With Previous Period");
            header.add("Variance With Previous Year");
            header.add("Resolved Expression");
            header.add("Validation Status");
            header.add("Validation Message");

            OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            CSVPrinter csvFilePrinter = new CSVPrinter(streamWriter, csvFileFormat);
            //LOG.debug("Header: " + header);
            csvFilePrinter.printRecord(header);
            for (TermResultsDoc item : results) {
                List vals = new ArrayList();
                vals.add(item.companyName);
                vals.add(item.getCikString());

                vals.add(item.termName);

                vals.add(item.FY);
                vals.add(item.FQ);
                vals.add(item.periodEndDate);
                vals.add(item.value);

                if (item.percentVarianceWithPrevPeriod == Double.MIN_VALUE) {
                    vals.add("");
                } else {
                    vals.add(item.percentVarianceWithPrevPeriod);
                }
                if (item.percentVarianceWithPrevYear == Double.MIN_VALUE) {
                    vals.add("");
                } else {
                    vals.add(item.percentVarianceWithPrevYear);
                }

                vals.add(item.resolvedExpression);
                vals.add(item.validationStatus);
                if (item.validationMessages != null && item.validationMessages.size() > 0) {
                    StringBuilder strB = new StringBuilder();
                    for (String str : item.validationMessages) {
                        strB.append(str);
                    }
                    vals.add(strB.toString());
                }

                //LOG.debug("Row: " + vals);
                csvFilePrinter.printRecord(vals);
            }
            streamWriter.flush();
            streamWriter.close();
            csvFilePrinter.close();
        } catch (IOException e) {
            LOG.error("Exception raised in exportSingleTermEntityCsvToOutputStream. Exp Message: "
                    + e.getMessage());
        }

    }

    public void exportCsvToOutputStream(ServletOutputStream stream, ExportInput input) {
        try {
            ExportOutput exportOutput = getExportTermResults(input, 0);
            List<String> terms = exportOutput.getTermNameList();
            List<ExportItem> values = exportOutput.getExportItemList();
            List<String> header = new ArrayList();
            header.add("Company");
            header.add("CIK");
            header.add("Reporting Period");
            header.add("Period End Date");
            if (input.getIncludeValidationInfos()) {

                for (String term : terms) {

                    header.add(term);
                    header.add(term + " Resolved Expression");
                    header.add(term + " Validation Status");
                    header.add(term + " Validation Message");
                }

            } else {
                for (String term : terms) {

                    header.add(term);
                    header.add(term + " Resolved Expression");

                }
            }

            OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            CSVPrinter csvFilePrinter = new CSVPrinter(streamWriter, csvFileFormat);
            //LOG.debug("Header: " + header);
            csvFilePrinter.printRecord(header);
            for (ExportItem item : values) {
                List vals = new ArrayList();
                vals.add(item.getCompany());
                vals.add(item.getCik());
                vals.add(item.getReportingPeriod());
                vals.add(item.getPeriodEndDate());
                int counter = 0;
                for (Double d : item.getTermResultValues()) {

                    if (d == Double.MIN_VALUE) {
                        vals.add("");
                    } else {
                        vals.add(String.valueOf(d));
                    }

                    vals.add(item.getResolvedExpressions().get(counter));
                    if (input.getIncludeValidationInfos()) {

                        vals.add(item.getValidationStatuses().get(counter));
                        vals.add(item.getValidationMessages().get(counter));

                    }

                    counter++;
                }
                //LOG.debug("Row: " + vals);
                csvFilePrinter.printRecord(vals);
            }
            streamWriter.flush();
            streamWriter.close();
            csvFilePrinter.close();
        } catch (IOException e) {
            LOG.error("Exception raised in exportCsvToOutputStream. Exp Message: "
                    + e.getMessage());
        }

    }

    private ExportOutput getExportTermResults(ExportInput input, int limit) {
        List<TermResultsDoc> results = getTermResults(input);
        return BuildExportOutput(input, results, limit);
    }

 private List<TermResultsDoc> getTermResults(ExportInput input) {
    	
    	List<TermResultsDoc> termResults = new ArrayList<TermResultsDoc>();
    	if(input.getEntityList().size() > 200) {
    		int n = 200;
    		for (int i = 1; i <= input.getEntityList().size(); i += n) {
    			List<String> temp = input.getEntityList().subList(i,  Math.min(i+ n, input.getEntityList().size()));//subArray(input.getEntityList(), ((i * n) - n), (i * n) - 1);
    			 if (input.getIncludeFiscalYears() && input.getIncludeFiscalQuarters()) {
    				 termResults.addAll(this.termResultService.findByTermsEntitiesAndFY(input.getTermIdList(), temp, input.getStartYear(), input.getEndYear(),
    		                    input.getIsForAllEntities()));
    		        } else if (input.getIncludeFiscalYears()) {
    		        	termResults.addAll(this.termResultService.findByTermsEntitiesAndFYYealyOnly(input.getTermIdList(),
    		            		temp, input.getStartYear(), input.getEndYear(),
    		                    input.getIsForAllEntities()));
    		        } else if (input.getIncludeFiscalQuarters()) {
    		        	termResults.addAll(this.termResultService.findByTermsEntitiesAndFYQuarterlyOnly(input.getTermIdList(),
    		            		temp, input.getStartYear(), input.getEndYear(),
    		                    input.getIsForAllEntities()));
    		        }
    		}
    	} else {
    		if (input.getIncludeFiscalYears() && input.getIncludeFiscalQuarters()) {
				 termResults.addAll(this.termResultService.findByTermsEntitiesAndFY(input.getTermIdList(), input.getEntityList(), input.getStartYear(), input.getEndYear(),
		                    input.getIsForAllEntities()));
		        } else if (input.getIncludeFiscalYears()) {
		        	termResults.addAll(this.termResultService.findByTermsEntitiesAndFYYealyOnly(input.getTermIdList(),
		        			input.getEntityList(), input.getStartYear(), input.getEndYear(),
		                    input.getIsForAllEntities()));
		        } else if (input.getIncludeFiscalQuarters()) {
		        	termResults.addAll(this.termResultService.findByTermsEntitiesAndFYQuarterlyOnly(input.getTermIdList(),
		        			input.getEntityList(), input.getStartYear(), input.getEndYear(),
		                    input.getIsForAllEntities()));
		        }
    	}
    	
       

        return termResults;
    }

    private List<TermResultsDoc> getTermResultsForEntityAndTermId(ExportInput input) {
        if (input.getIncludeFiscalYears() && input.getIncludeFiscalQuarters()) {
            return this.termResultService.findByTermIdEntityIdAndFY(input.getTermId(), input.getEntityId(), input.getStartYear(), input.getEndYear());
        } else if (input.getIncludeFiscalYears()) {
            return this.termResultService.findByTermIdEntityIdAndFYYealyOnly(input.getTermId(), input.getEntityId(), input.getStartYear(), input.getEndYear());
        } else if (input.getIncludeFiscalQuarters()) {
            return this.termResultService.findByTermIdEntityIdAndFYQuarterlyOnly(input.getTermId(), input.getEntityId(), input.getStartYear(), input.getEndYear());
        }

        return null;
    }

    private ExportOutput BuildExportOutput(ExportInput input,
            List<TermResultsDoc> termResults, int limit) {

        ExportOutput output = new ExportOutput();
        HashMap<String, Integer> resultPosition = new HashMap<>();
        int pos = 0;
        for (String termId : input.getTermIdList()) {
            resultPosition.put(termId, pos++);
        }

        HashMap<String, ExportItem> outputList = new HashMap<>();

        for (TermResultsDoc termResult : termResults) {
            String key = getKey(termResult);
            ExportItem item = null;
            if (outputList.containsKey(key)) {
                item = outputList.get(key);
                if (termResult.periodEndDate != null && !termResult.periodEndDate.isEmpty()) {
                    item.setPeriodEndDate(termResult.periodEndDate);
                }
            } else {
                item = new ExportItem();
                item.setCompany(termResult.companyName);
                item.setCik(termResult.getCikString());
                item.setPeriodEndDate(termResult.periodEndDate);
                item.setAccession(termResult.accession);
                item.setTicker(termResult.stockSymbol);
                item.setReportingPeriod(String.format("%d%s", termResult.FY, termResult.FQ));
                item.setTermResultValues(new ArrayList<>(Collections.nCopies(input.getTermIdList().size(), Double.MIN_VALUE)));
                item.setResolvedExpressions(new ArrayList<>(Collections.nCopies(input.getTermIdList().size(), "")));
                if (input.getIncludeValidationInfos()) {
                    item.setValidationStatuses(new ArrayList<>(Collections.nCopies(input.getTermIdList().size(), "")));
                    item.setValidationMessages(new ArrayList<>(Collections.nCopies(input.getTermIdList().size(), "")));
                }

                outputList.put(key, item);
            }
            int indexPos = resultPosition.get(termResult.termId);

            item.getTermResultValues().set(indexPos, termResult.value);
            item.getResolvedExpressions().set(indexPos, termResult.resolvedExpression);
            if (input.getIncludeValidationInfos()
                    && termResult.validationStatus != ValidationStatus.na
                    && termResult.validationMessages != null
                    && !termResult.validationMessages.isEmpty()) {
                item.getValidationStatuses().set(indexPos, termResult.validationStatus.toString());

                StringBuilder strB = new StringBuilder();
                for (String str : termResult.validationMessages) {
                    strB.append(str);
                }

                item.getValidationMessages().set(indexPos, strB.toString());
            }

        }

        output.setTermNameList(input.getTermIdList());
        ArrayList<ExportItem> items = new ArrayList<ExportItem>(outputList.values());
        Collections.sort(items, new ExportItem());
        output.setExportItemList(items);
//        if (limit > 0 && items.size() > limit) {
//            //very inefficient.. need to do this in db..
//            //need to sort in db as well..
//            output.setExportItemList(items.subList(0, 99));
//        } else {
//            output.setExportItemList(items);
//        }

        return output;
    }

    private String getKey(TermResultsDoc termResult) {
        return String.format("%s:%s:%d", termResult.getEntityId(), termResult.FQ, termResult.FY);
    }

    private void setCSVHeader(CSVPrinter printer, ExportOutput output) throws IOException {
        ArrayList<String> header = new ArrayList<>();
        header.add("Company Name");
        header.add("Ticker");
        header.add("Period");

        header.addAll(output.getTermNameList());

        printer.printRecord(header);

    }

    private void setCSVData(CSVPrinter printer, ExportOutput output) throws IOException {

        for (ExportItem item : output.getExportItemList()) {
            ArrayList<Object> record = new ArrayList<>();
            record.add(item.getCompany());
            record.add(item.getTicker());
            record.add(item.getReportingPeriod());

            record.addAll(item.getTermResultValues());
            printer.printRecord(record);
        }

    }
}
