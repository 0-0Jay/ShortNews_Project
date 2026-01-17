import React, { useEffect } from "react";

const KakaoLogin = () => {
  const KEY = process.env.REACT_APP_KAKAO;
  // const redirectUrl = "http://localhost:3000/login/oauth2/code/kakao";
  const redirectUrl = "http://www.shortnews.kr/login/oauth2/code/kakao";
  const kakaoAuthUrl = `https://kauth.kakao.com/oauth/authorize?client_id=${KEY}&redirect_uri=${redirectUrl}&response_type=code`;

  const loginHandler = () => {
    window.location.href = kakaoAuthUrl;
  };
  return (
    <button onClick={loginHandler}>
      <img src={process.env.REACT_APP_S3_SHORT + "assets/kakaologin.svg"}></img>
    </button>
  );
};

export default KakaoLogin;
