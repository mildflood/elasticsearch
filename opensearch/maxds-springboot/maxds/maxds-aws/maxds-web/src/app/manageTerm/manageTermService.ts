import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';

@Injectable()
export class ManageTermService {
    private manageTermMapUrl: string = "/api/TermMap/GetAllMapInformation";
    private termMapGroupNameListUrl: string = "/api/TermMap/GroupNames";
    private addTermMapGroupUrl: string = "/api/TermMap/AddGroup";
    private removeTermMapGroupUrl = '/api/TermMap/RemoveGroup';
    private saveTermMapUrl = '/api/TermMap/MapInformation/Save';
    private removeTermMapUrl = '/api/TermMap/MapInformation/RemoveItem';
    

    // private manageTermMapUrl: string = "https://localhost:18081/api/TermMap/GetAllMapInformation";
    // private termMapGroupNameListUrl: string = "https://localhost:18081/api/TermMap/GroupNames";
    // private addTermMapGroupUrl: string = "https://localhost:18081/api/TermMap/AddGroup";
    // private removeTermMapGroupUrl = 'https://localhost:18081/api/TermMap/RemoveGroup';
    // private saveTermMapUrl = 'https://localhost:18081/api/TermMap/MapInformation/Save';
    // private removeTermMapUrl = 'https://localhost:18081/api/TermMap/MapInformation/RemoveItem';
    
    constructor(private http: Http){}

    getManageTermMap() {
        console.log('Manage Term Map : getManageTermMap()...');
        return this.http.get(`${this.manageTermMapUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }
    getTermMapGroupNameList() {
        console.log('Manage Term Map : getTermMapGroupNameList()...');
        return this.http.get(`${this.termMapGroupNameListUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));

    }
    addTermMappingGroup(groupName: string) {
        const headers = new Headers();
        const body = JSON.stringify({groupName: groupName});
        headers.append('Content-Type', 'application/json');
        return this.http.get(`${this.addTermMapGroupUrl}?groupName=` + groupName).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }
    removeTermMappingGroup(groupName: string) {
        const headers = new Headers();
        const body = JSON.stringify({groupName: groupName});
        headers.append('Content-Type', 'application/json');
        return this.http.get(`${this.removeTermMapGroupUrl}?groupName=` + groupName + `&userId=` +  localStorage.getItem('userid')).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

 

    saveTermMap(info: any) {
        const headers = new Headers();
        const body = JSON.stringify({termId: info.termId, name: info.name, description: info.description, isEditing: info.isEditing, isTermRule: info.isTermRule,
            mappedInfoSets: info.mappedInfoSets});
        headers.append('Content-Type', 'application/json');
        
        return this.http.put(`${this.saveTermMapUrl}`, body, {
            headers: headers
          }).pipe(map((data: Response) => {
            const res = data;
            return res;
          }
          ))
    }

    removeTermMap(info: any) {
        const headers = new Headers();
        const body = JSON.stringify({termId: info.termId, name: info.name, description: info.description, isEditing: info.isEditing, isTermRule: info.isTermRule,
            mappedInfoSets: info.mappedInfoSets});
        headers.append('Content-Type', 'application/json');
        
        return this.http.put(`${this.removeTermMapUrl}`, body, {
            headers: headers
          }).pipe(map((data: Response) => {
            const res = data;
            return res;
          }
          ))
    }
}