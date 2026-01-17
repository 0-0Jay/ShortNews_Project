import { useState, useEffect, SyntheticEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import NewsCard from "../components/news-card";
import { getDate, myActivityDislike, myActivityLike, myActivityReply } from "../API";
import Header from "../components/header";
import ScrollUp from "../components/scroll-up";
import SearchKeyword from "../components/searchKeyword";
import { useDate } from "../context/DateContext";
import { useSearchKeyword } from "../context/SearchKeywordContext";

interface CategoryState {
  [key: string]: boolean;
}

interface Log {
  title: string;
  link: string;
  type: string;
  date: number;
  reply?: string;
  upper_reply?: string;
}
interface newsSubject {
  news_id: string;
  title: string;
  bookmark: number;
  dislike: number;
  like: number;
  imgs?: string | null;
  reply: number;
  views: number;
  cate_id?: string;
}
const formatDate = (date: number): string => {
  const strDate = String(date);
  const year = strDate.substring(0, 4);
  const month = strDate.substring(4, 6);
  const day = strDate.substring(6);
  return `${year}-${month}-${day}`;
};

function MyLog() {
  const navigate = useNavigate();
  const { isSearchOpen } = useSearchKeyword();
  useEffect(() => {
    if (isSearchOpen === 2){
      navigate("/main/search")
    }
  }, [isSearchOpen])
  const { status } = useParams();
  const cate: string[] = ["좋아요", "싫어요", "댓글"];
  const dict = new Map<string, string>();
  dict.set("좋아요", "like");
  dict.set("싫어요", "dislike");
  dict.set("댓글", "reply");
  dict.set("like", "좋아요");
  dict.set("dislike", "싫어요");
  dict.set("reply", "댓글");
  const [categoryState, setCategoryState] = useState<CategoryState>({
    좋아요: true,
    싫어요: false,
    댓글: false,
  });

  const [sortOrder, setSortOrder] = useState<"desc" | "asc">("desc");

  const likeData: newsSubject[] = [];
  const [Like, SetLike] = useState([]);
  const [Dislike, SetDislike] = useState([]);
  const [Reply, SetReply] = useState([]);

  const handleCategoryClick = (category: string) => {
    navigate("/myActivity/" + dict.get(category));
    setCategoryState((prev) => ({
      ...Object.fromEntries(Object.entries(prev).map(([key]) => [key, false])),
      [dict.get(status!)!]: true,
    }));
  };

  const handleSortClick = (order: "desc" | "asc") => {
    if (sortOrder !== order) {
      Like.reverse();
      Dislike.reverse();
      Reply.reverse();
    }
    setSortOrder(order);
  };

  // container 높이 맞추는 코드
  const [containerHeight, setContainerHeight] = useState<number | null>(null);
  useEffect(() => {
    const mylogContainer =
      document.querySelector<HTMLElement>(".mylog-container");

    if (!mylogContainer) return;

    const adjustContainerHeight = () => {
      const windowHeight = window.innerHeight;
      const currentContainerHeight = mylogContainer.clientHeight;
      const minFixedHeight = 0.78 * windowHeight; // 원하는 최소 고정 높이

      if (currentContainerHeight < minFixedHeight) {
        setContainerHeight(minFixedHeight);
      }
    };
    setCategoryState((prev) => ({
      ...Object.fromEntries(Object.entries(prev).map(([key]) => [key, false])),
      [dict.get(status!)!]: true,
    }));
    // 최초 렌더링 시 한 번 실행
    adjustContainerHeight();
  }, [categoryState]);

  const basicProfile =
    process.env.REACT_APP_S3_SHORT + "assets/basicProfile.png";

  useEffect(() => {
    myActivityLike(SetLike);
    myActivityDislike(SetDislike);
    myActivityReply(SetReply);
    
  }, []);

  const changeDefault = (e: SyntheticEvent<HTMLImageElement, Event>) => {
    e.currentTarget.src = basicProfile;
  };
  const [isOpen, setOpen] = useState([false, false, false]);
  const { date, selectedDate, setSelectedDate, handleDateChange } = useDate();

  return (
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
      <div className="cate-btn-zone">
        <span>마이페이지</span>
        <div className="vertical-line"></div>
        <button style={{fontSize : "18px"}} type="button" onClick={() => navigate("/member")}>
          회원정보
        </button>
        <button
          type="button"
          className="cate-btn-zone-pick"
          onClick={() => navigate("/myActivity/" + status)}
        >
          나의 활동
        </button>
        {cate.map((category, index) => (
          <button
            key={index}
            className={`mylog-tab-btn${
              categoryState[category] ? " cate-btn-zone-pick" : ""
            }`}
            onClick={() => handleCategoryClick(category)}
          >
            {category}
          </button>
        ))}
      </div>
      <div
        className="mylog-container"
        style={likeData.length <= 3 ? { height: containerHeight! } : undefined}
      >
        <div className="sort-btn">
          <button
            type="button"
            onClick={() => handleSortClick("desc")}
            className={sortOrder == "desc" ? "" : "reply-sort-off"}
          >
            최신순
          </button>
          <span>|</span>
          <button
            type="button"
            onClick={() => handleSortClick("asc")}
            className={sortOrder == "asc" ? "" : "reply-sort-off"}
          >
            오래된 순
          </button>
        </div>
        <div className="mylog-zone news-zone ">
          {status === "like"
            ? Like.map((value, idx) => {
                if (idx % 3 === 0) {
                  const group = Like.slice(idx, idx + 3);
                  return (
                    <div className="news-row" key={idx}>
                      {group.map((value2, index) => (
                        <NewsCard key={idx + index} data={value2} />
                      ))}
                    </div>
                  );
                }
              })
            : status === "dislike"
            ? Dislike.map((value, idx) => {
                if (idx % 3 === 0) {
                  const group = Dislike.slice(idx, idx + 3);
                  return (
                    <div className="news-row" key={idx}>
                      {group.map((value2, index) => (
                        <NewsCard key={index + idx} data={value2} />
                      ))}
                    </div>
                  );
                }
              })
            : Reply.map((value: any, idx) => {
              console.log(value);
                return (
                  <div className="mylog-reply-container" key={idx} onClick={()=>navigate("/selectNews/" + value.news_id)}>
                    <div className="mylog-reply-content">
                      <div className="mylog-reply-title">{value.title}</div>
                      <div className="mylog-reply-reply">
                        <div className="mylog-reply-profile">
                          <img
                            src={
                              (process.env.REACT_APP_S3_USER as string) +
                              value.id
                            }
                            onError={changeDefault}
                          />
                        </div>
                        <table>
                          <tr>
                            <td className="mylog-reply-name">
                              {value.nickname}
                            </td>
                            <td className="mylog-reply-date">{getDate(value.reply_id)}</td>
                          </tr>
                          <tr>
                            <td>{value.content}</td>
                          </tr>
                        </table>
                      </div>
                      {value.low_content !== null && (
                        <div className="mylog-reply-reply rereply">
                          <div className="mylog-reply-profile">
                            <img
                              src={
                                (process.env.REACT_APP_S3_USER as string) +
                                value.low_uid
                              }
                              
                              onError={changeDefault}
                            />
                          </div>
                          <table>
                            <tr>
                              <td className="mylog-reply-name">
                                {value.low_nickname}
                              </td>
                              <td className="mylog-reply-date">{getDate(value.low_rid)}</td>
                            </tr>
                            <tr>
                              <td>{value.low_content}</td>
                            </tr>
                          </table>
                        </div>
                      )}
                    </div>
                    <div className="mylog-reply-img">
                      <img
                        src={
                          value.imgs !== null
                            ? value.imgs
                            : process.env.REACT_APP_S3_SHORT +
                              "news-default.svg"
                        }
                        style={{
                          objectFit: 'cover', // 'cover'로 설정하여 이미지를 잘라서 표시
                          objectPosition: 'top center'
                        }}
                      />
                    </div>
                  </div>
                );
              })}
        </div>
      </div>
    </div>
  );
}

export default MyLog;
