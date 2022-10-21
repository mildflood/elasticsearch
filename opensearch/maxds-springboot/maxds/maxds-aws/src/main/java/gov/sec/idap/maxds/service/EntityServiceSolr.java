/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.service;


import gov.sec.idap.maxds.domain.Entity;

import java.util.HashMap;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service("entityServiceSolr")
public class EntityServiceSolr  {

   

    @Autowired
    private SecApiService idapAPIService;

    public Entity getEntityById(String id) {

        String query = String.format("entityId:%s", id);

        List<Entity> result = idapAPIService.getEntitiesByQuery(query);

        if (result != null && result.size() == 1) {
            return result.get(0);
        }

        return null;

    }

    public List<Entity> findAll() {

        return getAllByFields(null);
    }

    public long count() {

        return getAllByFields("entityId").size();
    }

    private List<Entity> getAllByFields(String field) {
        
        
        return idapAPIService.getAllEntities(field);

        
    }
    
       public HashMap<String, Entity> findEntitiesByDivisionSectorAndSicCode(
            final String division,
            final String sector,
            final String sic,
            final String filerCategory) {

        

       String query = buildQuery(division, sector, sic, filerCategory);
        List<Entity> result 
                = idapAPIService.getEntitiesByQuery(query);
        
        HashMap<String, Entity> ret = new HashMap<>();
        for (Entity doc : result) {
            ret.put(doc.getEntityId(), doc);
        }
        


        return ret;
    }

    public HashMap<String, String> findByDivisionSectorAndSicCode(
            final String division,
            final String sector,
            final String sic,
            final String filerCategory) {

        String fields = "entityId,companyName";

        String query = buildQuery(division, sector, sic, filerCategory);
        List<Entity> result 
                = idapAPIService.getEntityFieldsByQuery(query, fields);
        
        HashMap<String, String> ret = new HashMap<>();
        for (Entity doc : result) {
            ret.put(doc.getEntityId(), doc.getCompanyName());
        }
        


        return ret;
    }

    private String buildQuery(final String division, final String sector, final String sic, final String filerCategory) {
        String query = "";
        if (division != null && !division.isEmpty() && !division.equals("NULL")) {

            query += String.format("division:\"%s\" AND ", division);

        }
        if (sector != null && !sector.isEmpty() && !sector.equals("NULL")) {

            query += String.format("sector:\"%s\" AND ", sector);

        }
        if (sic != null && !sic.isEmpty() && !sic.equals("NULL")) {

            query += String.format("sic:\"%s\" AND ", sic);

        }
        if (filerCategory != null && !filerCategory.isEmpty() && !filerCategory.equals("NULL")) {

            query += String.format("filerCategory:\"%s\" AND ", filerCategory);

        }
        if( query.length() > 0)
        {
            query = query.substring(0,query.length()-5 );
        }
        return query;
    }

    public List<Entity> findLimitedSlow() {

        String fields = "entityId,companyName,cik,division,sector,sic,industry,filerCategory";
        return getAllByFields(fields);

    }

}
