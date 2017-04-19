import sqlite3 as db
import glob, json
from API import API

class DB:

    def __init__(self):
        self.conn = db.connect('shoes.db')
        self.cursor = self.conn.cursor()

    def data_exists(self, url):
        query = "SELECT COUNT(*) FROM data WHERE image_url=?"
        self.cursor.execute(query, [url])
        return self.cursor.fetchall()[0][0]

    def add_data_table(self, url, json_obj):
        query = 'INSERT INTO data (image_url, image_json) VALUES (?, ?)'
        self.cursor.execute(query, (url, str(json_obj)))
        self.conn.commit()

    def print_all_data(self):
        query = "SELECT * FROM data"
        self.cursor.execute(query)
        res = self.cursor.fetchall()
        restr = ''
        for r in res:
            restr = restr + '<br><br>' + '<br>'.join(r)
        return restr

# dbs = DB()
# dbs.add_from_images_folder()
# print dbs.print_all_data()