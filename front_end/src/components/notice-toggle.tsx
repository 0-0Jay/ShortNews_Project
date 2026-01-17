import React, { useState } from "react";
import { alarmSwitch, expiration } from "../API";
import { getCookie } from "../Cookies";

function NoticeToggle() {
  const DTO = getCookie("dto");
  const [status, setStatus] = useState(DTO.alarm === 1);
  expiration();
  return (
    <div
      className={
        "toggle-out" + (status == true ? " notice-on-out" : " notice-off-out")
      }
      onClick={()=>alarmSwitch(status ? 1 : 0)}
    >
      <button
        className={
          "toggle-in" + (status == true ? " notice-on-in" : " notice-off-in")
        }
        onClick={() => {
          setStatus(!status);
        }}
      ></button>
    </div>
  );
}

export default NoticeToggle;
