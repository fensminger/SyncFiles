import {Component, AfterViewInit, ElementRef, OnInit} from '@angular/core';
import {BreadCrumbService} from './shared';
import {Subscription} from 'rxjs/Subscription'
import * as _ from 'lodash';
import * as moment from 'moment';
import {SynchroRunningService} from './forms/synchro_running.service';

declare var Ultima: any;

@Component({
  selector: 'my-app',
  templateUrl: 'application.html',
  providers: [BreadCrumbService, SynchroRunningService]
})
export class AppComponent implements AfterViewInit, OnInit {

  layoutCompact: boolean = true;

  layoutMode: string = 'static';

  darkMenu: boolean = false;

  profileMode: string = 'inline';

  syncFilesInfo : any[] = [];

  constructor(private el: ElementRef, private synchroRunningService : SynchroRunningService) {
  }

  ngAfterViewInit() {
    Ultima.init(this.el.nativeElement);
  }

  changeTheme(event, theme) {
    let themeLink: HTMLLinkElement = <HTMLLinkElement> document.getElementById('theme-css');
    let layoutLink: HTMLLinkElement = <HTMLLinkElement> document.getElementById('layout-css');

    themeLink.href = '/assets/ultimang/theme/theme-' + theme + '.css';
    layoutLink.href = '/assets/ultimang/layout/css/layout-' + theme + '.css';
    event.preventDefault();
  }

  ngOnInit() {
    this.synchroRunningService.syncFilesinfoObservable.subscribe(
      s => {
        this.syncFilesInfo = s;
      }
    );
  }

  public getTitleSynchroMenu(sync : any): any {
    let running = (sync.running)?"Running":"Stopped";
    return sync.type + " - " + sync.paramSyncFiles.name + " (" + running + ")";
  }

}
