import React from "react";
import { useSearchKeyword } from "../context/SearchKeywordContext";

const SearchType = () => {
  const { searchType, isSearchTypeOpen, updateSearchType, updateIsSearchOpen } =
    useSearchKeyword();
  return (
    <div
      className={
        "search-type-container " +
        (isSearchTypeOpen
          ? "search-type-open-animation"
          : "search-type-close-animation")
      }
    >
      <p>검색어 유형을 선택하세요.</p>
      <div>
        <button
          type="button"
          className={
            searchType === 1
              ? "search-type-btn search-type-pick"
              : "search-type-btn"
          }
          onClick={() => {
            updateSearchType(1);
            updateIsSearchOpen(2);
          }}
        >
          기본 검색
        </button>
        <button
          type="button"
          disabled
          className={
            searchType === 2
              ? "search-type-btn search-type-pick"
              : "search-type-btn"
          }
          onClick={() => {
            updateSearchType(2);
            updateIsSearchOpen(2);
          }}
        >
          실시간 검색
        </button>
      </div>
      <div className="search-type-container-text">
        <p>기본 검색</p>
        <p>
          요약되어 있는 기사 중 제목과 내용에 검색어를 포함한 기사를 검색합니다.
        </p>
      </div>
      <div className="search-type-container-text">
        <p>실시간 검색</p>
        <p>
          실시간으로 제목과 내용에 검색어를 포함한 기사를 찾아 요약합니다.
          <br />
          실시간 검색으로 요약된 기사는 기록이 남지 않습니다.
        </p>
      </div>
    </div>
  );
};

export default SearchType;
