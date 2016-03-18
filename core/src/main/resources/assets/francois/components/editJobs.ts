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
import {_} from "../common/common";

@Component({
    templateUrl: Consts.basePath + '/job-edit.html',
    directives: [FORM_DIRECTIVES, TopNav, ROUTER_DIRECTIVES, NgFor, NgIf, CORE_DIRECTIVES],
})
export class EditJob {

    public parameters:JobEditParameter [] = [];

    public templateName:string;

    public jobName:string;

    public state:StateTracking = new StateTracking();

    constructor(private francoisApi:FrancoisApi,
                private routeParams:RouteParams) {

        this.templateName = routeParams.get('templateName');
        this.jobName = routeParams.get('jobName');

        francoisApi.getTemplateParameters(this.templateName)
            .map(r => r.json())
            .subscribe(templateParameters => {
                var jobs = francoisApi.getTemplateJobs(this.templateName);

                jobs
                    .map(r => r.json())
                    .flatMap(r => r)
                    // find the job we want
                    .filter(r => r.jobName == this.jobName)
                    .subscribe((job:Job) => {
                        this.parameters = _.map(templateParameters, templateParameter => this.merge(job.config.parameters, templateParameter));

                        setTimeout(() => {
                            $(document).foundation();
                            this.state.isEditable = true;
                        }, 30);
                    });
            });
    }

    merge(jobParameters:any[], templateParameter):JobEditParameter {
        // find the job parameter that matches the template parameter
        var jobParameter = _.findWhere(jobParameters, {name: templateParameter.name});

        if (jobParameter === undefined) {
            return templateParameter;
        }

        var cloned = _.clone(jobParameter);

        // return a new job parameter that has the default value merged from the template
        cloned.defaultValue = templateParameter.defaultValue;

        return cloned;
    }

    saveJob() {
        console.log(this.parameters);

        this.state.reset();

        this.francoisApi.updateJob(
            this.templateName,
            new JobApplication(this.jobName, this.parameters.filter(p => !!p.value)))
            .subscribe(r => {
                console.log(r);

                this.state.success = true;
                this.state.isEditable = true;

                setTimeout(() => {
                    $(document).foundation();
                    this.state.success = false;
                }, 5000);
            }, oops => {
                console.log(oops);
                this.state.isEditable = true;
            }, comp => console.log('Create finished'));
    }

    setJobName(evt) {
        console.log(evt.target.value);
        this.jobName = evt.target.value;
    }
}