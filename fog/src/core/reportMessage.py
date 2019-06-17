import json
from datetime import datetime

SENSOR_TYPE_HUMIDITY = 0
SENSOR_TYPE_TEMPERATURE = 1
SENSOR_TYPE_UV = 2


class ReportMessage:

    def __init__(self, message: str) -> None:
        super().__init__()
        message_split = message.split(',')

        if len(message_split) <= 3:
            raise ValueError('Message does not contain enough values')

        self.edge_id = int(message_split[0])
        self.started_at = int(message_split[1])
        self.stopped_at = self.started_at + int(message_split[2])
        self.started_at = datetime.utcfromtimestamp(self.started_at/1000).isoformat()
        self.stopped_at = datetime.utcfromtimestamp(self.stopped_at/1000).isoformat()

        for sensor_value in message_split[3:]:
            self._assign_sensor_value(sensor_value)

    def _assign_sensor_value(self, sensor_value: str) -> None:
        sensor_value_split = sensor_value.split(';')
        sensor_type = int(sensor_value_split[0])
        sensor_id = sensor_value_split[1]
        sensor_read = float(sensor_value_split[2])
        if sensor_type == SENSOR_TYPE_HUMIDITY:
            self.humidity = float(sensor_read) / 100
            self.humidity_sensor_id = sensor_id
        elif sensor_type == SENSOR_TYPE_TEMPERATURE:
            self.temperature = float(sensor_read) / 100
            self.temperature_sensor_id = sensor_id
        elif sensor_type == SENSOR_TYPE_UV:
            self.uv = float(sensor_read) / 250
            self.uv_sensor_id = sensor_id
        else:
            raise ValueError('Unknown Sensor Type')

    def to_json(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)