import { useEffect, useState } from "react";
import { google, googleLogin, socialLogin } from "../API";
import CateModal from "../components/cate-modal";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const Social = () => {
  const navi = useNavigate();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const openModal = () => {
    setIsModalOpen(true);
  };
  const closeModal = () => {
    setIsModalOpen(false);
  };

  const 인가코드 = new URL(window.location.href).searchParams.get(
    "code"
  ) as string;
  let Path = new URL(window.location.href).pathname;
  const start = Path.lastIndexOf("/");
  const platform = Path.substring(start + 1, Path.length).toLowerCase();
  const platformTag = Path.substring(start + 1, start + 2).toUpperCase();


  let [UserInfo, setUserInfo] = useState({
    id: "",
    phone: "",
    nickname: "",
    pw: "",
    cate: [false, false, false, false, false, false, false, false],
    platform: platformTag,
  });
  const gggLogin = async () => {
    setIsModalOpen(await googleLogin(navi, 인가코드, UserInfo, setUserInfo));
  };
  const sssLogin = async () => {
    setIsModalOpen(await socialLogin(platform, 인가코드, setUserInfo, UserInfo, navi));
  };

  useEffect(() => {
    console.log(platformTag);
    if (platformTag === 'G') 
      gggLogin()
    else
      sssLogin()
  }, [platformTag]);

  return (
    <div>
      {
        isModalOpen && 
        <CateModal
        isOpen={isModalOpen}
        onRequestClose={closeModal}
        UserInfo={UserInfo}
        shouldCloseOnOverlayClick={false}
        shouldCloseOnEsc={false}
      />
      }
      <div className="circle-zone">
        <div className="back-circle" id="circle-violet"></div>
        <div className="back-circle" id="circle-pink"></div>
        <div className="back-circle" id="circle-blue"></div>
      </div>
    </div>
  );
};

export default Social;
