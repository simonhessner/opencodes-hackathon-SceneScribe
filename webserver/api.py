from flask import Flask
from flask import request
import cv2
import numpy as np

app = Flask(__name__)

@app.route("/image", methods=["POST"])
def index():
	image = request.files["image"]
	nparr = np.fromstring(image.stream.read(), np.uint8)
	i = cv2.imdecode(nparr, cv2.CV_LOAD_IMAGE_COLOR)
	return "%d" % i.shape[0]

@app.route("/image", methods=["GET"])
def default():
	return "Use POST plz"



if __name__ == "__main__":
	app.run(host='0.0.0.0', port=9999)
