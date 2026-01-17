import { useNavigate } from "react-router-dom";
import google from "../assets/google_logo.svg";
import naver from "../assets/naver_logo.svg";
import kakao from "../assets/kakao_logo.svg";

function MypageAccount() {
  const navigate = useNavigate();
  return (
    <div>
      <table className="mypage-account-table">
        <tr className="mypage-account-row">
          <td className="mypage-account-row-span">
            <img src={naver} alt=""></img>
            <span>네이버 계정</span>
          </td>
          <td className="mypage-account-row-btn">
            <div className="mypage-account-btn mypage-account-btn-connect">
              연동
            </div>
          </td>
        </tr>
        <tr className="mypage-account-row">
          <td className="mypage-account-row-span">
            <img src={google} alt=""></img>
            <span>구글 계정</span>
          </td>
          <td className="mypage-account-row-btn">
            <div className="mypage-account-btn">연동</div>
          </td>
        </tr>
        <tr className="mypage-account-row">
          <td className="mypage-account-row-span">
            <img src={kakao} alt=""></img>
            <span>카카오 계정</span>
          </td>
          <td className="mypage-account-row-btn">
            <div className="mypage-account-btn">연동</div>
          </td>
        </tr>
      </table>
      <button
        className="mypage-submit-btn"
        id="withdraw"
        type="button"
        onClick={() => {
          navigate("/WithDraw");
        }}
      >
        회원 탈퇴
      </button>
    </div>
  );
}

export default MypageAccount;
