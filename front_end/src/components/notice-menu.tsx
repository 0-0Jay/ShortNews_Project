import { deleteAlarmAll, expiration } from "../API";
import NoticeMsg from "./notice-msg";
import NoticeToggle from "./notice-toggle";



function Notice(props : any) {
  expiration();
  return (
    <div className="show-notice toggle-menu">
      <div className="toggle-menu-header">
        <span>새 알림 </span>
        <NoticeToggle></NoticeToggle>
      </div>
      <button className="notice-del" onClick={()=>deleteAlarmAll(props.SetAlarms, props.SetCount)}>전체 삭제</button>
      {
        props.Alarms.map((reply:any, index: any) => {
          return (< NoticeMsg 
                            key = {index} 
                            data = {reply}
                            Alarms = {props.Alarms} 
                            SetAlarms = {props.SetAlarms}
                            SetCount = {props.SetCount}/>);
        })
      }
    </div>
  );
}

export default Notice;
