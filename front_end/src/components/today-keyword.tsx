import React, { useEffect, useState } from "react";
// npm install react-wordcloud
import ReactWordcloud, {
  Optional,
  Word,
  Callbacks,
  Options,
} from "react-wordcloud";
import { useSearchKeyword } from "../context/SearchKeywordContext";
import { useNavigate } from "react-router-dom";
import { getKeywords, getSearch } from "../API";
// npm i --save-dev @types/lodash
import _, { replace } from 'lodash';

export interface keywordDataProps {
  text: string;
  cate: string;
  value: number;
}

const categoryColors: Record<string, string> = {
  100: "#ff5ed2", // 정치
  101: "#ff4141", // 경제
  102: "#ffbf44", // 사회
  103: "#ffec41", // 생활/문화
  104: "#40dc4f", // 세계
  105: "#37fff3", // IT/과학
  106: "#3783f6", // 연예
  107: "#8f10f3", // 스포츠
};

const TodayKeyword = (props: any) => {
  const category = props.pickCate;
  const words:keywordDataProps[] = props.words;
  const navigate = useNavigate();
  const { updateKeyword, updateIsSearchOpen } = useSearchKeyword();

  // 워드클라우드 라이브러리 속성
  let filteredWords = words || []; // 기본적으로는 모든 단어를 포함

  if (category !== "recommend") {
    // "추천"이 아닌 경우에만 카테고리에 해당하는 단어들을 필터링
    filteredWords = words.filter((word) => word.cate == category);
  }else {
    words.sort((a, b) => b.value - a.value);
    filteredWords = words.slice(0, 30);
  }
  const wordsWithCategory = filteredWords.map((word) => ({
    ...word,
    attributes: { category: word.cate },
  }));

  const options: Optional<Options> = {
    rotations: 2,
    rotationAngles: [0, 0],
    fontFamily: "NanumSquareBold",
    fontSizes: [17, 50],
    enableTooltip: true,
    transitionDuration: 0,
  };

  const callbacks: Optional<Callbacks> = {
    getWordColor: (word: Word) => categoryColors[word.cate] || "#000000",
    getWordTooltip: () => {},
    onWordClick: (word: Word) => {
      updateIsSearchOpen(2);
      updateKeyword(word.text);
      navigate(`/main/search`, {replace : false});
    },

  };
  // 워드클라우드 라이브러리 속성 끝
  return (
    <div className="keywords-container">
      <div className="keywords-box">
        <ReactWordcloud
          callbacks={callbacks}
          words={wordsWithCategory}
          options={options}
          minSize={[200, 240]}
          size={[710, 240]}
        />
      </div>
      <div className="keywords-cate-container">
        <div className="keywords-cate-container-row">
          <div>
            <div
              className="keywords-cate-container-circle"
              style={{ backgroundColor: "#ff5ed2" }}
            ></div>
            <span>정치</span>
          </div>
          <div>
            <div
              className="keywords-cate-container-circle"
              style={{ backgroundColor: "#ff4141" }}
            ></div>
            <span>경제</span>
          </div>
          <div>
            <div
              className="keywords-cate-container-circle"
              style={{ backgroundColor: "#ffbf44" }}
            ></div>
            <span>사회</span>
          </div>
          <div>
            <div
              className="keywords-cate-container-circle"
              style={{ backgroundColor: "#ffec41" }}
            ></div>
            <span>생활/문화</span>
          </div>
        </div>
        <div className="keywords-cate-container-row">
          <div>
            <div
              className="keywords-cate-container-circle"
              style={{ backgroundColor: "#40dc4f" }}
            ></div>
            <span>세계</span>
          </div>
          <div>
            <div
              className="keywords-cate-container-circle"
              style={{ backgroundColor: "#37fff3" }}
            ></div>
            <span>IT/과학</span>
          </div>
          <div>
            <div
              className="keywords-cate-container-circle"
              style={{ backgroundColor: "#3783f6" }}
            ></div>
            <span>연예</span>
          </div>
          <div>
            <div
              className="keywords-cate-container-circle"
              style={{ backgroundColor: "#8f10f3" }}
            ></div>
            <span>스포츠</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default React.memo(TodayKeyword);
