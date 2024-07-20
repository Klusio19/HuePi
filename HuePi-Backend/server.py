import json
import os
import time
from fastapi import FastAPI, BackgroundTasks, Header
import hue_light_utils
from typing import Annotated
from dotenv import load_dotenv

app = FastAPI()


def get_hue_bridge_ip():
    try:
        with open("hue-bridge-ip.txt", "r") as file:
            return file.read().strip()
    except FileNotFoundError:
        print("Error: hue-bridge-ip.txt file not found.")
        return None


def get_hue_api_key():
    load_dotenv()
    return os.getenv("hue-application-key")


hue_bridge_ip_address = get_hue_bridge_ip()
hue_api_key = get_hue_api_key()


@app.get("/change-power/{light_id}")
async def change_power(light_id: str):
    _header = {"hue-application-key": hue_api_key}
    if hue_bridge_ip_address:
        hue_light_utils.change_power(header=_header, light_id=light_id, bridge_ip=hue_bridge_ip_address)
        return {"message": "Power state changed"}
    else:
        return {"message": "Hue bridge IP address not found"}, 500


@app.get("/turn-on/{light_id}")
async def turn_on(light_id: str):
    _header = {"hue-application-key": hue_api_key}
    response =  hue_light_utils.turn_on(header=_header, light_id=light_id, bridge_ip=hue_bridge_ip_address)
    if response.get("message") == "OK":
        return {"message": "Turned on!"}
    elif response.get("message") == "Already turned on!":
        return {"message": "Already turned on!"}
    else:
        return {"message": "Something's wrong!"}, 500


@app.get("/turn-off/{light_id}")
async def turn_off(light_id: str):
    _header = {"hue-application-key": hue_api_key}
    response =  hue_light_utils.turn_off(header=_header, light_id=light_id, bridge_ip=hue_bridge_ip_address)
    if response.get("message") == "OK":
        return {"message": "Turned off!"}
    elif response.get("message") == "Already turned off!":
        return {"message": "Already turned off!"}
    else:
        return {"message": "Something's wrong!"}, 500


@app.get("/change-brightness/{light_id}")
async def change_brightness(light_id: str, level: int | None = None):
    if level is None:
        return {"message": "Brightness level not specified!"}
    else:
        if level < 0:
            level = 0
        elif level > 100:
            level = 100
        _header = {"hue-application-key": hue_api_key}
        response =  hue_light_utils.change_brightness(header=_header, light_id=light_id, level=level, bridge_ip=hue_bridge_ip_address)
        if response.get("message") == "OK":
            return {"message": f"Brightness changed to: {level}"}


@app.get("/get-lights")
async def get_lights():
    _header = {"hue-application-key": hue_api_key}
    lights = hue_light_utils.get_lights(header=_header, bridge_ip=hue_bridge_ip_address)
    return lights


# keep_going = False


# def infinite_loop_task():
#     global keep_going
#     num = 1
#     while keep_going:
#         print(f"Infinite loop nr: {num}")
#         time.sleep(1)
#         num += 1


# @app.get("/header-test")
# def lol(my_header: Annotated[str | None, Header()] = None):
#     print(f"This is value of \"my-header\": {my_header}")
#     return {"my-header" : my_header}


# @app.get("/loop")
# async def infinite_loop(background_tasks: BackgroundTasks):
#     global keep_going
#     if keep_going:
#         return {"message": "Loop already running"}
#     else:
#         keep_going = True
#         background_tasks.add_task(infinite_loop_task)
#         return {"message": "Infinite loop started"}


# @app.get("/terminate-loop")
# def terminate_loop():
#     global keep_going
#     keep_going = False
#     return {"message": "Infinite loop terminated"}
