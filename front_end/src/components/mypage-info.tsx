import React, { useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPenToSquare } from "@fortawesome/free-solid-svg-icons";
import { faXmark } from "@fortawesome/free-solid-svg-icons";

function MypageInfo() {
  const [modal, setModal] = useState(false);
  return (
    <div>
      <form>
        <table>
          <tr className="mypage-main-row">
            <td className="mypage-main-row-span">
              <span>닉네임</span>
            </td>
            <td className="mypage-main-row-input">
              <input type="text" value="닉네임" readOnly></input>
            </td>
            <td>
              <button>
                <FontAwesomeIcon
                  icon={faPenToSquare}
                  style={{ color: "#000000" }}
                />
              </button>
            </td>
          </tr>
          <tr className="mypage-main-row">
            <td className="mypage-main-row-span">
              <span>이메일</span>
            </td>
            <td className="mypage-main-row-input">
              <input type="text" value="이메일" readOnly></input>
            </td>
            <td>
              <button>
                <FontAwesomeIcon
                  icon={faPenToSquare}
                  style={{ color: "#000000" }}
                />
              </button>
            </td>
          </tr>
          <tr className="mypage-main-row">
            <td className="mypage-main-row-span">
              <span>전화번호</span>
            </td>
            <td className="mypage-main-row-input">
              <input type="text" value="전화번호" readOnly></input>
            </td>
            <td>
              <button>
                <FontAwesomeIcon
                  icon={faPenToSquare}
                  style={{ color: "#000000" }}
                />
              </button>
            </td>
          </tr>
          <tr className="mypage-main-row" id="mypage-main-row-pw">
            <td className="mypage-main-row-span">
              <span>비밀번호 변경</span>
            </td>
            <td className="mypage-main-row-input">
              <input readOnly id="mypage-main-row-pw"></input>
            </td>
            <td>
              <button onClick={() => setModal(true)}>
                <FontAwesomeIcon
                  icon={faPenToSquare}
                  style={{ color: "#000000" }}
                />
              </button>
            </td>
          </tr>
        </table>
        <button className="mypage-submit-btn">저장</button>
      </form>
      {modal && (
        <div className="modal-container">
          <div className="modal-popup mypage-pwd-box">
            <button className="x-mark">
              <FontAwesomeIcon
                icon={faXmark}
                style={{ color: "#ff0000" }}
                onClick={() => setModal(false)}
              />
            </button>
            <form>
              <table>
                <tr>
                  <input placeholder="기존 비밀번호"></input>
                </tr>
                <tr>
                  <input placeholder="새 비밀번호"></input>
                </tr>
                <tr>
                  <input placeholder="새 비밀번호 확인"></input>
                </tr>
                <tr>
                  <button className="mypage-submit-btn mypage-pwd-submit-btn">
                    비밀번호 변경
                  </button>
                </tr>
              </table>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default MypageInfo;
