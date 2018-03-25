from flask import Flask
from flask import request
import im2txt.server as server
import numpy as np
import tempfile
import os
import cv2

app = Flask(__name__)


app.config['UPLOAD_FOLDER'] = '/tmp/'

backend = server.Server()
backend.init("/home/zkmhackers/data/mscoco/word_counts.txt", "/home/zkmhackers/models/model_updated.ckpt-2000000")


@app.route("/image", methods=["POST"])
def index():
	# check if the post request has the file part
	if 'image' not in request.files:
		flash('No file part')
		return redirect(request.url)
	image = request.files["image"]
	filename = image.filename
	filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
	nparr = np.fromstring(image.stream.read(), np.uint8)
	i = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
	maxWidth = maxHeight = 512
	srcHeight, srcWidth, _ = i.shape
	print("Height {}, Width {}".format(srcHeight, srcWidth))
	ratio = min(maxWidth / srcWidth, maxHeight / srcHeight)
	resized_image = cv2.resize(i, (int(ratio * srcWidth), int(ratio * srcHeight)))	 
	resized_image_jpg = cv2.imencode('.jpg', resized_image)[1].tostring()
	return backend.caption_image(resized_image_jpg)['caption']

@app.route("/image", methods=["GET"])
def default():
	return "Use POST plz"



if __name__ == "__main__":
	app.run(host='0.0.0.0', port=9999)
