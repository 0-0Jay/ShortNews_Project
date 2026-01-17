import React from "react";
import Checkbox from "./checkbox";

function MypageCate() {
  return (
    <div>
      <form>
        <table className="cate-check-table">
          <tr>
            <td>
              <Checkbox isCheckedParent = {false} label="정치" className="cate-check"></Checkbox>
            </td>
            <td>
              <Checkbox isCheckedParent = {false} label="경제" className="cate-check"></Checkbox>
            </td>
            <td>
              <Checkbox isCheckedParent = {false} label="사회" className="cate-check"></Checkbox>
            </td>
            <td>
              <Checkbox isCheckedParent = {false} label="IT/과학" className="cate-check"></Checkbox>
            </td>
          </tr>
          <tr>
            <td>
              <Checkbox isCheckedParent = {false} label="연예" className="cate-check"></Checkbox>
            </td>
            <td>
              <Checkbox isCheckedParent = {false} label="스포츠" className="cate-check"></Checkbox>
            </td>
            <td>
              <Checkbox isCheckedParent = {false} label="문화" className="cate-check"></Checkbox>
            </td>
            <td>
              <Checkbox isCheckedParent = {false} label="세계" className="cate-check"></Checkbox>
            </td>
          </tr>
        </table>
        <button className="mypage-submit-btn">저장</button>
      </form>
    </div>
  );
}

export default MypageCate;
