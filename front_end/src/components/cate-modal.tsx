import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Checkbox from "./checkbox";
import axios from "axios";
import { setCookieAll } from "../Cookies";
import {
  faGlobe,
  faFilm,
  faFutbol,
  faMicrophoneLines,
  faFlaskVial,
  faPeopleRoof,
  faCircleDollarToSlot,
  faLandmark,
} from "@fortawesome/free-solid-svg-icons";
import ReactModal, { Props as ReactModalProps } from "react-modal";
import { JAVA, setSubscribe, uploadS3 } from "../API";
// 모달에 사용될 타입 선언
interface ModalProps extends ReactModalProps {
  isOpen: boolean;
  onRequestClose: () => void;
  UserInfo: any;
}

const CateModal: React.FC<ModalProps> = ({
  isOpen,
  onRequestClose,
  UserInfo,
  ...restProps
}) => {
  const CATES = [
    "정치",
    "경제",
    "사회",
    "생활/문화",
    "세계",
    "IT/과학",
    "연예",
    "스포츠",
  ];
  const ICON = [
    faLandmark,
    faCircleDollarToSlot,
    faPeopleRoof,
    faFilm,
    faGlobe,
    faFlaskVial,
    faMicrophoneLines,
    faFutbol,
  ];
  const navigate = useNavigate();
  let [data, SetData] = useState({
    id: "",
    phone: "",
    nickname: "",
    pw: "",
    cate: [false, false, false, false, false, false, false, false],
    platform: "",
  });
  const basicProfile = process.env.REACT_APP_S3_USER + "default";
  let [img, SetImg] = useState(basicProfile);
  let fileRef = useRef<any>();
  useEffect(() => {
    let copy = { ...data };
    copy.id = UserInfo.id;
    copy.phone = UserInfo.phone;
    copy.cate = UserInfo.cate;
    copy.platform = UserInfo.platform;
    copy.pw = UserInfo.pw;
    copy.nickname = UserInfo.nickname;
    SetData(copy);
  }, [UserInfo]);

  function Change(idx: any) {
    let copy = { ...data };
    copy.cate[idx] = !copy.cate[idx];
    SetData(copy);
  }

  async function setCategory() {
    // data.nickname = data.phone.substring(0, data.phone.indexOf("@")) + data.platform;
    console.log(data);
    await axios
      .post(JAVA + "signup/selectCategory", data)
      .then((res) => {
        if (res.data.status) {
          uploadS3({
            id: data.id,
            file: fileRef.current.files[0],
            value: fileRef.current.value,
          });
          setCookieAll(res.data);
          alert("수정은 마이페이지에서 가능합니다.");
          onRequestClose();
          navigate("/main/recommend", { replace: true });
        } else {
          alert("문제 발생 재시도해주세요");
        }
      })
      .catch((res) => {
        console.log("Cate modal setCategory errer");
        console.error(res);
      });
  }

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

  const customStyles = {
    content: {
      backgroundColor: "#F5FBFF",
      width: "1000px",
      height: "700px",
      top: "50%",
      left: "50%",
      right: "auto",
      bottom: "auto",
      marginRight: "-50%",
      transform: "translate(-50%, -50%)",
      overflow: "hidden",
      padding: "0px",
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

  function imgHandle(e: any) {
    const file = e.target.files[0];
    if (file) {
      const FileType: string = file.type
        .substring(file.type.lastIndexOf("/") + 1)
        .toLowerCase();
      if (!(FileType === "png" || FileType === "jpg" || FileType === "jpeg")) {
        alert("지원하지 않는 파일입니다.");
      } else if (file.size > 5 * 1024 * 1024) {
        alert("용량이 5MB초과된 파일은 업로드 할 수 없습니다.");
      } else {
        const reader = new FileReader();
        reader.onloadend = () => {
          const imageBase64 = reader.result as string;
          // 이미지를 localStorage에 저장하지 않고 state에 직접 저장
          SetImg(imageBase64);
        };
        reader.readAsDataURL(file);
      }
    }
  }

  function basicImg() {
    fileRef.current.value = "";
    SetImg(basicProfile);
  }
  return (
    <ReactModal
      isOpen={isOpen}
      onRequestClose={onRequestClose}
      style={isMobile ? customMobileStyles : customStyles}
      {...restProps}
    >
      <div className="login-container cate-container">
        <div>
          <form>
            <p className="center">프로필 사진을 설정해주세요.</p>
            <table className="catepick-table">
              <tr className="center">
                <img src={img} className="profile" alt="오류"></img>
              </tr>
              <tr className="center">
                <button type="button" className="img-btn" onClick={basicImg}>
                  기본 이미지로 변경
                </button>
                <label className="img-btn" htmlFor="imgFile">
                  이미지 등록
                </label>
                <input
                  type="file"
                  id="imgFile"
                  ref={fileRef}
                  onChange={imgHandle}
                  accept=".png, .jpg, .jpeg"
                  hidden
                />
              </tr>
            </table>
            <p className="center">선호하는 카테고리를 선택해주세요.</p>
            <table className="catepick-table">
              {CATES.map((value, index) => {
                const GROUP = CATES.slice(index, index + 4);
                return (
                  index % 4 === 0 && (
                    <tr key={index}>
                      {GROUP.map((child, idx) => {
                        return (
                          <td key={idx} onChange={() => Change(idx + index)}>
                            <Checkbox
                              icon={ICON[idx + index]}
                              label={child}
                              className="cate-btn mypage-cate-btn"
                              isCheckedParent={false}
                            ></Checkbox>
                          </td>
                        );
                      })}
                    </tr>
                  )
                );
              })}
            </table>
            <div className="bottom-right">
              <button
                type="button"
                className="cate-btn skip-btn"
                onClick={() => {
                  SetData({
                    ...data,
                    cate: [
                      false,
                      false,
                      false,
                      false,
                      false,
                      false,
                      false,
                      false,
                    ],
                  });
                  setCategory();
                }}
              >
                건너뛰기
              </button>
              <button
                type="button"
                className="cate-btn skip-btn submit-btn"
                onClick={setCategory}
              >
                완료
              </button>
            </div>
          </form>
        </div>
      </div>
    </ReactModal>
  );
};

export default CateModal;
