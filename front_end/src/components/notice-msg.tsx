import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from "react-router-dom";
import { deleteAlarm, selectAlarm } from "../API";
import { useSearchKeyword } from "../context/SearchKeywordContext";

interface replyNotice {
  link: string;
  nickname: string;
  status: number;
  target_id: string;
  time: string;
  type: number;
}

function NoticeMsg(props: any) {
  const { updateKeyword, updateIsSearchOpen } = useSearchKeyword();
  let data:replyNotice = props.data;
  let navi = useNavigate();
  const dict:Map<number, string> = new Map<number, string>();
  dict.set(1, `'${data.nickname}'님이 추천을 눌렀습니다.`);
  dict.set(0, `'${data.nickname}'님이 답글을 달았습니다.`);
  dict.set(-1, `'${data.nickname}'님이 비추천을 눌렀습니다.`);
  const date:string = data.time;
  const year = date.substring(0, 4);
  const month = date.substring(5, 7);
  const day = date.substring(8, 10);
  const hour = date.substring(11, 13);
  const minute = date.substring(14, 16);
  const second = date.substring(17, 19);
  const CLICK = () => {
    updateKeyword("");
    updateIsSearchOpen(0);
    navi("/selectNews/" + data.link);
    selectAlarm({
      time : data.time,
      news_id : data.link
    },
    props.SetAlarms,
    props.SetCount)
  }
  return (
    <div className="msg-box">
      <button onClick={()=>deleteAlarm({time : data.time, news_id : data.link}, props.SetAlarms, props.SetCount)}>
        <FontAwesomeIcon icon={faXmark} style={{ color: "#e63737" }} />
      </button>
      <div className={data.status === 1 ? "msg-read" : ""} >
        <p>{dict.get(data.type)}</p>
        <p
          className={
            data.status === 1 ? "msg-read msg-box-time" : "msg-box-time"
          }
        >
          {year}년 {month}월 {day}일{" "}
          {hour}시 {minute}분 {second}초
          <br />
          <div onClick={CLICK} style={{cursor:"pointer"}}>확인</div>
        </p>
      </div>
    </div>
  );
}

export default NoticeMsg;

