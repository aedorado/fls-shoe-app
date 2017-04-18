from clarifai.rest import ClarifaiApp

class API():

	def get_json(self, img_url):
		app = ClarifaiApp("LCdpXnEZ5fLYNo_3vxuwhT_mxvLEka_dasmFtUWJ", "oqz0XDkG9zVU3pY7s7HVbVyd-DP1VrH_84z-j5MF")
		model = app.models.get("general-v1.3")	# get the general model
		return model.predict_by_url(url=img_url)		# predict with the model