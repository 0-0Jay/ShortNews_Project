import React, { ChangeEvent, useEffect, useRef } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMagnifyingGlass } from "@fortawesome/free-solid-svg-icons";
import { useSearchKeyword } from "../context/SearchKeywordContext";
import { useLocation, useNavigate } from "react-router-dom";
import SearchType from "./search-type";
const SearchKeyword = (props: any) => {
  const navigate = useNavigate();
  const Ref = useRef<any>();
  const {
    keyword,
    isSearchOpen,
    isSearchTypeOpen,
    searchType,
    updateKeyword,
    updateIsSearchOpen,
    updateIsSearchTypeOpen,
    updateSearchType,
  } = useSearchKeyword();
  let location = useLocation();
  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    let text = event.target.value;
    updateKeyword(text);
  };
  let cnt = 0;
  useEffect(() => {
    if (Ref.current !== undefined && isSearchOpen === 2) {
      Ref.current!.focus();
    }
  }, [isSearchOpen]);

  useEffect(() => {
    // 검색 유형을 선택하면 선택창이 꺼짐
    if (searchType != 0) {
      updateIsSearchTypeOpen(false);
    }
    // 검색 유형이 바뀌면 키워드 리셋
    // updateKeyword("");
  }, [searchType]);
  useEffect(() => {
    // 검색 인풋창이 열려있는데 검색유형 선택창을 열면 인풋창 닫음
    if (isSearchTypeOpen && isSearchOpen == 2) {
      updateIsSearchOpen(1);
    }
    // 검색창이 꺼지면 검색 유형을 초기화시킴
    if (!isSearchTypeOpen) {
      updateSearchType(0);
    }
  }, [isSearchTypeOpen]);
  return (
    <div>
      {isSearchTypeOpen && <SearchType />}
      <div className="search">
        <button
          className="toggle-button toggle-search"
          onClick={() => {
            // updateIsSearchTypeOpen(!isSearchTypeOpen);
            if (isSearchOpen === 2){
              updateIsSearchOpen(1);
            }
            else if (isSearchOpen <= 1)
              updateIsSearchOpen(2);
          }}
        >
          <FontAwesomeIcon icon={faMagnifyingGlass} size="2x" />
        </button>
        <input
          placeholder="검색어를 입력하세요"
          className={`search-input ${
            isSearchOpen === 2
              ? "search-input-animation"
              : isSearchOpen === 1
              ? "close-animation"
              : isSearchOpen === 0
              ? "close"
              : ""
          }`}
          value={keyword}
          onChange={handleInputChange}
          ref={Ref}
        ></input>
      </div>
    </div>
  );
};

export default React.memo(SearchKeyword);
