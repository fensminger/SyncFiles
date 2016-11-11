import { Component, OnInit, OnDestroy, Input, ElementRef, ViewChild
  , AfterViewInit, AfterViewChecked, OnChanges } from '@angular/core';
import {Response} from "@angular/http";

@Component({
  selector: 'synchro-simple-msg',
  templateUrl: 'synchro_simple_msg.html'
})
export class SynchroSimpleMsg implements AfterViewChecked, OnChanges {
    @Input('msgList') msgList :any;

    @ViewChild('msgListElt')
    msgListElt : ElementRef;

    constructor() {

    }

    ngAfterViewChecked() {
      this.ngOnChanges();
    }

    previousClientHeight : number = null;

    ngOnChanges() {
      //console.log("Logs : " + this.previousClientHeight + " -> " + this.msgListElt.nativeElement.children[0].clientHeight);
          if (this.previousClientHeight==null || this.previousClientHeight!=this.msgListElt.nativeElement.children[0].clientHeight) {
            //console.log("Logs synchro : " + this.msgListElt.nativeElement.children[0].clientHeight);
            this.msgListElt.nativeElement.scrollTop  = this.msgListElt.nativeElement.children[0].clientHeight;
            if (this.msgListElt.nativeElement.children[0].clientHeight<400) {
              this.msgListElt.nativeElement.style.height = this.msgListElt.nativeElement.children[0].clientHeight + "px";
            } else {
              this.msgListElt.nativeElement.style.height = "400px";
            }
            this.previousClientHeight = this.msgListElt.nativeElement.children[0].clientHeight;
          }
    }

}