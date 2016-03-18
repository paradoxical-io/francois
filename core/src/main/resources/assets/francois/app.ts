import {bootstrap, Injectable, Injector, Component, View, FORM_DIRECTIVES, provide} from 'angular2/angular2';
import {HTTP_PROVIDERS, Http, Request, RequestMethods} from 'angular2/http';
import {RouteConfig, LocationStrategy, HashLocationStrategy, RouterOutlet, ROUTER_DIRECTIVES, ROUTER_PROVIDERS} from 'angular2/router';

import {Router, Location, Route} from 'angular2/router';

import {TopNav} from "./top-nav";
import {FrancoisApi} from "./services/francois";
import {ListTemplates} from "./components/listTemplates";
import {ListJobs} from "./components/templateJobs";
import {EditJob} from "./components/editJobs";
import {CreateTemplate} from "./components/createTemplate";
import {CreateJob} from "./components/createJob";


@Component({
    selector: 'francois-app',
    directives: [FORM_DIRECTIVES, TopNav, ROUTER_DIRECTIVES],
    template: `<top-nav></top-nav>
               <router-outlet></router-outlet>`
})
@RouteConfig([
    {path: '/', redirectTo: '/templates' },
    {path: '/templates', component: ListTemplates, as: 'ListTemplates'},
    {path: '/templates/:templateName/jobs', component: ListJobs, as: 'ListJobs'},
    {path: '/templates/:templateName/:jobName', component: EditJob, as: 'EditJobs'},
    {path: '/templates/new', component: CreateTemplate, as: 'CreateTemplate'},
    {path: '/create/:templateName/new', component: CreateJob, as: 'CreateJob'}
])
class FrancoisApp {
}

bootstrap(FrancoisApp, [
    ROUTER_PROVIDERS,
    HTTP_PROVIDERS,
    FrancoisApi,
    provide(LocationStrategy, {useClass: HashLocationStrategy})
]);