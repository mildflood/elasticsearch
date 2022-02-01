import { Injectable } from '@angular/core';
@Injectable()
export class Home {

    homeCol: any[];
    homeEntityCol: any[];
    label: string;
    children: any = [];
    leaf: boolean;

    homeCols() {
        this.homeCol = [
            { field: 'rank', header: 'Rank', width: '12%' },
            { field: 'type', header: 'Type', width: '25%' },
            { field: 'expression', header: 'Expression', width: '58%' },
            { field: 'options', header: 'Options', width: '15%' },
        ];
        return this.homeCol;
    }

    modalExpressioncols() {
        return [
            { field: 'rank', header: 'Rank', width: '12%' },
            { field: 'type', header: 'Type', width: '25%' },
            { field: 'expression', header: 'Expression', width: '58%' }
        ];
    }

    homeEntityCols() {
        this.homeEntityCol = [
            { field: 'cik', header: 'Cik', width: '10%' },
            { field: 'companyName', header: 'Company Name', width: '20%' },
            { field: 'viewResultLink', header: 'View Result Link', width: '15%' },
            { field: 'validationStatus', header: 'Validation Status', width: '15%' },
            { field: 'researchLink', header: 'Research Link', width: '15%' },
            { field: 'fsqvLink', header: 'FSQV Link', width: '15%' },
            { field: 'processTermLink', header: 'Process Term Link', width: '10%' },
        ];
        return this.homeEntityCol;
    }


}
