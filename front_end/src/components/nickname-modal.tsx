import React, { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { setCookie } from "../Cookies";
import Modal from "react-modal";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { updateNickname } from "../API";
// 모달에 사용될 타입 선언
interface ModalProps {
  isOpen: boolean;
  onRequestClose: () => void;
  UserInfo: any;
}

const NicknameModal: React.FC<ModalProps> = ({ isOpen, onRequestClose }) => {
  let [data, SetData] = useState({ nickname: "" });
  const re = /^[A-Za-z0-9가-힇\s]+$/;
  const [Err, setErr] = useState("");

  const customStyles = {
    content: {
      backgroundColor: "#F5FBFF",
      width: "500px",
      height: "200px",
      top: "50%",
      left: "50%",
      right: "auto",
      bottom: "auto",
      marginRight: "-50%",
      transform: "translate(-50%, -50%)",
      overflow: "hidden",
      padding: "30px 0",
    },
  };

  const customMobileStyles = {
    content: {
      backgroundColor: "#F5FBFF",
      width: "90%",
      height: "20%",
      top: "50%",
      left: "50%",
      right: "auto",
      bottom: "auto",
      marginRight: "-50%",
      transform: "translate(-50%, -50%)",
      overflow: "hidden",
      padding: "30px 0",
      zIndex: "2000",
    },
  };

  const [isMobile, setIsMobile] = useState(false);
  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth < 600);
    };
    // 초기 확인
    handleResize();
    // 창 크기 변경을 위한 이벤트 리스너
    window.addEventListener("resize", handleResize);
    // 컴포넌트가 언마운트될 때 이벤트 리스너를 제거
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const nickHandle = (e: any) => {
    SetData({ nickname: e.target.value });
  };
  const update = () => {
    data.nickname = data.nickname.trim();
    if (data.nickname === "") {
      setErr("닉네임을 입력해주세요");
    }
    if (!re.test(data.nickname)) {
      setErr("영어, 한글, 숫자를 제외한 문자는 사용할 수 없습니다.");
    } else {
      updateNickname(data, setErr);
    }
  };
  return (
    <Modal
      isOpen={isOpen}
      onRequestClose={onRequestClose}
      style={isMobile ? customMobileStyles : customStyles}
    >
      <button className="x-mark">
        <FontAwesomeIcon
          icon={faXmark}
          style={{ color: "#B91313" }}
          onClick={onRequestClose}
        />
      </button>

      <div className="pw-modal-form">
        <table className="pw-modal-table">
          <tr>
            <input
              type="text"
              placeholder="변경할 닉네임을 입력하세요."
              className="mypage-btn pw-modal-input"
              onChange={nickHandle}
              onKeyDown={(e) => {
                if (e.code === "Enter" || e.code === "NumpadEnter") {
                  update();
                }
              }}
            ></input>
          </tr>
          {Err != "" && (
            <tr>
              <span className="pw-modal-err">{Err}</span>
            </tr>
          )}
          <tr className="pw-modal-btn">
            <button type="button" className="mypage-btn" onClick={update}>
              변경
            </button>
          </tr>
        </table>
      </div>
    </Modal>
  );
};

export default NicknameModal;
