import { useEffect, useState } from "react";
import "../App.css";
import { useLocation, useNavigate } from "react-router-dom";
import { updatePassword } from "../API";

function NewPasswd() {
  const navigate = useNavigate();
  const location = useLocation();
  let [Check, SetCheck] = useState("");
  let [UserInfo, SetUserInfo] = useState({
    id: "",
    pw: "",
  });
  const [Err, SetErr] = useState("");
  const [Status, SetStatus] = useState(false);
  const [lodding, loddingSet] = useState(false);
  useEffect(() => {
    SetCheck("");
    SetUserInfo(location.state.UserInfo);
  }, []);

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
            type="password"
            className="login-page-input"
            placeholder="새 비밀번호"
            value={UserInfo.pw}
            onChange={pwHandler}
          ></input>

          <input
            onChange={checkHandler}
            className="login-page-input"
            placeholder="새 비밀번호 확인"
            value={Check}
            type="password"
          ></input>
          <div className="err-msg">{Err}</div>

          <button
            type="button"
            className="login-page-btn login-btn"
            onClick={() => {
              if (!Status) {
                alert("다시 확인해주세요");
              }else {
                updatePassword(UserInfo, SetErr, loddingSet, navigate);

              }
            }}
          >
            비밀번호 변경
          </button>
        </form>
        {/* login-page-a-div end */}
      </div>
      {/* login-center end */}
    </div>
  );

  function pwHandler(text: { target: { value: string } }) {
    let copy = { ...UserInfo };
    copy.pw = text.target.value;
    // 비밀번호 체크
    if (copy.pw.length < 4 || copy.pw.length > 30) {
      SetErr("4자리 이상 30자리 이하로 입력해주세요");
      SetStatus(false);
    } else if (copy.pw !== Check) {
      SetErr("비밀번호가 일치하지 않습니다.");
      SetStatus(false);
    } else {
      SetErr("");
      SetStatus(true);
    }
    SetCheck("");
    SetUserInfo(copy);
  }
  function checkHandler(text: { target: { value: string } }) {
    let txt = text.target.value;
    // 비밀번호 체크
    if (UserInfo.pw.length < 4 || UserInfo.pw.length > 30) {
      SetErr("4자리 이상 30자리 이하로 입력해주세요");
      SetStatus(false);
    } else if (UserInfo.pw !== txt) {
      SetErr("새 비밀번호와 일치하지 않습니다.");
      SetStatus(false);
    } else {
      SetErr("");
      SetStatus(true);
    }
    SetCheck(txt);
  }
}

export default NewPasswd;
