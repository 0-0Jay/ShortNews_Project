import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faThumbsUp,
  faThumbsDown,
  faCommentDots,
  faEye,
} from "@fortawesome/free-regular-svg-icons";
import { faBookmark } from "@fortawesome/free-solid-svg-icons";
import { CUT } from "../API";

interface newsSubject {
  news_id: string;
  title: string;
  bookmark: number;
  dislike: number;
  like: number;
  imgs?: string | null;
  reply: number;
  views: number;
  report : number;
  cate_id?: string;
}
interface dataProps {
  data: newsSubject;
  pickCate?: any;
}

const dict: Map<string, string> = new Map();
dict.set("100", "정치");
dict.set("101", "경제");
dict.set("102", "사회");
dict.set("103", "생활/문화");
dict.set("104", "세계");
dict.set("105", "IT/과학");
dict.set("106", "연예");
dict.set("107", "스포츠");

const NewsCard: React.FC<dataProps> = (props) => {
  let navi = useNavigate();
  let imgRef = useRef<any>();
  function getDetail(newsItem: newsSubject) {
    navi(`/selectNews/${newsItem.news_id}`);
  }
  // 0: 원래 없음, 1: 있음, 2: 있다가 없어짐
  const [isBlind, setIsBlind] = useState(1);
  useEffect(()=>{
    console.log(props.data.report >= 1 )
  },[])
  return (
    <div>
      
      {props.data.report >= 1 && (
        <div
          className={
            isBlind === 1
              ? "blind-news"
              : isBlind === 2
              ? "blind-news off-blind-animation"
              : ""
          }
        >
          <div>
            <p className="blind-news-notice">
              <img src={process.env.REACT_APP_S3_SHORT + "assets/notice.svg"} alt="notice" /> 숨겨진 기사입니다.
            </p>
            <p className="blind-news-text">
              다수의 신고가 접수되어 일시적으로 숨겨진 기사입니다. <br />
              아래 버튼을 클릭하면 기사를 확인할 수 있습니다.
            </p>
            <button
              type="button"
              className="blind-news-btn"
              onClick={() => setIsBlind(2)}
            >
              <p>기사 확인하기</p>
            </button>
          </div>
        </div>
      )}
      <button
        className="news-card"
        type="button"
        onClick={() => getDetail(props.data)}
      >
        {props.data.bookmark === 1 && (
        <div className="bookmark">
          <FontAwesomeIcon icon={faBookmark} style={{ color: "#57AEFF" }} />
        </div>
        )}
        <div>
          <img
            className="news-card-img"
            src={
              props.data.imgs !== null
                ? props.data.imgs
                : process.env.REACT_APP_S3_SHORT + "news-default.png"
            }
            alt=""
            ref = {imgRef}
            style={{
              objectFit: 'cover', // 'cover'로 설정하여 이미지를 잘라서 표시
              objectPosition: 'top center'
            }}
          />
        </div>
        <div>
          <div className="news-card-content">
            <p className="news-card-title">{props.data.title}</p>
            <div className="news-card-icon">
              <FontAwesomeIcon icon={faThumbsUp} />
              <span>{CUT(props.data.like)}</span>
              <FontAwesomeIcon icon={faThumbsDown} />
              <span>{CUT(props.data.dislike)}</span>
              <FontAwesomeIcon icon={faCommentDots} />
              <span>{CUT(props.data.reply)}</span>
              <FontAwesomeIcon icon={faEye} />
              <span>{CUT(props.data.views)}</span>
            </div>
            <div className="news-card-bottom">
              <span>
                {props.data.news_id.substring(0, 4)}.{/** Year */}
                {props.data.news_id.substring(4, 6)}.{/** Month */}
                {props.data.news_id.substring(6, 8)} {/** Day */}
                {props.data.news_id.substring(8, 10)}시 {/** hour */}
              </span>
              <span>
                {props.pickCate !== null
                  ? dict.get(props.data.cate_id!) || dict.get(props.pickCate)
                  : dict.get(props.pickCate)}
              </span>
            </div>
          </div>
        </div>
      </button>
    </div>
  );
};

export default NewsCard;
