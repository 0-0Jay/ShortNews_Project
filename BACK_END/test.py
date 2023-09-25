from bs4 import BeautifulSoup            
import requests                          
from selenium import webdriver           
import time                              
from selenium.webdriver.common.by import By
import codecs

def basic_clear(text) :
    text = text.replace('\n', '')
    text = text.replace('<dt><a href="', '')
    text = text.replace('">', ' ')
    text = text.replace('</a></dt>', '')
    text = text.replace('\t', '')
    return text

# 브라우저 켜기
url = 'https://news.naver.com/main/list.naver?mode=LS2D&mid=shm&sid1=100&sid2=266'
browser = webdriver.Safari()
browser.get(url)

# f = codecs.open("c:/crawler2/kyoboBest.txt", mode="w", encoding="utf-8")

# // 책 순위 표기를 위해 미리 으로 선언
book_rank = 0
f1 = open('./test.txt', 'w')
f1.write("title,link\n")
for i in range(1, 11):
    # // 교보문고 베스트셀러 페이지 접속
    browser.get('https://news.naver.com/main/list.naver?mode=LS2D&sid2=266&sid1=100&mid=shm&date=20230925&page=%s' % i)
    
    # // 1초마다 페이지 넘김, 안할시 로봇으로 간주되어 블락이 될수 있음
    time.sleep(1)
    
    image_list = []
    
    # // 소스코드 저장
    source = browser.page_source
    
    # // 소스코드 파싱
    parsed_source = BeautifulSoup(source, "html.parser")
    
    ul_list = parsed_source.find_all("ul", class_="type06_headline")
    
    ul_list = ul_list[0]
    
    div_image_list = ul_list.select("dt:nth-child(2)")
    for idx, item in enumerate(div_image_list):
        src = item.get('src')
        #print(idx, "번째임")
        temp = basic_clear(str(item))
        space = temp.index(' ')
        link = temp[:space]
        title = temp[space + 1:].strip()
        #print(f'링크 {link}\n제목 {title}')
        f1.write(f"{title},{link}\n")
f1.close()
#section_body > ul.type06_headline > li:nth-child(1) > dl > dt:nth-child(2) > a

f1 = open('./test.txt', 'r')
print(f1.read())
f1.close()