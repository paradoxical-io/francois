import {Injectable} from 'angular2/angular2';
import {HTTP_PROVIDERS, Http, Request, RequestMethods} from 'angular2/http';

export class JobApplication {
    constructor(public jobName:string, public parameters:any[]) {
    }
}

export class Template {
    constructor(public name:string, public parameters:any[]) {
    }
}

export class JobConfig {
    constructor(public parameters:any[]) {
    }
}

export class Job {
    constructor(public jobName:string, public config:JobConfig) {
    }
}

export class JobEditParameter {
    constructor(public name:string, public defaultValue:String, public value:String) {
    }
}

@Injectable()
export class FrancoisApi {

    constructor(public http:Http) {
    }

    getTemplates() {
        return this.http.get('/api/v1/francois/templates');
    }

    getTemplateParameters(templateName:string) {
        return this.http.get(`/api/v1/francois/templates/${templateName}/parameters`);
    }

    getTemplateJobs(templateName:string) {
        return this.http.get(`/api/v1/francois/templates/${templateName}/jobs`);
    }

    createTemplate(templateName:string) {
        return this.http.post(`/api/v1/francois/templates/${templateName}`);
    }

    updateJob(templateName:string, job:JobApplication) {
        return this.http.put(`/api/v1/francois/templates/${templateName}/jobs/${job.jobName}`, JSON.stringify(job), {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    createJob(templateName:string, job:JobApplication) {
        return this.http.post(`/api/v1/francois/templates/${templateName}/jobs`, JSON.stringify(job), {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    reapplyTemplate(templateName:string) {
        return this.http.put(`/api/v1/francois/templates/${templateName}/jobs`);
    }
}