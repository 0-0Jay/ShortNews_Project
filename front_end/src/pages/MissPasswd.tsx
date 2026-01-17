import { useState } from "react";
import "../App.css";
import { useNavigate } from "react-router-dom";
import Spin from "../components/lodding";
import { JAVA, findPassword } from "../API";
import Auth from "./Auth";

function MissPasswd(props: any) {
  const navigate = useNavigate();

  let [UserInfo, setUserInfo] = useState({
    id: "",
    phone: "",
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
    if (Status) {
      SetStatus(false);
      SetErr("");
    }
  }
  function idHandler(text: { target: { value: string } }) {
    let copy = { ...UserInfo };
    copy.id = text.target.value;
    setUserInfo(copy);
  }
  const goNewPW = () => {
    console.log(EmailAuth);
    console.log(Status);
    if (!(EmailAuth && Status)) SetErr("인증해주세요");
    else {
      SetErr("");
      navigate("/NewPasswd", {
        state: { UserInfo: { id: UserInfo.id, pw: "" } },
      });
    }
  };

  const refresh = () => {
    findPassword(UserInfo, SetErr, SetStatus, loddingSet, SetCode);
  };
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
            <p className="top-msg">비밀번호를 잊어버리셨나요?</p>
          </div>
          <input
            className="login-page-input"
            placeholder="아이디"
            onChange={idHandler}
          ></input>
          <input
            value={UserInfo.phone}
            className="signup-page-input signup-page-btn-input"
            type="text"
            placeholder="전화번호"
            maxLength={11}
            onKeyDown={ e => {
              if (e.key === "Enter" || e.key === "NumpadEnter") refresh();
            }}
            onChange={emailHandler}
          ></input>
          {!Status && (
            <button
              type="button"
              className="btn-on-input misspw-page-btn-input"
              
              onClick={refresh}
            >
              인증
            </button>
          )}
          {lodding ? <Spin></Spin> : null}
          <div>
            {Status ? (
              <Auth
                code={code}
                SetCode={SetCode}
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
            onClick={goNewPW}
            type="button"
            className="login-page-btn login-btn"
          >
            비밀번호 찾기
          </button>
        </div>
        {/* login-page-a-div end */}
      </div>
      {/* login-center end */}
    </div>
  );
}

export default MissPasswd;
