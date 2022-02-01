import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { UtilService } from '../utils/utilService';

@Injectable({
  providedIn: 'root'
})
export class FilerCategorylistResolverService implements Resolve<any> {

  constructor(private utilService: UtilService ) { }

  resolve(): Observable<any> {
    return this.utilService.getFilerCategoryList();
  }
}

