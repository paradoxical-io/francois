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
import {HttpUtils} from "../common/common";

@Component({
    templateUrl: Consts.basePath + '/template-create.html',
    directives: [FORM_DIRECTIVES, TopNav, ROUTER_DIRECTIVES, NgFor, NgIf, CORE_DIRECTIVES],
})
export class CreateTemplate {

    public templateName:string;

    public state:StateTracking = new StateTracking(true);

    constructor(private francoisApi:FrancoisApi) {

    }

    createTemplate() {
        this.state.reset();

        this.francoisApi.createTemplate(this.templateName)
            .subscribe(r => {
                console.log(r);

                if (!HttpUtils.success(r.status)) {
                    this.state.setFailureEdit();
                }
                else {
                    this.state.success = true;
                }
            }, oops => {
                console.log(oops);

                this.state.failure = true;
                this.state.isEditable = true;

            }, comp => console.log('Create finished'));
    }
}