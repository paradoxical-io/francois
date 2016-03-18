import {Component, View, FORM_DIRECTIVES, NgFor, NgIf, TemplateRef, CORE_DIRECTIVES} from 'angular2/angular2';
import {ROUTER_DIRECTIVES, RouteParams} from 'angular2/router';
import {TopNav} from './top-nav'
import {FrancoisApi, JobApplication} from './services/francois'
import Collator = Intl.Collator;
import {Consts} from "./consts"

declare var _;

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
            (value) => value.forEach(template => {
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

    public isCreateable:boolean = true;
    public createSucceeded:boolean = false;
    public createFailed:boolean = false;

    constructor(private francoisApi:FrancoisApi) {

    }

    createTemplate() {
        this.isCreateable = false;
        this.createSucceeded = false;
        this.createFailed = false;
        this.francoisApi.createTemplate(this.templateName)
            .subscribe(r => {
                console.log(r);

                if (!HttpUtils.success(r.status)) {
                    this.createFailed = true;
                    this.isCreateable = true;
                }
                else {
                    this.createSucceeded = true;
                }
            }, oops => {
                console.log(oops);
                this.isCreateable = true;
                this.createFailed = true;
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

    public isCreateable:boolean = false;
    public createSucceeded:boolean = false;
    public createFailed:boolean = false;

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
                    this.isCreateable = true;
                }, 30);
            });
    }

    createJob() {
        console.log(this.parameters);

        this.isCreateable = false;
        this.createSucceeded = false;
        this.createFailed = false;

        this.francoisApi.createJob(
            this.templateName,
            new JobApplication(this.newJobName, this.parameters.filter(p => !!p.value)))
            .subscribe(r => {
                console.log(r);

                if (!HttpUtils.success(r.status)) {
                    this.createFailed = true;
                    this.isCreateable = true;
                }
                else {
                    this.createSucceeded = true;
                }
            }, oops => {
                console.log(oops);
                this.isCreateable = true;
                this.createFailed = true;
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

    public parameters:any[] = [];

    public templateName:string;

    public jobName:string;

    public isCreateable:boolean = false;
    public createSucceeded:boolean = false;

    constructor(private francoisApi:FrancoisApi,
                private routeParams:RouteParams) {

        this.templateName = routeParams.get('templateName');
        this.jobName = routeParams.get('jobName');

        francoisApi.getTemplateParameters(this.templateName)
            .map(r => r.json())
            .subscribe(template => {
                var jobs = francoisApi.getTemplateJobs(this.templateName);

                jobs
                    .map(r => r.json())
                    .flatMap(r => r)
                    .filter(r => r.jobName == this.jobName)
                    .subscribe(job => {
                        var merged = _.map(template, templateValue => this.merge(job.config.parameters, templateValue);

                        this.parameters = merged;

                        setTimeout(() => {
                            $(document).foundation();
                            this.isCreateable = true;
                        }, 30);
                    });
            });
    }

    merge(jobParameters, templateValue) {
        var jobParameter = _.findWhere(jobParameters, {name: templateValue.name});

        if (jobParameter === undefined) {
            return templateValue;
        }

        var cloned = _.clone(jobParameter);

        cloned.defaultValue = templateValue.defaultValue;

        return cloned;
    }

    saveJob() {
        console.log(this.parameters);
        this.isCreateable = false;
        this.francoisApi.updateJob(
            this.templateName,
            new JobApplication(this.jobName, this.parameters.filter(p => !!p.value)))
            .subscribe(r => {
                console.log(r);
                this.createSucceeded = true;
                this.isCreateable = true;

                setTimeout(() => {
                    $(document).foundation();
                    this.createSucceeded = false;
                }, 5000);
            }, oops => {
                console.log(oops);
                this.isCreateable = true;
            }, comp => console.log('Create finished'));
    }

    setJobName(evt) {
        console.log(evt.target.value);
        this.jobName = evt.target.value;
    }
}
