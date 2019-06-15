from weather.weatherProvider import WeatherProvider
from plant.plantRecipe import PlantRecipe
from random import randint
from control.controlMessageConstants import WATER_MORE, UV_LESS, UV_MORE, UV_NO_CHANGE, WATER_NO_CHANGE


class PlantProcessor:

    plant: PlantRecipe
    weather_provider: WeatherProvider

    def __init__(self, weather_provider: WeatherProvider, plant: PlantRecipe) -> None:
        super().__init__()
        self.plant = plant
        self.weather_provider = weather_provider

    async def water_status(self, current_humidity: float) -> int:
        current_unsued_forecast = self.weather_provider.get_rain_forecast()

        if current_humidity < self.plant.target_humidity() and not await self.weather_provider.will_it_rain():
            return WATER_MORE

        return WATER_NO_CHANGE

    async def uv_status(self, current_uv: float) -> int:
        current_unsued_forecast = self.weather_provider.get_clear_forecast()

        if await self.weather_provider.will_it_be_clear():
            return UV_LESS

        if current_uv > 0:
            return randint(UV_NO_CHANGE, UV_MORE)

        return UV_NO_CHANGE
