import { Component, OnInit, OnDestroy, Input, ElementRef, ViewChild, AfterViewInit, OnChanges } from '@angular/core';
import {Response} from "@angular/http";

@Component({
  selector: 'synchro-simple-msg',
  templateUrl: 'synchro_simple_msg.html'
})
export class SynchroSimpleMsg implements AfterViewInit, OnChanges {
    @Input('msgList') msgList :any;

    @ViewChild('msgListElt')
    msgListElt : ElementRef;

    constructor() {

    }

    ngAfterViewInit() {
      this.ngOnChanges();
    }

    ngOnChanges() {
      this.msgListElt.nativeElement.scrollTop  = this.msgListElt.nativeElement.children[0].clientHeight;
      if (this.msgListElt.nativeElement.children[0].clientHeight<400) {
        this.msgListElt.nativeElement.style.height = this.msgListElt.nativeElement.children[0].clientHeight + "px";
      } else {
        this.msgListElt.nativeElement.style.height = "400px";
      }
    }

}