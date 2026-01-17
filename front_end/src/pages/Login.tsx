import React, { useEffect, useState } from "react";
import { removeCookieAll, setCookie } from "../Cookies";
import "../App.css";
import { useNavigate, Link } from "react-router-dom";
import axios from "axios";
import KakaoLogin from "./kakaoLogin";
import Spin from "../components/lodding";
import NaverLogin from "./NaverLogin";
import GoogleLogin from "./GoogleLogin";
import { JAVA } from "../API";
function Login(props: any) {
  useEffect(()=>removeCookieAll,[])
  const navigate = useNavigate();
  let [UserInfo, setUserInfo] = useState({
    id: "",
    pw: "",
  });

  const [Err, SetErr] = useState("");
  const [Status, SetStatus] = useState(false);
  const [spin, SetSpin] = useState(false);

  // 입력창 핸들러
  function idHandler(text: { target: { value: string } }) {
    let copy = { ...UserInfo };
    copy.id = text.target.value;
    setUserInfo(copy);
  }

  //로그인(아이디 체크) 버튼
  async function idCheck() {
    const re = /^[A-Za-z0-9]+$/;
    let copy = Err;
    let status = Status;
    if (UserInfo.id === "") {
      copy = "아이디를 입력해주세요.";
      SetErr(copy);
      return;
    } else if (UserInfo.id.length > 16) {
      copy = "15자 이내로 입력해주세요.";
      SetErr(copy);
      return;
    } else if (!re.test(UserInfo.id)) {
      copy = "영문자와 숫자만 입력해주세요.";
      SetErr(copy);
      return;
    } else {
      SetSpin(true);
      await axios
        .post(JAVA + "login/idCheck", { id: UserInfo.id })
        .then((res) => {
          let status = res.data.status;
          if (status === "OK") {
            status = true;
            navigate("/LoginPasswd", { state: { UserInfo: UserInfo }, replace : true });
          } else {
            copy = "존재하지 않는 아이디";
            SetErr(copy);
            status = false;
          }
        })
        .catch((e) => {
          console.log("Login, func=idCheck axios error");
          console.error(e);
        })
        .finally(() => {
          SetSpin(false);
          SetErr(copy);
          SetStatus(status);
        });
    }
  }

  return (
    <div>
      <div className="login-container">
        <div>
          <div className="logo-div">
            <img
              src={process.env.REACT_APP_S3_SHORT + "logo.svg"}
              id="login-page-logo"
            ></img>
          </div>
          <input
            value={UserInfo.id}
            className="login-page-input"
            placeholder="아이디"
            onChange={idHandler}
            onKeyDown={e => {
              if (e.code === "Enter" || e.code === "NumpadEnter"){
                idCheck();
              }
            }}
          ></input>
          {spin ? <Spin /> : null}
          {Err}
          <button className="login-page-btn login-btn" onClick={idCheck}>
            로그인
          </button>
          <div className="login-page-a-div">
            <Link to="/signup" className="login-page-a">
              회원가입하기
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
          <div className="login-page-or">
            <hr></hr>
            <p>or</p>
            <hr></hr>
          </div>
          {/* login-page-or end */}
          <div className="login-api-btn">
            <KakaoLogin />
            <NaverLogin />
            {/* <GoogleLogin /> */}
          </div>
        </div>
        {/* login-center end */}
      </div>
      <div className="circle-zone">
        <div className="back-circle" id="circle-violet"></div>
        <div className="back-circle" id="circle-pink"></div>
        <div className="back-circle" id="circle-blue"></div>
      </div>
    </div>
  );
}

export default Login;
