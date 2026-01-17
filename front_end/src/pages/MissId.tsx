import React, { useState } from "react";
import "../App.css";
import { useNavigate } from "react-router-dom";
import Spin from "../components/lodding";
import { findId } from "../API";
import Auth from "./Auth";

function MissId(props: any) {
  const navi = useNavigate();

  let [UserInfo, setUserInfo] = useState({
    id: "",
    phone : "",
    code: "",
  });

  const [Err, SetErr] = useState("");
  const [Status, SetStatus] = useState(false);
  const [EmailAuth, SetEmailAuth] = useState(false);
  let [code, SetCode] = useState("");
  const [lodding, loddingSet] = useState(false);

  const phoneCheck = /^[0-9]+$/
  function emailHandler(text: { target: { value: string } }) {
    let copy = { ...UserInfo };
    copy.phone = text.target.value;
    let txt = text.target.value;
    if (txt === "" || phoneCheck.test(txt)){
      copy.phone = text.target.value;
      setUserInfo(copy);
      SetErr("");
    }else {
      SetErr("숫자만 입력 가능합니다.");
      SetStatus(false);
    }

    if (Status) {
      SetStatus(false);
      SetErr("");
    }
  }
  function check(
    count: number,
    auth: string,
    timerId: any,
    SetEmailShake: any,
    emailShake: boolean
  ) {
    if (count > 0 && code === auth) {
      SetErr("인증완료");
      SetEmailAuth(true);
      clearTimeout(timerId);
    } else {
      SetErr("");
      SetEmailAuth(false);
      SetEmailShake(!emailShake);
    }
  }

  const refresh = () => {
    findId(
      setUserInfo,
      SetErr,
      SetStatus,
      SetCode,
      UserInfo,
      loddingSet
    );
  };
  const goFind = () => {
    if (!(EmailAuth && Status)) SetErr("인증 후 진행 가능합니다.");
    else navi("/FindId", { state: { UserInfo: UserInfo } });
  };
  return (
    <div>
      <div className="login-container">
        <div className="misspw-form">
          <a href="/">
            <img
              src={process.env.REACT_APP_S3_SHORT + "assets/logo.svg"}
              id="login-page-logo"
            ></img>
          </a>
          <div>
            <p className="top-msg">아이디를 잊어버리셨나요?</p>
          </div>
          <input
            value={UserInfo.phone}
            className="signup-page-input signup-page-btn-input"
            type="text"
            placeholder="전화번호"
            onChange={emailHandler}
            maxLength={11}
            onKeyDown={(e) =>
              e.key === "Enter"
                ? findId(
                    setUserInfo,
                    SetErr,
                    SetStatus,
                    SetCode,
                    UserInfo,
                    loddingSet
                  )
                : null
            }
          ></input>
          {!Status && (
            <button
              type="button"
              className="btn-on-input misspw-page-btn-input"
              onClick={() =>
                findId(
                  setUserInfo,
                  SetErr,
                  SetStatus,
                  SetCode,
                  UserInfo,
                  loddingSet
                )
              }
            >
              인증
            </button>
          )}
          {lodding ? <Spin></Spin> : null}
          <div>
            {Status ? (
              <Auth
                code={code}
                Err={Err}
                SetErr={SetErr}
                refresh={refresh}
                EmailAuth={EmailAuth}
                SetEmailAuth={SetEmailAuth}
                check={check}
              />
            ) : null}
            <div className="err-msg">{Err}</div>
          </div>

          <button
            onClick={goFind}
            type="button"
            className="login-page-btn login-btn"
          >
            아이디 찾기
          </button>
        </div>
        {/* login-page-a-div end */}
      </div>
      {/* login-center end */}
    </div>
  );
}

export default MissId;
