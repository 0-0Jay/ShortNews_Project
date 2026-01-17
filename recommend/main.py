from fastapi import FastAPI, Request
from RecommendService import *
from KeywordService import *
from SearchService import *
from http import HTTPStatus
from fastapi.middleware.cors import CORSMiddleware
import asyncio
import datetime, time

#127.0.0.1:8000/main/recommend
app = FastAPI()
origins = ["*"]
# CORS 설정 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get('/main/recommend')
async def recommend(request: Request) -> dict:
    token = request.headers.get("Authorization")
    result = {}
    if token == None:
        result['message'] = "로그인이 필요한 서비스입니다."
        result['status'] = HTTPStatus.BAD_REQUEST
        return result
    id = getUserId(token)
    if id == "Invalid":
        result['message'] = "유효하지 않은 토큰입니다."
        result['status'] = HTTPStatus.UNAUTHORIZED
    else:
        cnt = 12  # 반환할 기사 갯수 설정
        logData = findAllLog()
        pref = findPreference()
        result = getRecommendNews(id, logData, pref, cnt)
        result['status'] = HTTPStatus.OK
    return result

@app.get('/main/keyword/{date}')
async def keyword(date : str) -> dict:
    start = datetime.timedelta(seconds=time.time())
    result = {}
    result['wordlist'] = getKeywords(date)
    result['status'] = HTTPStatus.OK
    end = datetime.timedelta(seconds=time.time())
    return result

@app.get('/main/search/{keyword}')
async def search(keyword : str) -> dict:
    result = {}
    result['query'] = await query(keyword)
    return result

# 실행방법
# 1. cmd를 켜고 main.py가 있는 폴더로 cd를 통해 이동
# 2. uvicorn main:app --reload 명령어 실행

# 테스트용 코드
if __name__ == '__main__':
    print(asyncio.run(keyword()))
    
