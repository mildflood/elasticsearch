package gov.sec.idap.maxds.elasticsearch.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.data.solr.repository.Query;
import org.springframework.stereotype.Repository;

import gov.sec.idap.maxds.elasticsearch.document.NormalizedFact;

@Repository
public interface NormalizedFactsRepository extends ElasticsearchRepository<NormalizedFact, String> {
//	 @Query(value = "sub_cik:(?1) AND tag_custom:false AND num_qtrs:(0 1 4)",
//             fields = "fact_fp,fact_fy_i,num_value,sub_cik,cell_elt,tag_datatype,sub_filed_dt,num_uom,tag_iord,dim_segments,sub_act_standard",
//               filters = {"cell_elt:(?0)"} )
	@Query("{\"bool\": {\"must\":[{\"match\" : { \"sub_cik\" : \"?1\" }},{\"match\" : { \"tag_custom\" : false }},{\"terms\" : {\"num_qtrs\":[0,1,4]}}],\"filter\":{\"match\":{\"cell_elt\":\"?0\"}}}}")
    public List<NormalizedFact> findByEntityIdListAndElementName(String element, String ciks,  Pageable pageable);
    
//     @Query(value = "cell_elt:(?0) AND sub_cik:(?1) AND tag_custom:false AND num_qtrs:(0 1 4) AND -dim_segments:*",
//             fields = "fact_fp,fact_fy_i,num_value,sub_cik,cell_elt,tag_datatype,sub_filed_dt,num_uom,tag_iord,sub_act_standard" )
    @Query("{\"bool\": {\"must\":[{\"match\" : { \"cell_elt\" : \"?0\" }},{\"match\" : { \"sub_cik\" : \"?1\" }},{\"match\": {\"tag_custom\": false}},{\"terms\" : {\"num_qtrs\":[0,1,4]}}],\"must_not\": {\"match\": {\"dim_seqment\": \"*\"}}}}")
	public List<NormalizedFact> findByEntityIdListAndElementNameNoDims(String element, String ciks,  Pageable pageable);
}
