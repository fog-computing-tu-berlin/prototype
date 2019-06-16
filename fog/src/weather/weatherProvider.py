from datetime import datetime, timedelta, timezone
from typing import List

import pyowm
from pyowm.weatherapi25.forecaster import Forecaster
from pyowm.weatherapi25.weather import Weather

from core.config import Config

# Yeah, I know.. Hardcoded creds
owm = pyowm.OWM('d1b343df8e5e1067b5b28d4272cca01f')


class WeatherProvider:
    city = ""
    __weather_cache = None
    __weather_buffer_created_at = None

    def __init__(self, config: Config, owm_param = None) -> None:
        super().__init__()
        self.__config = config
        self.city = config.WEATHER_CITY

        if owm_param is not None:
            self.owm = owm_param
        else:
            self.owm = owm

    async def get_weather(self) -> Forecaster:
        self.__refresh_weather_if_cache_to_old()

        return self.__weather_cache

    def __refresh_weather_if_cache_to_old(self) -> None:
        if self.__weather_cache is None or self.__weather_buffer_created_at is None or datetime.now() + timedelta(
                minutes=30) < self.__weather_buffer_created_at:
            self.__weather_buffer_created_at = datetime.now()
            self.__refresh_weather_cache()

    def __refresh_weather_cache(self):
        try:
            self.__weather_cache = self.owm.three_hours_forecast(self.city)
            if self.__weather_cache is None:
                print('City not found')
                return
        except:
            print('Error getting weather from API')
            return

        weathers: List[Weather] = self.__weather_cache.get_forecast().get_weathers()
        hours_in_future = self.__config.WEATHER_LIMIT_HOURS_IN_FUTURE
        last_key = len(weathers)
        for k, w in enumerate(weathers):
            if w.get_reference_time(timeformat='date') > datetime.now(timezone.utc) + timedelta(hours=hours_in_future):
                last_key = k
                break
        while len(weathers) > last_key + 1:
            weathers.pop()
        self.__weather_cache._forecast._weathers = weathers

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
