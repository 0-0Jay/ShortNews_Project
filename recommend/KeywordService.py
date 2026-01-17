from Repository import *

# 카테고리 별로 키워드 중요도 채점
def categorizeKeywords(news : pd.DataFrame) -> dict:
    result = {
        '100' : {},
        '101' : {},
        '102' : {},
        '103' : {},
        '104' : {},
        '105' : {},
        '106' : {},
        '107' : {}
    }
    for index, row in news.iterrows():
        if row['keyword'] == None: continue
        words = row['keyword'].strip().split(" ")
        cate = row['cate_id']
        for wd in words:
            if wd not in result[cate]: result[cate][wd] = 0
            result[cate][wd] += 1
    return result

# 키워드 별로 상위 20개 추출
def getKeywords(date : str) -> list:
    news = findAllKeyword(date)
    keyword_list = categorizeKeywords(news)

    for i in range(8):
        cate = "10" + str(i)
        keyword_list[cate] = sorted(keyword_list[cate].items(), key= lambda x : -x[1])[:20]  # 일반 탭 키워드 수 설정

    return keyword_list
