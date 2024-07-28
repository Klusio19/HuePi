from rgbxy import Converter
from rgbxy import GamutC
from colorsys import hsv_to_rgb

converter = Converter(GamutC)


def hsv2rgb(h, s, v):
    """Converts hsv to rgb in 0-255 range"""
    return tuple(round(i * 255) for i in hsv_to_rgb(h, s, v))


def hsv2xy(h, s, v):
    """Converts hsv (0-1, 0-1, 0-1 ranges) to Philips' Hue xy values"""
    _v = v
    if v <= 0.001:
        _v = 0.01
    r, g, b = hsv2rgb(h, s, _v)
    x, y = converter.rgb_to_xy(r, g, b)
    return x, y


def rgb2xy(r, g, b):
    if r == 0 and g == 0 and b == 0:
        return 0, 0
    x, y = converter.rgb_to_xy(r, g, b)
    return x, y
