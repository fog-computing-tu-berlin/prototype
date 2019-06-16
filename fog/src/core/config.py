import os


class Config:
    def __init__(self) -> None:
        super().__init__()
        self.IS_DEBUG_LOGGING = bool(os.environ.get('IS_DEBUG_LOGGING', False))
        self.CONTROL_MESSAGE_TICK_RATE = int(os.environ.get('CONTROL_MESSAGE_TICK_RATE', 0))
        self.CONTROL_MESSAGE_TICKER_UPDATE_TIMEOUT = int(os.environ.get('CONTROL_MESSAGE_TICKER_UPDATE_TIMEOUT', 300000))
        self.EDGE_RECEIVER_LISTEN_PORT = int(os.environ.get('EDGE_RECEIVER_LISTEN_PORT', 5555))
        self.EDGE_CONTROLLER_PORT = int(os.environ.get('EDGE_CONTROLLER_PORT', 5556))
        self.EDGE_ID_GENERATOR_LISTEN_PORT = int(os.environ.get('EDGE_ID_GENERATOR_LISTEN_PORT', 5557))
        self.CLOUD_UPLOAD_URL = os.environ.get('CLOUD_UPLOAD_URL', 'tcp://localhost:5558')
        self.EDGE_ID_UPSTREAM_URL = os.environ.get('EDGE_ID_UPSTREAM_URL', 'tcp://localhost:5559')
        self.EDGE_RECEIVER_MAX_QUEUE_LENGTH = int(os.environ.get('EDGE_RECEIVER_MAX_QUEUE_LENGTH', 10000))
        self.EDGE_ID_RELAY_MAX_QUEUE_LENGTH = int(os.environ.get('EDGE_ID_RELAY_MAX_QUEUE_LENGTH', 10000))
        self.INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH = int(os.environ.get('INTERNAL_MESSAGE_CACHE_MAX_QUEUE_LENGTH', 100000))
        self.CLOUD_SUBMIT_TIMEOUT = int(os.environ.get('CLOUD_SUBMIT_TIMEOUT', 60000))
        self.CLOUD_ID_RELAY_CLOUD_TIMEOUT = int(os.environ.get('CLOUD_ID_RELAY_CLOUD_TIMEOUT', 5000))
        self.WEATHER_LIMIT_HOURS_IN_FUTURE = int(os.environ.get('WEATHER_LIMIT_HOURS_IN_FUTURE', 6))
        self.WEATHER_FORCE_ALWAYS_WATER_RETURN_VALUE = int(os.environ.get('WEATHER_FORCE_ALWAYS_WATER_RETURN_VALUE', -1))
        self.WEATHER_FORCE_ALWAYS_LIGHT_RETURN_VALUE = int(os.environ.get('WEATHER_FORCE_ALWAYS_LIGHT_RETURN_VALUE', -1))
        self.WEATHER_CITY = os.environ.get('WEATHER_CITY', 'Berlin')

        self.validate_weather_city_exists()

    def validate_weather_city_exists(self):
        # This really ugly, but keeps the Creds in one Place
        from weather.weatherProvider import owm
        try:
            if owm.three_hours_forecast(self.WEATHER_CITY) is None:
                raise ValueError('WEATHER_CITY not found')
        except:
            raise ValueError('WEATHER_CITY not found')
