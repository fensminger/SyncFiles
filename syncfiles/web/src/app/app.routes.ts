import { Routes, RouterModule } from '@angular/router';
import {Login} from './login/login';
import {Users} from './users/users';
import {User} from './users/user';
import { SynchroList } from './forms/synchro_list';
import { SynchroDetail } from './forms/synchro_detail';
import { SynchroRun } from './forms/synchro_running';
import {ModuleWithProviders} from '@angular/core';
import {DashboardDemo} from './demo/view/dashboarddemo';
import {SampleDemo} from './demo/view/sampledemo';
import {FormsDemo} from './demo/view/formsdemo';
import {DataDemo} from './demo/view/datademo';
import {PanelsDemo} from './demo/view/panelsdemo';
import {OverlaysDemo} from './demo/view/overlaysdemo';
import {MenusDemo} from './demo/view/menusdemo';
import {MessagesDemo} from './demo/view/messagesdemo';
import {MiscDemo} from './demo/view/miscdemo';
import {EmptyDemo} from './demo/view/emptydemo';
import {ChartsDemo} from './demo/view/chartsdemo';
import {FileDemo} from './demo/view/filedemo';
import {UtilsDemo} from './demo/view/utilsdemo';
import {Documentation} from './demo/view/documentation';
import {SynchroRunningInfos} from './forms/synchro_running_infos';


const routes: Routes = [
    { path: 'home', component: SynchroList },
    { path: 'running', component: SynchroRun },
    { path: 'running-infos/:id/:originFile/:index', component: SynchroRunningInfos },
    { path: 'detail', component: SynchroDetail },
    { path: 'detail/:id', component: SynchroDetail },
    { path: 'detail/:id/:tabName', component: SynchroDetail },
  
  
    {path: 'authenticate', component: Login},
    {path: 'users', component: Users, },
    {path: 'user/:id', component: User,},

    {path: '', component: DashboardDemo},
    {path: 'sample', component: SampleDemo},
    {path: 'forms', component: FormsDemo},
    {path: 'data', component: DataDemo},
    {path: 'panels', component: PanelsDemo},
    {path: 'overlays', component: OverlaysDemo},
    {path: 'menus', component: MenusDemo},
    {path: 'messages', component: MessagesDemo},
    {path: 'misc', component: MiscDemo},
    {path: 'empty', component: EmptyDemo},
    {path: 'charts', component: ChartsDemo},
    {path: 'file', component: FileDemo},
    {path: 'utils', component: UtilsDemo},
    {path: 'documentation', component: Documentation}
    
];

export const routing = RouterModule.forRoot(routes);
