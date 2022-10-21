import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { profile } from 'app/profile/profile';
import { MappedEntity } from 'app/domain/mappedEntity';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  constructor(private http: Http, private login: LoginService) {
  }

  // private TERMNAMESURL: string = "https://localhost:18081/api/termNamesListApi";
  // private ENTITIESLISTURL: string = "https://localhost:18081/EntitiesList";
  // private LISTPREFERENCESURL: string = "https://localhost:18081/listPreferences";
  // private SAVEPROFILEURL: string = "https://localhost:18081/savePreferences";
  // private DELETEPREFERENCESURL: string = "https://localhost:18081/deletePreferences";
  // private PROCESSPREFERENCESURL: string = "https://localhost:18081/api/TermRule/ProcessRuleForEntity/";
  // private CLEARPREFERENCESURL: string = "https://localhost:18081/clearPreferences";
  // private PROCESSALLPREFERENCESURL: string = "https://localhost:18081/processAllTermRules"
  //  private TERMMANAGERRESULTURL: string = "https://localhost:18081/api/TermResultsFor"
  //  private UPDATEDPROFILEURL: string = "https://localhost:18081/updateProfile"
  //  private PROCESSMULTIENTITIESPREFERENCESURL: string = "https://localhost:18081/processMultiEntitiesTermRules"
  //  private SHOWRESOLVEDDATAURL: string = "https://localhost:18081/api/MappedEntitiesForCriteria";


  private TERMNAMESURL: string = "/api/termNamesListApi";
  private ENTITIESLISTURL: string = "/EntitiesList";
  private LISTPREFERENCESURL: string = "/listPreferences";
  private LISTSHAREDPREFERENCESURL: string = "/listSharedPreferences";
  private SAVEPROFILEURL: string = "/savePreferences";
  private SAVESHAREDPREFERENCES: string = "/saveSharedPreferences";
  private DELETEPREFERENCESURL: string = "/deletePreferences";
  private ACCEPTSHAREDPREFERENCESURL: string = "/acceptSharedPreferences";
  private DELETESHAREDPREFERENCESURL: string = "/deleteSharedPreferences";
  private PROCESSPREFERENCESURL: string = "/api/TermRule/ProcessRuleForEntity/";
  private CLEARPREFERENCESURL: string = "/clearPreferences";
  private PROCESSALLPREFERENCESURL: string = "/processAllTermRules"
  private TERMMANAGERRESULTURL: string = "/api/TermResultsFor"
  private UPDATEDPROFILEURL: string = "/updateProfile"
  private PROCESSMULTIENTITIESPREFERENCESURL: string = "/processMultiEntitiesTermRules"
  private SHOWRESOLVEDDATAURL: string = "/api/MappedEntitiesForCriteria";//api/MappedEntitiesForCriteria
  //private SHOWRESOLVEDDATAURL: string = "/api/UnMappedEntitiesForCriteria";

  //for Admin page
  private REPORTDATA = "/api/getReportData";

  processAllTerms(termIds: any[]): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("access-control-allow-origin", "*");
    let userid = localStorage.getItem("userid");
    return this.http.post(this.PROCESSALLPREFERENCESURL + "/" + userid, termIds, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response
        return data;
      }
    ));
  }

  processTerms(termId: string, entityId: string, processId: string): Observable<any> {
    const headers = new Headers();
    const body = JSON.stringify({ termId: termId, entityId: entityId, processId: processId });
    headers.append('Content-Type', 'application/json');
    console.log(body);
    return this.http.put(`${this.PROCESSPREFERENCESURL}`, body, {
      headers: headers
    }).pipe(map((data: Response) => {
      const res = data;
      return res;
    }
    ))
  }


  processMultiEntitiesTerms(termId: string, entityId: string, processId: string): Observable<any> {
    const headers = new Headers();
    const body = JSON.stringify({ termId: termId, entityId: entityId, processId: processId, userid: localStorage.getItem("userid") });
    headers.append('Content-Type', 'application/json');
    console.log(body);
    return this.http.post(`${this.PROCESSMULTIENTITIESPREFERENCESURL}`, body, {
      headers: headers
    }).pipe(map((data: Response) => {
      const res = data;
      return res;
    }
    ))
  }


  getTermMngrResults(termName: string, entityId: string): Observable<any> {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.get(`${this.TERMMANAGERRESULTURL + "/" + termName + "/" + entityId}`, {
      headers: headers
    }).pipe(map((data: Response) => data.json()))
  }

  getTermNames(): Observable<any> {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.get(`${this.TERMNAMESURL}`, {
      headers: headers
    }).pipe(map((data: Response) => data.json()))
  }

  getCoompanyNames(): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    return this.http.get(this.ENTITIESLISTURL, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }
  getAllProfiles(): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    let userid = localStorage.getItem("userid");
    return this.http.get(this.LISTPREFERENCESURL + "/" + userid, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }
  getAllSharedProfiles(): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    let userid = localStorage.getItem("userid");
    return this.http.get(this.LISTSHAREDPREFERENCESURL + "/" + userid, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }
  profile: profile = new profile();
  savePreference(termName: string, companyName: string, preName: string): Observable<any> {
    console.log(companyName + termName + preName);
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    this.profile.termName = termName;
    this.profile.companyName = companyName;
    this.profile.preName = preName;
    this.profile.userid = localStorage.getItem("userid");
    return this.http.post(this.SAVEPROFILEURL, this.profile, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }

  saveSharedPreference(code: string, userid: string, reqData): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    return this.http.post(this.SAVESHAREDPREFERENCES + "/" + code + "/" + userid, reqData, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }

  updateProfile: profile = new profile();
  updatePreference(termName: string, companyName: string, preName: string, processId: number): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    this.updateProfile.termName = termName;
    this.updateProfile.companyName = companyName;
    this.updateProfile.preName = preName;
    this.updateProfile.profileId = processId;
    this.updateProfile.userid = localStorage.getItem("userid");
    return this.http.post(this.UPDATEDPROFILEURL, this.updateProfile, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }
  deletePrefernces(preferncesIds: any[]): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    let userid = localStorage.getItem("userid");
    return this.http.post(this.DELETEPREFERENCESURL + "/" + userid, preferncesIds, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }

  deleteSharedPrefernces(preferncesIds: any[]): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    let userid = localStorage.getItem("userid");
    return this.http.post(this.DELETESHAREDPREFERENCESURL + "/" + userid, preferncesIds, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }

  acceptSharedPrefernces(rowData): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    let userid = localStorage.getItem("userid");
    return this.http.post(this.ACCEPTSHAREDPREFERENCESURL + "/" + userid + "/" + rowData.preferenceName + "/" + rowData.code, [rowData.processId], { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }

  clearPrefernces(preferncesIds: any[]): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    let userid = localStorage.getItem("userid");
    return this.http.post(this.CLEARPREFERENCESURL + "/" + userid, preferncesIds, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }

  showReslovedData(mappedEntity: MappedEntity): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("Access-Control-Allow-Origin", "*");
    return this.http.put(this.SHOWRESOLVEDDATAURL, mappedEntity, { headers: headers }).pipe(map(
      (response: Response) => {
        const data = response.json();
        return data;
      }
    ));
  }

  // For Admin page
  getReportData(): Observable<any> {
    const headers = new Headers();
    headers.append('Content-Type', 'application/json');
    return this.http.get(`${this.REPORTDATA}`, {
      headers: headers
    }).pipe(map((data: Response) => data.json()))
  }
}
