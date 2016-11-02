import { Component } from '@angular/core';
import {Title} from '@angular/platform-browser';
import {SynchroFilesService} from './SynchroFilesService';
import {Response} from "@angular/http";
import {$WebSocket} from 'angular2-websocket/angular2-websocket';
import {ConfirmationService} from 'primeng/primeng';
import { Router } from '@angular/router';

@Component({
  selector: 'synchro-list',
  viewProviders: [Title],
  providers: [SynchroFilesService, ConfirmationService],
  templateUrl: 'synchro_list.html',
  styleUrls: ['synchro_list.css']
})
export class SynchroList {
  synchroFileList : any;
  isHttpRequest : boolean = false;

    private _index : number;
    private _id : number;

  constructor(title : Title
    , private _synchroFilesService : SynchroFilesService
    , private confirmationService: ConfirmationService
    , private router: Router) {
    title.setTitle("SyncFiles - Liste des synchronisations");
    this._synchroFilesService.loadAll().subscribe(
        (r : any) => {
          this.synchroFileList = r;
        },
        (e : any) => {
          // this.isHttpRequest = false;
          console.log("Error : " + e);
        }

    );
  }

  onSubmit(value: string): void {
    console.log('you submitted value: ', value);
  }

    dialogConfirmRemove(index : number, id : number) :void {
        this._index = index;
        this._id = id;
        this.confirmationService.confirm({
            message: 'Voulez-vous supprimer cette synchronisation ?',
            accept: () => {
              this.remove(this._index, this._id);
            }
        });
    }

  startSynchro(index : number, id : string) :void {
    this.isHttpRequest = true;
    this._synchroFilesService.synchronize(id).subscribe(
      (r:any) => {
        this.isHttpRequest = false;
        this.router.navigate(['running']);
      },
        (e : any) => {
          this.isHttpRequest = false;
          console.log("Error startSynchro : " + JSON.stringify(e));
        }
    );
  }    

  remove(index : number, id : number) {
    this.isHttpRequest = true;
    this._synchroFilesService.removeDetail(id).subscribe(
        (r: any) => {
          this.isHttpRequest = false;
          this.synchroFileList.splice(index, 1);
        },
        (e : any) => {
          this.isHttpRequest = false;
          console.log("Error remove : " + JSON.stringify(e));
        }
    );
  }

  addNewSynchro() : void {
    this.router.navigate(['detail']);
  }


}
