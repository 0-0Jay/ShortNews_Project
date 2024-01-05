from fastapi import FastAPI
from recommand import *

# 유저 id
target = str(1)

# 로그 데이터 피봇테이블로 정제
log_pivot = set_log_pivot(target)

# 로그 기반 유저별 유사도 체크
user_similarity = user_sim(log_pivot)

 # 추천 기사 산출
sel_cate, non_sel_cate = recommand_nid(target, log_pivot, user_similarity)
# 산출할 기사 갯수
n = 5

#127.0.0.1.:8000/r
app = FastAPI()
@app.get("/r") # 선택 카테고리 추천 기사 
def sel_recommand():
    sel_recommand = recommand_result(sel_cate, n)
    sel = sel_recommand.to_json(orient='records', indent = 4, force_ascii=False)
    return sel

#127.0.0.1.:8000/nr
@app.get("/nr") # 비선택 카테고리 추천 기사
def non_sel_recommand():
    non_sel_recommand = recommand_result(non_sel_cate, n)
    non_sel = non_sel_recommand.to_json(orient='records', indent = 4, force_ascii=False)
    return non_sel


# 실행방법
# 1. cmd를 켜고 main.py가 있는 폴더로 cd를 통해 이동
# 2. uvicorn main:app --reload 명령어 실행
# 3. 웹페이지에 localhost:8000/r로 반환값(선택 카테고리 추천 결과) 확인
# 4. /nr로 하면 비선택 카테고리 반환
