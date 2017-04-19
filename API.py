from clarifai.rest import ClarifaiApp
from DB import DB
# import pprint
from flask import jsonify

# pp = pprint.PrettyPrinter(indent=4)

class API():

	def get_json(self, img_url):
		app = ClarifaiApp("LCdpXnEZ5fLYNo_3vxuwhT_mxvLEka_dasmFtUWJ", "oqz0XDkG9zVU3pY7s7HVbVyd-DP1VrH_84z-j5MF")
		model = app.models.get("general-v1.3")	# get the general model
		return model.predict_by_url(url=img_url)		# predict with the model

	def compare(self, url):
		db = DB()
		dball = db.get_all()

		cjson = self.get_json(url)
		# cjson = eval(dball[1][1])
		cmap = {}
		concepts = cjson['outputs'][0]['data']['concepts']
		for con in concepts:
			cmap[con['name']] = float(con['value'])
		# pp.pprint(cmap)

		final_map = {}
		for row in dball:
			rjson = eval(row[1])
			rconcepts = rjson['outputs'][0]['data']['concepts']
			rmap = {}
			for con in rconcepts:
				rmap[con['name']] = float(con['value'])
			# pp.pprint(rmap)
			final_map[row[0]] = self.compare_maps(cmap, rmap)

		return final_map

	def compare_maps(self, map1, map2):
		msum = 0
		for key in map1.keys():
			if key in map2:
				# print key, map1[key], map2[key]
				msum += (map1[key] - map2[key]) ** 2
		for key in map1.keys():
			if not key in map2:
				msum += (1 + map1[key]) ** 2
		for key in map2.keys():
			if not key in map1:
				msum += (1 + map2[key]) ** 2
		return msum

# ap = API()
# pp.pprint((ap.compare('http://i.ebayimg.com/00/s/MjM2WDMxNQ==/z/5DIAAOSwLnBX6XR0/$_57.JPG?set_id=80000000000')))