import {
    Component
    , EventEmitter
    , Input
    , Output
} from '@angular/core';
import {
    CORE_DIRECTIVES
} from '@angular/common';

@Component({
    selector: 'uif-dialog',
    directives: [CORE_DIRECTIVES],
    template: `
<div class="modal fade" [ngClass]="{'in': isDisplay()}" *ngIf="isDisplay()" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" style="display: block">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close" (click)="cancel()"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Confirmation</h4>
            </div>
            <div class="modal-body">
                <ng-content></ng-content>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal" (click)="cancel()">Non</button>
                <button type="button" class="btn btn-primary" (click)="ok()">Oui</button>
            </div>
        </div>
    </div>
</div>
`
})
export class ConfirmDialog {
    @Input('display') display :boolean;

    @Output() confirmSelected: EventEmitter<boolean> = new EventEmitter<boolean>();

    constructor() {
    }

    isDisplay() : boolean {
        return this.display;
    }

    displayStatus() : string {
        if (this.display) {
            return "block";
        } else {
            return "none";
        }
    }

    ok() {
        this.display = false;
        this.confirmSelected.emit(true);
    }
    cancel() {
        this.display = false;
        this.confirmSelected.emit(false);
    }
}
