import {NgModule, LOCALE_ID }      from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpModule, Http,RequestOptions, XHRBackend }    from '@angular/http';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {LocationStrategy,HashLocationStrategy} from '@angular/common';
import {AppRoutes} from './app.routes';
import 'rxjs/add/operator/toPromise';

import {AppComponent}  from './app.component';
import {AppMenuComponent,AppSubMenu}  from './app.menu.component';
import {AppTopBar}  from './app.topbar.component';
import {AppFooter}  from './app.footer.component';
import {InlineProfileComponent}  from './app.profile.component';
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

import {IsAuthorized} from './utils/is-authorized.directive';
import {FsqLabelComponent} from './fsq-label';
import {HomeComponent} from './home/home.component';
import {Login} from './login/login';
import {Users} from './users/users';
import {User} from './users/user';
import {AccountEventsService} from './account/account.events.service';
import {HmacHttpClient} from './utils/hmac-http-client';
import { ConfigService } from './config.service';
import {SynchroList} from './forms/synchro_list';
import {SynchroDetail} from './forms/synchro_detail';
import {SynchroDetailEdit} from './forms/synchro_detail_edit';
import {SynchroRun} from './forms/synchro_running';
import {SynchroDetailRun} from './forms/synchro_detail_running';
import {SynchroSimpleMsg} from './forms/synchro_simple_msg';
import {SynchroRunningService} from './forms/synchro_running.service';
import {SynchroRunningInfos} from './forms/synchro_running_infos';
import {SynchroRunningList} from './forms/synchro_running_list';
import {SynchroRunningTree} from './forms/synchro_running_tree';
import {InputDebounceComponent} from './components/input_debouce.component';
import {SyncFilesCronComponent} from './components/cron.component';

import {AccordionModule} from 'primeng/primeng';
import {AutoCompleteModule} from 'primeng/primeng';
import {BreadcrumbModule} from 'primeng/primeng';
import {ButtonModule} from 'primeng/primeng';
import {CalendarModule} from 'primeng/primeng';
import {CarouselModule} from 'primeng/primeng';
import {ChartModule} from 'primeng/primeng';
import {CheckboxModule} from 'primeng/primeng';
import {ChipsModule} from 'primeng/primeng';
import {CodeHighlighterModule} from 'primeng/primeng';
import {ConfirmDialogModule} from 'primeng/primeng';
import {SharedModule} from 'primeng/primeng';
import {ContextMenuModule} from 'primeng/primeng';
import {DataGridModule} from 'primeng/primeng';
import {DataListModule} from 'primeng/primeng';
import {DataScrollerModule} from 'primeng/primeng';
import {DataTableModule} from 'primeng/primeng';
import {DialogModule} from 'primeng/primeng';
import {DragDropModule} from 'primeng/primeng';
import {DropdownModule} from 'primeng/primeng';
import {EditorModule} from 'primeng/primeng';
import {FieldsetModule} from 'primeng/primeng';
import {FileUploadModule} from 'primeng/primeng';
import {GalleriaModule} from 'primeng/primeng';
import {GMapModule} from 'primeng/primeng';
import {GrowlModule} from 'primeng/primeng';
import {InputMaskModule} from 'primeng/primeng';
import {InputSwitchModule} from 'primeng/primeng';
import {InputTextModule} from 'primeng/primeng';
import {InputTextareaModule} from 'primeng/primeng';
import {LightboxModule} from 'primeng/primeng';
import {ListboxModule} from 'primeng/primeng';
import {MegaMenuModule} from 'primeng/primeng';
import {MenuModule} from 'primeng/primeng';
import {MenubarModule} from 'primeng/primeng';
import {MessagesModule} from 'primeng/primeng';
import {MultiSelectModule} from 'primeng/primeng';
import {OrderListModule} from 'primeng/primeng';
import {OverlayPanelModule} from 'primeng/primeng';
import {PaginatorModule} from 'primeng/primeng';
import {PanelModule} from 'primeng/primeng';
import {PanelMenuModule} from 'primeng/primeng';
import {PasswordModule} from 'primeng/primeng';
import {PickListModule} from 'primeng/primeng';
import {ProgressBarModule} from 'primeng/primeng';
import {RadioButtonModule} from 'primeng/primeng';
import {RatingModule} from 'primeng/primeng';
import {ScheduleModule} from 'primeng/primeng';
import {SelectButtonModule} from 'primeng/primeng';
import {SlideMenuModule} from 'primeng/primeng';
import {SliderModule} from 'primeng/primeng';
import {SpinnerModule} from 'primeng/primeng';
import {SplitButtonModule} from 'primeng/primeng';
import {StepsModule} from 'primeng/primeng';
import {TabMenuModule} from 'primeng/primeng';
import {TabViewModule} from 'primeng/primeng';
import {TerminalModule} from 'primeng/primeng';
import {TieredMenuModule} from 'primeng/primeng';
import {ToggleButtonModule} from 'primeng/primeng';
import {ToolbarModule} from 'primeng/primeng';
import {TooltipModule} from 'primeng/primeng';
import {TreeModule} from 'primeng/primeng';
import {TreeTableModule} from 'primeng/primeng';

import {CarService} from './demo/service/carservice';
import {CountryService} from './demo/service/countryservice';
import {EventService} from './demo/service/eventservice';
import {NodeService} from './demo/service/nodeservice';
import {SyncFilesDirComponent} from "app/components/directory.component";
import {SynchroDetailRestore} from "./forms/synchro_detail_restore";


@NgModule({
  imports: [
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        AppRoutes,
        HttpModule,
        AccordionModule,
        AutoCompleteModule,
        BreadcrumbModule,
        ButtonModule,
        CalendarModule,
        CarouselModule,
        ChartModule,
        CheckboxModule,
        ChipsModule,
        CodeHighlighterModule,
        ConfirmDialogModule,
        SharedModule,
        ContextMenuModule,
        DataGridModule,
        DataListModule,
        DataScrollerModule,
        DataTableModule,
        DialogModule,
        DragDropModule,
        DropdownModule,
        EditorModule,
        FieldsetModule,
        FileUploadModule,
        GalleriaModule,
        GMapModule,
        GrowlModule,
        InputMaskModule,
        InputSwitchModule,
        InputTextModule,
        InputTextareaModule,
        LightboxModule,
        ListboxModule,
        MegaMenuModule,
        MenuModule,
        MenubarModule,
        MessagesModule,
        MultiSelectModule,
        OrderListModule,
        OverlayPanelModule,
        PaginatorModule,
        PanelModule,
        PanelMenuModule,
        PasswordModule,
        PickListModule,
        ProgressBarModule,
        RadioButtonModule,
        RatingModule,
        ScheduleModule,
        SelectButtonModule,
        SlideMenuModule,
        SliderModule,
        SpinnerModule,
        SplitButtonModule,
        StepsModule,
        TabMenuModule,
        TabViewModule,
        TerminalModule,
        TieredMenuModule,
        ToggleButtonModule,
        ToolbarModule,
        TooltipModule,
        TreeModule,
        TreeTableModule,


    BreadcrumbModule, MenubarModule, MultiSelectModule,
      DataTableModule, SharedModule, DialogModule, MessagesModule, SelectButtonModule, DropdownModule, ButtonModule, TreeTableModule, TabViewModule,
      InputTextModule, ToolbarModule, TooltipModule, CheckboxModule, FileUploadModule, ProgressBarModule, ConfirmDialogModule,
      ListboxModule, InputTextareaModule, PaginatorModule,ReactiveFormsModule],        // module dependencies
  declarations: [ HomeComponent, Login, Users, User,
      FsqLabelComponent,
      SynchroSimpleMsg,
      IsAuthorized,
      SynchroList, SynchroDetail, SynchroDetailEdit, SynchroRun, SynchroDetailRun, SynchroRunningInfos,
      SynchroRunningList, SynchroRunningTree, InputDebounceComponent, SyncFilesCronComponent, SyncFilesDirComponent
    , SynchroDetailRestore, 

      // Demo Ultima
        AppComponent,
        AppMenuComponent,
        AppSubMenu,
        AppTopBar,
        AppFooter,
        InlineProfileComponent,
        DashboardDemo,
        SampleDemo,
        FormsDemo,
        DataDemo,
        PanelsDemo,
        OverlaysDemo,
        MenusDemo,
        MessagesDemo,
        MessagesDemo,
        MiscDemo,
        ChartsDemo,
        EmptyDemo,
        FileDemo,
        UtilsDemo,
        Documentation

  ],   // components and directives
  bootstrap: [ AppComponent ],     // root component
  providers: [AccountEventsService, SynchroRunningService
//  ,{provide : Http, useFactory :(xhrBackend: XHRBackend, requestOptions: RequestOptions,accountEventService:AccountEventsService) => {
//            return new HmacHttpClient(xhrBackend, requestOptions,accountEventService);
//        },
//        deps: [XHRBackend, RequestOptions, AccountEventsService],
//        multi:false}
  , CarService,CountryService,EventService,NodeService,
  { provide: LOCALE_ID, useValue: "en-US" }, //replace "en-US" with your locale
  ]                    // services
})
export class AppModule { }
