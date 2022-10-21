import { Injectable } from '@angular/core';
@Injectable()
export class TermRule {
 id: string = '';
 termId: string = '';
 name: string = '';
 description: string = '';
 type: string = '';
 periodType: string = '';
  includeInAccuracyTests: boolean = false;
 lastModified = new Date();
 order: number= 0;
 processingStatus: string = 'NotProcessed';
 priorityGroup: string = '';

 financialStatement: string = "Uncategorized";

  expressions : any[]= [];
 validationExpressions:  any[] = [];
 derivedZeroExpressions:  any[] = [];

 overrides: any[] =[];
}