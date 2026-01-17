import pandas as pd
import numpy as np
import math
from sklearn.metrics.pairwise import cosine_similarity
from Repository import *
from datetime import datetime, timedelta
import jwt

# 유저간 코사인 유사도 계산
def getUserSimilarity(id : str, logData : pd.DataFrame, pref : set) -> dict:
    user_pref = set((pref.loc[pref['id'] == id])['cate_id'])
    userInLog = findUserInLog()  # 로그에 기록된 유저 가져오기 (최근 7일 간)
    base = math.ceil(len(user_pref) * 0.60)  # 선택 카테고리 유사도 기준 지정(60% 이상)
    similar_user = set()
    for other in userInLog:
        other_pref = set((pref.loc[pref['id'] == other])['cate_id'])
        if len(user_pref & other_pref) >= base:  # 기준을 넘기면 타겟과 유사한 유저로 선택
            similar_user.add(other)
    # 카테고리가 유사한 유저들이 읽은 기사 피봇테이블로 정리
    similarUserData = logData[logData['id'].isin(similar_user)]
    similarUserPivot = similarUserData.pivot_table(index='id', columns=['cate_id', 'news_id'], aggfunc=len, fill_value=0)

    user_similarity = []
    if id in userInLog : user_similarity = cosine_similarity(similarUserPivot.loc[id:id, :], similarUserPivot.loc[:, :])
    else : user_similarity = np.ones((1,len(similar_user))) # 아무 것도 읽지 않은 유저는 모든 유저와 유사하다고 설정

    user_score = {}
    for user, score in enumerate(user_similarity[0]):
        user_score[similarUserPivot.index[user]] = round(score, 5)
    return user_score

# 해당 유저가 본 기사 반환
def getUserViews(id : str, logData : pd.DataFrame) -> dict:
    data = logData[logData['id'] == id]
    userViews = {}
    for _, row in data.iterrows():
        userViews[row['news_id']] = row['cate_id']
    return userViews

# 추천 기사의 (id, 추천 점수) 쌍으로 선택/미선택 카테고리 각각 나누어 반환
def getRecommendNewsId(id : str, logData : pd.DataFrame, pref : pd.DataFrame) -> pd.DataFrame:
    THREE_DAYS_BEFORE = datetime.today() - timedelta(days = 3)
    selectedCateNews = {}
    nonSelectedCateNews = {}
    inThreeDaysLog = set(logData[logData['cre_date'] > THREE_DAYS_BEFORE]['news_id'])
    user_pref = set((pref.loc[pref['id'] == id])['cate_id'])
    user_score = getUserSimilarity(id, logData, pref)
    userViews = getUserViews(id, logData)
    for user, score in user_score.items():
        if user == id: continue
        otherViews = getUserViews(user, logData)
        for news_id, cate_id in otherViews.items():
            if news_id not in userViews and news_id in inThreeDaysLog:  # 유저가 읽지 않은 기사이고 3일 내 다른 유저가 읽은 기사라면
                if cate_id in user_pref:  # 유저가 선택한 카테고리에 속해 있다면
                    if news_id not in selectedCateNews:  # 선택 카테고리 리스트에 점수 누적
                        selectedCateNews[news_id] = score
                    else:
                        selectedCateNews[news_id] += score
                else:  # 유저가 선택한 카테고리에 속해있지 않다면
                    if news_id not in nonSelectedCateNews:  # 미선택 카테고리 리스트에 점수 누적
                        nonSelectedCateNews[news_id] = score
                    else:
                        nonSelectedCateNews[news_id] += score
    
    tmp_sel = dict(sorted(selectedCateNews.items(), key=lambda x: x[1], reverse=True))
    tmp_non = dict(sorted(nonSelectedCateNews.items(), key=lambda x: x[1], reverse=True))
    selectedCateNews = {'news_id' : list(tmp_sel.keys()), 'score' : list(tmp_sel.values())}
    nonSelectedCateNews = {'news_id' : list(tmp_non.keys()), 'score' : list(tmp_non.values())}
    return pd.DataFrame(selectedCateNews), pd.DataFrame(nonSelectedCateNews)

# 기사 추천 결과 반환
def getRecommendNews(id : str, logData : pd.DataFrame, pref : pd.DataFrame, cnt : int) -> dict:
    news = findAllNews()
    sel_list, non_list = getRecommendNewsId(id, logData, pref)
    result = {}
    sel_list = pd.merge(sel_list, news, on = 'news_id', how ='inner')[:cnt]#.drop('score', axis=1)[:cnt]
    non_list = pd.merge(non_list, news, on = 'news_id', how ='inner')[:cnt]#.drop('score', axis=1)[:cnt]
    sub_list = getSubRecommendNews(id, news, pref)[:cnt]
    sel_list['imgs'] = sel_list['imgs'].apply(lambda x: x.split(',')[0] if x is not None else x) # 이미지 하나만 반환
    non_list['imgs'] = non_list['imgs'].apply(lambda x: x.split(',')[0] if x is not None else x)
    sub_list['imgs'] = sub_list['imgs'].apply(lambda x: x.split(',')[0] if x is not None else x)
    result['selectedList'] = sel_list.to_dict(orient='records')
    result['nonselectedList'] = non_list.to_dict(orient='records')
    result['subList'] = sub_list.to_dict(orient='records')
    return result

# 토큰 해독해 유저 id 반환
def getUserId(token : str) -> str:
    secretKey = "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQ"
    try:
        auth = jwt.decode(token, secretKey, algorithms=["HS256"])
        return auth.get('sub')
    except jwt.InvalidTokenError:
        return "Invalid"

# 조회수, 좋아요로 추천하는 결과 반환
def getSubRecommendNews(id : str, news: pd.DataFrame, pref : pd.DataFrame) -> pd.DataFrame:
    # 유저 카테고리 내의 기사들을 관심도 내림차순으로 정렬해 반환
    # 관심도 = 조회수 + 좋아요 * 2
    user_pref = set((pref.loc[pref['id'] == id])['cate_id'])
    news['interest'] = news['views'] + news['like'] * 2
    news = news.sort_values(by='interest', ascending=False)
    newsOfUserPref = news[news['cate_id'].isin(user_pref)]
    return newsOfUserPref

# <<구현된 부분>>
# 선택한 카테고리 내에서 추천
# 선택하지 않은 카테고리 내에서 나와 유사한 회원이 읽은 기사 추천 -> 이런 기사는 어때요?
# 만약 아무 카테고리도 선택하지 않았다면 모든 유저가 나와 유사하다고 판단하고 모든 유저에게서 점수 획득
#  -> 이 때, 단순 조회수 아님. 몇명의 서로 다른 인원이 그 기사를 읽어 봤는지가 기준
# 조회수 + (좋아요 * 2) 높은 것 추천
# 유저간 유사도는 7일 내 기사 조회 로그 활용
# 뉴스 추천은 최근 3일 내에 읽어진 기사만 활용
# -> 만약 3일 이전에 작성된 기사라도 뒤늦게 화제가 되어 최근에 읽혀지는 기사라면 추천에 올라와야 하기 때문