import { Injectable } from '@angular/core';
import { Subject }    from 'rxjs/Subject';
import {MenuItem} from 'primeng/primeng';

@Injectable()
export class BreadCrumbService {

  private breadCrumbSubject = new Subject<MenuItem[]>();

  breadCrumbSubjectObs = this.breadCrumbSubject.asObservable();

  constructor() {
  }

  changePage(menu : MenuItem[]) {
    this.breadCrumbSubject.next(menu);
  }

}
