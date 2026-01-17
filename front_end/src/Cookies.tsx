import {Cookies} from 'react-cookie';

const cookies = new Cookies();
let short = 60 * 30 ;
let long = 60 * 60 * 24 * 7;
export const setCookie = (name: string, value: string) => {
 	cookies.set(name, value, {maxAge : long, secure: false, path : "/"}); 
}

export const setTime = (alarms : any[]) => {
    for (let i = 0; i < alarms.length; i++) {
        // 시간 문자열 변환
        alarms[i].time = alarms[i].time.replaceAll("-", "/");
        alarms[i].time = alarms[i].time.replace("T", " ");
        alarms[i].time = alarms[i].time.substring(0, alarms[i].time.lastIndexOf("+"));
    
        // 시간을 Date 객체로 파싱하고 9시간을 더함
        const originalDate = new Date(alarms[i].time);
        const modifiedDate = new Date(originalDate.getTime() + 9 * 60 * 60 * 1000);
    
        // 원하는 양식으로 포맷팅
        const formattedDate = `${modifiedDate.getFullYear()}/${(modifiedDate.getMonth() + 1).toString().padStart(2, '0')}/${modifiedDate.getDate().toString().padStart(2, '0')} ${modifiedDate.getHours().toString().padStart(2, '0')}:${modifiedDate.getMinutes().toString().padStart(2, '0')}:${modifiedDate.getSeconds().toString().padStart(2, '0')}.${modifiedDate.getMilliseconds().toString().padStart(3, '0')}`;
    
        alarms[i].time = formattedDate;
    }
    
    return alarms;
}

export const setCookieAll = (value:any) => {
 	cookies.set("access_token", value.access_token, {maxAge : short, secure: false, path : "/"}); 
 	cookies.set("refresh_token", value.refresh_token, {maxAge : long, secure: false, path : "/"}); 
    if (value.dto.platform !== 'I') value.dto.phone = value.dto.phone.substring(0, value.dto.phone.length - 1);
    value.dto.speed = parseFloat(value.dto.speed);
 	cookies.set("dto", value.dto, {maxAge : long, secure: false, path : "/"}); 
}

export const getCookie = (name: string) => {
    return cookies.get(name); 
}

export const getCookieAll = () => cookies.getAll();

export const removeCookieAll = () => {
    document.cookie = 'access_token=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    document.cookie = 'refresh_token=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    document.cookie = 'dto=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    cookies.remove("access_token", {path : "/"});
    cookies.remove("refresh_token", {path : "/"});
    cookies.remove("dto_token", {path : "/"});
    window.localStorage.clear();
    window.location.reload();
}

export const cookieEmpty = () => {
    return Object.keys(getCookieAll()).length === 0;
}


