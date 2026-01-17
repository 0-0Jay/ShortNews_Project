import pandas as pd
from sqlalchemy import create_engine

engine = create_engine("oracle://")
conn = engine.connect()

def findPreference():
    global conn
    query = "SELECT id, cate_id FROM preference"
    return pd.read_sql_query(query, conn)

def findUserInLog():
    global conn
    query = "SELECT distinct(id) FROM user_log WHERE CRE_DATE >= TRUNC(SYSDATE) - 7"
    return set(pd.read_sql_query(query, conn)['id'])

def findAllLog():
    global conn
    query = "SELECT id, cate_id, news_id, cre_date from user_log WHERE CRE_DATE >= TRUNC(SYSDATE) - 7"
    return pd.read_sql_query(query, conn)

def findAllNews():
    global conn
    query = '''SELECT n.news_id, n.cate_id, n.title, n.views, n.imgs, 
               (SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = 1 AND l.reply_id is NULL) AS "like",
               (SELECT COUNT(*) FROM recommend l WHERE l.news_id = n.news_id AND type = -1 AND l.reply_id is NULL) AS "dislike",
               (SELECT COUNT(*) FROM reply r WHERE r.news_id = n.news_id) AS reply
               FROM news n'''
    return pd.read_sql_query(query, conn)

def findAllKeyword(date : str):
    global conn
    query = """SELECT n.cate_id, n.keyword 
               FROM news n WHERE news_id LIKE CONCAT('""" + date + """', '%')"""
    return pd.read_sql_query(query, conn)

