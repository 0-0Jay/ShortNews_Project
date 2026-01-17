import { useNavigate } from "react-router-dom";
import "../App.css";
import { removeCookieAll } from "../Cookies";
import { useSearchKeyword } from "../context/SearchKeywordContext";
import { Logout } from "../API";

function List(props:any) {
  const { updateKeyword, updateIsSearchOpen } = useSearchKeyword();
  const navigate = useNavigate();
  return (
    <span className="show-list toggle-menu">
      <table>
        <thead></thead>
        <tbody>
          <tr>
            <td>
              <button
                onClick={() => {
                  updateKeyword("");
                  updateIsSearchOpen(0);
                  navigate("/member");
                }}
              >
                마이페이지
              </button>
            </td>
          </tr>
          <tr>
            <td>
              <button
                onClick={() => {
                  updateKeyword("");
                  updateIsSearchOpen(0);
                  navigate("/BookMark/111");
                }}
              >
                나의 북마크
              </button>
            </td>
          </tr>
          <tr>
            <td>
              <button
                onClick={() => {
                  updateKeyword("");
                  updateIsSearchOpen(0);
                  navigate("/myActivity/like");
                }}
              >
                나의 활동
              </button>
            </td>
          </tr>
          <tr>
            <td>
              <button
                id="logout"
                onClick={() => {
                  navigate("/", {replace:true});
                  Logout();
                  props.SSE?.close();
                  updateKeyword("");
                  updateIsSearchOpen(0);
                  removeCookieAll();
                }}
              >
                로그아웃
              </button>
            </td>
          </tr>
        </tbody>
        <tfoot></tfoot>
      </table>
    </span>
  );
}

export default List;
