import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { UtilService } from '../utils/utilService';

@Injectable({
  providedIn: 'root'
})
export class TermlistResolverService implements Resolve<any> {
  termRuleList: Observable<any>;
  constructor(private utilService: UtilService ) { }

  resolve(): Observable<any> {
    if (this.termRuleList) {
      return this.termRuleList;
    } else {
      this.termRuleList = this.utilService.getTermRuleList();
      return this.termRuleList;
    }
  }
  getTermRuleList() {
    return this.termRuleList;
  }
}
