import {Injectable, EventEmitter} from 'angular2/angular2';

@Injectable()
export class EventDispatcherService {
    public jenkinsUrlChanged$:EventEmitter = new EventEmitter();
}