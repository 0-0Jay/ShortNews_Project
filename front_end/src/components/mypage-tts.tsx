import React from "react";
import RadioGroup from "./radio-group";

function MypageTTS() {
  const options = ["모델A", "모델B"];
  const audio = "http://www.rskv.co.kr/7_DB/7_k24/비디오.mp4";
  return (
    <div>
      <form>
        <table>
          <tr className="mypage-tts-row">
            <td className="mypage-tts-row-span">
              <span>모델 선택</span>
            </td>
            <td className="mypage-tts-row-btn">
              <RadioGroup
                options={options}
                className="cate-check mypage-tts-model-btn"
              ></RadioGroup>
            </td>
          </tr>
          <tr className="mypage-tts-row">
            <td className="mypage-tts-row-span">미리 듣기</td>
            <td className="mypage-tts-row-btn">
              <audio controls controlsList="nodownload">
                <source src={audio}></source>
              </audio>
            </td>
          </tr>
        </table>
        <button className="mypage-submit-btn">저장</button>
      </form>
    </div>
  );
}

export default MypageTTS;
