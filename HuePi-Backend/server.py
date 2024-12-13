import os
import time

from fastapi import FastAPI, BackgroundTasks, Security, HTTPException, status, BackgroundTasks
from fastapi.security import APIKeyHeader
import hue_light_utils
from dotenv import load_dotenv
from color_conversions import hsv2xy
from bme280_utils import read_temperature
from light_functions import translate_temperature_to_hsv_color
from task_manager import task_manager


class TaskManager:
    def __init__(self):
        self.tasks = {}

    def start_task(self, light_id: str, task: callable, *args, **kwargs):
        """Start a background task for a specific light_id."""
        if light_id in self.tasks:
            raise ValueError(f"A task is already running for light_id {light_id}.")
        self.tasks[light_id] = True  # Mark the task as running
        task(*args, **kwargs)

    def stop_task(self, light_id: str):
        """Stop a background task for a specific light_id."""
        if light_id in self.tasks:
            self.tasks[light_id] = False  # Stop the task
            del self.tasks[light_id]

    def is_task_running(self, light_id: str) -> bool:
        """Check if a task is running for a specific light_id."""
        return self.tasks.get(light_id, False)


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


@app.get("/switch-power/{light_id}")
async def switch_power(light_id: str, api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    if hue_bridge_ip_address:
        hue_light_utils.switch_power(header=_header, light_id=light_id, bridge_ip=hue_bridge_ip_address)
        return {"message": "Power state switched"}
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
async def change_brightness(light_id: str, level: float | None = None, api_key: str = Security(get_api_key)):
    if level is None:
        return {"message": "Brightness level not specified!"}
    else:
        if level < 0.0:
            level = 0.0
        elif level > 1.0:
            level = 1.0
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


# @app.get("/temp-to-color/{light_id}")
# async def temp_to_color(background_tasks: BackgroundTasks, light_id: str,
#                         h_min: float, h_max: float, temp_min: float, temp_max: float,
#                         api_key: str = Security(get_api_key)):
#     _header = {"hue-application-key": hue_api_key}
#     if light_id in tasks:
#         tasks[light_id] = False
#         del tasks[light_id]
#         return {"message": f"Stopped displaying temp_to_light for light with id: {light_id}"}
#     else:
#         tasks[light_id] = True
#         background_tasks.add_task(display_temperature_to_color,
#                                   temp_min=temp_min, temp_max=temp_max,
#                                   hsv_color_min=h_min, hsv_color_max=h_max, header=_header, light_id=light_id)
#         return {"message": f"Displaying temp_to_light for light with id: {light_id}"}


@app.get("/temp-to-color/{light_id}")
async def temp_to_color(background_tasks: BackgroundTasks, light_id: str,
                        h_min: float, h_max: float, temp_min: float, temp_max: float,
                        api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    
    try:
        if task_manager.is_task_running(light_id):
            task_manager.stop_task(light_id)
            
        background_tasks.add_task(display_temperature_to_color,
                                    temp_min=temp_min, temp_max=temp_max,
                                    hsv_color_min=h_min, hsv_color_max=h_max,
                                    header=_header, light_id=light_id)
        task_manager.start_task(light_id, lambda: None)  # Register the task as running
        return {"message": f"Started displaying temp_to_light for light with id: {light_id}"}            
    except ValueError as e:
        return {"message": str(e)}
    

@app.get("/stop-temp-to-color/{light_id}")
async def temp_to_color(light_id: str, api_key: str = Security(get_api_key)):
    try:
        if task_manager.is_task_running(light_id):
            task_manager.stop_task(light_id)
            return {"message": f"Stopped displaying temp_to_light for light with id: {light_id}"}
    except ValueError as e:
        return {"message": str(e)}

def display_temperature_to_color(temp_min, temp_max, hsv_color_min, hsv_color_max, header, light_id):
    try:
        while task_manager.is_task_running(light_id):
            current_temp = read_temperature()
            converted_hue_value = translate_temperature_to_hsv_color(
                input_temp=current_temp, temp_min=temp_min, temp_max=temp_max,
                hsv_color_min=hsv_color_min, hsv_color_max=hsv_color_max)
            x, y = hsv2xy(converted_hue_value, 1, 1)
            hue_light_utils.change_color(header=header, light_id=light_id, bridge_ip=hue_bridge_ip_address, x=x, y=y)
            time.sleep(1)
    finally:
        task_manager.stop_task(light_id)  # Ensure task is marked as stopped on exit



@app.get("/get-all-lights-details")
async def get_lights(api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    lights = hue_light_utils.get_full_lights(header=_header, bridge_ip=hue_bridge_ip_address)
    
    # Add task status to each light
    for light in lights:
        light_id = light.get("rid")
        light["taskRunning"] = task_manager.is_task_running(light_id)
    
    return lights

@app.get("/get-details/{light_id}")
async def get_light_detail(light_id: str, api_key: str = Security(get_api_key)):
    _header = {"hue-application-key": hue_api_key}
    light = hue_light_utils.get_light_details(header=_header, bridge_ip=hue_bridge_ip_address, light_id=light_id)
    
    if light:
        light["taskRunning"] = task_manager.is_task_running(light_id)  # Add task status
    return light


@app.get("/check-connection")
async def check(api_key: str = Security(get_api_key)):
    return {"message": "OK"}


