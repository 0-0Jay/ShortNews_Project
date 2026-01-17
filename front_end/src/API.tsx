import axios from "axios";
import {
  getCookie,
  removeCookieAll,
  setCookie,
  setCookieAll,
  setTime,
} from "./Cookies";
import AWS from "aws-sdk";
import {
  S3Client,
  DeleteObjectCommand,
  GetObjectCommand,
} from "@aws-sdk/client-s3"; // ES Modules import
import { EventSourcePolyfill, NativeEventSource } from "event-source-polyfill";
export const JAVA = process.env.REACT_APP_JAVA;
export const PYTHON = process.env.REACT_APP_PYTHON;
let flag = true;
export function expiration() {
  if (flag && (!getCookie("dto") || !getCookie("refresh_token"))) {
    removeCookieAll();
    window.location.replace("/");
    flag = false;
    return false;
  }
  return true;
}

/**
 * Access 토큰이 만료되어서 Refrech 토큰 보내는 함수임
 * @param url 요청 보낼 url
 * @param data 요청 보낼 데이터
 * @param request axios function 예시) postRefresh(url, data, axios.get), postRefresh(url, data, axios.post) 이런식으로 보내면됨
 */
interface RefreshResult {
  flag: boolean;
  data: undefined | any;
}
export const postRefresh = async (url: string, request?: any, data?: any) => {
  let result: RefreshResult = {
    flag: false,
    data,
  };
  const refresh_token = getCookie("refresh_token");

  await request(url, {
    headers: {
      Authorization: refresh_token,
    },
    params: data,
  })
    .then((res: any) => {
      // refresh 토큰이 유효하다면
      var access_token = res.headers.authorization;
      access_token = access_token.substring(7, access_token.length);
      setCookie("access_token", access_token);

      result.flag = true;
      result.data = res.data;
    })
    .catch((e: any) => {
      result.flag = false;
      window.location.replace("/");
      removeCookieAll();
    });
  return result;
};

export const patchRefresh = async (url: string, request?: any, data?: any) => {
  let result: RefreshResult = {
    flag: false,
    data,
  };
  const refresh_token = getCookie("refresh_token");

  await request(url, data, {
    headers: {
      Authorization: refresh_token,
    },
  })
    .then((res: any) => {
      // refresh 토큰이 유효하다면
      var access_token = res.headers.authorization;
      access_token = access_token.substring(7, access_token.length);
      setCookie("access_token", access_token);

      result.flag = true;
      result.data = res.data;
    })
    .catch(() => {
      result.flag = false;
      removeCookieAll();
    });
  return result;
};
export const deleteRefresh = async (url: string, request?: any, data?: any) => {
  let result: RefreshResult = {
    flag: false,
    data,
  };
  const refresh_token = getCookie("refresh_token");

  await request(url, {
    headers: {
      Authorization: refresh_token,
    },
    data: data,
  })
    .then((res: any) => {
      // refresh 토큰이 유효하다면
      var access_token = res.headers.authorization;
      access_token = access_token.substring(7, access_token.length);
      setCookie("access_token", access_token);

      result.flag = true;
      result.data = res.data;
    })
    .catch(() => {
      result.flag = false;
      removeCookieAll();
    });
  return result;
};
/**
 *
 * @param SetNews SetNews 스테이트임
 * @param url JAVA url
 * @param cate 숫자로
 * @param date yyyymmdd 형식으로
 */
export async function getNews(
  SetNews: any,
  cate: string,
  date: string,
  navi: any,
  SetSpin: any
) {
  let URL = `${JAVA}main/news/${cate}/${date}`;
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      SetNews(res.data.news_list);
    })
    .catch(async (res) => {
      console.log("API getNews refresh CALL");
      console.error(res);
      let result = await postRefresh(URL, axios.get);
      if (result.flag) {
        SetNews(result.data.news_list);
      } else {
        removeCookieAll();
        navi("/");
      }
    })
    .finally(() => SetSpin(false));
}

export async function getRecommend(SetNews: any, navi: any, SetSpin: any) {
  const URL = PYTHON + "main/recommend";
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then(async (res) => {
      if (res.data.status === 200) {
        if (window.location.pathname === "/main/recommend") {
          SetNews([
            ...res.data.selectedList,
            ...res.data.nonselectedList,
            ...res.data.subList,
          ]);
        }
      } else {
        console.log("API getNews refresh CALL");
        console.error(res);
        let result = await postRefresh(URL, axios.get);
        if (result.flag) {
          SetNews(result.data.news_list);
        } else {
          removeCookieAll();
          navi("/");
        }
      }
    })
    .catch((e) => {
      console.log("getRecommend Error");
      console.error(e);
    })
    .finally(() => SetSpin(false));
}

export async function getNewsContent(
  SetNews: any,
  url: string,
  cate: string,
  date: string
) {
  await axios
    .get(`${url}news/${cate}/${date}`)
    .then((res) => {
      console.log("getNews!");
      SetNews(res.data.news_list);
    })
    .catch((res) => {
      console.log("API getNews Error");
      console.error(res);
    });
}

/**
 *
 * @param Err 배열
 * @param Status 배열
 * @param UserInfo 객체
 * @param EMAIL 상수(숫자)
 * @param url 경로
 * @param loddingSet state
 * @param SetCode state
 * @param SetErr state
 * @param SetStatus state
 */
export async function emailCheck(
  Err: string[],
  Status: boolean[],
  email: string,
  EMAIL: number,
  loddingSet: any,
  SetCode: any,
  SetErr: any,
  SetStatus: any
) {
  // parameter
  let eRe = /^010[0-9]{8}$/;
  let copy = [...Err];
  let status = [...Status];
  let URL = (process.env.REACT_APP_JAVA as string) + "signup/phoneCheck";
  if (email === "") {
    copy[EMAIL] = "전화번호를 입력해주세요";
    status[EMAIL] = false;
    SetErr(copy);
    SetStatus(status);
  } else if (!eRe.test(email)) {
    copy[EMAIL] = "전화번호 양식을 지켜주세요";
    status[EMAIL] = false;
    SetErr(copy);
    SetStatus(status);
  } else {
    loddingSet(true);
    await axios
      .post(URL, { phone: email })
      .then((res) => {
        let flag = res.data.flag;
        if (flag) {
          console.log(res.data.code);
          SetCode(res.data.code);
          copy[EMAIL] = "";
          status[EMAIL] = true;
        } else {
          copy[EMAIL] = "중복된 전화번호입니다.";
          status[EMAIL] = false;
        }
      })
      .catch((res) => {
        console.log("SignUp Auth getCode Error");
        console.error(res);
      })
      .finally(() => {
        loddingSet(false);
        SetErr(copy);
        SetStatus(status);
      });
  }
}

interface missInfo {
  id: string;
  phone: string;
  code: string;
}
export async function findId(
  SetUserInfo: any,
  SetErr: any,
  SetStatus: any,
  SetCode: any,
  UserInfo: missInfo,
  loddingSet: any
) {
  let eRe = /^010[0-9]{8}$/;
  let URL = (process.env.REACT_APP_JAVA as string) + "login/findId";
  console.log(UserInfo.phone);
  if (UserInfo.phone === "") {
    SetErr("전화번호를 입력해주세요");
    SetStatus(false);
  } else if (!eRe.test(UserInfo.phone)) {
    SetErr("전화번호 양식을 지켜주세요");
    SetStatus(false);
  } else {
    loddingSet(true);
    await axios
      .post(URL, { phone: UserInfo.phone })
      .then((res) => {
        let flag = res.data.flag;
        if (flag) {
          console.log(res.data.code);
          SetCode(res.data.code);
          SetErr("");
          SetStatus(true);
          SetUserInfo({
            id: res.data.id,
            phone: UserInfo.phone,
            code: res.data.code,
          });
        } else {
          SetErr("존재하지 않은 번호입니다.");
          SetStatus(false);
        }
      })
      .catch((res) => {
        console.log("SignUp Auth getCode Error");
        console.error(res);
      })
      .finally(() => {
        loddingSet(false);
      });
  }
}
export async function updateEmail(
  SetErr: any,
  SetStatus: any,
  SetCode: any,
  email: string,
  loddingSet: any
) {
  let URL = (process.env.REACT_APP_JAVA as string) + "signup/phoneCheck";
  console.log(email);
  let eRe = /^010[0-9]{8}$/;

  if (email === "") {
    SetErr("전화번호를 입력해주세요");
    SetStatus(false);
  } else if (!eRe.test(email)) {
    SetErr("전화번호 양식을 지켜주세요");
    SetStatus(false);
  } else {
    loddingSet(true);
    await axios
      .post(URL, { phone: email })
      .then((res) => {
        let flag = res.data.flag;
        if (flag) {
          console.log(res.data.code);
          SetCode(res.data.code);
          SetErr("");
          SetStatus(true);
        } else {
          SetErr("이미 가입된 전화번호입니다.");
          SetStatus(false);
        }
      })
      .catch((res) => {
        console.log("SignUp Auth getCode Error");
        console.error(res);
      })
      .finally(() => {
        loddingSet(false);
      });
  }
}

export async function finalUpdateEmail(email: string) {
  console.log(email);
  let URL = JAVA + "member/mypage/updatePhone";
  let dto = getCookie("dto");
  if (!dto) {
    expiration();
    return;
  }
  let data = { phone: email };
  await axios
    .patch(URL, data, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      console.log("email update " + res.data.status);
      dto.phone = email;
      setCookie("dto", dto);
    })
    .catch(async (e) => {
      console.log("final update email refresh Call");
      alert(e);
      let result = await patchRefresh(URL, axios.patch, data);
      if (result.flag) {
        console.log("email update " + result.data.status);
        dto.phone = email;
        setCookie("dto", dto);
      } else {
        removeCookieAll();
        window.location.href = "/";
      }
    })
    .finally(() => {
      window.location.reload();
    });
}

export async function findPassword(
  UserInfo: missInfo,
  SetErr: any,
  SetStatus: any,
  loddingSet: any,
  SetCode: any
) {
  const re2 = /^[A-Za-z0-9]+$/;
  let eRe = /^010[0-9]{8}$/;
  loddingSet(true);
  if (UserInfo.id === "") {
    SetErr("아이디를 입력해주세요.");
    SetStatus(false);
  } else if (UserInfo.id.length < 2 || UserInfo.id.length > 20) {
    SetErr("2자리 이상 20자리 이하로 입력해주세요");
    SetStatus(false);
  } else if (!re2.test(UserInfo.id)) {
    SetErr("영문자 및 숫자만 입력해주세요.");
    SetStatus(false);
  } else if (UserInfo.phone === "") {
    SetErr("전화번호를 입력해주세요");
    SetStatus(false);
  } else if (!eRe.test(UserInfo.phone)) {
    SetErr("전화번호 양식을 지켜주세요");
    SetStatus(false);
  } else {
    await axios
      .post(`${JAVA}login/findPassword`, {
        id: UserInfo.id,
        phone: UserInfo.phone,
      })
      .then((res) => {
        let flag = res.data.flag;
        if (flag) {
          SetStatus(true);
          SetErr("");
          SetCode(res.data.code);
          console.log(res.data.code);
        } else {
          SetErr("일치하는 회원이 없습니다.");
        }
      })
      .catch((e) => {
        console.log("API findPassword Errer");
        console.error(e);
      });
  }
  loddingSet(false);
}

interface updatePw {
  id: string;
  pw: string;
}
export async function updatePassword(
  UserInfo: updatePw,
  SetErr: any,
  loddingSet: any,
  navi: any
) {
  if (UserInfo.pw === "") {
    SetErr("비밀번호를 입력해주세요");
    return;
  }
  loddingSet(true);
  await axios
    .patch(`${JAVA}login/updatePassword`, UserInfo)
    .then((res) => {
      let flag = res.data.status;
      if (flag) {
        alert("비밀번호 변경 성공");
        navi("/");
      } else {
        SetErr("재시도 해주세요");
      }
    })
    .catch((e) => {
      console.log("API updatePassword Errer");
      console.error(e);
    });
  loddingSet(false);
}

export async function shareNews(
  newsid: string,
  to : string,
  guest: string,
  SetArticle: any,
  SetResource: any
) {
  let URL = JAVA + to + newsid + "?guest=" + guest;
  await axios
    .get(URL)
    .then((res) => {
      if (res.data.status == "OK") {
        SetArticle(res.data.news);
        SetResource(res.data.link.split(","))
        console.log(res.data);
      } else {
        window.location.replace("/pageNotFound");
      }
    })
    .catch((e) => {
      console.log(e);
    })
}

export async function selectNews(
  newsid: string,
  navi: any,
  SetArticle: any,
  isClicked: any,
  setClicked: any
) {
  let URL = JAVA + "main/selectNews/" + newsid;

  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      if (res.data.status === "OK") {
        SetArticle(res.data.news);
        console.log(res.data.news)
        let copy = [...isClicked];
        copy[0] = copy[1] = false;
        if (res.data.news.type === 1) {
          copy[0] = true;
        } else if (res.data.news.type === -1) {
          copy[1] = true;
        }
        if (res.data.news.bookmark === 1) {
          copy[4] = true;
        }
        setClicked(copy);
      } else {
        navi("/pageNotFound", { replace: true });
      }
    })
    .catch(async (res) => {
      console.log("API getNews refresh CALL");
      let result = await postRefresh(URL, axios.get);
      if (result.data.status === "OK") {
        SetArticle(result.data);
        let copy = [...isClicked];
        copy[0] = copy[1] = false;
        if (result.data.type === 1) {
          copy[0] = true;
        } else if (result.data.type === 0) {
          copy[1] = true;
        }

        if (result.data.bookmark) {
          copy[4] = true;
        }
        setClicked(copy);
      } else {
        window.location.replace("/pageNotFound");
      }
    });
}

// 사진 가져옴
export async function getProfileS3(id: any) {
  const config = {
    region: process.env.REACT_APP_REGION as string,
    credentials: {
      accessKeyId: process.env.REACT_APP_ACCESS_KEY_ID as string,
      secretAccessKey: process.env.REACT_APP_SECRET_ACCESS_KEY as string,
    },
  };
  const client = new S3Client(config);
  const input = {
    // CopyObjectRequest
    Bucket: "snewsuserprofile", // required
    Key: id as string, // required
    Range: "bytes=0-9",
  };
  const command = new GetObjectCommand(input);
  await client
    .send(command)
    .then((res) => {
      console.log(res);
      console.log("profile delete");
      return res.Body?.transformToString();
    })
    .catch((e) => {
      console.log("getProfileS3 error");
      console.error(e);
    });
}

// 사진 업로드
export async function uploadS3(data: any) {
  console.log("uploadS3");
  console.log(data);
  if (data.value === "") return;
  const REGION = process.env.REACT_APP_REGION;
  const ACCESS_KEY = process.env.REACT_APP_ACCESS_KEY_ID;
  const SECRET_KEY = process.env.REACT_APP_SECRET_ACCESS_KEY;
  AWS.config.update({
    region: REGION,
    accessKeyId: ACCESS_KEY,
    secretAccessKey: SECRET_KEY,
  });

  const upload = new AWS.S3.ManagedUpload({
    params: {
      ACL: "public-read",
      Bucket: "snewsuserprofile",
      Key: data.id,
      Body: data.file,
    },
  });

  await upload
    .promise()
    .then(() => {console.log("img upload"); window.location.reload()})
    .catch((e: any) => {
      console.log("cate-modal s3 upload error");
      console.error(e);
    });
}
// 사진 삭제
export async function deleteS3(data: any) {
  console.log(data);
  const config = {
    region: process.env.REACT_APP_REGION as string,
    credentials: {
      accessKeyId: process.env.REACT_APP_ACCESS_KEY_ID as string,
      secretAccessKey: process.env.REACT_APP_SECRET_ACCESS_KEY as string,
    },
  };
  const client = new S3Client(config);
  const input = {
    // CopyObjectRequest
    Bucket: "snewsuserprofile", // required
    Key: data.id as string, // required
  };
  const command = new DeleteObjectCommand(input);
  await client
    .send(command)
    .then(() => {
      console.log("profile delete");
      window.location.reload()
    })
    .catch((e) => {
      console.log("deleteS3 error");
      console.error(e);
    });
}

export async function socialLogin(
  platform: string,
  code: string,
  setUserInfo: any,
  UserInfo: any,
  navi: any
) {
  let result = false;
  console.log(UserInfo);
  console.log(code);
  await axios
    .get(`${JAVA}${platform}/login/${code}`)
    .then((res) => {
      let copy = { ...UserInfo };
      if (!res.data.message) {
        copy.id = res.data.id;
        copy.nickname = res.data.nickname;
        copy.phone = res.data.phone;
        setUserInfo(copy);
        console.log("카피임");
        console.log(copy);
        console.log(res.data);
        result = true;
      } else {
        console.log(res.data);
        setCookieAll(res.data);
        navi("/main/recommend", { replace: true });
      }
    })
    .catch((e) => {
      console.error(e);
      alert(e);
      alert("다시 시도해주세요");
      navi("/");
      console.log("Social Login Error");
      console.error(e);
    });
  return result;
}

// google
export async function google(navi: any) {
  let URL = "";
  await axios
    .post(`${JAVA}login/oauth2/code/google`)
    .then((res: any) => {
      console.log("google info");
      console.log(res);
      if (res.status === 200) {
        URL = res.data;
      }
    })
    .catch((e: any) => {
      alert("다시 시도해주세요");
      navi("/");
      console.log("Google Login Error");
      console.error(e);
    });
  return URL;
}

export async function googleLogin(
  navi: any,
  code: any,
  UserInfo: any,
  setUserInfo: any
) {
  let result = false;
  let copy = { ...UserInfo };
  await axios
    .get(`${JAVA}login/oauth2/code/google`, {
      params: {
        code: code,
      },
    })
    .then((res) => {
      console.log("googleLogin info");
      console.log(res);
      if (res.data.message === false) {
        copy.id = res.data.id;
        copy.nickname = res.data.nickname;
        copy.email = res.data.email;
        setUserInfo(copy);
        result = true;
      } else {
        setCookieAll(res.data);
        navi("/main/recommend", { replace: true });
      }
    })
    .catch((e) => {
      alert("다시 시도해주세요");
      navi("/");
      console.log("Google Login Error");
      console.error(e);
    });
  return result;
}
// google end

// 기사나 댓글에 좋아요나 싫어요 누르면 호출
export function updateLike(data: any, navi: any, newsid: any, replyid?: any) {
  const URL = (process.env.REACT_APP_JAVA as string) + "main/like";
  let LIKE = data[0];
  let DISLIKE = data[1];
  let result = -1;
  if (!(LIKE || DISLIKE)) result = 0;
  else if (LIKE) result = 1;
  console.log(result);
  let body = {
    like: result,
    news_id: newsid,
    reply_id: replyid,
  };
  axios
    .patch(URL, body, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then(() => console.log("OK"))
    .catch(async (e) => {
      console.log("API updateLike call Refrech");
      console.error(e);
      let result = await patchRefresh(URL, axios.patch, body);
      if (result.flag === false) {
        removeCookieAll();
        navi("/");
      }
    });
}

// 북마크 설정 또는 해제
export function updateBookMark(newsid: any, type: boolean, navi: any) {
  const URL = (process.env.REACT_APP_JAVA as string) + "main/bookmark";
  let data = {
    news_id: newsid,
    type: !type,
  };
  console.log(type);
  axios
    .patch(URL, data, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then(() => console.log("bookmark update"))
    .catch(async (e) => {
      console.log("API updateBookMark call Refrech");
      let result = await patchRefresh(URL, axios.patch, data);
      if (result.flag === false) {
        removeCookieAll();
        navi("/");
      }
    });
}

// 기사 출처 가져오기
export async function getSource(data: any, setResource: any) {
  const URL = (process.env.REACT_APP_JAVA as string) + `main/link/${data}`;
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      console.log("Links");
      res.data.link = res.data.link.split(",");
      console.log(res.data);
      setResource(res.data.link);
    })
    .catch(async (e) => {
      console.log("API getSource call Refrech");
      console.error(e)
      let result = await patchRefresh(URL, axios.patch, data);
      if (result.flag === false) {
        removeCookieAll();
        window.location.replace("/");
      }
    });
}

// 댓글 가져올 때 호출
export async function getReply(
  news_id: string,
  setReplyList: any,
  status?: number
) {
  const URL =
    (process.env.REACT_APP_JAVA as string) + `main/selectNews/${news_id}/reply`;
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      if (status === 1) {
        res.data.replies.sort((a: any, b: any) => {
          return parseInt(b.reply_id) - parseInt(a.reply_id);
        });
      } else if (status === -1) {
        res.data.replies.sort((a: any, b: any) => {
          if (b.like - a.like !== 0) return b.like - a.like;
          else return parseInt(b.reply_id) - parseInt(a.reply_id);
        });
      }
      setReplyList(res.data.replies);
    })
    .catch(async (e) => {
      console.log("API getReply Refresh Call");
      let result = await postRefresh(URL, axios.get);
      if (result.flag) {
        if (status === 1) {
          result.data.replies.sort((a: any, b: any) => {
            return parseInt(b.reply_id) - parseInt(a.reply_id);
          });
        } else if (status === -1) {
          result.data.replies.sort((a: any, b: any) => {
            if (b.like - a.like !== 0) return b.like - a.like;
            else return parseInt(b.reply_id) - parseInt(a.reply_id);
          });
        }
      }
      console.error(e);
    });
}

// Create Reply
export async function createReply(
  setReplyList: any,
  navi: any,
  news_id: string,
  reply: string,
  status: number,
  upper_id?: string,
  upper_user?: string
) {
  const URL = (process.env.REACT_APP_JAVA as string) + `main/write`;
  let body = {
    news_id: news_id,
    reply: reply,
    upper_id: upper_id,
    upper_user: upper_user,
  };
  console.log(body);
  await axios
    .patch(URL, body, {
      headers: {
        Authorization: getCookie("access_token"),
      },
      responseEncoding: "utf8",
    })
    .then((res) => {
      console.log(res.data);
      if (status === 1) {
        res.data.replies.sort((a: any, b: any) => {
          return parseInt(b.reply_id) - parseInt(a.reply_id);
        });
      } else if (status === -1) {
        res.data.replies.sort((a: any, b: any) => {
          if (b.like - a.like !== 0) return b.like - a.like;
          else return parseInt(b.reply_id) - parseInt(a.reply_id);
        });
      }
      setReplyList(res.data.replies);
    })
    .catch(async (e) => {
      console.log("API createReply call Refresh");
      console.error(e);
      let result = await patchRefresh(URL, axios.patch, body);
      if (result.flag === true) {
        if (status === 1) {
          result.data.replies.sort((a: any, b: any) => {
            return parseInt(b.reply_id) - parseInt(a.reply_id);
          });
        } else if (status === -1) {
          result.data.replies.sort((a: any, b: any) => {
            if (b.like - a.like !== 0) return b.like - a.like;
            else return parseInt(b.reply_id) - parseInt(a.reply_id);
          });
        }
        setReplyList(result.data.replies);
      } else {
        navi("/");
      }
    });
}

// Delete Reply
export async function deleteReply(
  setReplyList: any,
  navi: any,
  news_id: string,
  reply_id: string,
  status: number
) {
  let URL = (process.env.REACT_APP_JAVA as string) + `main/delete`;
  let body = {
    news_id: news_id,
    reply_id: reply_id,
  };
  await axios
    .patch(URL, body, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      if (status === 1) {
        res.data.replies.sort((a: any, b: any) => {
          return parseInt(b.reply_id) - parseInt(a.reply_id);
        });
      } else if (status === -1) {
        res.data.replies.sort((a: any, b: any) => {
          if (b.like - a.like !== 0) return b.like - a.like;
          else return parseInt(b.reply_id) - parseInt(a.reply_id);
        });
      }
      setReplyList(res.data.replies);
    })
    .catch(async (e) => {
      console.log("API deleteReply call Refresh");
      console.error(e);
      let result = await patchRefresh(URL, axios.patch, body);
      if (result.flag === true) {
        if (status === 1) {
          result.data.replies.sort((a: any, b: any) => {
            return parseInt(b.reply_id) - parseInt(a.reply_id);
          });
        } else if (status === -1) {
          result.data.replies.sort((a: any, b: any) => {
            if (b.like - a.like !== 0) return b.like - a.like;
            else return parseInt(b.reply_id) - parseInt(a.reply_id);
          });
        }
      } else {
        navi("/");
      }
    });
}

// Update Reply
export async function updateReply(
  setReplyList: any,
  navi: any,
  news_id: string,
  reply_id: string,
  text: string,
  status: number
) {
  const URL = (process.env.REACT_APP_JAVA as string) + `main/update`;
  let body = {
    news_id: news_id,
    reply_id: reply_id,
    text: text,
  };
  console.log(body);
  await axios
    .patch(URL, body, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      console.log(res.data);
      console.log("수정됨");
      if (status === 1) {
        res.data.replies.sort((a: any, b: any) => {
          return parseInt(b.reply_id) - parseInt(a.reply_id);
        });
      } else if (status === -1) {
        res.data.replies.sort((a: any, b: any) => {
          if (b.like - a.like !== 0) return b.like - a.like;
          else return parseInt(b.reply_id) - parseInt(a.reply_id);
        });
      }
      setReplyList(res.data.replies);
    })
    .catch(async (e) => {
      console.log("API createReply call Refresh");
      console.error(e);
      let result = await patchRefresh(URL, axios.patch, body);
      if (result.flag === true) {
        if (status === 1) {
          result.data.replies.sort((a: any, b: any) => {
            return parseInt(b.reply_id) - parseInt(a.reply_id);
          });
        } else if (status === -1) {
          result.data.replies.sort((a: any, b: any) => {
            if (b.like - a.like !== 0) return b.like - a.like;
            else return parseInt(b.reply_id) - parseInt(a.reply_id);
          });
        }
        setReplyList(result.data.replies);
      } else {
        navi("/");
      }
    });
}

export async function setCategory(data: any) {
  console.log(data);
  await axios
    .post(JAVA + "selectCategory", data)
    .then((res) => {
      console.log(res.data);
      if (res.data.status) {
        setCookieAll(res.data);
        alert("수정은 마이페이지에서 가능합니다.");
      } else {
        alert("문제 발생 재시도해주세요");
      }
    })
    .catch((res) => {
      console.log("CategoryPick setCategory errer");
      console.error(res);
    });
}

export async function categoryUpdate(data: boolean[], navi: any) {
  const URL = JAVA + "member/categoryUpdate";
  const Arr = ["100", "101", "102", "103", "104", "105", "106", "107"];
  await axios
    .patch(
      URL,
      { cate: data },
      {
        headers: {
          Authorization: getCookie("access_token"),
        },
      }
    )
    .then((res) => {
      if (res.data.status === "OK") {
        let dto = getCookie("dto");
        if (dto === undefined) {
          window.location.replace("/");
          removeCookieAll();
          return;
        }
        let result = Arr.filter((value: string, index: number) => data[index]);
        dto.category = result;
        setCookie("dto", dto);
        alert("선호 카테고리가 수정되었습니다.");
      }
    })
    .catch(async (e) => {
      console.log("API category Update Refresh Call");
      console.error(e);
      let result = await patchRefresh(URL, axios.patch, { cate: data });
      if (result.flag) {
        let dto = getCookie("dto");
        if (!dto) {
          expiration();
          return;
        }
        let res = Arr.filter((value: string, index: number) => data[index]);
        dto.category = res;
        setCookie("dto", dto);
      } else {
        navi("/");
      }
    })
    .catch((e) => {
      window.location.replace("/");
      removeCookieAll();
    });
}

export async function memberTTS(data: any, navi: any) {
  const URL = JAVA + "member/tts";
  await axios
    .patch(URL, data, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      if (res.data.status === "OK") {
        console.log("변경 성공");
        let dto = getCookie("dto");
        if (!dto) {
          expiration();
          return;
        }
        dto.model = data.model;
        dto.speed = parseFloat(data.speed);
        setCookie("dto", dto);
        alert("TTS 수정이 완료되었습니다.");
      }
    })
    .catch(async (e) => {
      console.log("API memberTTS call refresh");
      console.error(e);
      let result = await patchRefresh(URL, axios.patch, data);
      if (result.flag) {
        let dto = getCookie("dto");
        if (!dto) {
          expiration();
          return;
        }
        dto.model = data.model;
        dto.speed = parseFloat(data.speed);
        setCookie("dto", dto);
      } else {
        navi("/");
      }
    });
}

export const memberDelete = async (data: any, navi: any) => {
  console.log(data);
  let URL = JAVA + "member/delete";
  if (!getCookie("dto")) {
    expiration();
    return;
  }
  await axios
    .patch(URL, data, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      if (res.data.status === "OK") {
        navi("/");
        alert("회원탈퇴 완료");
        deleteS3({ id: getCookie("dto").id });
        removeCookieAll();
      }
    })
    .catch(async (e) => {
      console.log("API memberDelete refrech call");
      console.error(e)
      let result = await deleteRefresh(URL, axios.patch, data);
      if (result.flag) {
        alert("회원탈퇴 완료");
        deleteS3({ id: getCookie("dto").id });
        removeCookieAll();
        window.localStorage.clear();
      } else {
        alert("다시 시도해주세요");
        removeCookieAll();
        window.location.replace("/");
      }
    });
};

export async function memberBookmark(setList: any) {
  let URL = JAVA + "member/bookmark";
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      setList(res.data.bookmark);
    })
    .catch(async (e) => {
      console.log("memberBookmard call Refresh");
      console.error(e);
      let res = await postRefresh(URL, axios.get);
      if (res.flag) setList(res.data.bookmark);
    });
}

export async function myActivityLike(setList: any) {
  let URL = JAVA + "myActivity/like";
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      console.log(res.data);
      setList(res.data.newsLike);
    })
    .catch(async (e) => {
      console.log("API myActivity like call refresh");
      console.error(e);
      let result = await postRefresh(URL, axios.get);
      if (result.flag) {
        setList(result.data.newsLike);
      }
    });
}

export async function myActivityDislike(setList: any) {
  let URL = JAVA + "myActivity/dislike";
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      console.log(res.data);
      setList(res.data.newsDislike);
    })
    .catch(async (e) => {
      console.log("API myActivity dislike call refresh");
      console.error(e);
      let result = await postRefresh(URL, axios.get);
      if (result.flag) {
        setList(result.data.newsDislike);
      }
    });
}

export async function myActivityReply(setList: any) {
  let URL = JAVA + "myActivity/reply";
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      res.data.result.sort((a: any, b: any) => b.reply_id - a.reply_id);
      console.log(res.data);
      setList(res.data.result);
    })
    .catch(async (e) => {
      console.log("API myActivity reply call refresh");
      console.error(e);
      let result = await postRefresh(URL, axios.get);
      if (result.flag) {
        result.data.result.sort((a: any, b: any) => b.reply_id - a.reply_id);
        setList(result.data.result);
      }
    });
}

export async function getSearch(
  text: string,
  setNews: any,
  start : number,
  end : number,
  SetMore : any,
) {
  let URL = `main/search?q=${text}&s=${start}&e=${end}`;
  // navigate("/" + URL, {replace:true});
  let check = /^[가-힇|A-Z|a-z|0-9|\s]+$/;
  if (text !== "" && check.test(text) && text.length >= 1) {
    await axios
      .get(JAVA + URL, {
        headers: {
          Authorization: getCookie("access_token"),
        },
      })
      .then((res) => {
        if (res.data.news_list.length > 0) SetMore(true);
        else SetMore(false);
        setNews((prev:any)=>{
          if (start == 1) return res.data.news_list;
          else return [...prev, ...res.data.news_list];
        })
      })
      .catch(async (e) => {
        console.log("API getSearch call Refresh");
        console.error(e);
        let res = await postRefresh(JAVA + URL, axios.get);
        if (res.flag) {
          if (res.data.news_list.length > 0) SetMore(true);
          else SetMore(false);
          setNews((prev:any)=>{
            if (start == 1) return res.data.news_list;
            else return [...prev, ...res.data.news_list];
          })
        }
      });
  } else setNews([]);
}

export const pythonGetSearch = async (
  text: string,
  navigate: any,
  updateKeyword: any,
  setNews: any
) => {
  navigate("main/search/" + text);
  updateKeyword(text);
  await axios
    .get(PYTHON + "main/search/" + text)
    .then((res) => {
      console.log("Python result");
      console.log(res.data);
      setNews(res.data.query);
    })
    .catch((e) => {
      console.log("API PYTHON ERROR");
      console.error(e);
    });
};

export const updateNickname = async (data: any, setErr: any) => {
  let URL = JAVA + "member/updateNickname";
  if (!getCookie("dto")) {
    expiration();
    return;
  }
  await axios
    .patch(URL, data, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      let flag = res.data.message === "";
      if (flag) {
        let dto = getCookie("dto");

        dto.nickname = data.nickname;
        setCookie("dto", dto);
        setErr("변경 완료");
      } else {
        setErr("중복된 닉네임입니다.");
      }
    })
    .catch(async (e) => {
      console.log("API updatenickname error");
      console.error(e);
      let res = await patchRefresh(URL, axios.patch, data);
      if (res.flag) {
        let dto = getCookie("dto");
        dto.nickname = data.nickname;
        setCookie("dto", dto);
        setErr("변경 완료");
      } else {
        setErr("중복된 닉네임입니다.");
      }
    });
};

export const mypageUpdatePW = async (before: string, after: string) => {
  let URL = JAVA + "member/mypage/updatePassword";
  let data = {
    origin_pw: before,
    new_pw: after,
  };
  let flag = false;

  await axios
    .patch(URL, data, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      flag = res.data.flag;
    })
    .catch(async (e) => {
      console.log("API mypageUpdatePW error");
      console.error(e);
      let result = await patchRefresh(URL, axios.patch, data);
      if (result.flag) {
        flag = result.data.flag;
      } else {
        removeCookieAll();
        window.location.replace("/");
      }
    });
  return flag;
};

// 알림
export const alarmSwitch = async (data: number) => {
  if (!expiration()) return;
  let URL = JAVA + "main/alarm/switch";
  await axios
    .patch(
      URL,
      { alarm: data },
      {
        headers: {
          Authorization: getCookie("access_token"),
        },
      }
    )
    .then((res) => {
      let dto = getCookie("dto");
      switch (dto.alarm) {
        case 1:
          dto.alarm = 0;
          break;
        case 0:
          dto.alarm = 1;
          break;
      }
      setCookie("dto", dto);
    })
    .catch(async (e) => {
      console.log("API alarmSwitch Error");
      console.error(e);
      let result = await patchRefresh(URL, axios.patch, { alarm: data });
      if (result.flag) {
        console.log("알림 변경 " + data);
      }
    });
};

export const setSubscribe = (func?: any) => {
  let URL = JAVA + "sub";
  const Event = EventSourcePolyfill || NativeEventSource;
  let SSE = new Event(URL, {
    headers: {
      Authorization: getCookie("access_token"),
    },
    heartbeatTimeout: 1000 * 60 * 60 * 24 * 7,
    withCredentials: true,
  });

  SSE.onopen = function (ev) {};
  SSE.onerror = function (ev) {
    console.log("SSE Error");
    console.error(ev);
    console.log("SSE 종료");
    SSE.close();
  };
  SSE.addEventListener("notification", func);

  return SSE;
};

export const Logout = async () => {
  if (!expiration()) return;
  let URL = JAVA + "logout";
  await axios
    .patch(
      URL,
      {},
      {
        headers: {
          Authorization: getCookie("access_token"),
        },
      }
    )
    .catch(async (e) => {
      console.log("API Logout Error");
      let result = await patchRefresh(URL, axios.patch);
      if (result.flag) {
        alert("로그아웃 " + result.data);
      } else {
        alert(e);
        window.location.replace("/");
      }
    });
};

export const deleteAlarm = async (
  data: { time: string; news_id: string },
  SetAlarms: any,
  SetCount: any
) => {
  let URL = process.env.REACT_APP_JAVA + "main/alarm/remove";
  if (!expiration()) return;
  await axios
    .delete(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
      data: data,
    })
    .then((res) => {
      alert("삭제 성공");
      getAlarms(SetAlarms, SetCount);
    })
    .catch(async (e) => {
      console.log("API deleteAlarm Error");
      console.error(e);
      let result = await deleteRefresh(URL, axios.delete, data);
      if (result.flag) {
        alert("삭제 성공");
        getAlarms(SetAlarms, SetCount);
      } else {
        window.location.replace("/");
      }
    });
};

export const selectAlarm = async (data: any, SetAlarms: any, SetCount: any) => {
  let URL = JAVA + "main/alarm/select";
  if (!expiration()) return;
  console.log(data);
  await axios
    .patch(URL, data, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      console.log(res);
      getAlarms(SetAlarms, SetCount);
    })
    .catch(async (e) => {
      console.log("API selectAlarm Error");
      console.error(e);
      // await patchRefresh(URL, axios.patch, data);
      window.location.replace("/");
    });
};

export const deleteAlarmAll = async (SetAlarms: any, SetCount: any) => {
  if (!expiration()) return;
  let URL = process.env.REACT_APP_JAVA + "main/alarm/drop";
  await axios
    .delete(URL, {
      headers: { Authorization: getCookie("access_token") },
    })
    .then((res) => {
      SetAlarms([]);
      SetCount(false);
    })
    .catch(async (e) => {
      let result = await deleteRefresh(URL, axios.delete);
      if (result.flag) {
        SetAlarms([]);
        SetCount(false);
      } else {
        window.location.replace("/");
        removeCookieAll();
        expiration();
      }
    });
};

export const getAlarms = async (SetAlarms: any, SetCount: any) => {
  if (!expiration()) return;
  let URL = process.env.REACT_APP_JAVA + "main/alarm";
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      console.log(res.data);
      SetAlarms(setTime(res.data.alarm));
      SetCount(
        res.data.alarm.filter((value: any) => {
          return value.status === 1;
        }).length < res.data.alarm.length
      );
    })
    .catch(async (e) => {
      console.log("API getAlarms Error");
      console.error(e);
      let res = await postRefresh(URL, axios.get);
      if (res.flag) {
        SetAlarms(setTime(res.data.alarm));
        SetCount(
          res.data.alarm.filter((value: any) => {
            return value.status === 1;
          }).length < res.data.alarm.length
        );
      } else {
        window.location.replace("/");
        removeCookieAll();
      }
    });
};

export const getCount = async (SetCount: any) => {
  if (!expiration()) return;
  let URL = process.env.REACT_APP_JAVA + "main/alarm";
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      let LEN = res.data.alarm.filter((value: any) => {
        return value.status === 0;
      }).length;
      SetCount(LEN > 0);
    })
    .catch(async (e) => {
      console.log("API getCount Error");
      console.error(e);
      let res = await postRefresh(URL, axios.get);
      if (res.flag) {
        SetCount(
          res.data.alarm.filter((value: any) => {
            return value.status === 1;
          }).length < res.data.alarm.length
        );
      } else {
        window.location.replace("/");
        removeCookieAll();
      }
    });
};
// 알림 끝

export const getKeywords = async (SetWords: any, date: any) => {
  let URL = process.env.REACT_APP_PYTHON + "main/keyword/" + date;
  await axios
    .get(URL, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      // SetWords([]);
      SetWords(res.data.wordlist);
    })
    .catch((e) => {
      console.log("API getKeywords 에러");
      console.error(e);
    });
};

export const mainReport = async (data: any, close: any) => {
  let URL = JAVA + "main/report";
  await axios
    .post(URL, data, {
      headers: {
        Authorization: getCookie("access_token"),
      },
    })
    .then((res) => {
      console.log(res.data);
      if (res.data.report) {
        alert("신고완료");
      } else {
        alert("한 번만 신고할 수 있습니다.");
      }
      close();
    })
    .catch(async (e) => {
      console.log("API mainReport Error");
      console.error(e);
      let res = await postRefresh(URL, axios.post, data);
      if (res.flag) {
        alert("신고완료");
        close();
      }
    });
};

export function CUT(num: number) {
  if (num >= 1000 * 1000 * 1000)
    return Math.floor(num / (1000 * 1000 * 1000)) + "B";
  if (num >= 1000 * 1000) return Math.floor(num / (1000 * 1000)) + "M";
  if (num >= 1000) return Math.floor(num / 1000) + "K";
  return num + "";
}

export function getDate(str: string, flag?: boolean) {
  if (flag)
    return `${str.substring(0, 4)}년 ${str.substring(4, 6)}월 ${str.substring(
      6,
      8
    )}일`;
  return `${str.substring(0, 4)}.${str.substring(4, 6)}.${str.substring(6, 8)}`;
}
