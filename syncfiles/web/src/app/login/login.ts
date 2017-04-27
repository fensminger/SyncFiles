import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {LoginService} from './login.service';
import {Account} from '../account/account';
import {AccountEventsService} from '../account/account.events.service';
import {Dropdown, SelectItem, InputText,Button, Message, Messages} from 'primeng/primeng';
import {BreadCrumbService} from '../shared';

@Component({
    selector: 'login',
    providers: [LoginService],
    templateUrl: './login.html'
})
export class Login {
    username:string;
    password:string;
    router:Router;
    wrongCredentials:boolean;
    loginService:LoginService;
    account:Account;
    msgs: Message[] = [];

    constructor(router: Router,loginService:LoginService,accountEventService:AccountEventsService, private breadCrumbService :BreadCrumbService) {
        this.router = router;
        this.wrongCredentials = false;
        this.loginService = loginService;

        accountEventService.subscribe((account) => {
            if(!account.authenticated) {
                if(account.error) {
                    if(account.error.indexOf('BadCredentialsException') !== -1) {
                        this.msgs.push({severity:"error", summary:"Erreur d'authentification", detail:"Mot de passe ou utilisateur invalide."});
                    } else {
                        this.msgs.push({severity:"error", summary:"Erreur d'authentification", detail:account.error});
                    }
                }
            }
        });
    }

  ngOnInit() {
    this.breadCrumbService.changePage([{label : "Authentification"}]);
  }



    authenticate(event, username, password) {
        event.preventDefault();
        this.loginService.authenticate(username,password)
            .subscribe((account) => {
                this.account = account;
                console.log('Successfully logged',account);
                this.router.navigate(['/hive-ref']);
            });
    }
}
