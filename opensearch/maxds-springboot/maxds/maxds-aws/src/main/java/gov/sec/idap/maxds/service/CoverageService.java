/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import gov.sec.idap.maxds.domain.TermCoverageExportOutput;
import gov.sec.idap.maxds.domain.TermCoverageExportOutputItem;
import gov.sec.idap.maxds.domain.TermCoverageItem;
import gov.sec.idap.maxds.domain.TermCoveragePeriodItem;
import gov.sec.idap.maxds.domain.TermRuleHeader;
import gov.sec.idap.maxds.elasticsearch.service.EntityService;
import gov.sec.idap.maxds.elasticsearch.service.TermRuleService;
import gov.sec.idap.maxds.elasticsearch.service.TermResultService;

@Service("coverageService")
public class CoverageService {

    private static final String NEW_LINE_SEPARATOR = "\n";
    @Autowired
    private TermResultService termResultService;

    @Autowired
    private TermRuleService termRulesService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private SecApiService secService;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public TermCoverageExportOutput getCoverageResults(ExportInput input) {

        List<TermCoverageItem> items = new ArrayList<>();

        List<TermRuleHeader> ruleInfos = termRulesService.findLimited();
        Boolean isForAllEntities = input.getIsForAllEntities();
        if( isForAllEntities){
            input.setEntityList(new ArrayList<>());
        }
        if (input.getEntityList() != null && input.getEntityList().size() > 0) {
            isForAllEntities = false;
        }

        HashMap<Integer, HashMap<String, Boolean>> entityCountByYear = new HashMap<>();
        if (isForAllEntities) {
            entityCountByYear = this.getEntitiesByYear(input.getStartYear(), input.getEndYear());
        }

        for (String termId : input.getTermIdList()) {
            TermCoverageItem item = new TermCoverageItem();
            item.setTermId(termId);
            TermRuleHeader tr = ruleInfos.stream().filter(x -> x.getTermId().equals(termId)).findFirst().get();
            if (tr != null) {
                item.setTermName(tr.getName());
            }

            item.setPeriodCoverageInfos(new ArrayList<>());

            HashMap<Integer, List<String>> coverageInfo
                    = termResultService.buildEntityCoverageByYear(
                            termId, input.getStartYear(), input.getEndYear(), input.getIncludeFiscalQuarters(),
                            input.getEntityList(), false, isForAllEntities);

            HashMap<Integer, List<String>> missingDataCoverageInfo
                    = termResultService.buildEntityCoverageByYear(
                            termId, input.getStartYear(), input.getEndYear(), input.getIncludeFiscalQuarters(),
                            input.getEntityList(), true, isForAllEntities);

            for (int year = input.getStartYear(); year <= input.getEndYear(); year++) {
                TermCoveragePeriodItem periodItem = new TermCoveragePeriodItem();
                periodItem.year = year;
                periodItem.countMappedEntities = 0;
                periodItem.countDerivedZeroEntities = 0;

                if (coverageInfo.containsKey(year)) {
                    periodItem.countMappedEntities = coverageInfo.get(year).size();

                    if (isForAllEntities && entityCountByYear.containsKey(year)) {
                        // we need to limit the results to the entities for the year.. as there could be 
                        //stray data....i.e. we do nto have sec filing for the year but we have term results...
                        periodItem.countMappedEntities = 0;
                        HashMap<String, Boolean> validEntities = entityCountByYear.get(year);
                        for (String entity : coverageInfo.get(year)) {
                            if (validEntities.containsKey(entity)) {
                                periodItem.countMappedEntities++;
                            }
                        }
                    }
                }
                if (missingDataCoverageInfo.containsKey(year)) {
                    //the same entity could be in both missing and covered..
                    //so we need to make sure we don't double count...
                    if (coverageInfo.containsKey(year)) {
                        int counter = 0;
                        List<String> covEntities = coverageInfo.get(year);
                        for (String entity : missingDataCoverageInfo.get(year)) {

                            if (isForAllEntities
                                    && entityCountByYear.containsKey(year)
                                    && !entityCountByYear.get(year).containsKey(entity)) {
                                continue;
                            }

                            if (!covEntities.contains(entity)) {
                                counter++;
                            }
                        }
                        periodItem.countDerivedZeroEntities = counter;
                    } else {
                        //could we have the same entity in both lists...
                        periodItem.countDerivedZeroEntities = missingDataCoverageInfo.get(year).size();
                        if (isForAllEntities && entityCountByYear.containsKey(year)) {
                            // we need to limit the results to the entities for the year.. as there could be 
                            //stray data....i.e. we do nto have sec filing for the year but we have term results...
                            periodItem.countDerivedZeroEntities = 0;
                            HashMap<String, Boolean> validEntities = entityCountByYear.get(year);
                            for (String entity : missingDataCoverageInfo.get(year)) {
                                if (validEntities.containsKey(entity)) {
                                    periodItem.countDerivedZeroEntities++;
                                }
                            }
                        }
                    }
                }
                item.getPeriodCoverageInfos().add(periodItem);

            }

            items.add(item);
        }

        return buildOutput(input, items, entityCountByYear);
    }

    private TermCoverageExportOutput buildOutput(ExportInput input,
            List<TermCoverageItem> items, HashMap<Integer, HashMap<String, Boolean>> entityCountByYear) {

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        TermCoverageExportOutput ret = new TermCoverageExportOutput();
        ret.columnList = new ArrayList<>();
        ret.exportItemList = new ArrayList<>();
        ret.columnList.add("Term Name");
        ret.columnList.add("Term Id");

        for (int year = input.getStartYear(); year <= input.getEndYear(); year++) {
            ret.columnList.add((new Integer(year)).toString() + " Entities With Data");
            ret.columnList.add((new Integer(year)).toString() + " Entities With Derived Zero Data");
            ret.columnList.add((new Integer(year)).toString() + " Total Entities");
            ret.columnList.add((new Integer(year)).toString() + " Coverage");
        }
        int totalCount = 1;

        if (!input.getEntityList().isEmpty()) {
            totalCount = input.getEntityList().size();
        }

        for (TermCoverageItem item : items) {
            TermCoverageExportOutputItem outputItem = new TermCoverageExportOutputItem();
            outputItem.termId = item.getTermId();
            outputItem.termName = item.getTermName();
            outputItem.coverageValues = new ArrayList<>();

            for (TermCoveragePeriodItem pItem : item.getPeriodCoverageInfos()) {
                if (input.getEntityList().isEmpty()) {
                    if (entityCountByYear.containsKey(pItem.year)) {
                        totalCount = entityCountByYear.get(pItem.year).size();
                    } else {
                        totalCount = (int) this.entityService.count();
                    }

                    //we need to know the total entity for the year...
                }
                pItem.countTotalEntities = totalCount;
                outputItem.coverageValues.add(String.format("%d", pItem.countMappedEntities));
                outputItem.coverageValues.add(String.format("%d", pItem.countDerivedZeroEntities));
                outputItem.coverageValues.add(String.format("%d", pItem.countTotalEntities));
                if (pItem.countTotalEntities > 0) {

                    Double d = ((double) (pItem.countMappedEntities + pItem.countDerivedZeroEntities)) / pItem.countTotalEntities;

                    outputItem.coverageValues.add(df.format(d * 100) + "%");
                } else {
                    outputItem.coverageValues.add("0%");
                }

            }

            ret.exportItemList.add(outputItem);
        }

        return ret;
    }

    public void exportCsvToOutputStream(ServletOutputStream stream, ExportInput input) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        try {
            TermCoverageExportOutput exportOutput = getCoverageResults(input);

            List<TermCoverageExportOutputItem> items = exportOutput.exportItemList;
            List<String> header = new ArrayList();
            header.addAll(exportOutput.columnList);

            OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            CSVPrinter csvFilePrinter = new CSVPrinter(streamWriter, csvFileFormat);
            //LOG.debug("Header: " + header);
            csvFilePrinter.printRecord(header);
            for (TermCoverageExportOutputItem item : items) {
                List vals = new ArrayList();
                vals.add(item.termName);
                vals.add(item.termId);
                vals.addAll(item.coverageValues);

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

    private HashMap<Integer, HashMap<String, Boolean>> getEntitiesByYear(int startYear, int endYear) {
        HashMap<Integer, HashMap<String, Boolean>> entityCountByYear = new HashMap<>();

        for (int year = startYear; year <= endYear; year++) {
            if (!entityCountByYear.containsKey(year)) {

                entityCountByYear.put(year, this.secService.getEntitiesForYear(year));

            }

        }

        return entityCountByYear;
    }

}
