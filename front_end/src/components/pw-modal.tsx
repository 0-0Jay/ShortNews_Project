import React, { useEffect, useState } from "react";
import Modal from "react-modal";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { mypageUpdatePW } from "../API";
// 모달에 사용될 타입 선언
interface ModalProps {
  isOpen: boolean;
  onRequestClose: () => void;
  UserInfo: any;
}

const PwModal: React.FC<ModalProps> = ({
  isOpen,
  onRequestClose,
  UserInfo,
}) => {
  const [Err, setErr] = useState(["", ""]);
  const [Status, setStatus] = useState(false);
  const [Before, SetBefore] = useState("");
  const [PW, setPW] = useState("");
  const [CK, setCK] = useState("");
  const BeforeHandler = (e: any) => {
    let txt = e.target.value;
    SetBefore(txt);
  };

  const PWHandler = (e: any) => {
    let txt = e.target.value;
    setPW(txt);
    if (CK !== "") {
      setCK("");
    }
  };

  const CKHandler = (e: any) => {
    let txt = e.target.value;
    let err = [...Err];
    setCK(txt);
    if (txt !== PW) {
      err[1] = "새 비밀번호와 일치하지 않습니다.";
    } else {
      err[1] = "";
    }
    setErr(err);
  };

  const updatePW = async () => {
    let err = ["", ""];
    if (Before.length === 0) err[0] = "비밀번호를 입력해주세요";
    else if (PW.length === 0) err[1] = "새 비밀번호를 입력해주세요";
    else if (Before.length < 4 || Before.length > 30) err[0] = "4자리 이상 30자리 이하로 입력해주세요";
    else if (PW.length < 4 || PW.length > 30) err[1] = "4자리 이상 30자리 이하로 입력해주세요";
    else if (PW != CK) err[1] = "새 비밀번호와 일치하지 않습니다.";
    else {
      let flag = await mypageUpdatePW(Before, PW);
      if (flag) {
        alert("비밀번호 변경 성공");
        onRequestClose();
      } else err[0] = "기존 비밀번호가 일치하지 않습니다.";
    }
    setErr(err);
  };
  const customStyles = {
    content: {
      backgroundColor: "#F5FBFF",
      width: "500px",
      height: "350px",
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
      height: "35%",
      top: "50%",
      left: "50%",
      right: "auto",
      bottom: "auto",
      marginRight: "-50%",
      transform: "translate(-50%, -50%)",
      overflow: "hidden",
      padding: "40px 0",
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
              type="password"
              placeholder="기존 비밀번호"
              className="mypage-btn pw-modal-input"
              value={Before}
              onChange={BeforeHandler}
            ></input>
          </tr>
          {Err[0] !== "" && (
            <tr>
              <span className="pw-modal-err">{Err[0]}</span>
            </tr>
          )}
          <tr>
            <input
              type="password"
              placeholder="새 비밀번호"
              className="mypage-btn pw-modal-input"
              value={PW}
              onChange={PWHandler}
            ></input>
          </tr>
          <tr>
            <input
              type="password"
              placeholder="새 비밀번호 확인"
              className="mypage-btn pw-modal-input"
              value={CK}
              onChange={CKHandler}
            ></input>
          </tr>
          {Err[1] !== "" && (
            <tr>
              <span className="pw-modal-err">{Err[1]}</span>
            </tr>
          )}
          <tr className="pw-modal-btn">
            <button type="button" className="mypage-btn" onClick={updatePW}>
              변경
            </button>
          </tr>
        </table>
      </div>
    </Modal>
  );
};

export default PwModal;
