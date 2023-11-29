import pandas as pd
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import cx_Oracle
import sqlalchemy
from sqlalchemy import create_engine
from fastapi import FastAPI
from fastapi.responses import JSONResponse
from DBConnection import DBConnection

db = DBConnection('oracle://ist:ist@localhost:1521/xe')
db.create_db_engine()
target_cate = set()

def user_cate():
    global target_cate
    return target_cate

def category_pivot():
    pref_data = db.select(columns=["mem_id", "cate_id"], table="test_pref")
    pivot = pref_data.pivot_table(index = 'mem_id', columns = 'cate_id', aggfunc = len, fill_value = 0)
    return pivot

def similar_category_user(target):
    global target_cate
    usr_category_pivot = category_pivot()
    target_cate = set(usr_category_pivot.columns[usr_category_pivot.loc[str(target)] == 1])
    same_cate_users = set()
    for mid, row in usr_category_pivot.iterrows():
        other = set(usr_category_pivot.columns[row == 1])
        if len(target_cate.intersection(other)) >= 2:  # 2개 이상 겹칠 때만 
            same_cate_users.add(mid)   
    return same_cate_users

def set_log_pivot(target):
    log_data = db.select(columns=["mem_id", "cate_id", "news_id"], table="user_log")
    log_data = log_data[log_data['mem_id'].isin(similar_category_user(target))]
    log_pivot = log_data.pivot_table(index='mem_id', columns=['cate_id', 'news_id'], aggfunc=len, fill_value=0)
    return log_pivot

def user_sim(a : pd.DataFrame):
    cos_sim = cosine_similarity(a, a)
    user_similarity = pd.DataFrame(data = cos_sim, index = a.index, columns = a.index)
    return user_similarity

def recommand_nid(target, log : pd.DataFrame, sim : pd.DataFrame):
    sel_cate_recommend = {} # 선택 카테고리의 기사
    non_sel_cate_recommend = {} # 미선택 카테고리의 기사
    for user in log.index:
        if user == target: continue  # 자기 자신은 제외
        score = sim.at[str(target), str(user)]
        # 두 회원의 유사도를 점수로 활용 -> 유사도가 높은 회원이 읽은 기사가 추천 될 확률 증가

        for nid in log.columns:
            if log.at[user, nid] == 1 and log.at[target, nid] == 0: # target이 안봤고, user가 봤으면
                title = list(nid)  # [카테고리, 뉴스 ID]
                if title[0] in target_cate:
                    if nid not in sel_cate_recommend:
                        sel_cate_recommend[title[1]] = score
                    else :
                        sel_cate_recommend[title[1]] += score
                else:
                    if nid not in non_sel_cate_recommend:
                        non_sel_cate_recommend[title[1]] = score
                    else :
                        non_sel_cate_recommend[title[1]] += score
    return sel_cate_recommend, non_sel_cate_recommend

def recommand_result(nid : dict, n : int):
    news = db.select(columns=['news_id', 'cate_id', 'title'], table='test_news')
    res = dict(sorted(nid.items(), key=lambda x : -x[1]))
    news_id = pd.DataFrame({"news_id" : res.keys()})
    result = pd.merge(news_id, news, on="news_id")
    return result.head(n)
