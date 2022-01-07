import os
import keras
import numpy as np

from skimage import io as skio
from skimage import transform as sktransform
from skimage.util import crop

# This method resizes images to the desired square shape.
#
# If Width != Height
#   - Resize shorter side to desired shape
#   - Crop Left+Right or Bottom+Top to shape (+- 1 Pixel due odd numbers)
#
# If Width x Height is the same, it just resizes
# 
# Resize to shape to Fix cropping
# 
#
def cropscale_to_square(img_file_name, to_shape):
    img_arr = skio.imread(img_file_name, as_gray=False)
    height, width, channels = img_arr.shape
    print(img_file_name + ": " + str(img_arr.shape))

    new_height = to_shape
    new_width = to_shape
    if width > height:
        new_height = to_shape
        new_width = int(round((width / height) * to_shape, 0))
        img = sktransform.resize(img_arr, (new_height, new_width), anti_aliasing=True, preserve_range=True)
        print(img_file_name + ": " + str(img.shape))
        # Problem with odd numbers
        crop_img = crop(img,
                        ((0, 0), (int((new_width - to_shape) / 2), int(round((new_width - to_shape) / 2, 0))), (0, 0)))
        print(img_file_name + ": " + str(crop_img.shape))

    if height > width:
        new_height = int(round(((height / width) * to_shape), 0))
        new_width = to_shape
        img = sktransform.resize(img_arr, (new_height, new_width), anti_aliasing=True, preserve_range=True)
        print(img_file_name + ": " + str(img.shape))
        # Problem with odd numbers
        crop_img = crop(img, (
        (int(round((new_height - to_shape) / 2, 0)), int(round((new_height - to_shape) / 2, 0))), (0, 0), (0, 0)))
        print(img_file_name + ": " + str(crop_img.shape))

    if height == width:
        crop_img = sktransform.resize(img_arr, (new_height, new_width), anti_aliasing=True, preserve_range=True)

    # Fix problem with odd numbers
    final = sktransform.resize(crop_img, (to_shape, to_shape), anti_aliasing=True, preserve_range=True)
    return final[:, :, :3].astype(np.uint8)