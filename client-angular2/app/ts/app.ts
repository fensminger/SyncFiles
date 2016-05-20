/*
 * Angular
 */
import {Component, provide} from '@angular/core';
import {NgClass} from '@angular/common';
import {bootstrap} from '@angular/platform-browser-dynamic';
import {
    ROUTER_DIRECTIVES,
    ROUTER_PROVIDERS,
    Router,
    RouteConfig,
} from '@angular/router-deprecated';
import {LocationStrategy, HashLocationStrategy} from '@angular/common';
import { HTTP_PROVIDERS } from '@angular/http';

/*
 * Components
 */
import {SynchroList} from './forms/synchro_list';
import {SynchroDetail} from './forms/synchro_detail';
import {SynchroFilesService} from './forms/SynchroFilesService';

/*
 * Webpack
 */
require('../../node_modules/bootstrap/dist/css/bootstrap.css');
// require('../css/styles.css');
// require('../css/semantic.min.css');
require('../images/ng-book-2-minibook.png');
require('../images/favicon-32x32.png');
require('../images/favicon.ico');

@Component({
    selector: 'synchro-app',
    directives: [ROUTER_DIRECTIVES],
    template: `
<style>
</style>
        <nav class="navbar navbar-default">
          <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
              <button type="button" class="navbar-toggle" [ngClass]="{collapsed: menuClosed}" (click)="openCloseMobileMenu(menu)">
                <span class="sr-only">Nav</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
              </button>
              <span class="navbar-brand" href="#">Home</span>
            </div>
        
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="navbar-collapse" [ngClass]="{collapse: menuClosed}" #menu>
              <ul class="nav navbar-nav">
                <li><a [routerLink]="['/Home']" (click)="openCloseMobileMenu()">Home</a></li>
                <li><a [routerLink]="['/Detail']" (click)="openCloseMobileMenu()">Création d'une synchronisation</a></li>
              </ul>
            </div><!-- /.navbar-collapse -->
          </div><!-- /.container-fluid -->
        </nav>
        <router-outlet></router-outlet>
  `
})
@RouteConfig([
    { path: '/', name: 'root', redirectTo: ['/Home'] },
    { path: '/home', name: 'Home', component: SynchroList },
    { path: '/detail', name: 'Detail', component: SynchroDetail },
    { path: '/detail/:id', name: 'DetailUpdate', component: SynchroDetail }
])
class SynchroApp {
    menuClosed : boolean = true;

    constructor(public router: Router) {
    }

    openCloseMobileMenu(elt : HTMLDivElement) {
        this.menuClosed = ! this.menuClosed;
        if (elt) {
            console.log("open or close menu");
        } else {
            console.log("Not mobile menu");
        }
    }
}

bootstrap(SynchroApp, [
    ROUTER_PROVIDERS,
    HTTP_PROVIDERS,
    SynchroFilesService,
    provide(LocationStrategy, {useClass: HashLocationStrategy}),
]);
