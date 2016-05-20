import { Component } from '@angular/core';
import {
  CORE_DIRECTIVES,
  FORM_DIRECTIVES,
  FormBuilder,
  ControlGroup,
  Validators
} from '@angular/common';
import {Title} from '@angular/platform-browser';
import {AlertComponent} from 'ng2-bootstrap/ng2-bootstrap';
import {SynchroFilesService} from './SynchroFilesService';
import {ConfirmDialog} from './confirm-dialog';
import {Response} from "@angular/http";
import {
    ROUTER_DIRECTIVES,
} from '@angular/router-deprecated';

@Component({
  selector: 'synchro-list',
  viewProviders: [Title],
  providers: [SynchroFilesService],
  directives: [AlertComponent, ConfirmDialog, CORE_DIRECTIVES, FORM_DIRECTIVES, ROUTER_DIRECTIVES],
  template: `
  <div class="panel panel-primary">
    <div class="panel-heading">Liste des synchronisations</div>

    <div class="panel-body">
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>Détail</th>
                    <th>Nom</th>
                    <th>Rép. maître</th>
                    <th>Rép. esclave</th>
                    <th> </th>
                </tr>
            </thead>
            <tbody>
                <tr *ngFor="let synchro of synchroFileList;let i = index">
                    <td><a [routerLink]="['/Detail', {id : synchro.id}]">Détail</a></td>
                    <td>{{synchro.name}}</td>
                    <td>{{synchro.masterDir}}</td>
                    <td>{{synchro.slaveDir}} </td>
                    <td><a (click)="dialogConfirmRemove(i, synchro.id)"><span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span></a></td>
                </tr>
            </tbody>
        </table>
    </div>
  </div>
  
    <uif-dialog (confirmSelected)="confirmDelete($event)" [display]="confirmDialog" [dialogId]="'confirm'">
        Voulez-vous supprimer cette synchronisation ?
    </uif-dialog>
  `
})
export class SynchroList {
  synchroFileList : any;
  isHttpRequest : boolean = false;
    confirmDialog : boolean = false;

    private _index : number;
    private _id : number;

  constructor(fb: FormBuilder, title : Title, private _synchroFilesService : SynchroFilesService) {
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

    openDialog() {
        this.confirmDialog = true;
    }

    confirmDelete(isConfirmed) {
        this.confirmDialog = false;
        if (isConfirmed) {
            this.remove(this._index, this._id);
        }
    }

  onSubmit(value: string): void {
    console.log('you submitted value: ', value);
  }

    dialogConfirmRemove(index : number, id : number) {
        this._index = index;
        this._id = id;
        this.openDialog();
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


}
