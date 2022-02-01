/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.repository;

import gov.sec.idap.maxds.solr.document.TermResultsDoc;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.repository.Facet;
import org.springframework.data.solr.repository.Pivot;
import org.springframework.data.solr.repository.Query;

import org.springframework.data.solr.repository.SolrCrudRepository;

public interface TermResultsDocRepository extends SolrCrudRepository<TermResultsDoc, String> {

    public void deleteByTermId(String termRuleId);

    public void deleteByTermName(String termName);

    public void deleteByTermIdAndEntityId(String termRuleId, String entityId);

    public List<TermResultsDoc> findByTermIdAndMyDocType(String termId, String myDocType);

    public List<TermResultsDoc> findByEntityIdAndMyDocType(String entityId, String myDocType);

    public List<TermResultsDoc> findByEntityIdAndTermNameAndMyDocType(String entityId, String name, String myDocType);
    
     @Query(value = "entityId:?0 AND termId:?1 AND myDocType:?2")
     public List<TermResultsDoc> findByEntityIdListAndTermIdAndMyDocType(String entityIds, String termId, String myDocType);


    public List<TermResultsDoc> findByEntityIdAndTermIdAndMyDocType(String entityId, String termId, String myDocType);

    //  @Query(value="termId:?0&rank:[* TO ?1]&myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermIdAndMyDocTypeAndRankLessThanEqual(String termId,
            String myDocType, int rank);

    //  @Query(value="{'termId':{$in : ?0 } , 'entityId': { $in : ?1 } , 'FY' : { $gte :  ?2, $lte : ?3}}", 
    // fields="{'FQ':1,'FY':1,'value':1,'entityId':1,'termId':1,'termName':1,'companyName':1,'filingDate':1}")
    @Query(value = "termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermIdInAndEntityIdInAndFYBetween(Collection<String> termIds,
            Collection<String> entityIds, int start, int end);
    
    
    @Query(value = "termId:(?0) AND entityId:?1 AND FY:?2 AND FQ:?3 AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermIdInAndEntityIdAndFYAndFQ(Collection<String> termIds,
            String entityId, int fy, String fq);
    

    //@Query(value="{'termId':{$in : ?0 } , 'FY' : { $gte :  ?1, $lte : ?2}}", 
//    fields="{'FQ':1,'FY':1,'value':1,'entityId':1,'termId':1,'termName':1,'companyName':1,'filingDate':1}")
    @Query(value = "termId:(?0) AND FY:[?1 TO ?2] AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermsAndFY(Collection<String> termIds, int start, int end);

    

    //  @Query(value = "{'termId':{$in : ?0 } , 'entityId': { $in : ?1 } , 'FY' : { $gte :  ?2, $lte : ?3}, 'FQ': { $ne : 'FY'} }",
    //    fields = "{'FQ':1,'FY':1,'value':1,'entityId':1,'termId':1,'termName':1,'companyName':1}")
    @Query(value = "termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND -FQ:FY AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermsEntitiesAndFYQuarterlyOnly(Collection<String> termIds,
            Collection<String> entityIds, int start, int end);

    //@Query(value = "{'termId':{$in : ?0 } , 'FY' : { $gte :  ?1, $lte : ?2}, 'FQ': { $ne : 'FY'} }",
    //  fields = "{'FQ':1,'FY':1,'value':1,'entityId':1,'termId':1,'termName':1,'companyName':1}")
    @Query(value = "termId:(?0) AND FY:[?1 TO ?2] AND -FQ:FY AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermsAndFYQuarterlyOnly(Collection<String> termIds, int start, int end);

    //@Query(value = "{'termId':{$in : ?0 } , 'entityId': { $in : ?1 } , 'FY' : { $gte :  ?2, $lte : ?3}, 'FQ': { $eq : 'FY'}}",
    //      fields = "{'FQ':1,'FY':1,'value':1,'entityId':1,'termId':1,'termName':1,'companyName':1}")
    @Query(value = "termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:FY AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermsEntitiesAndFYYealyOnly(Collection<String> termIds,
            Collection<String> entityIds, int start, int end);

    //@Query(value = "{'termId':{$in : ?0 } , 'FY' : { $gte :  ?1, $lte : ?2}, 'FQ': { $eq : 'FY'}}",
    //       fields = "{'FQ':1,'FY':1,'value':1,'entityId':1,'termId':1,'termName':1,'companyName':1}")
    @Query(value = "termId:(?0) AND FY:[?1 TO ?2] AND FQ:FY AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermsAndFYYealyOnly(Collection<String> termIds,
            int start, int end);

    //@Query(value = "{'termId':?0 , 'entityId': ?1 , 'FY' : { $gte :  ?2, $lte : ?3}}",
    //     fields = "{'FQ':1,'FY':1,'value':1,'termName':1,'companyName':1,'resolvedExpression':1 ,'percentVarianceWithPrevPeriod':1,'percentVarianceWithPrevYear':1, 'validationMessages':1 , 'validationStatus':1,'filingDate':1}")
    @Query(value = "termId:?0 AND entityId:?1 AND FY:[?2 TO ?3] AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermIdEntityIdAndFY(String termId,
            String entityId, int start, int end);

    //@Query(value = "{'termId':?0 , 'entityId': ?1 , 'FY' : { $gte :  ?2, $lte : ?3}, 'FQ': { $ne : 'FY'} }",
    //        fields = "{'FQ':1,'FY':1,'value':1,'termName':1,'companyName':1,'resolvedExpression':1 ,'percentVarianceWithPrevPeriod':1,'percentVarianceWithPrevYear':1, 'validationMessages':1 , 'validationStatus':1,'filingDate':1}")
    @Query(value = "termId:?0 AND entityId:?1 AND FY:[?2 TO ?3] AND -FQ:FY AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermIdEntityIdAndFYQuarterlyOnly(String termId,
            String entityId, int start, int end);

    //@Query(value = "{'termId':?0 , 'entityId': ?1 , 'FY' : { $gte :  ?2, $lte : ?3}, 'FQ': { $eq : 'FY'} }",
    //       fields = "{'FQ':1,'FY':1,'value':1,'termName':1,'companyName':1,'resolvedExpression':1 ,'percentVarianceWithPrevPeriod':1,'percentVarianceWithPrevYear':1, 'validationMessages':1 , 'validationStatus':1,'filingDate':1}")
    @Query(value = "termId:?0 AND entityId:?1 AND FY:[?2 TO ?3] AND FQ:FY AND myDocType:primaryTermResult")
    public List<TermResultsDoc> findByTermIdEntityIdAndFYYealyOnly(String termId,
            String entityId, int start, int end);

    @Query(value = "myDocType:(?5) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:(?4)")
    @Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    public FacetPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFyRangeFQ(String termId, Collection<String> entityIds,
            int start, int end, String fq, String type, Pageable pageable);
    
    
    @Query(value = "myDocType:(?5) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:(?4) AND validationStatus:InValid")
    @Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    public FacetPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFyRangeFQWithValError(String termId, Collection<String> entityIds,
            int start, int end, String fq, String type, Pageable pageable);

    @Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3]")
    @Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    public FacetPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFyRange(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);
    
    
    @Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND validationStatus:InValid")
    @Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    public FacetPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFyRangeWithValError(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);
    

    @Query(value = "myDocType:(?5) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:(?4) AND rank:(?6)")
    @Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    public FacetPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFyRangeFQRank(String termId, Collection<String> entityIds,
            int start, int end, String fq, String type, int rank, Pageable pageable);
    
     @Query(value = "myDocType:(?5) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:(?4) AND rank:(?6) AND validationStatus:InValid")
    @Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    public FacetPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFyRangeFQRankWithValError(String termId, Collection<String> entityIds,
            int start, int end, String fq, String type, int rank, Pageable pageable);
    
    

    @Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND rank:(?5)")
    @Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    public FacetPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFyRangeRank(String termId, Collection<String> entityIds,
            int start, int end, String type, int rank, Pageable pageable);
    
    
      @Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND rank:(?5) AND validationStatus:InValid")
    @Facet(fields = {"entityId"}, minCount = 1, limit = 1000)
    public FacetPage<TermResultsDoc> findEntityFacetByTermIdEntitiesFyRangeRankWithValError(String termId, Collection<String> entityIds,
            int start, int end, String type, int rank, Pageable pageable);
    

    @Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3]")
    @Facet(pivots = @Pivot({"rank", "entityId"}), limit = 15000)
    public FacetPage<TermResultsDoc> findEntityRankPivotFacetByTermIdEntitiesFyRange(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);

    @Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3]  AND FQ:FY")
    @Facet(pivots = @Pivot({"rank", "entityId"}), limit = 15000)
    public FacetPage<TermResultsDoc> findEntityRankPivotFacetByTermIdEntitiesFyRangeFQ(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);

    @Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3]")
    @Facet(pivots = @Pivot({"FY", "entityId"}), limit = 15000)
    public FacetPage<TermResultsDoc> findEntityYearPivotFacetByTermIdEntitiesFyRange(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);

    @Query(value = "myDocType:(?4) AND termId:(?0) AND entityId:(?1) AND FY:[?2 TO ?3] AND FQ:FY")
    @Facet(pivots = @Pivot({"FY", "entityId"}), limit = 15000)
    public FacetPage<TermResultsDoc> findEntityYearPivotFacetByTermIdEntitiesFyRangeFQ(String termId, Collection<String> entityIds,
            int start, int end, String type, Pageable pageable);

    @Query(value = "myDocType:(?3) AND termId:(?0) AND FY:[?1 TO ?2]")
    @Facet(pivots = @Pivot({"FY", "entityId"}), limit = 15000)
    public FacetPage<TermResultsDoc> findAllEntityYearPivotFacetByTermIdEntitiesFyRange(String termId,
            int start, int end, String type, Pageable pageable);

    @Query(value = "myDocType:(?3) AND termId:(?0) AND FY:[?1 TO ?2] AND FQ:FY")
    @Facet(pivots = @Pivot({"FY", "entityId"}), limit = 15000)
    public FacetPage<TermResultsDoc> findAllEntityYearPivotFacetByTermIdEntitiesFyRangeFQ(String termId,
            int start, int end, String type, Pageable pageable);

}
