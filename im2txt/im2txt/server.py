# Copyright 2016 The TensorFlow Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ==============================================================================
r"""Generate captions for images using default beam search parameters."""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import math
import os


import tensorflow as tf

from im2txt import configuration
from im2txt import inference_wrapper
from im2txt.inference_utils import caption_generator
from im2txt.inference_utils import vocabulary

tf.logging.set_verbosity(tf.logging.INFO)

class Server:

  def init(self, vocab_file, checkpoint_path):
    # Build the inference graph.
    tf.reset_default_graph()
    g = tf.Graph()
    with g.as_default():
      model = inference_wrapper.InferenceWrapper()
      restore_fn = model.build_graph_from_config(configuration.ModelConfig(),
                                                 checkpoint_path)
    g.finalize()
  
    # Create the vocabulary.
    self.vocab = vocabulary.Vocabulary(vocab_file)
  
    self.sess = tf.Session(graph=g)
    # Load the model from checkpoint.
    restore_fn(self.sess)
  
    # Prepare the caption generator. Here we are implicitly using the default
    # beam search parameters. See caption_generator.py for a description of the
    # available beam search parameters.
    self.generator = caption_generator.CaptionGenerator(model, self.vocab)
  
  def caption_image(self, path_to_image):
    with tf.gfile.GFile(path_to_image, "rb") as f:
      image = f.read()
    captions = self.generator.beam_search(self.sess, image)
    for i, caption in enumerate(captions):
      # Ignore begin and end words.
      sentence = [self.vocab.id_to_word(w) for w in caption.sentence[1:-1]]
      sentence = " ".join(sentence)
      return { 
  	"caption": sentence,
  	"prob": math.exp(caption.logprob)
      }
