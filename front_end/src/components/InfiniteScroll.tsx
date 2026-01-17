import { useState, useCallback, useEffect } from "react";
import NewsCard from "./news-card";
import { useDate } from "../context/DateContext";
import Header from "../components/header";
import ScrollUp from "../components/scroll-up";
import SearchKeyword from "../components/searchKeyword";

const InfiniteScroll = () => {
  const [index, setIndex] = useState(0);
  const [post, setPost] = useState(getPostList(index));

  const handleScroll = useCallback(() => {
    const { innerHeight } = window;
    const { scrollHeight, scrollTop } = document.documentElement;

    if (Math.round(scrollTop + innerHeight) >= scrollHeight) {
      setIndex((prevIndex) => prevIndex + 9);
    }
  }, []);

  useEffect(() => {
    const handleScrollEvent = () => {
      handleScroll();
    };

    window.addEventListener("scroll", handleScrollEvent, true);

    return () => {
      window.removeEventListener("scroll", handleScrollEvent, true);
    };
  }, [handleScroll]);

  useEffect(() => {
    if (index > 2) {
      setPost((prevPost) => prevPost.concat(getPostList(index)));
    }
  }, [index]);

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
      <SearchKeyword />
      <ScrollUp />
      <div className="cate-btn-zone">
        <button>추천</button>
      </div>
      <div className="main-body-container">
        <div className="date-zone">
          <span>ss</span>
        </div>
        <div className="news-zone">
          {post.map(function (value: any, idx) {
            if (idx % 3 === 0) {
              const group = post.slice(idx, idx + 3);
              return (
                <div className="news-row" key={idx}>
                  {group.map((newsItem: any, index) => (
                    <NewsCard key={index} data={newsItem} />
                  ))}
                </div>
              );
            }
            return null;
          })}
        </div>
      </div>
    </div>
  );
};

export default InfiniteScroll;

export const getPostList = (index: any) => {
  return postList.slice(index, index + 9);
};

const postList = [
  {
    news_id: "202401311",
    title: "test1",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 1,
  },
  {
    news_id: "202401312",
    title: "test2",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 1,
  },
  {
    news_id: "202401313",
    title: "test3",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 1,
  },
  {
    news_id: "202401314",
    title: "test4",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 1,
  },
  {
    news_id: "202401315",
    title: "test5",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 1,
  },
  {
    news_id: "202401316",
    title: "test6",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 1,
  },
  {
    news_id: "202401317",
    title: "test7",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 1,
  },
  {
    news_id: "202401318",
    title: "test8",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "202401319",
    title: "test9",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "2024013110",
    title: "test10",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "202401314",
    title: "test11",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "202401315",
    title: "test12",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "202401316",
    title: "test13",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "202401317",
    title: "test14",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "202401317",
    title: "test15",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "202401317",
    title: "test16",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "202401317",
    title: "test17",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
  {
    news_id: "202401317",
    title: "test18",
    bookmark: false,
    dislike: 3,
    like: 6,
    reply: 4,
    views: 0,
    cate_id: "101",
    page: 2,
  },
];
