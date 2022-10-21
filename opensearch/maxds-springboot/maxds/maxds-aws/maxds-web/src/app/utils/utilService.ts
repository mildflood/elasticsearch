import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { TermRule } from '../domain/termRule';

@Injectable()
export class UtilService {


    private companyListUrl = "/EntitiesList";
    private divisionListUrl = "/DivisionList";
    private disionSectorListUrl = "/DivSectorList";
    private industryListUrl = "/IndustryList";
    private sectorListUrl = "/SectorList";
    private filerCategoryListUrl = "/FilerCategory";
    private saveTermUrl = "/api/TermRule/save";
    private removeTermUrl = "/api/TermRule/DeleteRule";
    private processTermForCriteriaUrl = "/api/TermRule/ProcessRuleWithCriteria";
    private processTermUrl = "/api/TermRule/ProcessRule"
    private taxonomyElementsUrl = "/api/TaxonomyElements";
    private taxonomyElementUrl = "/api/TaxonomyElement";
    private resolvedExtndedExprUrl = "/api/ResolveExtendedExpression";
    private completeCompanyListUrl = "/api/EntityList";
    private processTermRuleEntityUrl: string = "/api/TermRule/ProcessRuleForEntity/";
    private ProcessAllRulesWithCriteriaUrl = '/api/TermRule/ProcessAllRulesWithCriteria';
    private cancelProcessingUrl = '/api/TermRule/CancelPendingProcessing';
    private upLoadFileUrl = '/api/file/upload';
    private termRuleListUrl = '/api/TermRulesList';
    private userRolesListUrl = '/api/UserRolesList';
    private processSelectedUrl = '/api/TermRule/SelectedProcessRule';
    private processSelectedUrlWithCompany = '/api/TermRule/processTermsandEntities';
    private processAccuracyTestTermsUrl = '/api/processAccuracyTestTerms';
    private processAccuracyTestTermsAndEntitiesUrl = '/api/processAccuracyTestTermsAndEntities';
    private resetProcessingUrl = '/api/TermRule/resetProcessing';
    private autoLoginUrl = '/api/autoLogin';
    private profileIdUrl = '/profileId';

    // private companyListUrl = "https://localhost:18081/EntitiesList";
    // private divisionListUrl = "https://localhost:18081/DivisionList";
    // private disionSectorListUrl = "https://localhost:18081/DivSectorList";
    // private industryListUrl = "https://localhost:18081/IndustryList";
    // private sectorListUrl = "https://localhost:18081/SectorList";
    // private filerCategoryListUrl = "https://localhost:18081/FilerCategory";
    // private saveTermUrl = "https://localhost:18081/api/TermRule/save";
    // private removeTermUrl = "https://localhost:18081/api/TermRule/DeleteRule";
    // private processTermForCriteriaUrl = "https://localhost:18081/api/TermRule/ProcessRuleWithCriteria";
    // private processTermUrl = "https://localhost:18081/api/TermRule/ProcessRule"
    // private taxonomyElementsUrl = "https://localhost:18081/api/TaxonomyElements";
    // private taxonomyElementUrl = "https://localhost:18081/api/TaxonomyElement";
    // private resolvedExtndedExprUrl = "https://localhost:18081/api/ResolveExtendedExpression";
    // private completeCompanyListUrl = "https://localhost:18081/api/EntityList";
    // private processTermRuleEntityUrl: string = "https://localhost:18081/api/TermRule/ProcessRuleForEntity/";
    // private ProcessAllRulesWithCriteriaUrl = 'https://localhost:18081/api/TermRule/ProcessAllRulesWithCriteria';
    // private cancelProcessingUrl = 'https://localhost:18081/api/TermRule/CancelPendingProcessing';
    // private upLoadFileUrl = 'https://localhost:18081/api/file/upload';
    // private termRuleListUrl = 'https://localhost:18081/api/TermRulesList';
    // private userRolesListUrl = 'https://localhost:18081/api/UserRolesList';
    // private processSelectedUrl = 'https://localhost:18081/api/TermRule/SelectedProcessRule';
    // private processAccuracyTestTermsUrl = 'https://localhost:18081/api/processAccuracyTestTerms';
    // private resetProcessingUrl = 'https://localhost:18081/api/TermRule/resetProcessing';
    // private autoLoginUrl = 'https://localhost:18081/api/autoLogin';

    fullname: string;
    constructor(private http: Http, private rule: TermRule) { }

    getProfileId() {
      console.log('Util: getProfileId() ...');
      return this.http.get(`${this.profileIdUrl}`).pipe(map(
        (response: Response) => {
          const data = response;
          return data;
        }
      ))
    }

    getCompanyList() {
        console.log('Util : getCompanyList()...');
        return this.http.get(`${this.companyListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }
    getCompleteCompanyList() {
        console.log('Util : getCompanyList()...');
        return this.http.get(`${this.completeCompanyListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }
    getDivisionList() {
        console.log('Util : getDivisionList()...');
        return this.http.get(`${this.divisionListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getIndustryList() {
        console.log('Util : getIndustryList()...');
        return this.http.get(`${this.industryListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getSectorList() {
        console.log('Util : getSectorList()...');
        return this.http.get(`${this.sectorListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getFilerCategoryList() {
        console.log('Util : getFilerCategoryList()...');
        return this.http.get(`${this.filerCategoryListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getDivSectorList() {
        console.log('Util : getDivSectorList()...');
        return this.http.get(`${this.disionSectorListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    saveTerm(rule: TermRule) {
        const headers = new Headers();
        const body = JSON.stringify({
            id: rule.id, termId: rule.termId, name: rule.name, description: rule.description, type: rule.type, periodType: rule.periodType,
            includeInAccuracyTests: rule.includeInAccuracyTests, lastModified: rule.lastModified, order: rule.order, processingStatus: rule.processingStatus, priorityGroup: rule.priorityGroup,
            financialStatement: rule.financialStatement,
            expressions: rule.expressions, validationExpressions: rule.validationExpressions,
            derivedZeroExpressions: rule.derivedZeroExpressions, overrides: rule.overrides
        });
        // const body = JSON.stringify({termRule})
        headers.append('Content-Type', 'application/json');
        //   return this.http.put(`${this.saveTermUrl}`, body, {
        //     headers: headers
        // }).pipe(map((data: Response) => data.json()));

        return this.http.put(`${this.saveTermUrl}`, body, {
            headers: headers
        }).pipe(map((data: Response) => {
            const res = data;
            return res;
        }
        ))

    }

    removeTerm(termId) {
        console.log("Remove Rule : " + termId);
        const headers = new Headers();
        const body = JSON.stringify({ id: termId });
        headers.append('Content-Type', 'application/json');
        return this.http.put(`${this.removeTermUrl}`, body, {
            headers: headers
        }).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    };
    processTermForCriteria(termId: string, division: string, sector: string, sic: string, filerCategory: string) {
        console.log("processTermForCriteria : " + termId);
        const headers = new Headers();
        let userid = localStorage.getItem("userid");
        const body = JSON.stringify({ termId: termId, userid: userid, division: division, sector: sector, sic: sic, filerCategory: filerCategory });
        headers.append('Content-Type', 'application/json');
        return this.http.put(`${this.processTermForCriteriaUrl}`, body, {
            headers: headers
        }).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }


    processTerm(rule: TermRule) {
        console.log("processTerm : " + rule);
        const headers = new Headers();
        let userid: string = localStorage.getItem("userid");
        const body = JSON.stringify({
            id: rule.id, userid: userid, termId: rule.termId, name: rule.name, description: rule.description, type: rule.type, periodType: rule.periodType,
            includeInAccuracyTests: rule.includeInAccuracyTests, lastModified: rule.lastModified, order: rule.order, processingStatus: rule.processingStatus, priorityGroup: rule.priorityGroup,
            financialStatement: rule.financialStatement,
            expressions: rule.expressions, validationExpressions: rule.validationExpressions,
            derivedZeroExpressions: rule.derivedZeroExpressions, overrides: rule.overrides
        });
        headers.append('Content-Type', 'application/json');
        return this.http.put(`${this.processTermUrl}`, body, {
            headers: headers
        }).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }
    getTaxonomyElements(q: string, isTextBlock: boolean) {
        console.log('UtilService : getTaxonomyElements() with q ' + q);
        return this.http.get(`${this.taxonomyElementsUrl}?q=` + q + `&isTextBlock=` + isTextBlock).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            },
            (error => {
                console.log('no data')
            })
        ));
    }

    getTaxonomyElement(id: string) {
        console.log('UtilService : getTaxonomyElement() with id ' + id);
        return this.http.get(`${this.taxonomyElementUrl}?id=` + id).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            },
            (error => {
                console.log('no data')
            })
        ));
    }

    extendedEntityLookup(query: any) {
        console.log('UtilService : extendedEntityLookup() with query ' + query);
        const headers = new Headers();
        const body = JSON.stringify({ division: query.division, sector: query.sector, sic: query.sic, filerCategory: query.filerCategory, expression: query.expression });
        headers.append('Content-Type', 'application/json');
        return this.http.post(`${this.resolvedExtndedExprUrl}`, body, {
            headers: headers
        }).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    processTerms(termId: string, entityId: string, processId: string): Observable<any> {
        const headers = new Headers();
        const body = JSON.stringify({ termId: termId, entityId: entityId, processId: processId, userid: localStorage.getItem("userid") });
        headers.append('Content-Type', 'application/json');
        console.log(body);
        return this.http.put(`${this.processTermRuleEntityUrl}`, body, {
            headers: headers
        }).pipe(map((data: Response) => {
            const res = data;
            return res;
        }
        ))
    }

    processAllTermWithCriteria(division: string, sector: string, sic: string, filerCategory: string, entityId: string, isNewAction: boolean): Observable<any> {
        const headers = new Headers();
        const body = JSON.stringify({ division: division, sector: sector, sic: sic, filerCategory: filerCategory, entityId: entityId, isNewAction: isNewAction, userid: localStorage.getItem("userid") });
        headers.append('Content-Type', 'application/json');
        console.log(body);
        return this.http.put(`${this.ProcessAllRulesWithCriteriaUrl}`, body, {
            headers: headers
        }).pipe(map((data: Response) => {
            const res = data;
            return res;
        }
        ))
    }

    cancelProcessing() {
        const headers = new Headers();
        const body = JSON.stringify({ userid: localStorage.getItem("userid") });
        headers.append('Content-Type', 'application/json');
        console.log(body);
        return this.http.put(`${this.cancelProcessingUrl}`, body, {
            headers: headers
        }).pipe(map((data: Response) => {
            const res = data;
            return res;
        }
        ))
    }

    resetProcessing() {
        const headers = new Headers();
        const body = JSON.stringify({ userid: localStorage.getItem("userid") });
        headers.append('Content-Type', 'application/json');
        console.log(body);
        return this.http.put(`${this.resetProcessingUrl}`, body, {
            headers: headers
        }).pipe(map((data: Response) => {
            const res = data;
            return res;
        }
        ))
    }

    uploadLookupReferenceFile(file) {
        const headers = new Headers();
        const body = JSON.stringify({ file: file });
        headers.append('Content-Type', 'multipart/form-data');
        console.log(body);
        return this.http.put(`${this.upLoadFileUrl}`, file, {

        }).pipe(map((data: Response) => {
            const res = data;
            return res;
        }
        ))
    }

    getTermRuleList() {
        console.log('UtilService : getTermRuleList()...');
        return this.http.get(`${this.termRuleListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getUserRolesList() {
        console.log('UtilService : getUserRolesList()...');

        return this.http.get(`${this.userRolesListUrl + "/" + localStorage.getItem("userid")}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    processSelctedTerms(termIds: any[]): Observable<any> {
        const headers = new Headers();
        const body = JSON.stringify({ termIds: termIds, userid: localStorage.getItem("userid") });
        headers.append('Content-Type', 'application/json');
        console.log(body);
        return this.http.put(`${this.processSelectedUrl}`, body, {
            headers: headers
        }).pipe(map((data: Response) => {
            const res = data;
            return res;
        }
        ))
    }

    processSelctedTermsWithCompany(terms, entities): Observable<any> {
        const headers = new Headers();
        const body = JSON.stringify({ terms: terms, entities: entities, userid: localStorage.getItem("userid") });
        headers.append('Content-Type', 'application/json');
        console.log(body);
        return this.http.post(`${this.processSelectedUrlWithCompany}`, body, {
            headers: headers
        }).pipe(map((data: Response) => {
            const res = data;
            return res;
        }
        ))
    }



    processAccuTerms(userId: any): Observable<any> {
        return this.http.get(`${this.processAccuracyTestTermsUrl}?process=` + true + `&userId=` + localStorage.getItem("userid")).pipe(map(
            (response: Response) => {
                return response;
            },
            (error => {
                console.log('no data')
            })
        ));
    }

    processAccuTermsForTermsAndEntities(terms: any[], entities: any[]): Observable<any> {
      if (terms != null && terms.length == 0 && entities == null) {
        this.processAccuTerms(localStorage.getItem("userid")); //original AccuracyTest function from Term Process Page
      } else {
        const headers = new Headers();
        const body = JSON.stringify({ terms: terms, entities: entities, userid: localStorage.getItem("userid") });
        headers.append('Content-Type', 'application/json');
        console.log(body);
        return this.http.post(`${this.processAccuracyTestTermsAndEntitiesUrl}`, body, {
          headers: headers
        }).pipe(map(
            (data: Response) => {
                return data;
            },
            (error => {
                console.log('no data')
            })
        ));
      }
    }


    autoLogin(): Observable<any> {
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        headers.append("access-control-allow-origin", "*");
        const body = JSON.stringify({ username: 'username', password: 'password' });
        return this.http.post(`${this.autoLoginUrl}`, body, {
            headers: headers
        }).pipe(map(
            (response: Response) => {
                const data = response.json();
                if (data.resultObject != null) {
                    this.fullname = data.resultObject.fullUserName;

                    localStorage.setItem("userName", data.resultObject.fullUserName);
                    localStorage.setItem("userid", data.resultObject.userid);
                    localStorage.setItem("userEmail", data.resultObject.email);
                    localStorage.setItem('currentUser', JSON.stringify(this.fullname));
                    localStorage.setItem('user', JSON.stringify(data.resultObject.userid))
                }
                ;
                return data;
            }
        ));
    }

}
