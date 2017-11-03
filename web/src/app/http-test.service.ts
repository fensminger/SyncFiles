import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable }     from 'rxjs/Rx';


export /**
 * Hello
 */
class Hello {
  constructor(public id, public content) {
    
  }
}

@Injectable()
export class HttpTestService {

  constructor(private http : Http) {}

  getHello(name : String) : Observable<Hello> {
    return this.http.get('/api/hello?name='+name)
            .map(this.extractData)
            .catch(this.handleError);
  }

  private extractData(res: Response) {
    let body = res.json();
    return body || { };
  }

  private handleError (error: any) {
    // In a real world app, we might use a remote logging infrastructure
    // We'd also dig deeper into the error to get a better message
    let errMsg = (error.message) ? error.message :
      error.status ? `${error.status} - ${error.statusText}` : 'Server error';
    console.error(errMsg); // log to console instead
    return Observable.throw(errMsg);
  }  
}
