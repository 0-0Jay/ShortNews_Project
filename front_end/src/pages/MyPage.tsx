import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getCookie } from "../Cookies";
import Checkbox from "../components/checkbox";
import PwModal from "../components/pw-modal";
import EmailModal from "../components/email-modal";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPenToSquare } from "@fortawesome/free-regular-svg-icons";
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
import {
  deleteS3,
  uploadS3,
  categoryUpdate,
  memberTTS,
  expiration,
} from "../API";
import NicknameModal from "../components/nickname-modal";
import Header from "../components/header";
import ScrollUp from "../components/scroll-up";
import SearchKeyword from "../components/searchKeyword";
import { useDate } from "../context/DateContext";
import { useSearchKeyword } from "../context/SearchKeywordContext";

function MyPage(props: any) {
  const { isSearchOpen } = useSearchKeyword();
  useEffect(() => {
    if (isSearchOpen === 2){
      navigate("/main/search", {replace : false})
    }
  }, [isSearchOpen])
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
  const [seleted, SetSeleted] = useState([
    false,
    false,
    false,
    false,
    false,
    false,
    false,
    false,
  ]);
  const [isReadOnly, setIsReadOnly] = useState(true);
  const toggleReadOnly = () => {
    setIsReadOnly((prevReadOnly) => !prevReadOnly);
  };
  const navigate = useNavigate();
  const fileRef = useRef<any>();
  let [data, setData] = useState(getCookie("dto"));
  let Name = "";
  // const PH = data?.phone.substring(0, 3) + "-" + data?.phone.substring(3, 7) + "-" + data?.phone.substring(7, 11);
  const PH =
    data?.phone.substring(0, 3) + " - " + data?.phone.substring(3, 7) + " - " + data?.phone.substring(7, 11);
  const EMAIL = data?.phone;
  const Platform = data?.platform;
  const basicProfile =
    process.env.REACT_APP_S3_SHORT + "assets/basicProfile.png";
  let [img, SetImg] = useState(basicProfile);

  function basicImg() {
    fileRef.current.value = "";
    deleteS3({ id: data?.id });
    SetImg(basicProfile);
  }

  const [isModalOpen, setIsModalOpen] = useState([false, false, false]);
  const openNicknameModal = () => {
    setIsModalOpen([true, false, false]);
  };
  const openEmailModal = () => {
    setIsModalOpen([false, true, false]);
  };
  const openPwModal = () => {
    setIsModalOpen([false, false, true]);
  };
  const closeModal = () => {
    setIsModalOpen([false, false, false]);
    setData(getCookie("dto"));
  };

  function Change(idx: any) {
    let copy = [...seleted];
    copy[idx] = !copy[idx];
    SetSeleted(copy);
  }
  const [ttsModel, setModel] = useState([
    data?.model === "_male",
    data?.model === "_female",
  ]);
  const [ttsSpeed, setSpeed] = useState(data?.speed);
  const changeDefault = (e: any) => {
    e.currentTarget.src = basicProfile;
  };
  const imgHandle = (e: any) => {
    const FILE: File = e.target.files[0];
    console.log(FILE);
    const TYPE = FILE.type.toLowerCase();
    if (
      !(TYPE === "image/jpeg" || TYPE === "image/png" || TYPE === "image/jpg")
    ) {
      alert("지원하지 않는 파일입니다.");
      fileRef.current.value = "";
    } else if (FILE.size > 5 * 1024 * 1024) {
      alert("용량이 5MB초과된 파일은 업로드 할 수 없습니다.");
      fileRef.current.value = "";
    } else {
      uploadS3({
        id: data?.id,
        file: fileRef.current.files[0],
        value: fileRef.current.value,
      });
    }
  };
  useEffect(() => {
    data = getCookie("dto");
    if (data) {
      let copy = [...seleted];
      for (let i = 0; i < data?.category?.length; i++) {
        copy[parseInt(data?.category[i]) - 100] = true;
      }
      SetSeleted(copy);
    }
  }, []);
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

  const [InputNickname, InputNicknameHandle] = useState(data?.nickname);

  const [isOpen, setOpen] = useState([false, false, false]);
  const { date, selectedDate, setSelectedDate, handleDateChange } = useDate();
  expiration();
  return (
    data && (
      <div>
        <div className="header">
          <Header
            isOpen={isOpen}
            setOpen={setOpen}
            date={date}
            selectedDate={selectedDate}
            setSelectedDate={setSelectedDate}
          />
        </div>
        <SearchKeyword setOpen={setOpen} />
        <ScrollUp />
        <div>
          <div className="cate-btn-zone">
            <span>마이페이지</span>
            <div className="vertical-line"></div>
            <button
              type="button"
              onClick={() => navigate("/member")}
              className="cate-btn-zone-pick"
            >
              회원정보
            </button>
            <button type="button" onClick={() => navigate("/myActivity/like")}>
              나의 활동
            </button>
          </div>
          <div className="mypage-container-zone">
            <div className="mypage-container profile-zone">
              <div className="mypage-container-label">
                <span>내 프로필</span>
                <hr></hr>
              </div>
              <div className="profile-zone-profile">
                <div className="center">
                  <img
                    src={(process.env.REACT_APP_S3_USER as string) + data?.id}
                    onError={changeDefault}
                    className="profile"
                    alt="오류"
                  ></img>
                </div>
                <p className="profile-name">{Name}</p>
                {/* <p className="profile-email">{Platform === "I" ? PH : EMAIL}</p> */}
              </div>
              <div className="mypage-withdraw">
                <button type="button" onClick={() => navigate("/WithDraw")}>
                  회원 탈퇴
                </button>
              </div>
            </div>
            <div className="mypage-container profile-modify-zone">
              <div className="mypage-container-label">
                <span>회원정보 수정</span>
                <hr></hr>
              </div>
              <div className="mypage-container-separate">
                <table className="profile-modify-zone-profile">
                  <tbody>
                  <tr>
                    <td className="profile-label">프로필 사진</td>
                    <td>
                      <img
                        src={
                          (process.env.REACT_APP_S3_USER as string) + data?.id
                        }
                        onError={changeDefault}
                        className="profile mypage-profile"
                        alt="오류"
                      ></img>
                      <div style={{width : "100%"}}>
                        <label className="mypage-btn" htmlFor="imgFile">
                          사진 변경
                        </label>
                        <button
                          type="button"
                          className="mypage-btn"
                          onClick={basicImg}
                        >
                          기본 이미지로 변경
                        </button>
                        {/* <button
                          type="button"
                          className="mypage-btn"
                          onClick={() => window.location.reload()}
                        >
                          새로고침
                        </button> */}
                        <input
                          type="file"
                          id="imgFile"
                          ref={fileRef}
                          hidden
                          accept=".png, .jpg, .jpeg"
                          onChange={imgHandle}
                        />
                      </div>
                    </td>
                  </tr>
                  <tr>
                    <td>닉네임</td>
                    <td className="modify-input">
                      <input
                        type="text"
                        value={InputNickname}
                        readOnly={isReadOnly}
                        disabled={isReadOnly}
                        onChange={(e) => InputNicknameHandle(e.target.value)}
                        className="mypage-btn mypage-input"
                      ></input>
                      <button
                        type="button"
                        className="modify-btn"
                        onClick={openNicknameModal}
                      >
                        <FontAwesomeIcon icon={faPenToSquare} />
                      </button>
                    </td>
                  </tr>
                  {
                    <NicknameModal
                      isOpen={isModalOpen[0]}
                      onRequestClose={closeModal}
                      UserInfo={data}
                    />
                  }
                  <tr>
                    <td>{Platform === "I" ? "전화번호" : "이메일"}</td>
                    <td className="modify-input">
                      <input
                        type="email"
                        value={Platform === "I" ? PH : EMAIL}
                        readOnly
                        disabled={data?.platform !== "I"}
                        className="mypage-btn mypage-input"
                      ></input>
                      <button
                        type="button"
                        className="modify-btn"
                        hidden={data?.platform !== "I"}
                        onClick={openEmailModal}
                      >
                        <FontAwesomeIcon icon={faPenToSquare} />
                      </button>
                    </td>
                  </tr>
                  {isModalOpen[1] && 
                  <EmailModal
                    isOpen={isModalOpen[1]}
                    onRequestClose={closeModal}
                    UserInfo={data}
                  />}
                  {data.platform === "I" && (
                    <tr>
                      <td>비밀번호</td>
                      <td>
                        <button
                          type="button"
                          className="mypage-btn "
                          onClick={openPwModal}
                        >
                          비밀번호 변경
                        </button>
                      </td>
                    </tr>
                  )}
                  {isModalOpen[2] && (
                    <PwModal
                      isOpen={isModalOpen[2]}
                      onRequestClose={closeModal}
                      UserInfo={data}
                    />
                  )}
                  </tbody>
                </table>
              </div>
              <div className="mypage-container-label">
                <span>선호 카테고리 수정</span>
                <hr></hr>
              </div>
              <div className="center">
                <table className="mypage-cate-table">
                  <tbody>
                  {CATES.map((value, index) => {
                    const GROUP = CATES.slice(index, index + 4);
                    return (
                      <tr key={index}>
                        {index % 4 === 0 &&
                          GROUP.map((child, idx) => {
                            return (
                              <td
                                key={idx}
                                onChange={() => Change(idx + index)}
                              >
                                <Checkbox
                                  icon={ICON[idx + index]}
                                  label={child}
                                  className="cate-btn mypage-cate-btn"
                                  isCheckedParent={seleted[index + idx]}
                                ></Checkbox>
                              </td>
                            );
                          })}
                      </tr>
                    );
                  })}
                  </tbody>
                </table>
              </div>
              <div className="mypage-submit-btn-div">
                <button
                  type="button"
                  className="cate-btn skip-btn mypage-submit-btn"
                  onClick={() => categoryUpdate(seleted, navigate)}
                >
                  저장
                </button>
              </div>
              <div className="mypage-container-label">
                <span>TTS 수정</span>
                <hr></hr>
              </div>
              <div className="profile-modify-zone-profile">
                <table className="mypage-tts-table">
                  <thead></thead>
                  <tbody>
                    <tr>
                      <td>성별</td>
                      <td>
                        <label className="radio-circle-label">
                          <input
                            type="radio"
                            name="model"
                            value="남성"
                            className="radio-circle"
                            checked={ttsModel[0]}
                            onChange={() => {
                              setModel([true, false]);
                            }}
                          ></input>
                          <span>남성</span>
                        </label>
                      </td>
                      <td>
                        <label className="radio-circle-label">
                          <input
                            type="radio"
                            name="model"
                            value="여성"
                            checked={ttsModel[1]}
                            onChange={() => {
                              setModel([false, true]);
                            }}
                            className="radio-circle"
                          ></input>
                          <span>여성</span>
                        </label>
                      </td>
                    </tr>
                    <tr>
                      <td>속도</td>
                      <td>
                        <label className="radio-circle-label">
                          <input
                            type="radio"
                            name="speed"
                            value="느림"
                            className="radio-circle"
                            checked={ttsSpeed === 0.75}
                            onChange={() => setSpeed(0.75)}
                          ></input>
                          <span>느림</span>
                        </label>
                      </td>
                      <td>
                        <label className="radio-circle-label">
                          <input
                            type="radio"
                            name="speed"
                            value="보통"
                            className="radio-circle"
                            checked={ttsSpeed === 1.0}
                            onChange={() => setSpeed(1.0)}
                          ></input>
                          <span>보통</span>
                        </label>
                      </td>
                      <td>
                        <label className="radio-circle-label">
                          <input
                            type="radio"
                            name="speed"
                            value="빠름"
                            className="radio-circle"
                            checked={ttsSpeed === 1.25}
                            onChange={() => setSpeed(1.25)}
                          ></input>
                          <span>빠름</span>
                        </label>
                      </td>
                    </tr>
                  </tbody>
                  <tfoot></tfoot>
                </table>
              </div>
              <div className="mypage-submit-btn-div">
                <button
                  type="button"
                  className="cate-btn skip-btn mypage-submit-btn"
                  onClick={() =>
                    memberTTS(
                      {
                        model: ttsModel[0] ? "_male" : "_female",
                        speed: "" + ttsSpeed,
                      },
                      navigate
                    )
                  }
                >
                  저장
                </button>
                <div className="mypage-withdraw">
                <button type="button" hidden = {!isMobile} onClick={() => navigate("/WithDraw")}>
                  회원 탈퇴
                </button>
              </div>
              </div>
              
            </div>
          </div>
          
        </div>
        
      </div>
    )
  );
}

export default MyPage;
