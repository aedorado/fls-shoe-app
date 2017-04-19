import sqlite3 as db
import glob, json
from API import API

class DB:

    def __init__(self):
        self.conn = db.connect('shoes.db')
        self.cursor = self.conn.cursor()

    # def add_from_images_folder(self):
    #     api = API()
    #     all_img = glob.glob("images/*")
    #     for img_url in all_img[0:3]:
    #         img_url = 'https://fls-shoe-app.herokuapp.com/' + img_url
    #         if not self.data_exists(img_url):
    #             print 'Processing: ' + img_url
    #             self.add_data_table(img_url, api.get_json(img_url))
    #         else:
    #             print 'Already processed: ' + img_url
    #     # self.cursor.execute(q)
    #     # return self.cursor.fetchall()

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