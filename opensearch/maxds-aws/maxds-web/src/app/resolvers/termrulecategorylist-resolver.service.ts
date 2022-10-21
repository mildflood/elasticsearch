import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { HomeService } from '../home/homeService';

@Injectable({
  providedIn: 'root'
})
export class TermRuleCategoryListResolverService implements Resolve<any> {
  termRuleCategoryList: Observable<any>;
  constructor(private homeService: HomeService ) { }

  resolve(): Observable<any> {
    if (this.termRuleCategoryList) {
      return this.getTermRuleCategoryList();
    } else {
      this.termRuleCategoryList = this.homeService.getTermRuleCategoryList();
      return this.termRuleCategoryList;
    }
  }
  getTermRuleCategoryList() {
    return this.termRuleCategoryList;
  }
}
