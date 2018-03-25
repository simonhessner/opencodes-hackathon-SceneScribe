This project was implemented during the opencodes hackathon @ ZKM Karlsruhe (24./25. March 2018). The team consists of Lucas S., Jonas F., Lukas K. and Simon H.

The project comprises an Android app that captures pictures from the camera and sends it to a server that applies a image captioning model. The result is then delivered to the user by both Text-To-Speech and a textual output on the screen.
This app can assist visually impaired people with understanding the scenery they currently are in.


# Old README
TODO make nice README
Android app that describes sceneries captured by the smartphone camera with speech

# REST API
curl -F 'image=@test.png' http://localhost:5000/image 

Send HTTP form with multipart/form-data encoding as POST
