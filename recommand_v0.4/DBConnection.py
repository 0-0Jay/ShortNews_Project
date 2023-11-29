# DBConnection.py
import pandas as pd
from sqlalchemy import create_engine

class DBConnection:
    def __init__(self, url):
        self.url = url
        self.engine = ''
        self.con = ''

    def create_db_engine(self):
        self.engine = create_engine(self.url)

    def select(self, columns : list, table : str):
        self.con = self.engine.connect()
        query = "SELECT "
        for col in columns:
            query += col + ", "
        query = query[:-2]
        query += " FROM " + table
        data =  pd.read_sql_query(query, self.con)
        return data