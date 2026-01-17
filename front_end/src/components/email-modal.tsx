import React, { useEffect, useState } from "react";
import Modal from "react-modal";
import { finalUpdateEmail, findId, updateEmail } from "../API";
import Spin from "../components/lodding";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import Auth from "../pages/Auth";
// 모달에 사용될 타입 선언
interface ModalProps {
  isOpen: boolean;
  onRequestClose: () => void;
  UserInfo: any;
}

const EmailModal: React.FC<ModalProps> = ({
  isOpen,
  onRequestClose,
  UserInfo,
}) => {
  const [Err, SetErr] = useState("");
  const [Status, SetStatus] = useState(false);
  const [EmailAuth, SetEmailAuth] = useState(false);
  let [code, SetCode] = useState("");
  const [lodding, loddingSet] = useState(false);
  let [email, SetEmail] = useState("");
  let [data, SetData] = useState({
    id: "",
    email: email,
    code: "",
  });

  useEffect(() => {
    SetData({
      id: UserInfo.id,
      email: email,
      code: "",
    });
  }, [UserInfo]);

  const phoneCheck = /^[0-9]+$/;
  function emailHandler(text: { target: { value: string } }) {
    let copy = { ...UserInfo };
    let txt = text.target.value;
    console.log(txt);
    if (txt === "" || phoneCheck.test(txt)) {
      copy.phone = text.target.value;
      SetData(copy);
      SetEmail(copy.phone);
      SetErr("");
    } else {
      SetErr("숫자만 입력 가능합니다.");
      SetStatus(false);
    }
    if (Status) {
      SetStatus(false);
      SetErr("");
    }
  }

  const refresh = () => {
    findId(SetData, SetErr, SetStatus, SetCode, UserInfo, loddingSet);
  };

  const customStyles = {
    content: {
      backgroundColor: "#F5FBFF",
      width: "400px",
      height: "350px",
      top: "50%",
      left: "50%",
      right: "auto",
      bottom: "auto",
      marginRight: "-50%",
      transform: "translate(-50%, -50%)",
      overflow: "hidden",
      padding: "40px 0",
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
    return () => {
      SetStatus(false);
      
      window.removeEventListener("resize", handleResize)
    };
  }, []);

  function check(
    count: number,
    auth: string,
    timerId: any,
    SetEmailShake: any,
    emailShake: boolean
  ) {
    if (count > 0 && code === auth) {
      SetErr("인증완료");
      SetEmailAuth(true);
      clearTimeout(timerId);
    } else {
      SetErr("");
      SetEmailAuth(false);
      SetEmailShake(!emailShake);
    }
  }
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
      <div className="email-modal-form">
        <table>
          <tr>
            <div className="signup-page-row">
              <input
                className="signup-page-input signup-page-btn-input pw-modal-input"
                value={email}
                type="text"
                placeholder="변경할 전화번호"
                maxLength={11}
                onChange={emailHandler}
                onKeyDown={(e) => {
                  if (e.code === "Enter" || e.code === "NumpadEnter") {
                    updateEmail(SetErr, SetStatus, SetCode, email, loddingSet);
                  }
                }}
              ></input>
              {lodding ||
                (!Status && (
                  <button
                    type="button"
                    className="btn-on-input pw-modal-btn mypage-email-modal-btn"
                    onClick={() => {
                      updateEmail(
                        SetErr,
                        SetStatus,
                        SetCode,
                        email,
                        loddingSet
                      );
                    }}
                  >
                    전화번호 인증
                  </button>
                ))}
            </div>
          </tr>
          <tr>
            <div className="pw-modal-err">{!EmailAuth && Err}</div>
          </tr>
          <tr>
            <div className="signup-form">
              {Status && (
                <Auth
                  code={code}
                  Err={Err}
                  SetErr={SetErr}
                  refresh={refresh}
                  EmailAuth={EmailAuth}
                  SetEmailAuth={SetEmailAuth}
                  check={check}
                />
              )}
              <div className="pw-modal-err">{Err}</div>
            </div>
          </tr>
          <tr>
            <td>
              {EmailAuth && (
                <button
                  type="button"
                  className="cate-btn skip-btn mypage-submit-btn mypage-email-submit-btn"
                  onClick={() => finalUpdateEmail(email)}
                >
                  저장
                </button>
              )}
            </td>
          </tr>
        </table>
      </div>
    </Modal>
  );
};

export default EmailModal;
