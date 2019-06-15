import json
from datetime import datetime

class ReportMessage:

    def __init__(self, message: str) -> None:
        super().__init__()
        message_split = message.split(',')

        if len(message_split) != 6:
            raise ValueError("Message as an unknown amount of values")

        self.edge_id = int(message_split[0])
        self.humidity = float(message_split[1][4:]) / 100
        self.humidity_sensor_id = message_split[1][0:3]
        self.temperature = float(message_split[2][4:]) / 100
        self.temperature_sensor_id = message_split[2][0:3]
        self.uv = float(message_split[3][4:]) / 250
        self.uv_sensor_id = message_split[3][0:3]
        self.started_at = int(message_split[4])
        self.stopped_at = self.started_at + int(message_split[5])

        self.started_at = datetime.utcfromtimestamp(self.started_at/1000).isoformat()
        self.stopped_at = datetime.utcfromtimestamp(self.stopped_at/1000).isoformat()

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)
