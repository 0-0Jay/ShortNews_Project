import React, { SyntheticEvent, useEffect, useState } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faThumbsUp, faThumbsDown } from "@fortawesome/free-regular-svg-icons";
import { faEllipsisVertical } from "@fortawesome/free-solid-svg-icons";
import ReportModal from "./report-modal";
import { getCookie } from "../Cookies";
import {  deleteReply, expiration, updateLike, updateReply } from "../API";
import { useNavigate } from "react-router-dom";

export interface Replies {
  reply_id: string;
  id: string;
  content: string;
  like: number;
  hate: number;
  edited: number;
  news_id: string;
  nickname: string;
  report : number;
  type?: number;
  lower?: Replies[];
}

interface Body {
  data: Replies;
  setReplyList: any;
  low: boolean;
  isExpanded?: boolean;
  status: number;
  onToggle?: () => void;
}

const Reply: React.FC<Body> = ({
  data,
  setReplyList,
  isExpanded,
  onToggle,
  low,
  status,
}) => {
  const [isReplyTopClicked, setReplyTopClicked] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const openModal = () => {
    setReplyTopClicked(false);
    setIsModalOpen(true);
  };
  const closeModal = () => {
    setIsModalOpen(false);
  };

  const [isClicked, setIsClicked] = useState([false, false]);
  let year = data.reply_id.substring(0, 4);
  let month = data.reply_id.substring(4, 6);
  let day = data.reply_id.substring(6, 8);
  let hour = data.reply_id.substring(8, 10);
  let min = data.reply_id.substring(10, 12);
  let navigator = useNavigate();
  const DTO = getCookie("dto");

  useEffect(() => {
    if (DTO) {
      setIsClicked([data.type === 1, data.type === -1]);
    }
  }, [data]);

  const basicProfile = process.env.REACT_APP_S3_USER + "default";
  const changeDefault = (e: SyntheticEvent<HTMLImageElement, Event>) => {
    e.currentTarget.src = basicProfile;
  };

  const [isUpdateClicked, setUpdateClicked] = useState(false);
  const [isUpdateReply, setUpdateReply] = useState(data.content);
  const updateHandle = (e: any) => setUpdateReply(e.target.value);
  const navi = useNavigate();
  
  expiration();

  // 댓글 숨김 div 사이즈를 각 댓글에 맞추는 코드
  const setBlindReplySize = () => {
    const replyDivs = document.querySelectorAll(".reply");
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
  }, []);

  const [isArr, setIsArr] = useState<boolean>();
  const handleBlindButtonClick = () => {
    setIsArr(true);
  };

  return (
    <div>
      {
        data.report >= 1 &&
        <div
          className={!isArr ? "blind-reply" : "blind-reply off-blind-animation"}
        >
          <div>
            <p className="blind-news-notice blind-reply-notice">
              <img src="https://snewsimgs.s3.ap-northeast-2.amazonaws.com/notice.svg" alt="notice" /> 숨겨진 댓글입니다.
            </p>
            <p className="blind-reply-text">
              다수의 신고가 접수되어 일시적으로 숨겨진 댓글입니다.
              <br />
              아래 버튼을 클릭하면 댓글을 확인할 수 있습니다.
            </p>
            <button
              type="button"
              className="blind-news-btn"
              onClick={() => handleBlindButtonClick()}
            >
              <p>댓글 확인하기</p>
            </button>
          </div>
        </div>
      }

      <div className="reply">
        <div className="reply-top">
          <div className="reply-profile">
            <img
              src={(process.env.REACT_APP_S3_USER as string) + data?.id}
              onError={changeDefault}
              alt="userProfile"
            />
            <div className="reply-profile-name">
              <p className="reply-nickname">{data.nickname}</p>
              <p className="reply-time">
                {year}.{month}.{day} {hour}:{min}
              </p>
            </div>
          </div>
          <div className="reply-btn">
            <button
              className="reply-like reply-cmt"
              onClick={() => {
                let copy = [...isClicked];
                if (copy[1]) data.hate--;
                copy[0] = !copy[0];
                copy[1] = false;
                setIsClicked([copy[0], false]);
                updateLike(copy, navigator, data.news_id, data.reply_id);
                if (copy[0]) data.like++;
                else data.like = data.like - 1 >= 0 ? data.like - 1 : 0;
              }}
            >
              <FontAwesomeIcon
                icon={faThumbsUp}
                style={isClicked[0] ? { color: "#F16868" } : { color: "#000" }}
              />
              <span>{data.like > 999 ? 999 : data.like}</span>
            </button>{" "}
            {/** 좋아요 버튼 */}
            <button
              className="reply-like reply-cmt"
              onClick={() => {
                let copy = [...isClicked];
                if (copy[0]) data.like = data.like - 1 >= 0 ? data.like - 1 : 0;
                copy[0] = false;
                copy[1] = !copy[1];
                setIsClicked([false, copy[1]]);
                updateLike(copy, navigator, data.news_id, data.reply_id);
                if (copy[1]) data.hate++;
                else data.hate = data.hate - 1 >= 0 ? data.hate - 1 : 0;
              }}
            >
              <FontAwesomeIcon
                icon={faThumbsDown}
                style={isClicked[1] ? { color: "#669AF3" } : { color: "#000" }}
              />
              <span>{data.hate > 999 ? 999 : data.hate}</span>
            </button>
            {/** 싫어요 버튼 */}
            {
              <button
                className="reply-three-dots"
                onClick={() =>
                  setReplyTopClicked(!isReplyTopClicked)
                }
              >
                <FontAwesomeIcon icon={faEllipsisVertical} />
              </button>
            }
            {isReplyTopClicked && (
              <div className="reply-report">
                <div>
                  {DTO?.id === data.id && (
                    <span>
                      <button
                        type="button"
                        className="del reply-report-btn"
                        onClick={() => {
                          let flag = window.confirm("삭제하시겠습니까?");
                          if (flag)
                            deleteReply(
                              setReplyList,
                              navigator,
                              data.news_id,
                              data.reply_id,
                              status
                            );
                        }}
                      >
                        삭제
                      </button>
                      <button
                        type="button"
                        className="reply-report-btn"
                        onClick={() => {
                          setUpdateClicked(true);
                          setReplyTopClicked(false);
                        }}
                      >
                        수정
                      </button>
                    </span>
                  )}
                  {DTO?.id !== data.id && (
                    <button
                      type="button"
                      onClick={openModal}
                      className="del reply-report-btn"
                    >
                      신고하기
                    </button>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
        {!isUpdateClicked && (
          <div className="reply-text">
            {data.content}
            {data.edited === 1 && <span className="update-reply">(수정됨)</span>}
          </div>
        )}
        {isUpdateClicked && (
          <div className="lower-reply-write">
            <input
              max={300}
              value={isUpdateReply}
              onChange={updateHandle}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  setUpdateClicked(false);
                  updateReply(
                    setReplyList,
                    navi,
                    data.news_id,
                    data.reply_id,
                    isUpdateReply,
                    status
                  );
                }
              }}
            ></input>
            <button
              onClick={() => {
                setUpdateClicked(false);
                updateReply(
                  setReplyList,
                  navi,
                  data.news_id,
                  data.reply_id,
                  isUpdateReply,
                  status
                );
              }}
            >
              수정
            </button>
          </div>
        )}
        {low && (
          <button className="reply-cmt" onClick={onToggle}>
            답글({data.lower ? data.lower.length : 0})
          </button>
        )}
        <ReportModal
          isOpen={isModalOpen}
          onRequestClose={closeModal}
          reply_id={data.reply_id}
        />
      </div>
    </div>
  );
};

export default Reply;
