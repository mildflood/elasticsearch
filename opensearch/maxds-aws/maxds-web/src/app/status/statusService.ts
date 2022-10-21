
import { map } from 'rxjs/operators';
import { Http, Response, Headers } from '@angular/http';
import { Injectable } from '@angular/core';
import 'rxjs/Rx';
import { Observable } from 'rxjs/Rx';

@Injectable({
    providedIn: 'root'
})
export class StatusService {
    // private PROCESSSTATUSURL: string = https://localhost:18081/api/ProcessingLogList/1000
    private PROCESSSTATUSURL: string = "/api/ProcessingLogList/1000";
    constructor(private http: Http) {

    }
    loadProcessStatus(): Observable<any> {
        const headers = new Headers();
        headers.append('Content-Type', 'application/json');
        return this.http.get(`${this.PROCESSSTATUSURL}`, {
            headers: headers
        }).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

}

