import hue_mdns_bridge_explorer

hue_bridge_explorer = hue_mdns_bridge_explorer.HueBridgeListener()


def init():
    # global hue_bridge_explorer
    print("Looking for Philips Hue bridges...")
    hue_bridge_ip_addresses_list = hue_mdns_bridge_explorer.get_hue_bridge_ips()
    if len(hue_bridge_ip_addresses_list) > 1:
        print("More than 1 Hue bridge found! Terminating...")
        quit(0)
    elif len(hue_bridge_ip_addresses_list) == 0:
        print("No Hue bridge found! Terminating...")
        quit(0)
    else:
        with open("hue-bridge-ip.txt", "w") as file:
            file.write(str(hue_bridge_ip_addresses_list[0]))
            print(f"Hue Bridge found! Its' IP address set to: {str(hue_bridge_ip_addresses_list[0])}")
