/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.sec.idap.maxds.elasticsearch.document.LookupDoc;
import gov.sec.idap.maxds.elasticsearch.service.ReferenceDataService;
//import gov.sec.idap.maxds.solr.document.LookupDoc;

/**
 *
 * @author srira
 */
@Service("adminService")
public class AdminService {

    @Autowired
    private ReferenceDataService lookupDataService;

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public void parseLookupReferenceCSV(InputStream inputStream) throws IOException {
        LOG.debug("Processing CSV file...");
        List<LookupDoc> docs = new ArrayList<LookupDoc>();
        try (Reader reader = new InputStreamReader(inputStream)) {

            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

            for (CSVRecord csvRecord : csvParser) {
                if (csvParser.getCurrentLineNumber() <= 1) {
                    continue;
                }

                if (csvRecord.size() != 5) {
                    LOG.debug("Begin bad row.................." + csvParser.getCurrentLineNumber());
                    for (String col : csvRecord) {
                        LOG.debug(col);
                    }
                    continue;
                }

                  
                LookupDoc item = new LookupDoc();
                item.setName(csvRecord.get(0));
                item.setLabel(csvRecord.get(1));
                item.setDescription(csvRecord.get(2));
                item.setIsTextBlock("True".equals(csvRecord.get(3)));

                String type = csvRecord.get(4);

                switch (type) {
                    case "taxonomyElement":
                        item.setType(LookupDoc.LookupType.taxonomyElement);
                        break;
                    case "termMapGroup":
                        item.setType(LookupDoc.LookupType.termMapGroup);
                        break;

                    case "termRuleCategory":
                        item.setType(LookupDoc.LookupType.termRuleCategory);
                        break;

                    case "termRulePriorityGroup":
                        item.setType(LookupDoc.LookupType.termRulePriorityGroup);
                        break;

                }
                
                if( item.getType() == LookupDoc.LookupType.none) continue;

                docs.add(item);

                //create new entity and save here...
            }
        }
        LOG.debug("Adding  CSV data to SOLR...");
        lookupDataService.add(docs);

    }
}
