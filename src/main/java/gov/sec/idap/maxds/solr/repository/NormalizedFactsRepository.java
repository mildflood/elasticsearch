/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.solr.repository;

import gov.sec.idap.maxds.domain.NormalizedFact;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;


public  interface NormalizedFactsRepository extends SolrCrudRepository<NormalizedFact, String> {
  
    
     @Query(value = "sub_cik:(?1) AND tag_custom:false AND num_qtrs:(0 1 4)",
             fields = "fact_fp,fact_fy_i,num_value,sub_cik,cell_elt,tag_datatype,sub_filed_dt,num_uom,tag_iord,dim_segments,sub_act_standard",
               filters = {"cell_elt:(?0)"} )
    public List<NormalizedFact> findByEntityIdListAndElementName(String element, String ciks,  Pageable pageable);
    
     @Query(value = "cell_elt:(?0) AND sub_cik:(?1) AND tag_custom:false AND num_qtrs:(0 1 4) AND -dim_segments:*",
             fields = "fact_fp,fact_fy_i,num_value,sub_cik,cell_elt,tag_datatype,sub_filed_dt,num_uom,tag_iord,sub_act_standard" )
    public List<NormalizedFact> findByEntityIdListAndElementNameNoDims(String element, String ciks,  Pageable pageable);
     
}