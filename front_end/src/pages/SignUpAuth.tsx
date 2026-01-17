import { useEffect, useState } from "react";

function Auth(props: any) {
  let [emailShake, SetEmailShake] = useState(false);
  let [count, SetCount] = useState(300);
  let timerId: NodeJS.Timeout;
  // let [auth, SetAuth] = useState(props.code);
  let [auth, SetAuth] = useState("");

  useEffect(() => {
    timerId = setTimeout(() => {
      if (count === 0) clearTimeout(timerId);
      else SetCount((count) => count - 1);
    }, 1000);
    // 컴포넌트가 언마운트되면 타이머 해제
    return () => clearTimeout(timerId);
  }, [count]);

  function check() {
    let err = [...props.Err];
    let status = [...props.Status];
    if (count > 0 && props.code === auth) {
      err[props.EmailAUTH] = "인증이 완료되었습니다.";
      status[props.EmailAUTH] = true;
      clearTimeout(timerId);
    } else {
      err[props.EmailAUTH] = "";
      status[props.EmailAUTH] = false;
      SetEmailShake(!emailShake);
    }
    props.SetErr(err);
    props.SetStatus(status);
  }

  function refresh() {
    SetAuth("");
    clearTimeout(timerId);

    props.refresh();

    SetCount(300);
    timerId = setTimeout(() => {
      if (count === 0) clearTimeout(timerId);
      else SetCount((count) => count - 1);
    }, 1000);
  }
  return (
    <div className="signup-page-row">
      <div>
        <input
          value={auth}
          className={"signup-page-input " + (emailShake ? "shake" : "")}
          id="signup-page-input-email"
          type="text"
          placeholder="인증코드"
          name="auth"
          disabled={props.Status[props.EmailAUTH]}
          onChange={(t) => {
            SetAuth(t.target.value);
          }}
          onKeyDown={(k) => {
            if (k.key === "Enter") {
              check();
            }
          }}
        ></input>
        {!props.Status[props.EmailAUTH] ? (
          <button type="button" className="btn-on-input" onClick={check}>
            인증
          </button>
        ) : null}
      </div>
      <div className="sign-up-err-msg">
        {!props.Status[props.EmailAUTH] ? (
          <div className="email-time">
            {" "}
            {Math.floor(count / 60)}분 {count % 60}초 내에 인증해주세요!{" "}
            <span onClick={refresh}>재전송</span>{" "}
          </div>
        ) : null}
      </div>
    </div>
  );
}

export default Auth;
