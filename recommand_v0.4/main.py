from fastapi import FastAPI
import pandas as pd
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
