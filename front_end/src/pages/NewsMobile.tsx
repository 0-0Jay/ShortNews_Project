import { useEffect, useState, useRef } from "react";
import { getDate, shareNews } from "../API";
import { useNavigate, useLocation, Link } from "react-router-dom";
import { Swiper, SwiperSlide } from "swiper/react";
import "../swiper.css";
import "swiper/components/navigation/navigation.min.css";
import "swiper/components/pagination/pagination.min.css";
import SwiperCore, { Navigation, Pagination } from "swiper";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faThumbsUp,
  faThumbsDown,
  faCommentDots,
} from "@fortawesome/free-regular-svg-icons";
import {
  faCaretLeft,
  faCaretRight,
} from "@fortawesome/free-solid-svg-icons";
// 이미지 슬라이더 세팅값
SwiperCore.use([Navigation, Pagination]);
const settings = {
  loop: true,
  navigation: true,
  pagination: { clickable: true },
  autoHeight: true,
  initialSlide: 1,
};

function NewsMobile(props: any) {
  const location = useLocation();
  const navi = useNavigate();
  const [resource, SetResource] = useState([]);
  const newsid = location.pathname.substring(11);
  const status = location.pathname.substring(1, location.pathname.lastIndexOf("/"))

  const [Article, SetArticle] = useState({
    bookmark: false,
    content: "",
    dislike: 0,
    imgs: [],
    like: 0,
    title: "",
    type: 0,
    views: 0,
    // reply: 0,
  });

  // 기사 하단 버튼 클릭 여부 (좋아요, 싫어요, 댓글, 공유, 북마크, 출처)
  const [isClicked, setClicked] = useState([
    false,
    false,
    false,
    false,
    false,
    false,
  ]);

  // 이미지 슬라이더 세팅값
  SwiperCore.use([Navigation, Pagination]);

  const navigationPrevRef = useRef(null);
  const navigationNextRef = useRef(null);

  const settings = {
    initialSlide: 0,
    navigation: {
      nextEl: navigationNextRef.current,
      prevEl: navigationPrevRef.current,
    },
    pagination: { clickable: true },
    autoHeight: true,
    grabCursor: true,
  };

  const guestHandler = () => {
    let res = window.confirm("로그인 후 이용가능합니다.\n로그인 하시겠습니까?");
    if (res) {
      navi("/")
      window.sessionStorage.setItem("path", newsid);
    }
  }

  useEffect(() => {
    shareNews(newsid, status + "/", "guest", SetArticle, SetResource);
    // selectNews(newsid!, navi, SetArticle, isClicked, setClicked);
  }, []);

  return (
    <div className="mobile-news">
      <div className="mobile-news-container">
        <div className="mobile-news-title">
          <p>{Article.title}</p>
          <br></br>
          <sub>
            {getDate(newsid!)} {newsid?.substring(8, 10)}:00
          </sub>
        </div>
        <div className="moblie-news-content">
          {Article.content}
          <div className="mobile-news-swiper">
            <Swiper
              {...settings}
              allowSlideNext={Article.imgs.length === 1 ? false : true}
              allowSlidePrev={Article.imgs.length === 1 ? false : true}>

              {Article.imgs ? (
                Article.imgs.map((value, index) => {
                  return (
                    <SwiperSlide key={index}>
                      <img src={value} alt="img" />
                    </SwiperSlide>
                  );
                })
              ) : (
                <img
                  src={process.env.REACT_APP_S3_SHORT + "news-default.svg"}
                  alt="img"
                />
              )}

              {Article.imgs.length != 1 && (
                <div className="slider-btn-zone">
                  <button
                    ref={navigationPrevRef}
                    type="button"
                    className="slider-prev-btn"
                  >
                    <FontAwesomeIcon icon={faCaretLeft} />
                  </button>
                  <button
                    ref={navigationNextRef}
                    type="button"
                    className="slider-next-btn"
                  >
                    <FontAwesomeIcon icon={faCaretRight} />
                  </button>
                </div>
              )}
            </Swiper>
          </div>
        </div>
        <div className="mobile-news-view">{Article.views} views</div>
        <div className="mobile-news-button">
          <button className="mobile-news-like" onClick={guestHandler}>
            <FontAwesomeIcon
              icon={faThumbsUp}
              style={isClicked[0] ? { color: "#F16868" } : { color: "#000" }}
            />
            <span>{Article.like > 999 ? 999 : Article.like}</span>
          </button>
          <button className="mobile-news-like" onClick={guestHandler}>
            <FontAwesomeIcon
              icon={faThumbsDown}
              style={isClicked[1] ? { color: "#669AF3" } : { color: "#000" }}
            />
            <span>{Article.dislike > 999 ? 999 : Article.dislike}</span>
          </button>
          <button className="mobile-news-reply" onClick={guestHandler}>
            <FontAwesomeIcon icon={faCommentDots} style={{fontSize : "140%"}}  />
          </button>
          
          <button 
            className="news-container-btn news-container-copyright" 
            style={{fontSize : "100%", width : "52.77px", height : "36px"}} 
            onClick={()=>{
              let copy = [...isClicked]
              copy[5] = !copy[5];
              setClicked(copy);
            }}>
            출처
          </button>
          {isClicked[5] && 
            <div className="news-copyright-zone">
              {
                resource.map((value: string, idx: number) => {
                  return (
                    <Link to={value} target="_blank">
                      {idx + 1}. {value}
                    </Link>
                  );
                })
              }
            </div>
          }
        </div>
      </div>
    </div>
  );
}

export default NewsMobile;
