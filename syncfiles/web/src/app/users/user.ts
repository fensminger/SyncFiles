import {Component} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {Account} from '../account/account';
import {UsersService} from './users.service';
import {Response} from '@angular/http';

@Component({
    selector: 'user',
    templateUrl: './user.html',
    providers:[UsersService]
})
export class User {
    user:Account;
    profiles:Array<string>;
    private sub:any;
    constructor(private router: Router,private userService:UsersService,private route: ActivatedRoute) {
        this.user = new Account();
        this.profiles = [];
        this.router = router;
        this.userService = userService;
        this.getProfiles();
    }
    ngOnInit():void {
        this.sub = this.route.params.subscribe(params => this.getUser(params['id']));
    }
    ngOnDestroy():void {
        this.sub.unsubscribe();
    }
    getUser(id:string):void {
        this.userService.getById(id).subscribe((user:Account) => this.user = user);
    }
    getProfiles():void {
        this.userService.getProfiles().subscribe((profiles:Array<string>) => this.profiles = profiles);
    }
    saveUser():void {
        this.userService.saveUser(this.user).subscribe(() => this.router.navigate(['/users']));
    }
    cancel():void {
        this.router.navigate(['/users']);
    }
}
