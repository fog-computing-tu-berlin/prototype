import pyowm
from pyowm.weatherapi25.forecaster import Forecaster
from pyowm.weatherapi25.weather import Weather
from datetime import datetime, timedelta
from typing import List


class WeatherProvider:
    # Yeah, I know.. Hardcoded creds
    owm = pyowm.OWM('d1b343df8e5e1067b5b28d4272cca01f')
    city = ""
    weather_buffer = None
    weather_buffer_created_at = None

    def __init__(self, city="Berlin", owm=None) -> None:
        super().__init__()
        self.city = city

        if owm is not None:
            self.owm = owm

    async def get_weather(self) -> Forecaster:
        self.__refresh_weather_buffer_to_old()
        return self.weather_buffer

    def __refresh_weather_buffer_to_old(self) -> None:
        if self.weather_buffer is None or self.weather_buffer_created_at is None or datetime.now() + timedelta(
                minutes=30) < self.weather_buffer_created_at:
            self.weather_buffer_created_at = datetime.now()
            self.weather_buffer = self.owm.three_hours_forecast(self.city)

    async def get_rain_forecast(self) -> List[Weather]:
        current_weather = await self.get_weather()
        return current_weather.when_rain()

    async def get_clear_forecast(self) -> List[Weather]:
        current_weather = await self.get_weather()
        return current_weather.when_clear()

    async def will_it_rain(self) -> bool:
        current_weather = await self.get_weather()
        return current_weather.will_have_rain()

    async def will_it_be_clear(self) -> bool:
        current_weather = await self.get_weather()
        return current_weather.will_have_clear()
