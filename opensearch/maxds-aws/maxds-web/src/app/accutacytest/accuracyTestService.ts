
import { map } from 'rxjs/operators';
import { Http, Response, Headers } from '@angular/http';
import { Injectable } from '@angular/core';
import 'rxjs/Rx';
import { Observable } from 'rxjs/Rx';
import { HttpParams, HttpRequest, HttpResponse } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class AccuracyTestService {
    private FILINGURL: string = 'api/getFilingUrl';
    private FILINGURLS: string = 'api/getFilingUrls';
    private LOADACCUDATAURL: string = "/api/AccuracyTest";
    private FILLINGHTMLDATAURL: string = "/api/AccuracyTestGetHtml";
    private DOWNLOADDATAURL: string = "/api/download/accuracyTestResults"
    private SAVEACCURACYDATA: string = "api/AccuracyTest/save";

    constructor(private http: Http/*, private request: HttpRequest<any>, private response: HttpResponse<any>*/) {

    }

    runAccuracyTest(cik: string, fiscalYear: Number): Observable<any> {
        let headers = new Headers();

        headers.append('Content-Type', 'application/json');
        headers.append("Access-Control-Allow-Origin", "*");
        return this.http.get(`${this.LOADACCUDATAURL + "/" + cik + "/" + fiscalYear}`, { headers: headers }).pipe(map(
            (response: Response) => {
                const data = response.json();
                console.log(data);
                return data;
            }
        ));
    }


    fillingHTMLView(fillingUrl: string): Observable<any> {
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        headers.append("Access-Control-Allow-Origin", "*");
        return this.http.post(`${this.FILLINGHTMLDATAURL}`, fillingUrl, { headers: headers }).pipe(map(
            (response: Response) => {
                const data = response.json();
                console.log(data);
                return data;
            }
        ));

    }


    saveAccuracyTestData(accuracyTestData: any): Observable<any> {
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        headers.append("Access-Control-Allow-Origin", "*");
        return this.http.post(`${this.SAVEACCURACYDATA}`, accuracyTestData, { headers: headers }).pipe(map(
            (response: Response) => {
                const data = response.json();
                console.log(data);
                return data;
            }
        ));

    }

    exportAccuracyResults(cik: string, companyName: string, fiscalYear: string): Observable<any> {
        let headers = new Headers();
        /* headers.append('Content-Type', 'application/json');
         headers.append("Access-Control-Allow-Origin", "*");*/
        return this.http.post(`${this.DOWNLOADDATAURL + "/" + companyName + "/" + cik + "/" + fiscalYear}`, { headers: headers }).pipe(map(
            (response: Response) => {
                const data = response;
                console.log(data);

                return data;
            }
        ));

    }

    getFilingUrl(reqData): Observable<any> {
        let headers = new Headers();
        let url;
        if (reqData) {
            if (reqData.termId) {
                url = `${this.FILINGURLS + "/" + reqData.companyId + "/" + reqData.year + "/" + reqData.termId}`
            } else {
                url = `${this.FILINGURL + "/" + reqData.companyId + "/" + reqData.year}`
            }
        }

        headers.append('Content-Type', 'application/json');
        headers.append("Access-Control-Allow-Origin", "*");
        return this.http.get(url, { headers: headers }).pipe(map(
            (response: Response) => {
                const data = response;
                console.log(data);
                return data;
            }
        ));
    }
}