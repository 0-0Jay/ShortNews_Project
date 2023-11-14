import React, {useState} from "react";
import '../css/LoginForm.css';
import logo from '../assets/logo.svg';
function LoginForm() {
  return (
    <div className="App">
        {/* 앱 로고*/}
        <div className = "logo">
            <img src={logo}/>
        </div>

        {/* 인풋창*/}
        <div className="userInfo">
            <input type="text" placeholder="이메일 or 전화번호" />
        </div>

        {/* 로그인*/}
        <div className="submit">
            <input type="submit" />
        </div>
    </div>
  );
}


export default LoginForm;
