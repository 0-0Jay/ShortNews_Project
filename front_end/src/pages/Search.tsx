import { useEffect, useState } from "react";
import { faFaceSadTear } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import NewsCard from "../components/news-card";
import { useSearchKeyword } from "../context/SearchKeywordContext";
import { getSearch } from "../API";
import { Link } from "react-router-dom";
import { getCookie } from "../Cookies";
import Header from "../components/header";
import SearchKeyword from "../components/searchKeyword";
import ScrollUp from "../components/scroll-up";
import Spin from "../components/lodding";
const Search = (props: any) => {
  const { keyword, updateKeyword, updateIsSearchTypeOpen, updateIsSearchOpen } = useSearchKeyword();
  const [isOpen, setOpen] = useState([false, false, false]);
  let mainBody;
  let mainBodyHeight;
  let [news, SetNews] = useState([]);
  let [page, SetPage] = useState(1);
  let [more, SetMore] = useState(false);
  const setMainBodyHeight = () => {
    mainBody = document.querySelector(
      ".main-body-container"
    ) as HTMLElement | null;
    if (mainBody) {
      mainBodyHeight = mainBody.getBoundingClientRect().height;
      if (news.length <= 6) {
        mainBody.style.height = `calc(100vh - 72px)`;
      } else {
        mainBody.style.height = "";
      }
    }
  };
  let DTO = getCookie("dto");
  let CateList: string[];
  if (DTO) CateList = ["recommend", ...DTO.category];
  else CateList = ["recommend"];
  
  const dict: Map<string, string> = new Map();
  dict.set("100", "정치");
  dict.set("101", "경제");
  dict.set("102", "사회");
  dict.set("103", "생활/문화");
  dict.set("104", "세계");
  dict.set("105", "IT/과학");
  dict.set("106", "연예");
  dict.set("107", "스포츠");
  dict.set("recommend", "추천");
  const seeMore = () => {
    SetPage(p => p + 10)
    getSearch(keyword, SetNews, page + 10, page + 19, SetMore);
    setMainBodyHeight()
  }

  useEffect(() => {
    setMainBodyHeight();
  }, [props]);

  useEffect(()=>{
    SetNews([])
  },[])

  useEffect(()=>{
    SetPage(1);
    SetMore(false);
    getSearch(keyword, SetNews, 1, 9, SetMore);
    setMainBodyHeight()
  }, [keyword])

  return (
    <div className="main-container">
      <div className="header">
          <Header
            isOpen={isOpen}
            setOpen={setOpen}
          />
        </div>
        <SearchKeyword setOpen={setOpen} />
        <ScrollUp />
      <div className="cate-btn-zone">
        {CateList.map((value: string, idx: any) => {
          return (
            <button key={idx}>
              <Link
                key={idx}
                onClick={() => {
                  updateIsSearchTypeOpen(false);
                  updateKeyword("");
                  updateIsSearchOpen(0);
                }}
                to={`/main/${value}`}
                className=""
              >
                {dict.get(value)}
              </Link>
            </button>
          );
        })}
      </div>
      <div className="main-body-container">
        <div className="search-count-zone">
          <span className="search-keyword">{keyword}</span>
          {/* <span>관련 뉴스 검색 결과 {news.length}건입니다.</span> */}
          <span>관련 뉴스 검색 결과입니다.</span>
        </div>
        <div className="news-zone search-news-zone">
          {news.length === 0 ? (
            <div className="search-none">
              <FontAwesomeIcon
                icon={faFaceSadTear}
                style={{ color: "#949494" }}
              />
            </div>
          ) : null}
          <div className="news-zone">

            {news.map(function (value: any, idx: any) {
              if (idx % 3 === 0) {
                const group = news.slice(idx, idx + 3);
                return (
                  <div className="news-row" key={idx}>
                    {group.map((newsItem: any, index: any) => (
                      <NewsCard
                        key={index}
                        data={newsItem}
                        pickCate={props.pickCate}
                      />
                    ))}
                  </div>
                );
              }
              return null;
            })}
          </div>
          {
            more && <div className="seeMore-Container"><button className="seemore-btn" onClick = {seeMore}>더보기</button></div>
          }
        </div>

      </div>

    </div>
    
  );
};
export default Search;
