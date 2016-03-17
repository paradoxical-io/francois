import {Component, View, FORM_DIRECTIVES} from 'angular2/angular2';
import {ROUTER_DIRECTIVES} from 'angular2/router';
import {Consts} from "./consts"

@Component({
    selector: 'top-nav',
    templateUrl: Consts.basePath + '/top-nav.html',
    directives: [FORM_DIRECTIVES, ROUTER_DIRECTIVES]
})
export class TopNav {
}