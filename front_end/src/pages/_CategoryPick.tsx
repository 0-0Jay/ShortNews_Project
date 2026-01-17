import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Checkbox from "../components/checkbox";
import axios from "axios";
import { setCookie, setCookieAll } from "../Cookies";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
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
import basicProfile from "../assets/basicProfile.png";
import { JAVA } from "../API";
function CategoryPick(props: any) {
  const navigate = useNavigate();
  let [data, SetData] = useState({
    id: "",
    email: "",
    nickname: "",
    pw: "",
    cate: [false, false, false, false, false, false, false, false],
  });

  const location = useLocation();
  useEffect(() => {
    SetData(location.state.data);
  }, []);

  function Change(idx: any) {
    let copy = { ...data };
    copy.cate[idx] = !copy.cate[idx];
    SetData(copy);
  }

  async function setCategory() {
    console.log(data);
    await axios.post(JAVA + "signup/selectCategory", data)
    .then((res)=>{
      console.log(res.data);
      if (res.data.status){
        setCookieAll(res.data);
        alert("수정은 마이페이지에서 가능합니다.");
        navigate('/main/recommend');
      }else{
        alert("문제 발생 재시도해주세요");
      }
    })
    .catch((res)=>{
      console.log("CategoryPick setCategory errer");
      console.error(res);
    })
  }
  return (
    <div className="login-container">
      <div>
        <form>
          <p className="center">선호하는 카테고리를 선택해주세요.</p>
          <table className="catepick-table">
            <tr>
              <td
                onChange={() => {
                  Change(0);
                }}
              >
                <Checkbox
                  isCheckedParent = {false}
                  icon={faLandmark}
                  label="정치"
                  className="cate-btn"
                ></Checkbox>
              </td>
              <td
                onChange={() => {
                  Change(1);
                }}
              >
                <Checkbox
                  isCheckedParent = {false}
                  icon={faCircleDollarToSlot}
                  label="경제"
                  className="cate-btn"
                ></Checkbox>
              </td>
              <td
                onChange={() => {
                  Change(2);
                }}
              >
                <Checkbox
                  isCheckedParent = {false}
                  icon={faPeopleRoof}
                  label="사회"
                  className="cate-btn"
                ></Checkbox>
              </td>
              <td
                onChange={() => {
                  Change(3);
                }}
              >
                <Checkbox
                  isCheckedParent = {false}
                  icon={faFlaskVial}
                  label="IT/과학"
                  className="cate-btn"
                ></Checkbox>
              </td>
            </tr>
            <tr>
              <td
                onChange={() => {
                  Change(4);
                }}
              >
                <Checkbox
                  isCheckedParent = {false}
                  icon={faMicrophoneLines}
                  label="연예"
                  className="cate-btn"
                ></Checkbox>
              </td>
              <td
                onChange={() => {
                  Change(5);
                }}
              >
                <Checkbox
                  isCheckedParent = {false}
                  icon={faFutbol}
                  label="스포츠"
                  className="cate-btn"
                ></Checkbox>
              </td>
              <td
                onChange={() => {
                  Change(6);
                }}
              >
                <Checkbox
                  isCheckedParent = {false}
                  icon={faFilm}
                  label="문화"
                  className="cate-btn"
                ></Checkbox>
              </td>
              <td
                onChange={() => {
                  Change(7);
                }}
              >
                <Checkbox
                  isCheckedParent = {false}
                  icon={faGlobe}
                  label="세계"
                  className="cate-btn"
                ></Checkbox>
              </td>
            </tr>
          </table>
          <p className="center">프로필 사진을 설정해주세요.</p>
          <table className="catepick-table">
            <tr className="center">
              <img src={basicProfile} className="profile"></img>
            </tr>
            <tr className="center">
              <button type="button" className="img-btn">
                사진 변경
              </button>
              <button type="button" className="img-btn">
                기본 이미지로 변경
              </button>
            </tr>
          </table>
          <div className="bottom-right">
            <button
              type="button"
              className="cate-btn skip-btn"
              onClick={setCategory}
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
  );
}

export default CategoryPick;
