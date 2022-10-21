
import { map } from 'rxjs/operators';
import { Http, Response, Headers } from '@angular/http';
import { Injectable } from '@angular/core';
import 'rxjs/Rx';
import { Observable } from 'rxjs/Rx';

@Injectable({
    providedIn: 'root'
})
export class SubmitFeedbackService {
    private FEEDBACKSUBMITURL: string = "sendFeedback";
    private getFeedbackUrl: string = "getFeedback";
    private getFeedbackByUserUrl: string = "getFeedbackByUser";

    constructor(private http: Http) { }

    submitFeedback(feedback: any): Observable<any> {
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        headers.append("access-control-allow-origin", "*");
        return this.http.post(this.FEEDBACKSUBMITURL, feedback, { headers: headers }).pipe(map(
            (response: Response) => {
                const data = response
                console.log(data);
                return data;
            }
        ));
    }
    getFeedback() {
        console.log('SubmitFeedback : getFeedback()...');
        return this.http.get(`${this.getFeedbackUrl}`).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }

    getFeedbackByUser(username: string) {
        console.log('SubmitFeedback : getFeedback()...');
        return this.http.get(`${this.getFeedbackByUserUrl}?username=` + username).pipe(map(
            (response: Response) => {
                const data = response.json();
                return data;
            }
        ));
    }
}