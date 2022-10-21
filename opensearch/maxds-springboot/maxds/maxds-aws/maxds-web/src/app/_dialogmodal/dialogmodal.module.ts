import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DialogModalComponent } from './dialogmodal.component';

@NgModule({
    imports: [CommonModule],
    declarations: [DialogModalComponent],
    exports: [DialogModalComponent]
})
export class DialogModalModule { }
