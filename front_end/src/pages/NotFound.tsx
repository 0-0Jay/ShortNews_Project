import { Link } from "react-router-dom";

function NotFound() {
  return (
    <div>
      <div className="login-container not-found-container">
        <div>
          <div className="logo-div">
            <img
              src={process.env.REACT_APP_S3_SHORT + "logo.svg"}
              id="login-page-logo"
            ></img>
          </div>
          <div className="center not-found-notice">
            <p>요청하신 페이지를 찾을 수 없습니다.</p>
          </div>
          <div className="not-found-guide">
            <p>
              방문하시려는 페이지의 주소가 잘못 입력되었거나,
              <br />
              페이지의 주소가 변경 혹은 삭제되어 요청하신 페이지를 찾을 수
              없습니다.
              <br />
              입력하신 주소가 정확한지 다시 한번 확인해 주시기 바랍니다.
            </p>
          </div>
          <div className="not-found-link">
            <Link to="/main/recommend">메인페이지로 이동</Link>
          </div>
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

export default NotFound;
