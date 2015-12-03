import {Component, View, FORM_DIRECTIVES} from 'angular2/angular2';
import {ROUTER_DIRECTIVES} from 'angular2/router';

@Component({
    selector: 'top-nav',
    templateUrl: '/assets/francois/top-nav.html',
    directives: [FORM_DIRECTIVES, ROUTER_DIRECTIVES]
})
export class TopNav {
}