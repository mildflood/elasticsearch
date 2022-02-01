import { Injectable } from '@angular/core';
@Injectable()
export class Status {
    statusCol: any[];
    statusCols() {
        this.statusCol = [
            { field: 'termId', header: 'Term ID', width: '10%' },
            { field: 'userName', header: 'User Name', width: '8%' },
            { field: 'logStatus', header: 'Status', width: '8%' },
            { field: 'noOfEntitiesBeingProcessed', header: 'Entity Count', width: '8%' },
            { field: 'lastModified', header: 'Last Modified', width: '10%' },
            { field: 'processingTimeInSec', header: 'Duration in Sec', width: '4%' },
            { field: 'description', header: 'Description', width: '40%' },

        ];
        return this.statusCol;
    }


}