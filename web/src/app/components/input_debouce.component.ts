import {Component, Input, Output, ElementRef, EventEmitter} from '@angular/core';
import {Observable} from 'rxjs/Rx';

@Component({
    selector: 'input-debounce',
    template: `
        <span class="md-inputfield">
            <input [type]="type" class="form-control" pInputText [(ngModel)]="inputValue">
            <label>{{placeholder}}</label>
        </span>`
})
export class InputDebounceComponent {
    @Input() placeholder: string;
    @Input() delay: number = 300;
    @Input() type: string = "text";
    @Output() value = new EventEmitter();

    public inputValue: string;

    constructor(private elementRef: ElementRef) {
        const eventStream = Observable.fromEvent(elementRef.nativeElement, 'keyup')
            .map(() => this.inputValue)
            .debounceTime(this.delay)
            .distinctUntilChanged();

        eventStream.subscribe(input => this.value.emit(input));
    }
}
