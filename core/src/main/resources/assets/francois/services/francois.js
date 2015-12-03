var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") return Reflect.decorate(decorators, target, key, desc);
    switch (arguments.length) {
        case 2: return decorators.reduceRight(function(o, d) { return (d && d(o)) || o; }, target);
        case 3: return decorators.reduceRight(function(o, d) { return (d && d(target, key)), void 0; }, void 0);
        case 4: return decorators.reduceRight(function(o, d) { return (d && d(target, key, o)) || o; }, desc);
    }
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var angular2_1 = require('angular2/angular2');
var http_1 = require('angular2/http');
var JobApplication = (function () {
    function JobApplication(newJobName, parameters) {
        this.newJobName = newJobName;
        this.parameters = parameters;
    }
    return JobApplication;
})();
exports.JobApplication = JobApplication;
var FrancoisApi = (function () {
    function FrancoisApi(http) {
        this.http = http;
    }
    FrancoisApi.prototype.getTemplates = function () {
        return this.http.get('/api/v1/francois/templates');
    };
    FrancoisApi.prototype.getTemplateParameters = function (templateName) {
        return this.http.get("/api/v1/francois/templates/" + templateName + "/parameters");
    };
    FrancoisApi.prototype.getTemplateJobs = function (templateName) {
        return this.http.get("/api/v1/francois/templates/" + templateName + "/jobs");
    };
    FrancoisApi.prototype.createJob = function (templateName, job) {
        return this.http.post("/api/v1/francois/templates/" + templateName + "/jobs", JSON.stringify(job), {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    };
    FrancoisApi.prototype.reapplyTemplate = function (templateName) {
        return this.http.put("/api/v1/francois/templates/" + templateName + "/jobs");
    };
    FrancoisApi = __decorate([
        angular2_1.Injectable(), 
        __metadata('design:paramtypes', [http_1.Http])
    ], FrancoisApi);
    return FrancoisApi;
})();
exports.FrancoisApi = FrancoisApi;
//# sourceMappingURL=francois.js.map