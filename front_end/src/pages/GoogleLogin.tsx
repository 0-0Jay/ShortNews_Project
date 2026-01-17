import { google } from "../API";
import { useNavigate } from "react-router-dom";

const GoogleLogin = () => {
  const navi = useNavigate();
  const loginHandler = async () => {
    window.location.href = await google(navi) as string;
  };
  return (
    <button onClick={loginHandler}>
      <img src={process.env.REACT_APP_S3_SHORT + "assets/googleLogin.svg"}></img>
    </button>
  );
};

export default GoogleLogin;
