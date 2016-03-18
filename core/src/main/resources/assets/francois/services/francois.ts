import {Injectable} from 'angular2/angular2';
import {HTTP_PROVIDERS, Http, Request, RequestMethods} from 'angular2/http';

export class JobApplication {
    constructor(public jobName: string, public parameters: any[]){

    }
}

@Injectable()
export class FrancoisApi {

    constructor(public http:Http) {
    }

    getTemplates() {
        return this.http.get('/api/v1/francois/templates');
    }

    getTemplateParameters(templateName: string) {
        return this.http.get(`/api/v1/francois/templates/${templateName}/parameters`);
    }

    getTemplateJobs(templateName: string){
        return this.http.get(`/api/v1/francois/templates/${templateName}/jobs`);
    }

    createTemplate(templateName: string){
        return this.http.post(`/api/v1/francois/templates/${templateName}`);
    }

    updateJob(templateName: string, job: JobApplication) {
        return this.http.put(`/api/v1/francois/templates/${templateName}/jobs/${job.jobName}`, JSON.stringify(job), {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    createJob(templateName: string, job: JobApplication) {
        return this.http.post(`/api/v1/francois/templates/${templateName}/jobs`, JSON.stringify(job), {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }

    reapplyTemplate(templateName: string) {
        return this.http.put(`/api/v1/francois/templates/${templateName}/jobs`);
    }
}