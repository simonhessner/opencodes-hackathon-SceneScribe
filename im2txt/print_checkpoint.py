# Prints all keys in the checkpoint
OLD_CHECKPOINT_FILE = "model/model_updated.ckpt-2000000"

import tensorflow as tf
new_checkpoint_vars = {}
reader = tf.train.NewCheckpointReader(OLD_CHECKPOINT_FILE)
for old_name in reader.get_variable_to_shape_map():
    print(old_name)
