import React, { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import "../App.css";

function FindId(props: any) {
  const location = useLocation();
  let [UserInfo, SetUserInfo] = useState({
    id: "",
    phone: "",
    code: "",
  });
  useEffect(() => {
    SetUserInfo(location.state.UserInfo);
  }, []);
  return (
    <div>
      <div className="login-container">
        <form action="#" className="misspw-form">
          <a href="/">
            <img
              src={process.env.REACT_APP_S3_SHORT + "assets/logo.svg"}
              id="login-page-logo"
            ></img>
          </a>
          <div className="findid-text-container">
            <p className="findid-container">{UserInfo.phone.substring(0, 3)}-{UserInfo.phone.substring(3, 7)}-{UserInfo.phone.substring(7, 11)} 번호로 등록된 아이디는</p>
            <div className="findid-container findid-id">{UserInfo.id}</div>
            <p className="findid-container">입니다.</p>
          </div>
          <div className="login-page-a-div">
            <Link to="/" className="login-page-a" id="forgot-passwd">
              로그인하기
            </Link>
            <span>&nbsp; | &nbsp;</span>
            <Link to="/MissPasswd" className="login-page-a" id="forgot-passwd">
              비밀번호 찾기
            </Link>
          </div>
        </form>
        {/* login-page-a-div end */}
      </div>
      {/* login-center end */}
    </div>
  );
}

export default FindId;
