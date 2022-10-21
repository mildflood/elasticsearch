import { Injectable } from '@angular/core';
@Injectable()
export class TermMap {
    termMapCol: any[];
  
    

    termMapCols() {
        this.termMapCol = [
            { field: 'termId', header: 'Term ID' ,  group:'Maxds', isDisplayed: true},
             { field: 'name', header: 'Term Name' , group:'Maxds', isDisplayed: true },
            { field: 'description', header: 'Description' , group:'Maxds' , isDisplayed: true },
           ];
        return this.termMapCol;
    }

    
    
}