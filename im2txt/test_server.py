import im2txt.server as server
s = server.Server()
s.init("/home/zkmhackers/data/mscoco/word_counts.txt", "/home/zkmhackers/models/model_updated.ckpt-2000000")


print(s.caption_image("/home/zkmhackers/data/test/surfer.jpg"))

