import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private MAXDSAPPLOGINURL: string = "/api/isValidUser";
  // private MAXDSAPPLOGINURL: string = "https://localhost:18081/api/isValidUser";

  fullname: string;
  userFullName: BehaviorSubject<string>;
  userId: string;
  private currentUserSubject: BehaviorSubject<String>;
  public currentUser: Observable<String>;

  constructor(private http: Http) {
    this.userFullName = new BehaviorSubject(this.fullname);
    this.currentUserSubject = new BehaviorSubject<String>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): String {
    return this.currentUserSubject.value;
  }

  loginApp(username: string, password: string): Observable<any> {
    let headers = new Headers();
    headers.append('Content-Type', 'application/json');
    headers.append("access-control-allow-origin", "*");
    const body = JSON.stringify({ username: username, password: password });
    return this.http.post(`${this.MAXDSAPPLOGINURL}`, body, {
      headers: headers
    }).pipe(map(
      (response: Response) => {
        const data = response.json();
        this.fullname = data.resultObject.fullUserName;

        localStorage.setItem("userName", data.resultObject.fullUserName);
        localStorage.setItem("userid", data.resultObject.userid);
        localStorage.setItem("userEmail", data.resultObject.email);
        this.userFullName = new BehaviorSubject(this.fullname);
        localStorage.setItem('currentUser', JSON.stringify(this.fullname));
        localStorage.setItem('user', JSON.stringify(data.resultObject.userid));
        // this.currentUserSubject.next(this.fullname);
        localStorage.setItem("userFullName", data.resultObject.fullUserName)
        return data;
      }
    ));
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    localStorage.removeItem('user');
    localStorage.clear();
    sessionStorage.clear();
    this.currentUserSubject = null;
    // this.currentUserValue = null;
  }
}
