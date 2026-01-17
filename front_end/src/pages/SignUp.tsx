import React, { useEffect, useState } from "react";
import "../App.css";
import { Link, useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import Spin from "../components/lodding";
import { JAVA, emailCheck } from "../API";
import SignUpAuth from "./SignUpAuth";
import CateModal from "../components/cate-modal";
const ID = 0,
  PASSWD = 1,
  EMAIL = 2,
  EmailAUTH = 3,
  FINAL = 4;
function SignUp(props: any) {
  const re = /^[A-Z|a-z|0-9|가-힇|\s]+$/;
  let [UserInfo, setUserInfo] = useState({
    id: "",
    phone: "",
    nickname: "",
    pw: "",
    cate: [false, false, false, false, false, false, false, false],
    platform: "I",
  });
  let [Check, SetCheck] = useState("");

  const [Err, SetErr] = useState(["", "", "", "", ""]);
  const [Status, SetStatus] = useState([false, false, false, false]);
  const [lodding, loddingSet] = useState(false);
  let [code, SetCode] = useState("");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const openModal = () => {
    setIsModalOpen(true);
  };
  const closeModal = () => {
    setIsModalOpen(false);
  };

  // 입력창 핸들러
  function idHandler(text: { target: { value: string } }) {
    let copy = { ...UserInfo };
    copy.id = text.target.value;
    setUserInfo(copy);
    if (Status[ID]) {
      let err = [...Err];
      let status = [...Status];
      err[ID] = "";
      status[ID] = false;
      SetErr(err);
      SetStatus(status);
    }
  }
  const phoneCheck = /^[0-9]+$/
  function emailHandler(text: { target: { value: string } }) {
    let copy = { ...UserInfo };
    let status = [...Status];
    let err = [...Err];
    let txt = text.target.value;
    if (txt === "" || phoneCheck.test(txt)){
      copy.phone = text.target.value;
      setUserInfo(copy);
      err[EmailAUTH] = "";
    }else {
      err[EmailAUTH] = "숫자만 입력 가능합니다.";
      status[EMAIL] = false;
    }

    if (Status[EMAIL]) {
      status[EMAIL] = false;
      status[EmailAUTH] = false;
      err[EmailAUTH] = "";
    }
    SetStatus(status);
    SetErr(err);
  }

  function nickHandler(text: { target: { value: string } }) {
    let copy = { ...UserInfo };
    copy.nickname = text.target.value;
    setUserInfo(copy);
  }
  function pwHandler(text: { target: { value: string } }) {
    let copy = { ...UserInfo };
    let err = [...Err];
    let status = [...Status];
    copy.pw = text.target.value;
    // 비밀번호 체크
    if (copy.pw.length < 4 || copy.pw.length > 30) {
      err[PASSWD] = "4자리 이상 30자리 이하로 입력해주세요";
      status[PASSWD] = false;
    } else if (copy.pw !== Check) {
      err[PASSWD] = "비밀번호가 일치하지 않습니다.";
      status[PASSWD] = false;
    } else {
      err[PASSWD] = "";
      status[PASSWD] = true;
    }
    SetCheck("");
    SetErr(err);
    setUserInfo(copy);
    SetStatus(status);
  }
  function checkHandler(text: { target: { value: string } }) {
    let txt = text.target.value;
    let copy = [...Err];
    let status = [...Status];
    // 비밀번호 체크
    if (UserInfo.pw.length < 4 || UserInfo.pw.length > 30) {
      copy[PASSWD] = "4자리 이상 30자리 이하로 입력해주세요";
      status[PASSWD] = false;
    } else if (UserInfo.pw !== txt) {
      copy[PASSWD] = "비밀번호가 일치하지 않습니다.";
      status[PASSWD] = false;
    } else {
      copy[PASSWD] = "";
      status[PASSWD] = true;
    }
    SetErr(copy);
    SetCheck(txt);
    SetStatus(status);
  }
  // 핸들러 끝

  //중복확인 버튼
  async function idCheck() {
    let copy = [...Err];
    let status = [...Status];
    const re2 = /^[A-Za-z0-9]+$/;
    if (UserInfo.id === "") {
      copy[ID] = "아이디를 입력해주세요.";
      SetErr(copy);
      return;
    } else if (UserInfo.id.length < 4 || UserInfo.id.length > 30) {
      copy[ID] = "4자리 이상 30자리 이하로 입력해주세요";
      SetErr(copy);
      return;
    } else if (!re2.test(UserInfo.id)) {
      copy[ID] = "영문자 및 숫자만 입력해주세요.";
      SetErr(copy);
      return;
    } else {
      await axios
        .post(JAVA + "signup/idCheck", { id: UserInfo.id })
        .then((res) => {
          let flag: boolean = res.data.flag;

          if (flag) {
            copy[ID] = "사용 가능한 아이디입니다.";
            status[ID] = true;

          } else {
            copy[ID] = "중복된 아이디입니다.";
            status[ID] = false;
          }
          SetErr(copy);
          SetStatus(status);
        })
        .catch((e) => {
          console.log("SignUp, func=idCheck axios error");
          console.error(e);
        });
    }
  }
  const handleEmailCheck = () => {
    let copy = [...Err];
    copy[EMAIL] = "아이디와 비밀번호를 확인해주세요";
    SetErr(copy);
  };

  const refresh = () => {
    emailCheck(
      Err,
      Status,
      UserInfo.phone,
      EMAIL,
      loddingSet,
      SetCode,
      SetErr,
      SetStatus
    );
  };
  return (
    <div>
      <div>
        <div className="login-container">
          <a href="/">
            <img
              src={process.env.REACT_APP_S3_SHORT + "logo.svg"}
              id="login-page-logo"
            ></img>
          </a>
          <form name="signupForm" className="signup-form">
            <div></div>
            <div className="signup-page-row">
              <span className="require">*</span>
              <input
                value={UserInfo.id}
                className="signup-page-input signup-page-btn-input"
                type="text"
                placeholder="아이디"
                name="id"
                onChange={idHandler}
                onKeyDown={(k) => {
                  if (k.key === "Enter") {
                    idCheck();
                  }
                }}
              ></input>
              <button
                type="button"
                className="sign-up-btn-on-input"
                onClick={idCheck}
              >
                중복확인
              </button>
            </div>
            <div className="sign-up-err-msg">{Err[ID]}</div>

            <div className="signup-page-row">
              <span className="require">*</span>
              <input
                value={UserInfo.pw}
                className="signup-page-input"
                placeholder="비밀번호"
                type="password"
                onChange={pwHandler}
              ></input>
            </div>

            <div className="signup-page-row">
              <span className="require">*</span>
              <input
                value={Check}
                className="signup-page-input"
                placeholder="비밀번호 확인"
                type="password"
                onChange={checkHandler}
              ></input>
            </div>
            <div className="sign-up-err-msg">{Err[PASSWD]}</div>

            <div className="signup-page-row">
              <span className="require">*</span>
              <input
                value={UserInfo.phone}
                className="signup-page-input signup-page-btn-input"
                type="text"
                placeholder="전화번호"
                onChange={emailHandler}
                maxLength={11}
              ></input>
              {lodding ||
                (!Status[EMAIL] && (
                  <button
                    type="button"
                    className="sign-up-btn-on-input"
                    onClick={
                      Status[ID]
                        ? Status[PASSWD]
                          ? () =>
                              emailCheck(
                                Err,
                                Status,
                                UserInfo.phone,
                                EMAIL,
                                loddingSet,
                                SetCode,
                                SetErr,
                                SetStatus
                              )
                          : handleEmailCheck
                        : handleEmailCheck
                    }
                  >
                    인증
                  </button>
                ))}
              {lodding ? (
                <div>
                  <Spin></Spin>
                </div>
              ) : null}
            </div>
            <div className="sign-up-err-msg">{Err[EMAIL]}</div>
            <div>
              {Status[EMAIL] ? (
                <SignUpAuth
                  code={code}
                  EmailAUTH={EmailAUTH}
                  Err={Err}
                  SetErr={SetErr}
                  Status={Status}
                  SetStatus={SetStatus}
                  refresh={refresh}
                />
              ) : null}
              <div className="sign-up-err-msg">{Err[EmailAUTH]}</div>
            </div>

            <div className="signup-page-row">
              <span className="require-none">*</span>
              <input
                value={UserInfo.nickname}
                className="signup-page-input"
                type="text"
                placeholder="닉네임(선택사항)"
                onChange={nickHandler}
                maxLength={30}
              ></input>
            </div>
            <div className="sign-up-err-msg">{Err[FINAL]}</div>

            <button
              type="button"
              className="login-page-btn login-btn singup-btn"
              onClick={SUBMIT}
              hidden = {!(Status[ID] && Status[PASSWD] && Status[EMAIL] && Status[EmailAUTH])}
            >
              회원가입
            </button>
            {
              isModalOpen && 
                <CateModal
                isOpen={isModalOpen}
                onRequestClose={closeModal}
                UserInfo={UserInfo}
                shouldCloseOnOverlayClick={false}
                shouldCloseOnEsc={false}
              />
            }
          </form>
          {/* login-page-a-div end */}
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

  // 유효성 검사 코드
  async function SUBMIT() {
    let data = {
      id: UserInfo.id,
      phone: UserInfo.phone,
      nickname: UserInfo.nickname,
      pw: UserInfo.pw,
      cate: [false, false, false, false, false, false, false, false],
    };
    console.log(data)

    // 닉네임 특수기호 검사
    if (data.nickname !== "" && !re.test(data.nickname)) {
      let copy = [...Err];
      copy[3] = "영문자 및 숫자만 입력해주세요.";
      SetErr(copy);
      return;
    } else {
      await axios
        .post(JAVA + "signup/submit", data)
        .then((res) => {
          let flag = res.data.flag;
          if (flag) {
            openModal();
          } else {
            let copy = [...Err];
            copy[FINAL] = "이미 존재하는 닉네임입니다.";
            SetErr(copy);
          }
        })
        .catch((e) => {
          console.log("SignUp SUBMIT ERROR");
          console.error(e);
        });
    }
  }
}

export default SignUp;
