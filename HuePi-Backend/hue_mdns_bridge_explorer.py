from zeroconf import ServiceBrowser, Zeroconf
import socket
import time


class HueBridgeListener:
    def __init__(self):
        self.hue_bridge_ips = []

    def remove_service(self, zeroconf: Zeroconf, type_: str, name: str) -> None:
        pass

    def add_service(self, zeroconf: Zeroconf, type_: str, name: str) -> None:
        info = zeroconf.get_service_info(type_, name)
        if info:
            ip_ = socket.inet_ntoa(info.addresses[0])
            self.hue_bridge_ips.append(ip_)

    def update_service(self, zeroconf: Zeroconf, type_: str, name: str) -> None:
        pass


def find_hue_bridges():
    zeroconf = Zeroconf()
    _listener = HueBridgeListener()
    browser = ServiceBrowser(zeroconf, "_hue._tcp.local.", _listener)
    time.sleep(5)
    zeroconf.close()
    return _listener.hue_bridge_ips


def get_hue_bridge_ips():
    _bridges = find_hue_bridges()
    return _bridges


if __name__ == '__main__':
    listener = HueBridgeListener()
    bridges = get_hue_bridge_ips()
    for bridge in bridges:
        print(bridge)
