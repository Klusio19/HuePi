import os
import time

from fastapi import FastAPI, BackgroundTasks, Header, Security, HTTPException, status, BackgroundTasks
from fastapi.security import APIKeyHeader
import hue_light_utils
from dotenv import load_dotenv
from color_conversions import hsv2xy, hsv2rgb, rgb2xy
from bme280_utils import read_temperature
from light_functions import translate_temperature_to_hsv_color


def get_hue_bridge_ip():
    try:
        with open("hue-bridge-ip.txt", "r") as file:
            return file.read().strip()
    except FileNotFoundError:
        print("Error: hue-bridge-ip.txt file not found.")
        return None


app = FastAPI()
load_dotenv()

hue_bridge_ip_address = get_hue_bridge_ip()
hue_api_key = os.getenv("hue-application-key")
server_api_key = os.getenv("server-api-key")

api_key_header = APIKeyHeader(name="api-key")


def get_api_key(api_key: str = Security(api_key_header)) -> str:
    if api_key == server_api_key:
        return api_key
    raise HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Invalid or missing server API Key",
    )


@app.get("/change-power/{light_id}")
async def change_power(light_id: str, api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    if hue_bridge_ip_address:
        hue_light_utils.change_power(header=_header, light_id=light_id, bridge_ip=hue_bridge_ip_address)
        return {"message": "Power state changed"}
    else:
        return {"message": "Hue bridge IP address not found"}, 500


@app.get("/turn-on/{light_id}")
async def turn_on(light_id: str, api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    response = hue_light_utils.turn_on(header=_header, light_id=light_id, bridge_ip=hue_bridge_ip_address)
    if response.get("message") == "OK":
        return {"message": "Turned on!"}
    elif response.get("message") == "Already turned on!":
        return {"message": "Already turned on!"}
    else:
        return {"message": "Something's wrong!"}, 500


@app.get("/turn-off/{light_id}")
async def turn_off(light_id: str, api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    response = hue_light_utils.turn_off(header=_header, light_id=light_id, bridge_ip=hue_bridge_ip_address)
    if response.get("message") == "OK":
        return {"message": "Turned off!"}
    elif response.get("message") == "Already turned off!":
        return {"message": "Already turned off!"}
    else:
        return {"message": "Something's wrong!"}, 500


@app.get("/change-brightness/{light_id}")
async def change_brightness(light_id: str, level: int | None = None, api_key: str = Security(get_api_key)):
    if level is None:
        return {"message": "Brightness level not specified!"}
    else:
        if level < 0:
            level = 0
        elif level > 100:
            level = 100
        _header = {"hue-application-key": hue_api_key}
        response = hue_light_utils.change_brightness(header=_header, light_id=light_id,
                                                     level=level, bridge_ip=hue_bridge_ip_address)
        if response.get("message") == "OK":
            return {"message": f"Brightness changed to: {level}"}


@app.get("/change-color/{light_id}")
async def change_color(light_id: str,
                       h: float | None = None, s: float | None = None, v: float | None = None,
                       api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    if (h and s and v) is not None:
        x, y = hsv2xy(h, s, v)
        response = hue_light_utils.change_color(header=_header, light_id=light_id, x=x, y=y,
                                                bridge_ip=hue_bridge_ip_address)
        if response.get("message") == "OK":
            return {"message": f"Color changed to: x:{x}, y:{y}"}
    else:
        return {"message": "Missing some parameters"}


tasks = {}


@app.get("/temp-to-color/{light_id}")
async def temp_to_color(background_tasks: BackgroundTasks, light_id: str,
                        h_min: float, h_max: float, temp_min: float, temp_max: float,
                        api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    if light_id in tasks:
        tasks[light_id] = False
        del tasks[light_id]
        return {"message": f"Stopped displaying temp_to_light for light id: {light_id}"}
    else:
        tasks[light_id] = True
        background_tasks.add_task(display_temperature_to_color,
                                  temp_min=temp_min, temp_max=temp_max,
                                  hsv_color_min=h_min, hsv_color_max=h_max, header=_header, light_id=light_id)
        return {"message": f"Displaying temp_to_light for light id: {light_id}"}


def display_temperature_to_color(temp_min, temp_max, hsv_color_min, hsv_color_max, header, light_id):
    while tasks.get(light_id, False):
        current_temp = read_temperature()
        converted_hue_value = translate_temperature_to_hsv_color(
            input_temp=current_temp, temp_min=temp_min, temp_max=temp_max,
            hsv_color_min=hsv_color_min, hsv_color_max=hsv_color_max)
        x, y = hsv2xy(converted_hue_value, 1, 1)
        hue_light_utils.change_color(header=header, light_id=light_id, bridge_ip=hue_bridge_ip_address, x=x, y=y)
        time.sleep(1)


@app.get("/get-lights")
async def get_lights(api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    lights = hue_light_utils.get_lights(header=_header, bridge_ip=hue_bridge_ip_address)
    return lights


@app.get("/check-connection")
async def check(api_key: str = Security(get_api_key)):
    return {"message": "OK"}
