import os

from UGATIT import UGATIT
from utils import *


class FakeArgs():
    phase='test'
    light=True
    dataset='selfie2anime'

    epoch=100
    iteration=10000
    batch_size=1
    print_freq=1000
    save_freq=1000
    decay_flag=True
    decay_epoch=50

    lr=0.0001
    GP_ld=10
    adv_weight=1
    cycle_weight=10
    identity_weight=10
    cam_weight=1000
    gan_type='lsgan'

    smoothing=True

    ch=64
    n_res=4
    n_dis=6
    n_critic=1
    sn=True

    img_size=256
    img_ch=3
    augment_flag=True

    checkpoint_dir='checkpoint'
    result_dir='results'
    log_dir='logs'
    sample_dir='samples'

def main():
    in_dir = os.getenv("INPUT_DIR")
    out_dir = os.getenv("RESULT_DIR")

    with tf.Session(config=tf.ConfigProto(allow_soft_placement=True)) as sess:
        args = FakeArgs()
        gan = UGATIT(sess, args)
        gan.build_model()

        tf.global_variables_initializer().run()

        gan.loop_on_input(in_dir, out_dir)

if __name__ == '__main__':
    main()
