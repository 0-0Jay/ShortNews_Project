import { useState, useEffect, useRef } from "react";
// npm install react-slick @types/react-slick slick-carousel
// npm install swiper@6.8.4
import { Swiper, SwiperSlide } from "swiper/react";
import "../swiper.css";
import "swiper/components/navigation/navigation.min.css";
import "swiper/components/pagination/pagination.min.css";
import SwiperCore, { Navigation, Pagination } from "swiper";
import { useNavigate, useParams, Link, useLocation } from "react-router-dom";
import {
  createReply,
  expiration,
  getDate,
  getReply,
  getSearch,
  getSource,
  selectNews,
  updateBookMark,
  updateLike,
} from "../API";
import { getCookie } from "../Cookies";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faThumbsUp,
  faThumbsDown,
  faCommentDots,
  faComments,
} from "@fortawesome/free-regular-svg-icons";
import {
  faHeadphones,
  faEllipsisVertical,
  faCaretLeft,
  faCaretRight,
  faCaretUp,
  faCaretDown,
} from "@fortawesome/free-solid-svg-icons";
import ReportModal from "../components/report-modal";
import TTSPlay from "../components/tts-play";
import Reply, { Replies } from "../components/reply";
import {
  CopyUrl,
  shareKakao,
  shareFacebook,
  shareNaverBlog,
} from "../components/share-link";
import Header from "../components/header";
import ScrollUp from "../components/scroll-up";
import SearchKeyword from "../components/searchKeyword";
import { useDate } from "../context/DateContext";
import notice from "../assets/notice.svg";
import { useSearchKeyword } from "../context/SearchKeywordContext";
import Main from "./Main";

// 이미지 슬라이더 세팅값
SwiperCore.use([Navigation, Pagination]);
const settings = {
  loop: true,
  navigation: true,
  // watchOverFlow: true,
  pagination: { clickable: true },
  autoHeight: true,
  initialSlide: 1,
};

function News(props: any) {
  let navi = useNavigate();
  const { keyword, isSearchOpen } = useSearchKeyword();
  useEffect(() => {
    if (keyword === "" && isSearchOpen === 2) {
      navi("/main/search", { replace: false });
    }
  }, [isSearchOpen]);
  const { newsid } = useParams();
  const [upperReply, SetUpper] = useState("");
  const DTO = getCookie("dto");
  const [Article, SetArticle] = useState({
    bookmark: false,
    content: "",
    dislike: 0,
    imgs: [],
    like: 0,
    title: "",
    type: 0,
    views: 0,
  });
  const [replyList, setReplyList] = useState<Replies[]>([]);
  const [replyStates, setReplyStates] = useState<{ [key: string]: boolean }>(
    {}
  );
  const [lowerReplyStates, setLowerReplyStates] = useState<string[]>([]);
  // 댓글 추가 및 초기 상태 설정
  const addReply = (name: string) => {
    setReplyStates((prevStates) => ({
      ...prevStates,
      [name]: false, // 각 댓글의 초기 상태를 false로 설정
    }));
  };
  // 댓글 상태 토글
  const toggleReplyState = (name: any) => {
    setReplyStates((prevStates) => ({
      ...prevStates,
      [name]: !prevStates[name],
    }));
  };

  // 기사 하단 버튼 클릭 여부 (좋아요, 싫어요, 댓글, 공유, 북마크, 출처)
  const [isClicked, setClicked] = useState([
    false,
    false,
    false,
    false,
    false,
    false,
  ]);
  // 댓글 온오프 (0: 디폴트, 1: 온, 2: 오프)
  const [isReplyShow, setReplyShow] = useState(0);
  // 기사 상단 버튼 클릭 여부 (tts, 더보기)
  const [isTopClicked, setTopClicked] = useState([false, false]);
  // 댓글 정렬 버튼 클릭 여부 (최신순, 좋아요순)
  const [isReplySortClicked, setReplySortClicked] = useState([true, false]);

  // 댓글 숨김 div 사이즈를 각 댓글에 맞추는 코드
  const setBlindReplySize = () => {
    const replyDivs = document.querySelectorAll(".reply-div");
    replyDivs.forEach((replyDiv) => {
      const blindReplyDiv = replyDiv.previousElementSibling as HTMLElement;
      if (blindReplyDiv) {
        const rect = replyDiv.getBoundingClientRect();
        const replyDivHeight = rect.height;
        const replyDivWidth = rect.width;
        blindReplyDiv.style.height = `${replyDivHeight}px`;
        blindReplyDiv.style.width = `${replyDivWidth}px`;
      }
    });
  };

  // 테스트용
  useEffect(() => {
    setBlindReplySize();
  }, [replyList]);

  useEffect(() => {
    if (DTO) {
      selectNews(newsid!, navi, SetArticle, isClicked, setClicked);
      getReply(newsid!, setReplyList, 1);
    } else {
      window.location.replace("/shareNews/" + newsid);
    }
    return () => {
      setReplyList([]);
    };
  }, [newsid]);

  // 댓글 컨테이너 높이를 기사 컨테이너 높이와 같게 설정하는 코드
  const newsContainerRef = useRef<HTMLDivElement>(null);
  const [boxHeight, setBoxHeight] = useState<number | null>(null);
  useEffect(() => {
    expiration();

    const updateBoxHeight = () => {
      if (newsContainerRef.current) {
        setBoxHeight(newsContainerRef.current.clientHeight);
      }
    };
    const observer = new ResizeObserver(updateBoxHeight);
    if (newsContainerRef.current) {
      observer.observe(newsContainerRef.current);
      // 컴포넌트가 마운트될 때 초기 높이를 확인하고 설정
      updateBoxHeight();
    }
    return () => {
      observer.disconnect();
    };
  }, []);

  const Sorting = (value: boolean[]) => {
    getReply(newsid!, setReplyList, value[0] ? 1 : -1);
    setReplyStates({});
  };

  const [isModalOpen, setIsModalOpen] = useState(false);
  const openModal = () => {
    setTopClicked([false, false]);
    setIsModalOpen(true);
  };
  const closeModal = () => {
    setIsModalOpen(false);
  };
  const location = useLocation();
  const shareLink = location.pathname;
  const upperHandle = (e: any) => SetUpper(e.target.value);

  const [isOpen, setOpen] = useState([false, false, false]);
  const { date, selectedDate, setSelectedDate } = useDate();

  const navigationPrevRef = useRef(null);
  const navigationNextRef = useRef(null);

  // 이미지 슬라이더 세팅값
  SwiperCore.use([Navigation, Pagination]);

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
  let VIEW = (Article.views + 1).toString();
  let RES = "";
  let point = 0;
  if (VIEW.length > 3) {
    if (VIEW.length % 3 == 0) point = Math.floor(VIEW.length / 3) - 1;
    else point = Math.floor(VIEW.length / 3);
    RES = VIEW.substring(0, VIEW.length % 3).concat(",");
    for (
      let i = VIEW.length % 3, j = 0, cnt = point;
      cnt != 0 && i < VIEW.length;
      i++, j++
    ) {
      if (j > 0 && j % 3 == 0) {
        cnt--;
        RES += ",";
      }
      RES += VIEW[i];
    }
  } else {
    RES = VIEW;
  }
  return (
    <div>
      <div>
        <div className="header">
          <Header
            isOpen={isOpen}
            setOpen={setOpen}
            date={date}
            selectedDate={selectedDate}
            setSelectedDate={setSelectedDate}
          />
        </div>
        <SearchKeyword setOpen={setOpen} />
        <ScrollUp />
        <div className="news-page">
          <div
            ref={newsContainerRef}
            className={
              "news-container " +
              (isReplyShow == 1
                ? "reply-true-article"
                : isReplyShow == 2
                ? "reply-false-article"
                : "")
            }
          >
            <div>
              <div>
                <div className="news-container-top-row">
                  <div className="news-title">
                    {Article.title}
                    <br />
                    <sub>
                      {getDate(newsid!)} {newsid?.substring(8, 10)}:00
                    </sub>
                  </div>
                  {
                    <div className="news-audio">
                      <button
                        className="news-audio-btn"
                        type="button"
                        onClick={() => setTopClicked([!isTopClicked[0], false])}
                      >
                        <FontAwesomeIcon icon={faHeadphones} />
                        <>&nbsp;뉴스 듣기</>
                      </button>
                      {isTopClicked[0] && <TTSPlay />}
                    </div>
                  }
                  <div>
                    <button
                      className="three-dots"
                      type="button"
                      onClick={() => setTopClicked([false, !isTopClicked[1]])}
                    >
                      <FontAwesomeIcon icon={faEllipsisVertical} />
                    </button>
                    {isTopClicked[1] && (
                      <div className="news-report">
                        <button
                          type="button"
                          onClick={openModal}
                          className="del"
                        >
                          신고하기
                        </button>
                      </div>
                    )}
                    <ReportModal
                      isOpen={isModalOpen}
                      onRequestClose={closeModal}
                      news_id={newsid}
                    />
                  </div>
                </div>
              </div>
            </div>
            <div
              className="news-container-bottom-row"
              onClick={() => setTopClicked([false, false])}
            >
              <div className="news-container-article-row">
                <div>{Article.content}</div>
                <Swiper
                  {...settings}
                  allowSlideNext={Article.imgs.length === 1 ? false : true}
                  allowSlidePrev={Article.imgs.length === 1 ? false : true}
                >
                  {Article.imgs ? (
                    Article.imgs.map((value, index) => {
                      return (
                        <SwiperSlide key={index}>
                          <img src={value} />
                        </SwiperSlide>
                      );
                    })
                  ) : (
                    <img
                      src={process.env.REACT_APP_S3_SHORT + "news-default.svg"}
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
              <div className="news-container-view-row">
                <div>{RES} views</div>
              </div>
              <div className="news-container-icon-row">
                <div className="news-container-icon-row-block">
                  <button
                    className="news-container-btn"
                    onClick={() => {
                      let copy = [...isClicked];
                      copy[0] = !copy[0];
                      if (copy[1])
                        Article.dislike =
                          Article.dislike - 1 >= 0 ? Article.dislike - 1 : 0;
                      copy[1] = false;
                      setClicked(copy);
                      updateLike(copy, navi, newsid!);
                      if (copy[0]) Article.like++;
                      else
                        Article.like =
                          Article.like - 1 >= 0 ? Article.like - 1 : 0;
                    }}
                  >
                    {/**좋아요 버튼 */}
                    <FontAwesomeIcon
                      icon={faThumbsUp}
                      style={
                        isClicked[0] ? { color: "#F16868" } : { color: "#000" }
                      }
                    />
                    <span>{Article.like > 999 ? 999 : Article.like}</span>
                  </button>
                  <button
                    className="news-container-btn"
                    onClick={() => {
                      let copy = [...isClicked];
                      if (copy[0])
                        Article.like =
                          Article.like - 1 >= 0 ? Article.like - 1 : 0;
                      copy[0] = false;

                      copy[1] = !copy[1];
                      setClicked(copy);
                      updateLike(copy, navi, newsid!);
                      if (copy[1]) Article.dislike++;
                      else
                        Article.dislike =
                          Article.dislike - 1 >= 0 ? Article.dislike - 1 : 0;
                    }}
                  >
                    {/**싫어요 버튼 */}
                    <FontAwesomeIcon
                      icon={faThumbsDown}
                      style={
                        isClicked[1] ? { color: "#669AF3" } : { color: "#000" }
                      }
                    />
                    <span>{Article.dislike > 999 ? 999 : Article.dislike}</span>
                  </button>
                  <button
                    className="icon"
                    onClick={() => {
                      window.scrollTo(0, 0);
                      isReplyShow == 1 ? setReplyShow(2) : setReplyShow(1);
                      isReplyShow % 2 == 0 &&
                        getReply(newsid!, setReplyList, 1);
                    }}
                  >
                    <FontAwesomeIcon icon={faCommentDots} />
                  </button>{" "}
                  {/** 댓글 버튼 */}
                </div>
                <div className="news-container-icon-row-block">
                  <button
                    className="icon"
                    onClick={() => {
                      setClicked([
                        isClicked[0],
                        isClicked[1],
                        isClicked[2],
                        !isClicked[3],
                        isClicked[4],
                        false,
                      ]);
                    }}
                  >
                    <img
                      src={process.env.REACT_APP_S3_SHORT + "assets/share.svg"}
                      alt=""
                    ></img>
                  </button>
                  <button
                    className="icon bookmark-icon"
                    onClick={() => {
                      let copy = [...isClicked];
                      copy[4] = !copy[4];
                      setClicked(() => copy);
                      updateBookMark(newsid, copy[4], navi);
                    }}
                  >
                    <img
                      src={
                        isClicked[4] == true
                          ? process.env.REACT_APP_S3_SHORT +
                            "assets/bookmarkColor.svg"
                          : process.env.REACT_APP_S3_SHORT +
                            "assets/bookmark.svg"
                      }
                      alt=""
                    ></img>
                  </button>
                  <button
                    className={
                      "news-container-btn news-container-copyright" +
                      (isClicked[5] == true
                        ? "news-container-copyright-open"
                        : "news-container-copyright-close")
                    }
                    onClick={() => {
                      setClicked([
                        isClicked[0],
                        isClicked[1],
                        isClicked[2],
                        false,
                        isClicked[4],
                        !isClicked[5],
                      ]);
                    }}
                  >
                    <span>출처</span>
                    {isClicked[5] ? (
                      <FontAwesomeIcon icon={faCaretUp} />
                    ) : (
                      <FontAwesomeIcon icon={faCaretDown} />
                    )}
                  </button>
                </div>
              </div>
              {isClicked[3] && (
                <div className="news-share-zone">
                  <div className="news-share-btn-zone">
                    <button
                      type="button"
                      id="kakaotalk-sharing-btn"
                      onClick={() => shareKakao(Article, newsid)}
                    >
                      <img
                        src="https://developers.kakao.com/assets/img/about/logos/kakaotalksharing/kakaotalk_sharing_btn_medium.png"
                        alt="카카오톡 공유 보내기 버튼"
                      />
                    </button>
                    <button
                      type="button"
                      onClick={() => shareNaverBlog(Article, newsid)}
                    >
                      <img
                        src={
                          process.env.REACT_APP_S3_SHORT +
                          "assets/naver_logo.svg"
                        }
                      />
                    </button>
                    <button
                      type="button"
                      onClick={() => shareFacebook(Article, newsid)}
                    >
                      <img
                        src={
                          process.env.REACT_APP_S3_SHORT + "assets/facebook.svg"
                        }
                      />
                    </button>
                  </div>
                  {/* <input
                    type="text"
                    value={shareLink}
                    readOnly
                    className="news-share-input"
                  />
                  <input
                    type="text"
                    value={"http://www.shortnews.kr/selectNews/" + newsid}
                    id="123"
                    hidden
                  />
                  <button
                    type="button"
                    className="news-share-copy"
                    onClick={() => CopyUrl(shareLink)}
                  >
                    URL 복사
                  </button> */}
                </div>
              )}
              {isClicked[5] && <Resources news_id={newsid} />}
            </div>
          </div>
        </div>
        <div
          className={
            "reply-container " +
            (isReplyShow == 1
              ? "reply-true-reply"
              : isReplyShow == 2
              ? "reply-false-reply"
              : "")
          }
          style={{ height: boxHeight! }}
        >
          <div className="reply-container-top-row">
            댓글 ({replyList.length})
            <span
              onClick={() => {
                isReplyShow == 1 ? setReplyShow(2) : setReplyShow(1);
                isReplyShow % 2 == 0 && getReply(newsid!, setReplyList, 1);
              }}
            >
              닫기
            </span>
          </div>
          <div className="reply-container-write">
            <div>
              <input
                placeholder="댓글을 작성해주세요"
                max={300}
                onKeyDown={(e) => {
                  if (e.key === "Enter") {
                    createReply(
                      setReplyList,
                      navi,
                      newsid!,
                      upperReply,
                      isReplySortClicked[0] ? 1 : -1
                    );
                    SetUpper("");
                  }
                }}
                onChange={upperHandle}
                value={upperReply}
              ></input>
              <button
                type="button"
                onClick={() => {
                  if (upperReply.length === 0) {
                    alert("댓글을 입력해주세요");
                  } else {
                    createReply(
                      setReplyList,
                      navi,
                      newsid!,
                      upperReply,
                      isReplySortClicked[0] ? 1 : -1
                    );
                    SetUpper("");
                  }
                }}
              >
                등록
              </button>
            </div>
          </div>
          <div className="sort-btn reply-sort-btn">
            <button
              onClick={() => {
                setReplySortClicked([true, false]);
                Sorting([true, false]);
              }}
              className={!isReplySortClicked[0] ? "reply-sort-off" : ""}
            >
              최신순
            </button>
            <span>|</span>
            <button
              onClick={() => {
                setReplySortClicked([false, true]);
                Sorting([false, true]);
              }}
              className={!isReplySortClicked[1] ? "reply-sort-off" : ""}
            >
              좋아요순
            </button>
          </div>
          <div className="reply-zone">
            {replyList.length == 0 && (
              <div className="reply-zero">
                <div>
                  <div className="reply-zero-icon">
                    <FontAwesomeIcon
                      icon={faComments}
                      style={{ color: "#575757" }}
                    />
                  </div>
                  댓글이 없습니다. <br /> 첫번째 댓글을 남겨보세요.
                </div>
              </div>
            )}
            {
              // 댓글 출력
              replyList.map((value, index) => (
                <div key={value.reply_id}>
                  <div key={value.reply_id} className="reply-div">
                    <Reply
                      status={isReplySortClicked[0] ? 1 : -1}
                      low={true}
                      data={value}
                      isExpanded={replyStates[value.reply_id]}
                      onToggle={() => toggleReplyState(value.reply_id)}
                      setReplyList={setReplyList}
                    />
                    {replyStates[value.reply_id] && (
                      <div className="lower-reply-zone">
                        {
                          // 답글 출력
                          value.lower!.map((lowerReply, lowerIndex) => {
                            return (
                              <div
                                className="lower-reply"
                                key={lowerReply.reply_id}
                              >
                                <Reply
                                  low={false}
                                  data={lowerReply}
                                  setReplyList={setReplyList}
                                  status={isReplySortClicked[0] ? 1 : -1}
                                />
                              </div>
                            );
                          })
                        }
                        <div className="lower-reply-write">
                          <input
                            placeholder="댓글을 작성해주세요"
                            onKeyDown={(e) => {
                              if (
                                e.key === "Enter" ||
                                e.key === "NumpadEnter"
                              ) {
                                createReply(
                                  setReplyList,
                                  navi,
                                  newsid!,
                                  lowerReplyStates[index],
                                  isReplySortClicked[0] ? 1 : -1,
                                  value.reply_id,
                                  value.id
                                );
                                let updatedStates = [...lowerReplyStates];
                                updatedStates[index] = "";
                                setLowerReplyStates(updatedStates);
                              }
                            }}
                            value={lowerReplyStates[index] || ""}
                            onChange={(e) => {
                              const updatedStates = [...lowerReplyStates];
                              updatedStates[index] = e.target.value;
                              setLowerReplyStates(updatedStates);
                            }}
                          />
                          <button
                            type="button"
                            onClick={() => {
                              createReply(
                                setReplyList,
                                navi,
                                newsid!,
                                lowerReplyStates[index],
                                isReplySortClicked[0] ? 1 : -1,
                                value.reply_id,
                                value.id
                              );
                              let updatedStates = [...lowerReplyStates];
                              updatedStates[index] = "";
                              setLowerReplyStates(updatedStates);
                            }}
                          >
                            등록
                          </button>
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              ))
            }
          </div>
          {/** Reply zone */}
        </div>
      </div>
    </div>
  );
}

export default News;

const Resources = (props: any) => {
  const [resource, SetResource] = useState([]);
  useEffect(() => {
    getSource(props.news_id, SetResource);
  }, []);
  return (
    <div className="news-copyright-zone">
      {resource.map((value: string, idx: number) => {
        return (
          <Link to={value} target="_blank">
            {idx + 1}. {value}
          </Link>
        );
      })}
    </div>
  );
};
