import {Component, View, FORM_DIRECTIVES, NgFor, NgIf, TemplateRef, CORE_DIRECTIVES} from 'angular2/angular2';
import {ROUTER_DIRECTIVES, RouteParams} from 'angular2/router';
import {EventDispatcherService} from "./eventDispatcherService";
import {Globals} from "../consts";
import {Consts} from "../consts";

declare var globals:Globals;

@Component({
    selector: 'jenkins',
    templateUrl: Consts.basePath + '/jenkins-iframe.html',
})
export class JenkinsFrame {

    public currentJenkinsUrl:string = globals.jenkinsUrl;

    constructor(private eventDispatcher:EventDispatcherService) {
        console.log("Jenkins frame");

        eventDispatcher
            .jenkinsUrlChanged$
            .toRx()
            .subscribe(change => this.currentJenkinsUrl = change);
    }
}
