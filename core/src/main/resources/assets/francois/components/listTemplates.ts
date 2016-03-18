import {Component, View, FORM_DIRECTIVES, NgFor, NgIf, TemplateRef, CORE_DIRECTIVES} from 'angular2/angular2';
import {ROUTER_DIRECTIVES, RouteParams} from 'angular2/router';
import {TopNav} from './../top-nav'
import {FrancoisApi, JobApplication} from './../services/francois'
import Collator = Intl.Collator;
import {Consts, Globals} from "./../consts"
import {StateTracking} from "./../stateTracking"
import {Job} from "./../services/francois";
import {Template} from "./../services/francois";
import {JobEditParameter} from "./../services/francois";

@Component({
    templateUrl: Consts.basePath + '/templates.html',
    directives: [FORM_DIRECTIVES, TopNav, ROUTER_DIRECTIVES, NgFor]
})
export class ListTemplates {

    public templates:any[] = [];

    constructor(private francoisApi:FrancoisApi) {
        var response = francoisApi.getTemplates();

        console.dir(response);

        response.map(r => r.json()).subscribe(
            (value) => value.forEach((template:Template) => {
                this.templates.push(template);
                template.parameters = [];
                francoisApi.getTemplateParameters(template.name)
                    .map(r => r.json())
                    .subscribe(params => {
                        params.forEach(p => template.parameters.push(p));
                    });
            }),
            error => console.error(error),
            c => console.log("Templates Loaded"));
    }
}




