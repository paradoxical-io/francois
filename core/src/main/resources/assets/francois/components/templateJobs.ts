import {Component, View, FORM_DIRECTIVES, NgFor, NgIf, TemplateRef, CORE_DIRECTIVES} from 'angular2/angular2';
import {ROUTER_DIRECTIVES, RouteParams} from 'angular2/router';
import {TopNav} from './../top-nav'
import {FrancoisApi, JobApplication} from './../services/francois'
import {Consts, Globals} from "./../consts"
import {StateTracking} from "./../stateTracking"
import {Job} from "./../services/francois";
import {Template} from "./../services/francois";
import {JobEditParameter} from "./../services/francois";
import {EventDispatcherService} from "./eventDispatcherService";

declare var _;
declare var globals:Globals;

@Component({
    templateUrl: Consts.basePath + '/template-jobs.html',
    directives: [FORM_DIRECTIVES, TopNav, ROUTER_DIRECTIVES, NgFor]
})
export class ListJobs {

    public jobs:Job[] = [];

    public templateName:string;

    public jenkinsUrl:string = globals.jenkinsUrl;

    constructor(private francoisApi:FrancoisApi, params:RouteParams) {
        console.log("here");

        this.templateName = params.get('templateName');

        var response = francoisApi.getTemplateJobs(this.templateName);

        console.dir(response);

        response.map(r => r.json())
            .subscribe(
                (value:Job[]) => value.forEach((job:Job) => {
                    this.jobs.push(job);
                }),
                error => console.error(error),
                c => console.log("Jobs Loaded"));
    }

    public jobUrl(job:Job) {
        return this.jenkinsUrl + "/job/" + job.jobName + "/configure";
    }

    reapplyTemplate($event:MouseEvent) {

        $event.stopPropagation();
        $event.preventDefault();

        this.francoisApi.reapplyTemplate(this.templateName)
            .subscribe(() => console.log('Template jobs refreshed.'),
                () => console.error('opps, update template jobs failed'));
    }
}
