import requests as rq
import json
import urllib3
from color_conversions import xy2hex
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


def check_response(response: rq.Response):
    if (response.status_code != 200) and (response.status_code != 207):
        print(f'There is something wrong with the Philips Hue API call! Status code: {response.status_code}')


def powered_on(header, light_id, bridge_ip):
    light_url = f"https://{bridge_ip}/clip/v2/resource/light/{light_id}"
    r = rq.get(url=light_url, headers=header, verify=False)
    check_response(r)
    response_json = r.json()
    light_on = response_json['data'][0]['on']['on']
    if light_on:
        return True
    else:
        return False


def switch_power(header, light_id, bridge_ip):
    light_url = f"https://{bridge_ip}/clip/v2/resource/light/{light_id}"
    payload_on = json.dumps({
        "on": {
            "on": True
        }
    })

    payload_off = json.dumps({
        "on": {
            "on": False
        }
    })
    if powered_on(header, light_id, bridge_ip):
        r = rq.put(url=light_url, headers=header, data=payload_off, verify=False)
        check_response(r)
        return {"message": "OK"}
    else:
        r = rq.put(url=light_url, headers=header, data=payload_on, verify=False)
        check_response(r)
        return {"message": "OK"}


def turn_on(header, light_id, bridge_ip):
    if powered_on(header, light_id, bridge_ip):
        return {"message": "Already turned on!"}
    else:
        light_url = f"https://{bridge_ip}/clip/v2/resource/light/{light_id}"
        payload = json.dumps({
            "on": {
                "on": True
            }
        })
        r = rq.put(url=light_url, headers=header, data=payload, verify=False)
        check_response(r)
        return {"message": "OK"}


def turn_off(header, light_id, bridge_ip):
    if not powered_on(header, light_id, bridge_ip):
        return {"message": "Already turned off!"}
    else:
        light_url = f"https://{bridge_ip}/clip/v2/resource/light/{light_id}"
        payload = json.dumps({
            "on": {
                "on": False
            }
        })
        r = rq.put(url=light_url, headers=header, data=payload, verify=False)
        check_response(r)
        return {"message": "OK"}


def change_brightness(header, light_id, level, bridge_ip):
    level_int = (round(level, 2) * 100)

    if level_int > 100:
        level_int = 100
    elif level_int < 0:
        level_int = 0

    light_url = f"https://{bridge_ip}/clip/v2/resource/light/{light_id}"
    payload = json.dumps({
        "dimming": {
            "brightness": level_int
        }
    })
    r = rq.put(url=light_url, headers=header, data=payload, verify=False)
    check_response(r)
    return {"message": "OK"}


def change_color(header, light_id, bridge_ip, x, y):
    light_url = f"https://{bridge_ip}/clip/v2/resource/light/{light_id}"
    r = rq.put(url=light_url, headers=header,
               json={
                   "color": {
                       "xy": {
                           "x": x,
                           "y": y
                       }
                   }
               }, verify=False)
    check_response(r)
    return {"message": "OK"}


def get_full_lights(header, bridge_ip):
    base_url = f"https://{bridge_ip}/clip/v2/resource"
    devices_url = f"{base_url}/device"
    
    # First GET request to retrieve all light devices which returns only "rid" and "name" 
    response = rq.get(url=devices_url, headers=header, verify=False)
    lights = []
    
    check_response(response)
    
    response_data = response.json()
    for device in response_data.get("data", []):
        for service in device.get("services", []):
            if service.get("rtype") == "light":
                light_rid = service["rid"]
                
                # Second GET request for detailed light information
                light_url = f"{base_url}/light/{light_rid}"
                light_response = rq.get(url=light_url, headers=header, verify=False)
                
                if (light_response.status_code != 200 and light_response.status_code != 207):
                    print("Error fetching light details:", light_response.status_code, light_response.text)
                    brightness = None
                    is_on: bool = None
                    color_hex = None

                light_data = light_response.json().get("data", [])[0]
                brightness = light_data.get("dimming", {}).get("brightness")
                is_on = light_data.get("on", {}).get("on")
                xy_color = light_data.get("color", {}).get("xy")
                color_hex = xy2hex(xy_color["x"], xy_color["y"]) if xy_color else None
                name = device["metadata"]["name"]

                light_info = {
                    "rid": light_rid,
                    "name": name,
                    "brightness": brightness,
                    "isOn": is_on,
                    "color": f"#{color_hex}",
                }
                lights.append(light_info)
    return lights


def get_light_details(header, bridge_ip, light_id):
    light_url = f"https://{bridge_ip}/clip/v2/resource/light/{light_id}"
    response = rq.get(url=light_url, headers=header, verify=False)
    
    print(response.status_code)
    if (response.status_code != 200 and response.status_code != 207):
        print("Error fetching light details:", response.status_code, response.text)
        return None

    light_data = response.json().get("data", [])[0]  # Get the first light data object
    
    brightness = light_data.get("dimming", {}).get("brightness")
    name = light_data.get("metadata", {}).get("name")
    is_on = light_data.get("on", {}).get("on")
    xy_color = light_data.get("color", {}).get("xy")
    color_hex = xy2hex(xy_color["x"], xy_color["y"]) if xy_color else None


    light_info = {
        "rid": light_id,
        "name": name,
        "brightness": brightness,
        "isOn": is_on,
        "color": f"#{color_hex}",
    }

    return light_info


