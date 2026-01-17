import React, { useEffect } from "react";

const NaverLogin = () => {
  const KEY = process.env.REACT_APP_NAVER;
  // const redirectUrl = "http://localhost:3000/login/oauth2/code/naver";
  const redirectUrl = "http://www.shortnews.kr/login/oauth2/code/naver";
  const naverAuthUrl = `https://nid.naver.com/oauth2.0/authorize?client_id=${KEY}&redirect_uri=${redirectUrl}&response_type=code`;

  const loginHandler = () => {
    window.location.href = naverAuthUrl;
  };
  return (
    <button onClick={loginHandler}>
      <img src={process.env.REACT_APP_S3_SHORT + "assets/naverLogin.svg"}></img>
    </button>
  );
};

export default NaverLogin;
