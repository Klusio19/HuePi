import subprocess
import setup
import hue_mdns_bridge_explorer


if __name__ == "__main__":
    hue_bridge_explorer = hue_mdns_bridge_explorer.HueBridgeListener()
    setup.init()

    command = [
        "uvicorn",
        "server:app",
        "--host", "0.0.0.0",
        "--port", "8000"
    ]

    print(f"Running command: \"{' '.join(command)}\"")

    subprocess.run(command, check=True)
