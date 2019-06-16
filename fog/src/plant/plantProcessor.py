from random import randint

from control.controlMessageConstants import WATER_MORE, UV_LESS, UV_MORE, UV_NO_CHANGE, WATER_NO_CHANGE
from core.config import Config
from plant.plantRecipe import PlantRecipe
from weather.weatherProvider import WeatherProvider


class PlantProcessor:

    __plant: PlantRecipe
    __weather_provider: WeatherProvider

    def __init__(self, config: Config, weather_provider: WeatherProvider, plant: PlantRecipe) -> None:
        super().__init__()
        self.__plant = plant
        self.__weather_provider = weather_provider
        self.__config = config

    async def water_status(self, current_humidity: float) -> int:
        current_unsued_forecast = self.__weather_provider.get_rain_forecast()

        if self.__config.WEATHER_FORCE_ALWAYS_WATER_RETURN_VALUE != -1:
            return self.__config.WEATHER_FORCE_ALWAYS_WATER_RETURN_VALUE

        if current_humidity < self.__plant.target_humidity() and not await self.__weather_provider.will_it_rain():
            return WATER_MORE

        return WATER_NO_CHANGE

    async def uv_status(self, current_uv: float) -> int:
        current_unsued_forecast = self.__weather_provider.get_clear_forecast()

        if self.__config.WEATHER_FORCE_ALWAYS_LIGHT_RETURN_VALUE != -1:
            return self.__config.WEATHER_FORCE_ALWAYS_LIGHT_RETURN_VALUE

        if await self.__weather_provider.will_it_be_clear():
            return UV_LESS

        if current_uv > 0:
            return randint(UV_NO_CHANGE, UV_MORE)

        return UV_NO_CHANGE
