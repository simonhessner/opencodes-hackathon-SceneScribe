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
	cv2.imwrite(filepath + "pre.jpg", i)
	resized_image = cv2.resize(i, (512, 512)) 
	cv2.imwrite(filepath, resized_image)
	return backend.caption_image(filepath)['caption']

@app.route("/image", methods=["GET"])
def default():
	return "Use POST plz"



if __name__ == "__main__":
	app.run(host='0.0.0.0', port=9999)
