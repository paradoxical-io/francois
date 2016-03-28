import {Component, View, FORM_DIRECTIVES, NgFor, NgIf, TemplateRef, CORE_DIRECTIVES} from 'angular2/angular2';
import {ROUTER_DIRECTIVES, RouteParams} from 'angular2/router';
import {TopNav} from '../top-nav'
import {FrancoisApi, JobApplication} from '../services/francois'
import Collator = Intl.Collator;
import {Consts, Globals} from "../consts"
import {StateTracking} from "../stateTracking"
import {Job} from "../services/francois";
import {Template} from "../services/francois";
import {JobEditParameter} from "../services/francois";
import {HttpUtils} from "../common/common";

@Component({
    templateUrl: Consts.basePath + '/job-create.html',
    directives: [FORM_DIRECTIVES, TopNav, ROUTER_DIRECTIVES, NgFor, NgIf, CORE_DIRECTIVES],
})
export class CreateJob {

    public parameters:any[] = [];

    public templateName:string;

    public newJobName:string = '';

    public state:StateTracking = new StateTracking(true);

    constructor(private francoisApi:FrancoisApi,
                private routeParams:RouteParams) {

        this.templateName = routeParams.get('templateName');

        var response = francoisApi.getTemplateParameters(this.templateName);

        response
            .map(r => r.json())
            .subscribe(params => {
                params.forEach(p => this.parameters.push(p));
                setTimeout(() => {
                    $(document).foundation();
                    this.state.isEditable = true;
                }, 30);
            });
    }

    createJob() {
        console.log(this.parameters);

        this.state.reset();

        this.francoisApi.createJob(
            this.templateName,
            new JobApplication(this.newJobName, this.parameters.filter(p => !!p.value)))
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

                this.state.setFailureEdit();
            }, comp => console.log('Create finished'));
    }

    setJobName(evt) {
        console.log(evt.target.value);
        this.newJobName = evt.target.value;
    }
}