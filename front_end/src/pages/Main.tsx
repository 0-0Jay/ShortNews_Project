import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { expiration, getKeywords, getNews, getRecommend, getSearch } from "../API";
import { getCookie } from "../Cookies";
import NewsCard from "../components/news-card";
import Calender from "../components/calender";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faAngleRight, faAngleLeft } from "@fortawesome/free-solid-svg-icons";
import { useSearchKeyword } from "../context/SearchKeywordContext";
import { useDate } from "../context/DateContext";
import TodayKeyword, { keywordDataProps } from "../components/today-keyword";
import { faCalendar } from "@fortawesome/free-regular-svg-icons";
import Header from "../components/header";
import ScrollUp from "../components/scroll-up";
import SearchKeyword from "../components/searchKeyword";
import Spin from "../components/lodding";
import Info from "../assets/information.svg";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
function Main() {
  const FROM = window.sessionStorage.getItem("path");
  let navi = useNavigate();
  const { date,  setSelectedDate, handleDateChange } = useDate();
  const { keyword, updateIsSearchOpen, isSearchOpen } = useSearchKeyword();
  useEffect(() => {
    if (isSearchOpen === 2){
      navi("/main/search", {replace : false})
    }
  }, [isSearchOpen])
  let [news, SetNews] = useState([]);
  let [SHOW, SetShow] = useState<any[]>([]);

  let DTO = getCookie("dto");
  const [spin, SetSpin] = useState(false);
  let CateList: string[];
  if (DTO) CateList = ["recommend", ...DTO.category];
  else CateList = ["recommend"];
  let { category } = useParams();
  // 날짜
  const getToday = (date: Date) => {
    var TDYear = `${date.getFullYear()}`;
    var TDMonth =
      (date.getMonth() + 1) / 10 < 1
        ? `0${date.getMonth() + 1}`
        : `${date.getMonth() + 1}`;
    var TDDay = `${
      date.getDate() / 10 < 1 ? "0" + date.getDate().toString() : date.getDate()
    }`;
    return TDYear + TDMonth + TDDay;
  };
  const getYear = (date: Date) => {
    return `${date.getFullYear()}`;
  };
  const getMonth = (date: Date) => {
    var TDMonth =
      (date.getMonth() + 1) / 10 < 1
        ? `0${date.getMonth() + 1}`
        : `${date.getMonth() + 1}`;
    return TDMonth;
  };
  const getDay = (date: Date) => {
    var TDDay = `${
      date.getDate() / 10 < 1 ? "0" + date.getDate().toString() : date.getDate()
    }`;
    return TDDay;
  };
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const dateChangeYesterday = () => {
    const yesterday = new Date(date);
    yesterday.setDate(date.getDate() - 1);
    setSelectedDate(yesterday);
    console.log(getToday(yesterday) + " " + yesterday);
    getNews(SetNews, category!, getToday(yesterday), navi, SetSpin);
  };

  const dateChangeTommorw = () => {
    const tomorrow = new Date(date);
    tomorrow.setDate(date.getDate() + 1);
    setSelectedDate(tomorrow);
    console.log(getToday(tomorrow) + " " + tomorrow);
    getNews(SetNews, category!, getToday(tomorrow), navi, SetSpin);
  };

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

  const getDateNews = (date: any) => {
    if (category !== "recommend" && dict.has(category!)) {
      getNews(SetNews, category!, getToday(date), navi, SetSpin);
    }
  };

  

  const [isOpen, setOpen] = useState([false, false, false]);
  const [words, SetKeywords] = useState<keywordDataProps[]>([]);

  function formatDateToYYYYMMDD(date:Date, status? :boolean) {
    var year = date.getFullYear();
    var month = padZero(date.getMonth() + 1); // 월은 0부터 시작하므로 1을 더해줍니다.
    var day = padZero(date.getDate());

    if (status) return `${year}년 ${month}월 ${day}일`;
    return year + month + day;
  }
  
  function padZero(number:number) {
    return (number < 10 ? '0' : '') + number;
  }

  
  const seeMore = () => {
    let value = [...news.slice(SHOW.length, SHOW.length + 6)]
      if (value && news.length != SHOW.length){
        console.log(value)
        SetShow(prev => [...prev, ...value])
      }
  }
  useEffect(()=>{
    expiration();
    let keys = Array.from(dict.keys());
    let flag = false;
    for(let k of keys){
      if (k == category){
        flag = true
      }
    }
    if (category !== "search" && !flag) {
      navi("/pageNotFound", {replace : true})
    }

    if (FROM){
      navi("/selectNews/" + FROM);
      window.sessionStorage.clear();
    }
  }, [])
  useEffect(()=>{
    SetKeywords([]);
    getKeywords(SetKeywords, formatDateToYYYYMMDD(date));
  },[date]);

  useEffect(()=>{
    SetShow(news.slice(0, 9))
  },[news]);

  useEffect(() => {
    window.scrollTo(0, 0);
    SetSpin(true);
    if (category === "recommend") {
      updateIsSearchOpen(0);
      // updateKeyword("");
      getRecommend(SetNews, navi, SetSpin);
    } else if (dict.has(category!)) {
      updateIsSearchOpen(0);
      // updateKeyword("");
      getNews(SetNews, category!, getToday(date), navi, SetSpin);
    }

    return () => {
      SetNews([])
      if (window.location.pathname !== "/main/search"){
          // updateIsSearchOpen(0);
          // updateKeyword("");
      }
      
    }
  }, [category]);

  return (
    DTO && (
      <div>
        <div className="header">
          <Header
            isOpen={isOpen}
            setOpen={setOpen}
          />
        </div>
        <SearchKeyword setOpen={setOpen} />
        <ScrollUp />
        <div className="main-container">
          <div className="cate-btn-zone">
            {CateList.map((value: string, idx: any) => {
              return (
                <button key={idx}>
                  <Link
                    key={idx}
                    to={`/main/${value}`}
                    className={category === value ? "cate-btn-zone-pick" : ""}
                  >
                    {dict.get(value)}
                  </Link>
                </button>
              );
            })}
            {category !== "recommend" && (
              <button
                type="button"
                className="calender-btn"
                onClick={() => setIsCalendarOpen(!isCalendarOpen)}
              >
                <FontAwesomeIcon icon={faCalendar} />
              </button>
            )}
          </div>
          {isCalendarOpen && (
            <Calender
              onDateChange={handleDateChange}
              selected={date}
              getNews={getDateNews}
            />
          )}
          {category !== "search" ? (
            <div className="main-body-container">
              <div>
                {category !== "recommend" ? (
                  <div className="date-zone">
                    <button onClick={dateChangeYesterday}>
                      <FontAwesomeIcon icon={faAngleLeft} />
                    </button>
                    <span>
                      {getYear(date)}.{getMonth(date)}.{getDay(date)}
                    </span>
                    {date.toDateString() !== new Date().toDateString() ? (
                      <button onClick={dateChangeTommorw}>
                        <FontAwesomeIcon icon={faAngleRight} />
                      </button>
                    ) : null}
                  </div>
                ) : (
                  <div className="date-zone">
                    <span>
                      {getYear(date)}.{getMonth(date)}.{getDay(date)}
                    </span>
                  </div>
                )}
                <div className="keywords-zone">
                  <table>
                    <tbody>
                      <tr>
                        <td id="today-keywords">
                        {
                          parseInt(formatDateToYYYYMMDD(new Date())) > parseInt(formatDateToYYYYMMDD(date))
                            ? `${formatDateToYYYYMMDD(date, true)} 인기 키워드`
                            : "오늘의 키워드"
                        }   <TriggerExample /> 
                        </td>
                      </tr>
                      <tr>
                        <td>
                          <TodayKeyword pickCate={category} words= {words} />
                        </td>
                      </tr>
                    </tbody>
                    <tfoot></tfoot>
                  </table>
                </div>
                <div className="news-zone">
                  {spin && <Spin></Spin>}
                  {SHOW.map(function (value: any, idx) {
                    if (idx % 3 === 0) {
                      const group = news.slice(idx, idx + 3);
                      return (
                        <div className="news-row" key={idx}>
                          {group.map((newsItem: any, index) => (
                            <NewsCard
                              key={index}
                              data={newsItem}
                              pickCate={category}
                            />
                          ))}
                        </div>
                      );
                    }
                    return null;
                  })}
                  {
                      SHOW.length !== news.length && 
                      <div className="seeMore-Container"><button className="seemore-btn" onClick = {seeMore}>더보기</button></div>
                  }
                </div>
              </div>
            </div>
          ) : null}
        </div>
      </div>
    )
  );
}

export default Main;


function TriggerExample() {
  const renderTooltip = (props:any) => (
    <Tooltip id="button-tooltip" {...props} className="Tooltip">
      키워드의 크기가 클수록<br/> <span >인기있는 키워드</span>입니다.
    </Tooltip>
  );

  return (
    <OverlayTrigger
      placement="top"
      delay={{ show: 10, hide: 400 }}
      overlay={renderTooltip}
    >
        <img 
          src={Info} 
          alt="키워드 정보" 
          width={23} 
          height={23} 
          title=""
          />  
    </OverlayTrigger>
  );
}
