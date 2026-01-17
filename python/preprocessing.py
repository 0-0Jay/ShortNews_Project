import re
from bareunpy import Tagger

# 본문 전처리
def cleanContent(text):

    text = re.sub('\([^)]+\)', '', text)
    text = re.sub('\[[^\]]+\]','',text)
    text = re.sub('([^\s]*\s기자)','',text)
    text = re.sub('([^\s]*\온라인 기자)','',text)
    text = re.sub('([^\s]*\s기상캐스터)','',text)
    text = re.sub('포토','',text)
    text = re.sub('\S+@[a-z.]+','',text)
    text = re.sub('[“”]','"',text)
    text = re.sub('[‘’]','\'',text)
    text = re.sub('\s{2,}',' ',text)
    text = re.sub('다\.(?=(?:[^"]*"[^"]*")*[^"]*$)', '다.\n', text)
    text = re.sub('[\t\xa0]','', text)
    text = re.sub('[ㄱ-ㅎㅏ-ㅣ]+','',text)
    text = re.sub('[=+#/^$@*※&ㆍ!』\\|\[\]\<\>`…》■□ㅁ◆◇▶◀▷◁△▽▲▼○●━]','',text)
        
    return text

# 제목 전처리
def cleanTitle(text):
    title = text

    title = re.sub('\([^)]+\)', '', title)
    title = re.sub('\[[^\]]+\]', '',title)
    title = re.sub('[ㄱ-ㅎㅏ-ㅣ]+','',title)
    title = re.sub('[“”]','"',title)
    title = re.sub('[‘’]','\'',title)
    title = re.sub('\.{2,3}','...',title)
    title = re.sub('…','...',title)
    title = re.sub('\·{3}','...',title)
    title = re.sub('[=+#/^$@*※&ㆍ!』\\|\<\>`》■□ㅁ◆◇▶◀▷◁△▽▲▼○●━]','',title)

    if not title:   # 제목이 다 사라졌으면 원래 제목으로
        title = text

    return title.strip()


# 카테고리 번호 변환 (정치 -> 100)
def convertCategory(news_df):    
    category = [("정치", "100"), ("경제", "101"), ("사회", "102"), ("생활/문화", "103"), ("세계", "104"), ("IT/과학", "105"), ("연예", "106"), ("스포츠", "107")]

    for name, num in category:
        news_df["category"][news_df["category"] == name] = num


# 키워드 추출
def getKeyword(summary_content):
    tagger = Tagger(apikey='koba-MBTOTZI-CRPEEBI-SRDUSVI-FPSMISA')
    result = []
    res = tagger.tags([summary_content])
    pa = res.pos()
    for word, type in pa:
        if type == 'NNP' and len(word) >= 2:
            result.append(word)

    return " ".join(result)