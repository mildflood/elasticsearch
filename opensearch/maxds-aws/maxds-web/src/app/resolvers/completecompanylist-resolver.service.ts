import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { UtilService } from '../utils/utilService';

@Injectable({
  providedIn: 'root'
})
export class CompleteCompanylistResolverService implements Resolve<any> {
  completeCompanyList: Observable<any>;
  constructor(private utilService: UtilService ) { }

  resolve(): Observable<any> {
    if (this.completeCompanyList) {
      return this.getCompleteCompanyList();
    } else {
      this.completeCompanyList = this.utilService.getCompleteCompanyList();
      return this.completeCompanyList;
    }
  }

  getCompleteCompanyList(): Observable<any> {
    return this.completeCompanyList;
  }
}


