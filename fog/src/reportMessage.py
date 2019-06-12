class ReportMessage:
    plant_id: str
    humidity: float
    humidity_sensor_id: str
    temperature: float
    temperature_sensor_id: str
    uv: float
    uv_sensor_id: str
    start_time: int
    end_time: int

    def __init__(self, message: str) -> None:
        super().__init__()
        message_split = message.split(',')

        if len(message_split) != 6:
            raise ValueError("Message as an unknown amount of values")

        self.plant_id = message_split[0]
        self.humidity = float(message_split[1][4:]) / 100
        self.humidity_sensor_id = message_split[1][0:3]
        self.temperature = float(message_split[2][4:]) / 100
        self.temperature_sensor_id = message_split[2][0:3]
        self.uv = float(message_split[3][4:]) / 250
        self.uv_sensor_id = message_split[3][0:3]
        self.start_time = int(message_split[4])
        self.end_time = self.start_time + int(message_split[5])
