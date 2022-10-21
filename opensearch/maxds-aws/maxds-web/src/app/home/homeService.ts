
import { map } from 'rxjs/operators';
import { Http, Response, Headers } from '@angular/http';
import { Injectable } from '@angular/core';
import 'rxjs/Rx';
import { MappedEntity } from '../domain/mappedEntity';
@Injectable()
export class HomeService {
    private termRuleCategoryListUrl = '/api/TermRulesCategoryList';
    private entityListUrl = '/api/EntityList';
    private termRulesPriorityGroupListUrl = '/api/TermRulesPriorityGroupList';
    private userRolesListUrl = '/api/UserRolesList';
    private userDisplayNameUrl = '/api/UserDisplayName';
    private termRuleUrl = '/api/TermRule';
    private companyListUrl = "/EntitiesList";
    private mappedEntityListUrl = '/api/MappedEntitiesForCriteria';
    private ruleGraphUrl = '/api/getRuleGraph/';
    private termResults = '/api/TermResultsByRuleEntity'

    //Test Purpose
    // private termRuleCategoryListUrl = 'https://localhost:18081/api/TermRulesCategoryList';
    // private entityListUrl = 'https://localhost:18081/api/EntityList';
    // private termRulesPriorityGroupListUrl = 'https://localhost:18081/api/TermRulesPriorityGroupList';
    // private userDisplayNameUrl = 'https://localhost:18081/api/UserDisplayName';
    // private termRuleUrl = 'https://localhost:18081/api/TermRule';
    // private companyListUrl = "https://localhost:18081/EntitiesList";
    // private unMappedEntityListUrl = 'https://localhost:18081/api/UnMappedEntitiesForCriteria';
    // private ruleGraphUrl = 'https://localhost:18081/api/api/getRuleGraph/';
    // private termResults = 'https://localhost:18081/api/TermResultsByRuleEntity'

    constructor(private http: Http) { }

    getTermRuleCategoryList() {
        console.log('HomeService : getTermRuleCategoryList()...');
        return this.http.get(`${this.termRuleCategoryListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }




    getEntityList() {
        console.log('HomeService : getEntityList()...');
        return this.http.get(`${this.entityListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getTermRulesPriorityGroupList() {
        console.log('HomeService : getTermRulesPriorityGroupList()...');
        return this.http.get(`${this.termRulesPriorityGroupListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }





    getUserDisplayName() {
        console.log('HomeService : getUserDisplayName()...');
        return this.http.get(`${this.userDisplayNameUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getTermRule(id: string) {
        console.log('HomeService : getTermRule() with termId ' + id);
        return this.http.get(`${this.termRuleUrl}?id=` + id).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getRuleGraph(id: string) {
        console.log('HomeService : getTermRule() with termId ' + id);
        return this.http.get(`${this.ruleGraphUrl}` + id + `/null`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getCompanyList() {
        console.log('HomeService : getCompanyList()...');
        return this.http.get(`${this.companyListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }
    getMappedEntityList(mappedEntity: MappedEntity, url: string) {
        console.log('HomeService : getMappedEntityList()...');
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        const body = JSON.stringify({
            termRuleId: mappedEntity.termRuleId, division: mappedEntity.division, sector: mappedEntity.sector, sic: mappedEntity.sic, filerCategory: mappedEntity.filerCategory,
            entityId: mappedEntity.entityId, maxYear: mappedEntity.maxYear, minYear: mappedEntity.minYear, includeQuarterly: mappedEntity.includeQuarterly, rankId: mappedEntity.rankId
        });
        return this.http.put(`${url}`, body, {
            headers: headers
        }).pipe(map((data: Response) => data.json()))
    }

    getTermResults(termId: string, entityId: string) {
        console.log('HomeService : getTermResults()');
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        const body = JSON.stringify({
            termRuleId: termId, entityId: entityId
        });
        return this.http.put(`${this.termResults}`, body, {
            headers: headers
        }).pipe(map((data: Response) => data.json()))
    }

}
