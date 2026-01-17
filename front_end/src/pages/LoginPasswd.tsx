import  { useEffect, useState } from "react";
import "../App.css";
import { useNavigate, Link, useLocation } from "react-router-dom";
import axios from "axios";
import { setCookieAll } from "../Cookies";
import { JAVA, setSubscribe } from "../API";
function LoginPasswd(props: any) {
  const navigate = useNavigate();
  const location = useLocation();
  let [UserInfo, setUserInfo] = useState({
    id: "",
    pw: "",
  });
  useEffect(() => {
    setUserInfo(location.state.UserInfo);
  }, []);
  function PWHandle(text: any) {
    let t = text.target.value;
    let copy = { ...UserInfo };
    copy.pw = t;
    setUserInfo(copy);
  }
  const [Err, SetErr] = useState("");
  const [Status, SetStatus] = useState(false);

  //로그인(패스워드 체크) 버튼
  async function password() {
    let copy = Err;
    let status = Status;
    let data = {
      id: UserInfo.id,
      pw: UserInfo.pw,
    };
    if (UserInfo.pw === "") {
      copy = "비밀번호를 입력해주세요.";
      SetErr(copy);
    } else if (UserInfo.pw !== '123' && (UserInfo.pw.length > 30 || UserInfo.pw.length < 4)) {
      copy = "4자리 이상 30자리 이하로 입력해주세요";
      SetErr(copy);
    } else {
      await axios
        .post("http://api.shortnews.kr:8090/login/password", data)
        // .post(JAVA + "login/password", data)
        .then((res) => {
          let status = res.data.status;
          if (status === "OK") {
            status = true;
            setCookieAll(res.data);
            navigate("/main/recommend", {replace : true});
          } else {
            copy = "틀린 비밀번호";
            status = false;
          }
        })
        .catch((e) => {
          console.log("password, func= password axios error");
          console.error(e);
          copy = "재시도해주세요";
        })
        .finally(() => {
          SetErr(copy);
          SetStatus(status);
        });
    }
  }

  return (
    <div className="login-container">
      <div>
        <div className="logo-div">
          <img
            src={process.env.REACT_APP_S3_SHORT + "assets/logo.svg"}
            id="login-page-logo"
          ></img>
        </div>
        <form action="#">
          <input
            className="login-page-input"
            value={UserInfo.id}
            readOnly
            disabled
          ></input>
          <input
            className="login-page-input"
            placeholder="비밀번호"
            type="password"
            autoComplete="off"
            onChange={PWHandle}
            autoFocus
            onKeyDown={(e) => {
              if (e.code === "Enter" || e.code === "NumpadEnter"){
                password();
              }
            }}
          ></input>
          {Err}
          <button
            onClick={password}
            type="button"
            className="login-page-btn login-btn"
          >
            로그인
          </button>
        </form>
        <div className="login-page-a-div">
          <Link to="/" className="login-page-a">
            다시입력하기
          </Link>
        </div>
        <div className="login-page-a-div">
          <Link to="/MissId" className="login-page-a" id="forgot-passwd">
            아이디 찾기
          </Link>
          <span>&nbsp; | &nbsp;</span>
          <Link to="/MissPasswd" className="login-page-a" id="forgot-passwd">
            비밀번호 찾기
          </Link>
        </div>
        {/* login-page-a-div end */}
      </div>
      {/* login-center end */}
    </div>
  );
}

export default LoginPasswd;
