/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.List;

/**
 *
 * @author SRIKANTH
 */
public class ExportOutput {
    
    private List<String> termNameList;
    private List<ExportItem> exportItemList;

    /**
     * @return the termNameList
     */
    public List<String> getTermNameList() {
        return termNameList;
    }

    /**
     * @param termNameList the termNameList to set
     */
    public void setTermNameList(List<String> termNameList) {
        this.termNameList = termNameList;
    }

    /**
     * @return the exportItemList
     */
    public List<ExportItem> getExportItemList() {
        return exportItemList;
    }

    /**
     * @param exportItemList the exportItemList to set
     */
    public void setExportItemList(List<ExportItem> exportItemList) {
        this.exportItemList = exportItemList;
    }
}
