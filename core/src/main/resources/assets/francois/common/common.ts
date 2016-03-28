import {Globals} from "../consts";

export class HttpUtils {
    public static success(code) {
        return code >= 200 && code < 300;
    }
}


declare module Foundation {
    class Accordian {
    }
}
