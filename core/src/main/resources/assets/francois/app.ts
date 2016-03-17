import {bootstrap, Injectable, Injector, Component, View, FORM_DIRECTIVES, provide} from 'angular2/angular2';
import {HTTP_PROVIDERS, Http, Request, RequestMethods} from 'angular2/http';
import {RouteConfig, LocationStrategy, HashLocationStrategy, RouterOutlet, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';

import {Router, Location, Route} from 'angular2/router';

import {Templates, TemplateJobs, CreateJob, CreateTemplate} from "./templates";
import {TopNav} from "./top-nav";
import {FrancoisApi} from "./services/francois";


@Component({
    selector: 'francois-app',
    directives: [FORM_DIRECTIVES, TopNav, ROUTER_DIRECTIVES],
    template: `<top-nav></top-nav>
               <router-outlet></router-outlet>`
})
@RouteConfig([
    {path: '/', redirectTo: '/templates' },
    {path: '/templates', component: Templates, as: 'Templates'},
    {path: '/templates/:templateName/jobs', component: TemplateJobs, as: 'Jobs'},
    {path: '/create/:templateName/new', component: CreateJob, as: 'Create'},
    {path: '/templates/new', component: CreateTemplate, as: 'CreateTemplate'},
])
class FrancoisApp {
}

bootstrap(FrancoisApp, [
    ROUTER_PROVIDERS,
    HTTP_PROVIDERS,
    FrancoisApi,
    provide(LocationStrategy, {useClass: HashLocationStrategy})
]);