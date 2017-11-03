import {Component,Inject,forwardRef, OnInit} from '@angular/core';
import {AppComponent} from './app.component';
import {SynchroRunningService} from './forms/synchro_running.service';

@Component({
    selector: 'app-topbar',
  templateUrl: './app.topbar.component.html'
})
export class AppTopBar implements OnInit {
  syncFilesInfo : any[] = [];

    constructor(@Inject(forwardRef(() => AppComponent)) public app:AppComponent, private synchroRunningService: SynchroRunningService) {}

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
