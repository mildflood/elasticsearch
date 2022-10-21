/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import gov.sec.idap.maxds.domain.GroupTermMapInformation;
import gov.sec.idap.maxds.domain.PostRequestResult;
import gov.sec.idap.maxds.domain.TermMapGroup;
import gov.sec.idap.maxds.domain.TermMapInformation;
import gov.sec.idap.maxds.domain.TermMapInformationDisplay;
import gov.sec.idap.maxds.elasticsearch.document.LookupDoc;
import gov.sec.idap.maxds.elasticsearch.document.TermMapInformationDoc;
import gov.sec.idap.maxds.elasticsearch.document.TermRuleDoc;
import gov.sec.idap.maxds.elasticsearch.repository.LookupDocRepository;
//import gov.sec.idap.maxds.solr.document.LookupDoc;
//import gov.sec.idap.maxds.solr.document.TermMapInformationDoc;
//import gov.sec.idap.maxds.solr.document.TermRuleDoc;
//import gov.sec.idap.maxds.solr.repository.LookupDocRepository;
//import gov.sec.idap.maxds.solr.repository.TermMapInformationDocRepository;
//import gov.sec.idap.maxds.solr.repository.TermRuleDocRepository;
import gov.sec.idap.maxds.elasticsearch.repository.TermMapInformationRepository;
import gov.sec.idap.maxds.elasticsearch.repository.TermRuleRepository;
import gov.sec.idap.maxds.elasticsearch.service.ReferenceDataService;

@Service("termMapServiceSolr")
public class TermMapService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private static final String NEW_LINE_SEPARATOR = "\n";

    @Autowired
    private ReferenceDataService lookupDataService;

    @Autowired
    private TermMapInformationRepository mapInformationRepository;

    @Autowired
    private TermRuleRepository termRuleRepository;

    @Autowired
    private LookupDocRepository solrRepository;

    public PostRequestResult deleteGroup(String groupName) {
        if (groupName.equals("Compustat")) {
            PostRequestResult ret = new PostRequestResult();
            ret.status = false;
            ret.errorMessage = "Hard coded prevention of deleting compustat group. Will be removed before updating SEC code";
            return ret;
        }

        //need to remove the map information....
        mapInformationRepository.deleteByMapName(groupName);
        solrRepository.deleteByNameAndType(groupName, LookupDoc.LookupType.termMapGroup.toString());

        PostRequestResult ret = new PostRequestResult();
        ret.status = true;
        return ret;
    }

    public PostRequestResult deleteGroupItem(GroupTermMapInformation info) {
        //only delete the groups passed in as some groups might be invisible...
        for (TermMapInformation mapInfo : info.mappedInfoSets) {
            mapInformationRepository.deleteByTermIdAndMapName(info.termId, mapInfo.mapName);
        }

        PostRequestResult ret = new PostRequestResult();
        ret.status = true;
        return ret;
    }

    public GroupTermMapInformation saveTermMapInformation(GroupTermMapInformation info) {
        if (info.termId == null || info.termId.isEmpty()) {
            int counter = 1;
            while (true) {
                //need to auto gen term Id...
                info.termId = "TBD" + String.format("%05d", counter);
                TermMapInformationDoc existingInfo = mapInformationRepository.findOneByTermId(info.termId);
                if (existingInfo == null) {
                    break;
                }
                counter++;
            }

        } else {
            mapInformationRepository.deleteByTermId(info.termId);
        }
        List<TermMapInformationDoc> saveList = new ArrayList<>();
        for (TermMapInformation mapInfo : info.mappedInfoSets) {
            mapInfo.termId = info.termId;
            mapInfo.id = mapInfo.mapName + "_" + mapInfo.mapTermId;
            if (mapInfo.mapTermId != null) {
                saveList.add(new TermMapInformationDoc(mapInfo));
            }

        }
        mapInformationRepository.saveAll(saveList);

        return info;
    }

    public List<GroupTermMapInformation> getAllTermMapInformations() {

        List<TermRuleDoc> rules = termRuleRepository.findNameInfos();

        HashMap<String, TermRuleDoc> validRules = new HashMap();
        for (TermRuleDoc tr : rules) {
            validRules.put(tr.getTermId(), tr);
        }

        HashMap<String, String> unknownTermRules = new HashMap();

        Iterable<TermMapInformationDoc> mapInfos = mapInformationRepository.findAll();
        HashMap<String, TermMapInformationDoc> mapsByTermIdAndGroup = new HashMap();
        for (TermMapInformationDoc ti : mapInfos) {
            String key = String.format("%s:%s", ti.getMapName(), ti.getTermId());
            mapsByTermIdAndGroup.put(key, ti);

            if (!validRules.containsKey(ti.getTermId())) {
                unknownTermRules.put(ti.getTermId(), ti.getTermId());
            }
        }
        List<TermMapGroup> groups = lookupDataService.getAllMappingGroups();

        HashMap<String, GroupTermMapInformation> mapsByTermId = new HashMap();

        for (TermRuleDoc tr : rules) {

            GroupTermMapInformation data = new GroupTermMapInformation();
            data.termId = tr.getTermId();
            data.name = tr.getName();
            data.description = tr.getDescription();
            data.isTermRule = true;
            data.mappedInfoSets = new ArrayList<>();
            for (TermMapGroup group : groups) {
                String key = String.format("%s:%s", group.mapName, data.termId);

                if (mapsByTermIdAndGroup.containsKey(key)) {
                    data.mappedInfoSets.add(mapsByTermIdAndGroup.get(key).getTermMapInformation());
                } else {
                    TermMapInformation empty = new TermMapInformation();
                    empty.mapName = group.mapName;
                    empty.termId = tr.getTermId();
                    data.mappedInfoSets.add(empty);
                }
            }

            mapsByTermId.put(data.termId, data);
        }

        //now to add the ones that are not real term rules...
        for (String termId : unknownTermRules.keySet()) {

            GroupTermMapInformation data = new GroupTermMapInformation();
            data.termId = termId;
            data.isTermRule = false;
            data.mappedInfoSets = new ArrayList<>();
            for (TermMapGroup group : groups) {
                String key = String.format("%s:%s", group.mapName, data.termId);

                if (mapsByTermIdAndGroup.containsKey(key)) {
                    data.mappedInfoSets.add(mapsByTermIdAndGroup.get(key).getTermMapInformation());
                } else {
                    TermMapInformation empty = new TermMapInformation();
                    empty.mapName = group.mapName;
                    empty.termId = termId;
                    data.mappedInfoSets.add(empty);
                }
            }

            mapsByTermId.put(data.termId, data);

        }

        List<GroupTermMapInformation> ret = new ArrayList<>(mapsByTermId.values());

        Collections.sort(ret, new GroupTermMapInformation());
        return ret;

    }

    public HashMap<String, TermMapInformationDoc> findCompustatIds(Collection<String> termIds) {
        HashMap<String, TermMapInformationDoc> ret = new HashMap<>();
        List<TermMapInformationDoc> results = this.mapInformationRepository.findByMapNameAndTermIdIn("Compustat", termIds, PageRequest.of(0, 2000));

        results.forEach((doc) -> {
            ret.put(doc.getTermId(), doc);
        });

        return ret;
    }

    public List<TermMapInformationDisplay> getTermMapInformation() {

        Iterable<TermMapInformationDoc> mapInfos = mapInformationRepository.findAll();

        HashMap<String, TermMapInformationDisplay> mapsByTermId = new HashMap();

        List<TermRuleDoc> rules = termRuleRepository.findNameInfos();
        HashMap<String, TermRuleDoc> rulebyId = new HashMap();
        for (TermRuleDoc tr : rules) {
            rulebyId.put(tr.getTermId(), tr);
        }

        for (TermMapInformationDoc info : mapInfos) {

            if (info.getTermId() == null || info.getTermId().length() == 0) {
                continue;
            }

            TermMapInformationDisplay current = null;
            if (!mapsByTermId.containsKey(info.getTermId())) {
                current = new TermMapInformationDisplay();
                current.termId = info.getTermId();
                current.mappedInfoSets = new ArrayList();
                if (rulebyId.containsKey(info.getTermId())) {
                    current.name = rulebyId.get(info.getTermId()).getName();
                    current.description = rulebyId.get(info.getTermId()).getDescription();
                }
                mapsByTermId.put(info.getTermId(), current);
            }

            current.mappedInfoSets.add(info.getTermMapInformation());
        }

        for (TermRuleDoc tr : rules) {

            if (!mapsByTermId.containsKey(tr.getTermId())) {
                //we do not have any mappings for these terms...
                TermMapInformationDisplay current = new TermMapInformationDisplay();
                current.termId = tr.getTermId();
                current.name = tr.getName();
                current.description = tr.getDescription();
                current.mappedInfoSets = new ArrayList();
                mapsByTermId.put(tr.getTermId(), current);

            }
        }

        List<TermMapInformationDisplay> ret = new ArrayList<>(mapsByTermId.values());

        Collections.sort(ret, new TermMapInformationDisplay());
        return ret;

    }

    public void exportTermMapInformation(ServletOutputStream stream) {
        try {

            List<String> header = new ArrayList();
            header.add("MAXDS Term ID");
            header.add("MAXDS Term Name");
            header.add("MAXDS Description");
            List<TermMapGroup> groups = lookupDataService.getAllMappingGroups();
            for (TermMapGroup group : groups) {
                header.add(group.mapName + " Term ID");
                header.add(group.mapName + " Term Name");
                header.add(group.mapName + " Description");
                header.add(group.mapName + " Mapping");
            }

            OutputStreamWriter streamWriter = new OutputStreamWriter(stream);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            CSVPrinter csvFilePrinter = new CSVPrinter(streamWriter, csvFileFormat);
            //LOG.debug("Header: " + header);
            csvFilePrinter.printRecord(header);

            List<TermMapInformationDisplay> results = getTermMapInformation();

            for (TermMapInformationDisplay map : results) {
                List vals = new ArrayList();
                vals.add(map.termId);
                vals.add(map.name);
                vals.add(map.description);

                HashMap<String, String> dataPoints = new HashMap();
                for (TermMapInformation info : map.mappedInfoSets) {
                    dataPoints.put(info.mapName + " Term ID", info.mapTermId);
                    dataPoints.put(info.mapName + " Term Name", info.mapTermName);
                    dataPoints.put(info.mapName + " Description", info.mapTermDescription);
                    dataPoints.put(info.mapName + " Mapping", info.mapTermMappingInfo);

                }

                for (TermMapGroup group : groups) {

                    vals.add(getDataPoint(group.mapName + " Term ID", dataPoints));
                    vals.add(getDataPoint(group.mapName + " Term Name", dataPoints));
                    vals.add(getDataPoint(group.mapName + " Description", dataPoints));
                    vals.add(getDataPoint(group.mapName + " Mapping", dataPoints));
                }

                csvFilePrinter.printRecord(vals);
            }

            streamWriter.flush();
            streamWriter.close();
            csvFilePrinter.close();
        } catch (IOException e) {
            LOG.error("Exception raised in exportTermMapInformation. Exp Message: "
                    + e.getMessage());
        }

    }

    private String getDataPoint(String key, HashMap<String, String> dataPoints) {
        return dataPoints.containsKey(key) ? dataPoints.get(key) : "";
    }

}
