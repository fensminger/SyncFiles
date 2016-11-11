import { Component, AfterViewInit, ElementRef, OnInit, OnDestroy } from '@angular/core';
import {InputText,Menubar, MenuItem, Breadcrumb, Toolbar} from 'primeng/primeng';
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
export class AppComponent implements AfterViewInit {

    layoutCompact: boolean = true;

    layoutMode: string = 'static';
    
    darkMenu: boolean = false;
    
    profileMode: string = 'inline';

    constructor(private el: ElementRef) {}

    ngAfterViewInit() {
        Ultima.init(this.el.nativeElement);
    }
    
    changeTheme(event, theme) {
        let themeLink: HTMLLinkElement = <HTMLLinkElement> document.getElementById('theme-css');
        let layoutLink: HTMLLinkElement = <HTMLLinkElement> document.getElementById('layout-css');
        
        themeLink.href = '/assets/ultimang/theme/theme-' + theme +'.css';
        layoutLink.href = '/assets/ultimang/layout/css/layout-' + theme +'.css';
        event.preventDefault();
    }

  /*
  private menu: MenuItem[];
  private breadCrumpItems : MenuItem[];
  public subscription : Subscription;

  public title = 'app works!';

  constructor(private breadCrumbService : BreadCrumbService) {
    console.log('lodash version:' + _.VERSION);
    let dateStr = moment().subtract(10, 'days').calendar();
    console.log('Calcul de date avec moment : ' + dateStr);
    this.breadCrumpItems = [];
    this.subscription = breadCrumbService.breadCrumbSubjectObs.subscribe(b => {
      console.log("Mise a jour du breadcrumb" + JSON.stringify(b));
      this.breadCrumpItems.length = 0;
      _(b).forEach(element => {
        this.breadCrumpItems.push(element);
      });
    });
  }

  ngOnInit() {
    this.menu = [
      {
        label: 'Synchro',
        icon: 'fa-cube',
        routerLink: ['/home']
      },
      {
        label: 'Add Synchro',
        icon: 'fa-edit',
        routerLink: ['/detail']
      },
      {
        label: 'Running',
        icon: 'fa-rocket',
        routerLink: ['/running']
      },
      {
        label: 'Tests',
        icon: 'fa-ambulance',
        items: [
          {label: 'Test 1', icon: 'fa-question', routerLink: ['/test-page']},
          {label: 'Test 2', icon: 'fa-gitlab', routerLink: ['/']}
        ]
      }
    ];
  }

  ngOnDestroy() {
    // prevent memory leak when component destroyed
    this.subscription.unsubscribe();
  }
  

  public alerts:Array<Object> = [
    {
      type: 'danger',
      msg: 'Oh snap! Change a few things up and try submitting again.'
    },
    {
      type: 'success',
      msg: 'Well done! You successfully read this important alert message.',
      closable: true
    }
  ];

  public closeAlert(i:number):void {
    this.alerts.splice(i, 1);
  }

  public addAlert():void {
    this.alerts.push({msg: 'Another alert!', type: 'warning', closable: true});
  }
  */
}
