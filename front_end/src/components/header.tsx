import { Link, useNavigate } from "react-router-dom";
import { getCookie } from "../Cookies";
import "../App.css";
import Notice from "./notice-menu";
import List from "./list-menu";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBell } from "@fortawesome/free-solid-svg-icons";
import {  useEffect, useState } from "react";
import { useDate } from "../context/DateContext";
import { expiration, getAlarms, getCount, setSubscribe } from "../API";
import { useSearchKeyword } from "../context/SearchKeywordContext";
export interface AlramInterface {
  link : string;
  nickname : string;
  status : number;
  target_id : string;
  time : string;
  type : number;
}
const Header = (props: any) => {
  const { updateIsSearchTypeOpen, updateIsSearchOpen, updateKeyword } = useSearchKeyword();
  const { setSelectedDate } = useDate();
  const dto: any | undefined = getCookie("dto");
  const basicProfile = process.env.REACT_APP_S3_USER + "default";
  const IMG = window.innerWidth < 600
  ? process.env.REACT_APP_S3_SHORT + "logo_small.svg"
  : process.env.REACT_APP_S3_SHORT + "logo.svg"
  const navi = useNavigate();

  let [Count, SetCount] = useState(false);
  let [Alarms, SetAlarms]= useState<AlramInterface[]>([]);
  let SSE:any;
  const handleAlarm = (ev : MessageEvent) => {
    let data = JSON.parse(ev.data);
    SetCount(true);
    getAlarms(SetAlarms, SetCount);
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

  useEffect(()=>{
    if (props.isOpen[1]){
      getAlarms(SetAlarms, SetCount);
    }
    return () => SetAlarms([]);
  }, [props.isOpen[1]])
  

  useEffect(()=>{
    getCount(SetCount);
    SSE = setSubscribe(handleAlarm);
    return () => {
      SSE.close();
    }
  }, [])


  expiration();
  return (
    dto && (
      <div className="nav-bar">
        <div className="nav-bar-column">
            <img
              src={IMG}
              className="nav-bar-logo"
              onClick={() => {
                navi("/main/recommend")
                window.scrollTo(0, 0);
                setSelectedDate(new Date());
                updateIsSearchTypeOpen(false);
                updateKeyword("");
                updateIsSearchOpen(0)
              }}
              alt="Shortnews"
            ></img>
        </div>
        <div className="nav-bar-column"></div>
        <div className="nav-bar-column">
          <button
            className="toggle-button toggle-notice"
            onClick={() => {
              props.setOpen([false, !props.isOpen[1], false]);
              updateIsSearchTypeOpen(false);
            }}
          >
            <FontAwesomeIcon icon={faBell} size="2x" />
          </button>
          { Count && 
            <span className="nav-notification"> </span>
          } {/* 알림 빨간 뱃지 */}
          <button
            className="toggle-button toggle-list"
            onClick={() => {
              props.setOpen([false, false, !props.isOpen[2]]);
              updateIsSearchTypeOpen(false);
            }}
          >
            <img
              src={(process.env.REACT_APP_S3_USER as string) + dto?.id}
              onError={(e) => {
                e.currentTarget.src = basicProfile;
              }}
              className="profile nav-bar-profile"
              alt="myprofile"
            ></img>
          </button>
          {/* menu open */}
          {props.isOpen[1] && 
          <Notice 
            Alarms = {Alarms} 
            SetAlarms = {SetAlarms}
            SetCount = {SetCount}></Notice>}
          {props.isOpen[2] && <List SSE = {SSE}></List>}
        </div>
      </div>
    )
  );
};
export default Header;
