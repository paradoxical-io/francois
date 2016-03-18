import {Component, View, FORM_DIRECTIVES, NgFor, NgIf, TemplateRef, CORE_DIRECTIVES} from 'angular2/angular2';
import {ROUTER_DIRECTIVES, RouteParams} from 'angular2/router';
import {TopNav} from './top-nav'
import {FrancoisApi, JobApplication} from './services/francois'
import Collator = Intl.Collator;
import {Consts, Globals} from "./consts"
import {StateTracking} from "./stateTracking"
import {Job} from "./services/francois";
import {Template} from "./services/francois";
import {JobEditParameter} from "./services/francois";

declare var _;
declare var globals:Globals;

export class HttpUtils {
    public static success(code) {
        return code >= 200 && code < 300;
    }
}

@Component({
    templateUrl: Consts.basePath + '/templates.html',
    directives: [FORM_DIRECTIVES, TopNav, ROUTER_DIRECTIVES, NgFor]
})
export class Templates {

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

@Component({
    templateUrl: Consts.basePath + '/template-jobs.html',
    directives: [FORM_DIRECTIVES, TopNav, ROUTER_DIRECTIVES, NgFor]
})
export class TemplateJobs {

    public jobs:any[] = [];

    public templateName:string;

    public jenkinsUrl:string = globals.jenkinsUrl;

    constructor(private francoisApi:FrancoisApi, params:RouteParams) {

        this.templateName = params.get('templateName');

        var response = francoisApi.getTemplateJobs(this.templateName);

        console.dir(response);

        response.map(r => r.json())
            .subscribe(
                (value) => value.forEach(job => {
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

declare module Foundation {
    class Accordian {
    }
}

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
                        var merged:JobEditParameter [] = _.map(templateParameters, templateParameter => this.merge(job.config.parameters, templateParameter);

                        this.parameters = merged;

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
