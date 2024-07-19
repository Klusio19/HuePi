import requests as rq
import json
import urllib3
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


def change_power(header, light_id, bridge_ip):
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
    if level > 100:
        level = 100
    elif level < 0:
        level = 0

    light_url = f"https://{bridge_ip}/clip/v2/resource/light/{light_id}"
    payload = json.dumps({
        "dimming": {
            "brightness": level
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
