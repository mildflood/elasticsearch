package gov.sec.idap.maxds.elasticsearch.service;

import gov.sec.idap.maxds.calculation.Domain.ExtendedCheckResolvedTermExpression;
import gov.sec.idap.maxds.domain.Entity;
import gov.sec.idap.maxds.domain.ExtendedCheckResolverOutput;
import gov.sec.idap.maxds.domain.NormalizedFact;
import gov.sec.idap.maxds.rest.client.ExtendedElementInfo;
import gov.sec.idap.maxds.service.SecApiService;
import gov.sec.idap.maxds.elasticsearch.repository.NormalizedFactsRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

@Service("normalizedFactService")
public class NormalizedFactService {

    @Autowired
    private SecApiService idapAPIService;

    @Autowired
    private NormalizedFactsRepository directFactsRepo;

    @Value("${maxds.min.fy.to.process:2016}")
    int minFYToProcess;

    @Value("${maxds.skip.fq:false}")
    Boolean skipFQProcessing;

    String corefieldsToQuery = "fact_fp,fact_fy_i,num_value,sub_cik,cell_elt,tag_datatype,sub_filed_dt,num_uom,tag_iord,sub_act_standard,num_ddate,sub_adsh";
// AND fact_fy_i:{%s TO *}
// AND fact_fp: "FY"

    private String getFiscalConfigurationFactQueryLimits() {

        return skipFQProcessing ? String.format("AND fact_fy_i:[%d TO *] AND fact_fp: \"FY\"", minFYToProcess)
                : String.format("AND fact_fy_i:[%d TO *]", minFYToProcess);
    }

    public List<NormalizedFact> findByEntityIdListAndElementNameIdap(String cikIds, String elementName,
            Boolean skipDimensions) {

        String fields = skipDimensions ? corefieldsToQuery : corefieldsToQuery + ",dim_segments";

        String query = String.format("sub_cik:%s AND cell_elt:%s AND tag_custom:false AND num_qtrs:(0 1 4)",
                cikIds, removePrefixfromElementName(elementName));
        query = skipDimensions ? query + " AND -dim_segments:*" : query;
        query = String.format("%s %s", query , getFiscalConfigurationFactQueryLimits());
        return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");

        //  PageRequest request = new PageRequest(0, limit, new Sort(Sort.Direction.DESC, "lastModified_dt"));
    }

    public List<NormalizedFact> findByEntityIdListAndElementNameDirect(String cikIds, String elementName,
            Boolean skipDimensions) {

        PageRequest request = new PageRequest(0, 20000, new Sort(Sort.Direction.DESC, "sub_filed_dt"));
        String ele = removePrefixfromElementName(elementName);
        if (skipDimensions) {
            return directFactsRepo.findByEntityIdListAndElementNameNoDims(ele, cikIds, request);
        } else {
            return directFactsRepo.findByEntityIdListAndElementName(ele, cikIds, request);
        }

    }

    public List<NormalizedFact> findByEntityIdListAndElementNameWithDimensions(String cikIds, String elementName) {

        String query = String.format("sub_cik:%s AND cell_elt:%s AND tag_custom:false AND num_qtrs:(0 1 4) AND dim_segments:*",
                cikIds, removePrefixfromElementName(elementName));
        String fields = corefieldsToQuery + ",dim_segments";
        query = String.format("%s %s", query , getFiscalConfigurationFactQueryLimits());
        return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");
    }

    public List<NormalizedFact> findValueInfoByEntityIdListAndExtendedElements(String cikIds,
            ExtendedCheckResolvedTermExpression cte) {

        String query = null;
        if (cte.balanceType == null || cte.balanceType == "none") {
            query = String.format("sub_cik:%s AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s", cikIds,
                    cte.periodType, cte.buildSolrQueryPartForElementName());
        } else {
            query = String.format("sub_cik:%s AND tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s", cikIds,
                    cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
        }

        query = String.format("%s %s", query, getFiscalConfigurationFactQueryLimits());
        return idapAPIService.findFacts(query, corefieldsToQuery, "sub_filed_dt+desc");

    }

    public List<NormalizedFact> findValueInfoByExtendedElements(
            ExtendedCheckResolvedTermExpression cte) {

        String query = null;
        if (cte.balanceType == null || cte.balanceType == "none") {
            query = String.format("tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s",
                    cte.periodType, cte.buildSolrQueryPartForElementName());
        } else {
            query = String.format("tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s",
                    cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
        }
        query = String.format("%s %s", query, getFiscalConfigurationFactQueryLimits());
        return idapAPIService.findFacts(query, corefieldsToQuery, "sub_filed_dt+desc");

    }

    public List<NormalizedFact> findDimensionFactsInfoByEntityIdListAndExtendedElements(String cikIds,
            ExtendedCheckResolvedTermExpression cte) {

        String query = null;
        if (cte.balanceType == null || cte.balanceType == "none") {
            query = String.format("sub_cik:%s AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s", cikIds,
                    cte.periodType, cte.buildSolrQueryPartForElementName());
        } else {
            query = String.format("sub_cik:%s AND tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s", cikIds,
                    cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
        }
        query = String.format("%s %s", query , getFiscalConfigurationFactQueryLimits());
        String fields = corefieldsToQuery + ",dim_segments";
        return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");

    }

    public List<NormalizedFact> findByEntityIdAndElementNameWithoutDimensions(String entityId, String elementName) {

        Integer cik = Integer.parseInt(entityId);

        String query = String.format("sub_cik:%d AND cell_elt:%s AND tag_custom:false AND num_qtrs:(0 1 4) AND -dim_segments:*", cik,
                removePrefixfromElementName(elementName));
        query = String.format("%s %s", query , getFiscalConfigurationFactQueryLimits());
        return idapAPIService.findFacts(query, corefieldsToQuery, "sub_filed_dt+desc");
    }

    public List<NormalizedFact> findByEntityIdAndElementNameWithDimensions(String entityId, String elementName) {

        Integer cik = Integer.parseInt(entityId);

        String query = String.format("sub_cik:%d AND cell_elt:%s AND tag_custom:false AND num_qtrs:(0 1 4) AND dim_segments:*", cik,
                removePrefixfromElementName(elementName));
        String fields = corefieldsToQuery + ",dim_segments";
        query = String.format("%s %s", query , getFiscalConfigurationFactQueryLimits());
        return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");
    }

    public List<NormalizedFact> findValueInfoByEntityIdAndExtendedElements(String entityId,
            ExtendedCheckResolvedTermExpression cte) {

        Integer cik = Integer.parseInt(entityId);
        String query = null;
        if (cte.balanceType == null || cte.balanceType == "none") {
            query = String.format("sub_cik:%d AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s", cik,
                    cte.periodType, cte.buildSolrQueryPartForElementName());
        } else {
            query = String.format("sub_cik:%d AND tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s", cik,
                    cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
        }
        query = String.format("%s %s", query , getFiscalConfigurationFactQueryLimits());
        return idapAPIService.findFacts(query, corefieldsToQuery, "sub_filed_dt+desc");

    }

    public List<NormalizedFact> findDimensionFactsInfoByEntityIdAndExtendedElements(String entityId,
            ExtendedCheckResolvedTermExpression cte) {

        Integer cik = Integer.parseInt(entityId);
        String query = null;
        if (cte.balanceType == null || cte.balanceType == "none") {
            query = String.format("sub_cik:%d AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s", cik,
                    cte.periodType, cte.buildSolrQueryPartForElementName());
        } else {
            query = String.format("sub_cik:%d AND tag_custom:true AND tag_crdr:%s AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s", cik,
                    cte.balanceType, cte.periodType, cte.buildSolrQueryPartForElementName());
        }
        query = String.format("%s %s", query , getFiscalConfigurationFactQueryLimits());
        String fields = corefieldsToQuery + ",dim_segments";
        return idapAPIService.findFacts(query, fields, "sub_filed_dt+desc");

    }

    private String removePrefixfromElementName(String elementName) {

        int indexOf = elementName.indexOf('_');

        return indexOf > 0 ? elementName.substring(indexOf + 1) : elementName;
    }

    public List<ExtendedCheckResolverOutput> findMatchingExtendedElementNameInfo(
            Collection<String> entityIds, String elementNameQueryInfo,
            String periodType, String balType) {

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(String.format("%s AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND -dim_segments:* %s",
                buildEntitySolrQueryStringForFacts(entityIds),
                periodType,getFiscalConfigurationFactQueryLimits() ));
        if (balType != null && !balType.equals("none")) {
            queryBuilder.append(String.format(" AND tag_crdr:%s", balType));
        }

        queryBuilder.append(elementNameQueryInfo);

        String fields = "cell_elt,sub_name";

        List<ExtendedElementInfo> facts = idapAPIService.findExtendedElementsFacts(queryBuilder.toString(),
                fields, "sub_name+asc");

        HashMap<String, ExtendedCheckResolverOutput> factsByKey = new HashMap<>();
        List<ExtendedCheckResolverOutput> ret = new ArrayList<>();
        for (ExtendedElementInfo f : facts) {
            String key = String.format("%s:%s", f.extendedElement, f.companyName);

            if (factsByKey.containsKey(key)) {
                continue;
            }
            ExtendedCheckResolverOutput item = new ExtendedCheckResolverOutput();
            item.setExtendedElement(f.extendedElement);
            item.setCompanyName(f.companyName);
            factsByKey.put(key, item);
        }

        return new ArrayList<>(factsByKey.values());
    }

    public List<ExtendedCheckResolverOutput> findMatchingExtendedElementNameInfoWithDimension(
            Collection<String> entityIds, String elementNameQueryInfo,
            String periodType, String balType) {

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(String.format("%s AND tag_custom:true AND tag_iord:%s AND num_qtrs:(0 1 4) AND dim_segments:* %s",
                buildEntitySolrQueryStringForFacts(entityIds),
                periodType,getFiscalConfigurationFactQueryLimits()));
        if (balType != null && !balType.equals("none")) {
            queryBuilder.append(String.format(" AND tag_crdr:%s", balType));
        }

        queryBuilder.append(elementNameQueryInfo);

        String fields = "cell_elt,sub_name,dim_segments";

        List<ExtendedElementInfo> facts = idapAPIService.findExtendedElementsFacts(queryBuilder.toString(),
                fields, "sub_name+asc");

        HashMap<String, ExtendedCheckResolverOutput> factsByKey = new HashMap<>();
        List<ExtendedCheckResolverOutput> ret = new ArrayList<>();
        for (ExtendedElementInfo f : facts) {
            String key = String.format("%s:%s", f.extendedElement, f.companyName);

            if (factsByKey.containsKey(key)) {
                continue;
            }
            ExtendedCheckResolverOutput item = new ExtendedCheckResolverOutput();
            item.setExtendedElement(f.extendedElement);
            item.setCompanyName(f.companyName);
            item.setAxisMemberName(f.axisMemberName);
            factsByKey.put(key, item);
        }

        return new ArrayList<>(factsByKey.values());
    }

    public static String buildEntitySolrQueryStringForFacts(Collection<String> entityList) {
        //sub_cik:(51143 63908 21344 34088 310158)
        StringBuilder bld = new StringBuilder();
        bld.append("sub_cik:(");
        for (String cik : entityList) {
            bld.append(String.format("%s ", cik.replaceFirst("^0+(?!$)", "")));
        }
        bld.append(")");
        return bld.toString();
    }

    public static String buildEntitySolrQueryStringForFacts(HashMap<String, Entity> entityList) {
        //sub_cik:(51143 63908 21344 34088 310158)
        StringBuilder bld = new StringBuilder();
        bld.append("(");
        for (Entity e : entityList.values()) {
            bld.append(String.format("%s ", e.getCik().replaceFirst("^0+(?!$)", "")));
        }
        bld.append(")");
        return bld.toString();
    }

    public static String buildEntitySolrQueryStringForFilings(HashMap<String, Entity> entityList) {
        //sub_cik:(51143 63908 21344 34088 310158)
        StringBuilder bld = new StringBuilder();
        bld.append("(");
        for (Entity e : entityList.values()) {
            bld.append(String.format("%s ", e.getEntityId()));
        }
        bld.append(")");
        return bld.toString();
    }

}
