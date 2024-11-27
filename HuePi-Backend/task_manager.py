class TaskManager:
    def __init__(self):
        self.tasks = {}

    def start_task(self, light_id: str, task: callable, *args, **kwargs):
        if light_id in self.tasks:
            raise ValueError(f"A task is already running for light_id {light_id}.")
        self.tasks[light_id] = True
        task(*args, **kwargs)

    def stop_task(self, light_id: str):
        if light_id in self.tasks:
            self.tasks[light_id] = False
            del self.tasks[light_id]

    def is_task_running(self, light_id: str) -> bool:
        return self.tasks.get(light_id, False)


task_manager = TaskManager()
