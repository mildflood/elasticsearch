/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.model;

/**
 *
 * @author SRIKANTH
 */
public class PostRequestResult {
    
    public Boolean status;
    public String errorMessage;
    public String processingLogGroupId;
    public Object resultObject;
    
    
    public static PostRequestResult GetErrorResult( String message)
    {
        PostRequestResult ret = new PostRequestResult();
        ret.status = false;
        ret.errorMessage = message;
        ret.resultObject = null;
        return ret;
    }
    public static PostRequestResult GetSuccessResultWithProcessingLogGroupId(String processingLogGroupId )
    {
         PostRequestResult ret = new PostRequestResult();
         ret.processingLogGroupId = processingLogGroupId;
        ret.status = true;
        return ret;
    }
    public static PostRequestResult GetSuccessResult()
    {
         PostRequestResult ret = new PostRequestResult();
        ret.status = true;
        return ret;
    }
    public static PostRequestResult GetSuccessResultWithReturnObject(Object obj)
    {
         PostRequestResult ret = new PostRequestResult();
        ret.status = true;
        ret.resultObject = obj;
        return ret;
    }
    
}
