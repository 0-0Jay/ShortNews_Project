import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { getCookie, removeCookieAll } from "../Cookies";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCirclePlay,
  faCirclePause,
  faCircleStop,
} from "@fortawesome/free-regular-svg-icons";
import { expiration } from "../API";

function TTSPlay() {
  const S3 = process.env.REACT_APP_S3_TTS as string;
  const { newsid } = useParams();
  const audioRef = useRef<HTMLAudioElement>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [status, setStatus] = useState(true);
  const dto = getCookie("dto");
  const [Speed, SetSpeed] = useState([dto?.speed === 0.75, dto?.speed === 1, dto?.speed === 1.25]);
  const Model = dto?.model === "_male";
  const Source = S3 + newsid + (Model ? "_male" : "_female") + ".mp3";
  const playAudio = () => {
    if (audioRef.current) {
      audioRef.current.play();
      setIsPlaying(true);
    }
  };

  const pauseAudio = () => {
    if (audioRef.current) {
      audioRef.current.pause();
      setIsPlaying(false);
    }
  };

  const stopAudio = () => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
      setIsPlaying(false);
    }
  };

  function setPlayBack() {
    if (audioRef.current){
      audioRef.current.playbackRate = Speed[0] ? 0.75 : Speed[1] ? 1 : 1.25;
    }
  }
  expiration();
  return (
    dto && <div className="tts-container">
      <audio
        controls
        ref={audioRef}
        onCanPlay={setPlayBack}
        style={{ display: "none" }}
        onEnded={stopAudio}
        onError={()=>setStatus(false)}
      >
        <source src={Source} type="audio/mp3" />
      </audio>
      <div>
        {
          status && 
            <button
            type="button"
            className="tts-btn"
            onClick={playAudio}
            disabled={isPlaying}
          ><FontAwesomeIcon icon={faCirclePlay} />
          </button>
          }
          
        <button
          type="button"
          className="tts-btn"
          onClick={pauseAudio}
          disabled={!isPlaying}
        >
          <FontAwesomeIcon icon={faCirclePause} />
        </button>
        <button
          type="button"
          className="tts-btn"
          onClick={stopAudio}
          disabled={!isPlaying}
        >
          <FontAwesomeIcon icon={faCircleStop} />
        </button>
      </div>
      <hr />
      <div>
        <table className="tts-table">
          <tr>성별</tr>
          <tr>
            <td hidden = {!Model}>
              <label className="radio-circle-label">
                <input
                  type="radio"
                  name="model"
                  checked = {Model}
                  value="남성"
                  className="radio-circle"
                  
                ></input>
                <span>남성</span>
              </label>
            </td>
            <td hidden = {Model}>
              <label className="radio-circle-label">
                <input
                  type="radio"
                  checked = {!Model}
                  name="model"
                  value="여성"
                  className="radio-circle"
                  hidden = {Model}
                ></input>
                <span hidden = {Model}>여성</span>
              </label>
            </td>
          </tr>
          <tr>속도</tr>
          <tr>
            <td>
              <label className="radio-circle-label">
                <input
                  type="radio"
                  name="speed"
                  value="느림"
                  className="radio-circle"
                  checked = {Speed[0]}
                  onClick = {()=>{
                    SetSpeed(p => [true, false, false]);
                  }}
                ></input>
                <span>느림</span>
              </label>
            </td>
            <td>
              <label className="radio-circle-label">
                <input
                  type="radio"
                  name="speed"
                  value="보통"
                  checked = {Speed[1]}
                  onClick = {()=>{
                    SetSpeed(p => [false, true, false]);
                  }}
                  className="radio-circle"
                ></input>
                <span>보통</span>
              </label>
            </td>
            <td>
              <label className="radio-circle-label">
                <input
                  type="radio"
                  name="speed"
                  value="빠름"
                  checked = {Speed[2]}
                  onClick = {()=>{
                    SetSpeed(p => [false, false, true]);
                  }}
                  className="radio-circle"
                ></input>
                <span >빠름</span>
              </label>
            </td>
          </tr>
        </table>
      </div>
    </div>
  );
}

export default TTSPlay;
