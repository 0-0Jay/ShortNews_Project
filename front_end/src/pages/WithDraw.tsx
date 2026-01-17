import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Checkbox from "../components/checkbox";
import { memberDelete } from "../API";

function WithDraw() {
  const [isEtcReasonChecked, setIsEtcReasonChecked] = useState(false);
  const navigator = useNavigate();
  const [reason, SetReason] = useState("");
  const [status, SetStatus] = useState([false, false, false, false, false])
  const reasonList = ["서비스 불만족", "컨텐츠 품질 불만족", "개인정보 유출 우려", "탈퇴 후 재가입"]
  const reasonHandle = (e:any) => SetReason(e.target.value);
  const handleEtcReasonCheckChange = (isChecked: boolean) => {
    setIsEtcReasonChecked(isChecked);
  };
  const Change = (index : any) => {
    let copy = [...status];
    copy[index] = !copy[index];
    SetStatus(copy);
    console.log(copy);
  }
  const WithDrawHandle = () => {
    let flag = window.confirm("정말로 탈퇴하시겠습니까?");
    if (flag){
      let result = "";
      for(let i = 0 ; i < status.length ; i++)
        if (status[i]) result += reasonList[i] + ",";
  
      if (reason.trim() !== "") result += reason;
      else result = result.substring(0, result.length - 1);
      memberDelete({content : result}, navigator);
    }
  }
  return (
    <div className="center">
      <div className="withdraw-container">
        <div className="logo-div">
          <img
            src={process.env.REACT_APP_S3_SHORT + "logo.svg"}
            id="login-page-logo"
          ></img>
        </div>
        <div className="withdraw-p withdraw-notice">
          <p>탈퇴 사유를 알려주세요</p>
        </div>
        <span onChange = {()=>Change(0)}>
          <Checkbox isCheckedParent = {false} 
            label={reasonList[0]} 
            className="circle-check"
          ></Checkbox>
        </span>
        <span onChange = {()=>Change(1)}>        
          <Checkbox isCheckedParent = {false}
            label={reasonList[1]} 
            className="circle-check"
          ></Checkbox>
          </span>
        <span onChange = {()=>Change(2)}>
          <Checkbox isCheckedParent = {false}
            label={reasonList[2]} 
            className="circle-check"
          ></Checkbox>
          </span>
        <span onChange = {()=>Change(3)}>
          <Checkbox isCheckedParent = {false}
           label={reasonList[3]} 
           className="circle-check"
          ></Checkbox>
          </span>
        <span onChange = {()=>Change(4)}>
          <Checkbox isCheckedParent = {false}
            label="기타 사유"
            className="circle-check"
            onCheckChange={handleEtcReasonCheckChange}
          ></Checkbox>
        </span>
          {isEtcReasonChecked && (
            <input
              className="login-page-input"
              placeholder="기타 사유를 알려주세요"
              type="text"
              value = {reason}
              maxLength={50}
              onChange={reasonHandle}
            />
          )}
          <button type="button" onClick={WithDrawHandle} className="login-page-btn login-btn">
            회원 탈퇴
          </button>
        <div className="withdraw-p">
          <Link to="/member">더 이용할래요</Link>
        </div>
      </div>
      <div className="circle-zone">
        <div className="back-circle" id="circle-violet"></div>
        <div className="back-circle" id="circle-pink"></div>
        <div className="back-circle" id="circle-blue"></div>
      </div>
    </div>
  );
}

export default WithDraw;
