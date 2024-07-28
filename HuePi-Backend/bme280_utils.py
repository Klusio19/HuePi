import bme280
import smbus2

port = 1
address = 0x76
bus = smbus2.SMBus(port)

bme280.load_calibration_params(bus, address)


def read_temperature():
    data = bme280.sample(bus, address)
    return round(data.temperature, 3)


def read_pressure():
    data = bme280.sample(bus, address)
    return round(data.pressure, 3)


def read_humidity():
    data = bme280.sample(bus, address)
    return round(data.humidity, 3)
