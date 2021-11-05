/* 
 * MAXDS was created by staff of the U.S. Securities and Exchange Commission.
 * Data and content created by government employees within the scope of their employment
 * are not subject to domestic copyright protection. 17 U.S.C. 105.
 */
package gov.sec.idap.maxds.domain;

import java.util.Objects;

/**
 *
 * @author srira
 */
public class ExpressionFormula {
    
    private String termName;
    private boolean nullable;
    private String operation;

    /**
     * @return the termName
     */
    public String getTermName() {
        return termName;
    }

    /**
     * @param termName the termName to set
     */
    public void setTermName(String termName) {
        this.termName = termName;
    }

    /**
     * @return the nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * @param nullable the nullable to set
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + Objects.hashCode(this.termName);
        hash = 73 * hash + (this.nullable ? 1 : 0);
        hash = 73 * hash + Objects.hashCode(this.operation);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExpressionFormula other = (ExpressionFormula) obj;
        if (this.nullable != other.nullable) {
            return false;
        }
        if (!Objects.equals(this.termName, other.termName)) {
            return false;
        }
        if (!Objects.equals(this.operation, other.operation)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return this.getTermName() + (this.isNullable()?"=0":"") + this.getOperation();        
    }
}
