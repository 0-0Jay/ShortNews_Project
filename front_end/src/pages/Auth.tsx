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
          type="text"
          placeholder="인증코드"
          name="auth"
          disabled={props.EmailAuth}
          onChange={(t) => {
            SetAuth(t.target.value);
          }}
          onKeyDown={(k) => {
            if (k.key === "Enter" || k.key == "NumpadEnter") {
              props.check(count, auth, timerId, SetEmailShake, emailShake)
            }
          }}
        ></input>
        {!props.EmailAuth ? (
          <button
            type="button"
            className="btn-on-input"
            onClick={() =>
              props.check(count, auth, timerId, SetEmailShake, emailShake)
            }
          >
            인증
          </button>
        ) : null}
      </div>
      <div className="err-msg">
        {!props.EmailAuth ? (
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
