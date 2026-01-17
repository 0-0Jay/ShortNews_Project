import { useState, useEffect } from "react";
import NewsCard from "../components/news-card";
import { getCookie, removeCookieAll } from "../Cookies";
import { expiration, memberBookmark } from "../API";
import { useNavigate, useParams } from "react-router-dom";
import { useDate } from "../context/DateContext";
import Header from "../components/header";
import ScrollUp from "../components/scroll-up";
import SearchKeyword from "../components/searchKeyword";
import { useSearchKeyword } from "../context/SearchKeywordContext";

interface CategoryState {
  [key: string]: boolean;
}

function BookMark() {
  const { isSearchOpen } = useSearchKeyword();
  useEffect(() => {
    if (isSearchOpen === 2){
      navi("/main/search")
    }
  }, [isSearchOpen])
  let DTO = getCookie("dto");
  let navi = useNavigate();
  let { cateid } = useParams();
  const dict: Map<string, string> = new Map();
  dict.set("111", "전체");
  dict.set("100", "정치");
  dict.set("101", "경제");
  dict.set("102", "사회");
  dict.set("103", "생활/문화");
  dict.set("104", "세계");
  dict.set("105", "IT/과학");
  dict.set("106", "연예");
  dict.set("107", "스포츠");

  const [categoryState, setCategoryState] = useState<CategoryState>({
    "111": true,
    "정치": false,
    "경제": false,
    "사회": false,
    "IT/과학": false,
    "연예": false,
    "스포츠": false,
    "생활/문화": false,
    "세계": false,
  });

  const [list, setList] = useState([]);
  const handleCategoryClick = (category: string) => {
    navi("/BookMark/" + category);
    setCategoryState((prev) => ({
      ...Object.fromEntries(Object.entries(prev).map(([key]) => [key, false])),
      [category]: true,
    }));
  };

  const [sortOrder, setSortOrder] = useState<"desc" | "asc">("desc");
  const handleSortClick = (order: "desc" | "asc") => {
    let copy = [...list];
    if (sortOrder !== order) {
      copy.reverse();
    }
    setList(copy);
    setSortOrder(order);
  };
  // container 높이 맞추는 코드
  const [containerHeight, setContainerHeight] = useState<number | null>(null);
  useEffect(() => {
    if (DTO) {
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

      // 최초 렌더링 시 한 번 실행
      adjustContainerHeight();
    }
  }, [categoryState]);

  
  useEffect(() => {
    DTO && memberBookmark(setList);
  }, []);
  const [isOpen, setOpen] = useState([false, false, false]);
  const { date, selectedDate, setSelectedDate, handleDateChange } = useDate();
  expiration();
  return (
    DTO && (
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
          <span>나의 북마크</span>
          <div className="vertical-line"></div>
          <button
              className={categoryState["111"] ? " cate-btn-zone-pick" : ""}
              onClick={() => handleCategoryClick("111")}
            >
              {dict.get("111")}
          </button>
          {["100", "101", "102", "103", "104", "105", "106", "107"].map((category: any) => (
            <button
              key={category}
              className={categoryState[category] ? " cate-btn-zone-pick" : ""}
              onClick={() => handleCategoryClick(category)}
            >
              {dict.get(category)}
            </button>
          ))}
        </div>
        <div className="mypage-box">
          <div
            className="mylog-container"
            style={list.length <= 3 ? { height: containerHeight! } : undefined}
          >
            <div className="sort-btn">
              <button
                type="button"
                onClick={() => handleSortClick("desc")}
                className={sortOrder === "desc" ? "" : "reply-sort-off"}
              >
                최신순
              </button>
              <span>|</span>
              <button
                type="button"
                onClick={() => handleSortClick("asc")}
                className={sortOrder === "asc" ? "" : "reply-sort-off"}
              >
                오래된 순
              </button>
            </div>
            <div className="mylog-zone news-zone">
              {
                list.map(function (value, idx) {
                  if (idx % 3 === 0) {
                    let group: any[];
                    if (cateid !== '111'){
                      group = list
                      .filter((elem: any) => elem.cate_id === cateid)
                      .slice(idx, idx + 3);
                    }
                    else group = list.slice(idx, idx + 3);

                    return (
                      group.length !== 0 && (
                        <div className="news-row" key={idx}>
                          {group.map(
                            (newsItem: any, index) =>
                              (cateid === "111" ||
                                newsItem.cate_id === cateid) && (
                                <NewsCard key={index} data={newsItem} />
                              )
                          )}
                        </div>
                      )
                    );
                  }
                })
              }
            </div>
          </div>
        </div>
      </div>
    )
  );
}

export default BookMark;
