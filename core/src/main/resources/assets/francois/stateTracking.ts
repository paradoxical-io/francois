export class StateTracking{
    private _initialState;
    get isEditable():boolean {
        return this._isEditable;
    }

    set isEditable(value:boolean) {
        this._isEditable = value;
    }
    get failure():boolean {
        return this._failure;
    }

    set failure(value:boolean) {
        this._failure = value;
    }
    get success():boolean {
        return this._success;
    }

    set success(value:boolean) {
        this._success = value;
    }

    private _isEditable : boolean = false;
    private _success : boolean = false;
    private _failure : boolean = false;

    constructor(initialEditState : boolean = false){
        this._isEditable = initialEditState;
        this._initialState = initialEditState;
    }

    public reset(){
        this.success = false;
        this.failure = false;
        this.isEditable = this._initialState;
    }

    public setFailureEdit(){
        this.failure = true;
        this.isEditable = true;
    }
}