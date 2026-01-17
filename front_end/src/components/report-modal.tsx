import React, { useEffect, useState } from "react";
import Modal from "react-modal";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { mainReport } from "../API";

// 모달에 사용될 타입 선언
interface ModalProps {
  isOpen: boolean;
  onRequestClose: () => void;
  reply_id?: string;
  news_id?: string;
}

const ReportModal: React.FC<ModalProps> = ({
  isOpen,
  onRequestClose,
  reply_id,
  news_id,
}) => {
  const customStyles = {
    content: {
      backgroundColor: "#F5FBFF",
      width: "70%",
      height: "77%",
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

  const customMobileStyles = {
    content: {
      backgroundColor: "#F5FBFF",
      width: "90%",
      height: "77%",
      top: "50%",
      left: "50%",
      right: "auto",
      bottom: "auto",
      marginRight: "-50%",
      transform: "translate(-50%, -50%)",
      overflow: "hidden",
      padding: "0",
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

  const [selectedReason, setSelectedReason] = useState<string | null>(null);
  const [reason, SetReason] = useState("");
  const handleRadioChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setSelectedReason(event.target.value);
  };
  const handleReason = (e: any) => {
    SetReason(e.target.value);
  };
  const REPORT = () => {
    if (selectedReason === "기타 사유" && reason.trim() === "") {
      alert("사유를 입력해주세요!");
    } else {
      mainReport(
        {
          type: selectedReason,
          constant: reason,
          reply_id: reply_id,
          news_id: news_id,
        },
        onRequestClose
      );
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
      <div className="report-logo">
        <img
          src={process.env.REACT_APP_S3_SHORT + "logo.svg"}
          width={200}
          height={130}
          alt="logo"
        />
      </div>
      <form className="pw-modal-form">
        <table className="pw-modal-table report-modal-table">
          <tr className="center">
            <span className="report-span">신고 사유를 알려주세요!</span>
          </tr>
          <tr>
            <label className="radio-circle-label">
              <input
                type="radio"
                name="report"
                value="허위 사실 유포"
                className="radio-circle"
                onChange={handleRadioChange}
              ></input>
              <span>허위 사실 유포</span>
            </label>
          </tr>
          <tr>
            <label className="radio-circle-label">
              <input
                type="radio"
                name="report"
                value="스팸/도배성 게시글"
                className="radio-circle"
                onChange={handleRadioChange}
              ></input>
              <span>스팸/도배성 게시글</span>
            </label>
          </tr>
          <tr>
            <label className="radio-circle-label">
              <input
                type="radio"
                name="report"
                value="음란성 게시글"
                className="radio-circle"
                onChange={handleRadioChange}
              ></input>
              <span>음란성 게시글</span>
            </label>
          </tr>
          <tr>
            <label className="radio-circle-label">
              <input
                type="radio"
                name="report"
                value="욕설/혐오/차별성 게시글"
                className="radio-circle"
                onChange={handleRadioChange}
              ></input>
              <span>욕설/혐오/차별성 게시글</span>
            </label>
          </tr>
          <tr>
            <label className="radio-circle-label">
              <input
                type="radio"
                name="report"
                value="개인정보 노출"
                className="radio-circle"
                onChange={handleRadioChange}
              ></input>
              <span>개인정보 노출</span>
            </label>
          </tr>
          <tr>
            <label className="radio-circle-label">
              <input
                type="radio"
                name="report"
                value="기타 사유"
                className="radio-circle"
                onChange={handleRadioChange}
              ></input>
              <span>기타 사유</span>
            </label>
          </tr>
          <tr>
            {selectedReason === "기타 사유" && (
              <div className="report-etc">
                <input
                  type="textarea"
                  placeholder="기타 사유를 입력해주세요."
                  onChange={handleReason}
                  value={reason}
                />
              </div>
            )}
          </tr>

          <tr>
            <button type="button" className="report-btn" onClick={REPORT}>
              신고하기
            </button>
          </tr>
        </table>
      </form>
      <span className="center report-span report-span-bottom">
        신고 사유가 허위로 밝혀질 경우 신고자에게 불이익이 있을 수 있습니다.
      </span>
    </Modal>
  );
};

export default ReportModal;
